/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.vk;

import java.util.*;

import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostAttachmentType;
import com.vk.api.sdk.objects.wall.PostType;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

class WallArea extends ListArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;

    WallArea(Luwrain luwrain, Strings strings, Base base,
	     Actions actions)
    {
	super(createParams(luwrain, strings, base));
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	showHome();
    }

    boolean showHome()
    {
	if (!actions.onHomeWallUpdate(()->{
		    refresh();
		    reset(false);
		    luwrain.setActiveArea(WallArea.this);
		}))
	    return false;
	base.shownUser = null;
	return true;
    }

    boolean showUserInfo(int userId)
    {
	return actions.onUserInfoUpdate(userId, ()->{
		refresh();
		reset(false);
		luwrain.setActiveArea(WallArea.this);
	    });
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ESCAPE:
		if (base.shownUser != null)
		    return showHome();
		base.closeApp();
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case ACTION:
	    if (ActionEvent.isAction(event, "delete"))
	    {
		final Object selected = selected();
		if (selected == null || !(selected instanceof WallpostFull))
		    return false;
		//FIXME:confirmation
		if (!actions.onWallDelete((WallpostFull)selected, ()->{
			    refresh();
			    luwrain.onAreaNewBackgroundSound(this);
			    luwrain.playSound(Sounds.OK);
			}))
		    return false;
		luwrain.onAreaNewBackgroundSound(this);
		return true;
	    }
	    return super.onSystemEvent(event);
	case CLOSE:
	    base.closeApp();
	    return true;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.BACKGROUND_SOUND:
	    if (base.isBusy())
	    {
		((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
		return true;
	    }
	    return false;
	default:
	    return super.onAreaQuery(query);
	}
    }

    @Override public Action[] getAreaActions()
    {
	return actions.lists.getWallActions();
    }

    static private ListArea.Params createParams(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain, strings);
	params.name = strings.wallAreaName();
	return params;
    }

    static private final class Model implements ListArea.Model
    {
	private final Base base;
	Model(Base base)
	{
	    NullCheck.notNull(base, "base");
	    this.base = base;
	}
	@Override public int getItemCount()
	{
	    NullCheck.notNullItems(base.wallPosts, "base.wallPosts");
	    NullCheck.notNullItems(base.shownUserWallPosts, "base.shownUserWallPosts");
	    if (base.shownUser != null)
		return base.shownUserWallPosts.length + 5;
	    return base.wallPosts.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.wallPosts, "base.wallPOsts");
	    NullCheck.notNullItems(base.shownUserWallPosts, "base.shownUserWallPosts");
	    if (base.shownUser != null)
		switch(index)
		{
		case 0:
		    return new Section(base.shownUser.getFirstName() + " " + base.shownUser.getLastName());
		case 1:
		    {
			final StringBuilder b = new StringBuilder();
			b.append("Дата рождения:");
			if (base.shownUser.getBdate() != null && !base.shownUser.getBdate().trim().isEmpty())
			    b.append(" ").append(base.shownUser.getBdate().trim());
			return new String(b);
		    }
		case 2:
		    {
			final StringBuilder b = new StringBuilder();
			b.append("Образование/работа:");
			if (base.shownUser.getOccupation() != null && base.shownUser.getOccupation().getName() != null && !base.shownUser.getOccupation().getName().trim().isEmpty())
			    b.append(" ").append(base.shownUser.getOccupation().getName().trim());
			return new String(b);
		    }
		case 3:
		    {
			final StringBuilder b = new StringBuilder();
			b.append("Интересы:");
			/*
			if (base.shownUser.getInterests() != null && !base.shownUser.getInterests().trim().isEmpty())
			    b.append(" ").append(base.shownUser.getInterests().trim());
			*/
			return new String(b);
		    }
		case 4:
		    return new Section("Стена");
		default:
		    return base.shownUserWallPosts[index - 5];
		}
	    return base.wallPosts[index];
	}
	@Override public void refresh()
	{
	}
    };

    static private final class Appearance extends ListUtils.DoubleLevelAppearance
    {
	private final Luwrain luwrain;
	private final Strings strings;
	Appearance(Luwrain luwrain, Strings strings)
	{
	    super(new DefaultControlContext(luwrain));
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(strings, "strings");
	    this.luwrain = luwrain;
	    this.strings = strings;
	}
	@Override public boolean isSectionItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return (item instanceof Section);
	}
	@Override public void announceNonSection(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof WallpostFull)
	    {

				final WallpostFull full = (WallpostFull)item;
String extInfo = "";
		if (full.getLikes() != null && full.getLikes().getCount() != null)
		    extInfo = "" + full.getLikes().getCount() + " ";

		
		final Wallpost post = getOrigPost((WallpostFull)item);
		boolean picture = false;
		final List<WallpostAttachment> attachments = post.getAttachments();
		if (attachments != null)
		    for(WallpostAttachment a: attachments)
			if (a.getType() == WallpostAttachmentType.PHOTO || a.getType() == WallpostAttachmentType.POSTED_PHOTO)
			    picture = true;
		final String text = getText(post);
		if (text.isEmpty())
		{
		    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
		    return;
		}
	    	luwrain.setEventResponse(DefaultEventResponse.listItem(picture?Sounds.PICTURE:Sounds.LIST_ITEM, extInfo + text, null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getNonSectionScreenAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof WallpostFull)
	    {
		final Wallpost post = getOrigPost((WallpostFull)item);
		return getText(post);
	    }
	    return item.toString();
	}
	private String getText(Wallpost post)
	{
	    NullCheck.notNull(post, "post");
	    boolean picture = false;
	    final List<WallpostAttachment> attachments = post.getAttachments();
	    if (attachments != null)
		for(WallpostAttachment a: attachments)
		    if (a.getType() == WallpostAttachmentType.PHOTO || a.getType() == WallpostAttachmentType.POSTED_PHOTO)
			picture = true;
	    if (post.getText() != null && !post.getText().trim().isEmpty())
		return post.getText().trim();
	    if (picture)
		return "[ФОТО]";//FIXME:
	    return "";
	}
	private Wallpost getOrigPost(WallpostFull post)
	{
	    NullCheck.notNull(post, "post");
	    if (post.getCopyHistory() == null || post.getCopyHistory().isEmpty())
		return post;
	    //	    return post.getCopyHistory().get(0/*post.getCopyHistory().size() - 1*/);
	    	    return post.getCopyHistory().get(post.getCopyHistory().size() - 1);
	}
    }

    static private final class Section
    {
	final String str;
	Section(String str)
	{
	    NullCheck.notNull(str, "str");
	    this.str = str;
	}
	@Override public String toString()
	{
	    return str;
	}
	@Override public boolean equals(Object sect)
	{
	    if (sect == null || !(sect instanceof Section))
		return false;
	    return str.equals(((Section)sect).str);
	}
    }
}
