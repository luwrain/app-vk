/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import com.vk.api.sdk.objects.wall.WallPostFull;

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

    @Override public boolean onInputEvent(KeyboardEvent event)
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
		if (selected == null || !(selected instanceof WallPostFull))
		    return false;
		//FIXME:confirmation
		if (!actions.onWallDelete((WallPostFull)selected, ()->{
			    refresh();
			    luwrain.onAreaNewBackgroundSound(this);
			    luwrain.playSound(Sounds.OK);
			}, ()->luwrain.onAreaNewBackgroundSound(this)))
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
	params.context = new DefaultControlEnvironment(luwrain);
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
		return base.shownUserWallPosts.length;
	    return base.wallPosts.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.wallPosts, "base.wallPOsts");
	    	    NullCheck.notNullItems(base.shownUserWallPosts, "base.shownUserWallPosts");
		    	    if (base.shownUser != null)
		return base.shownUserWallPosts[index];
	    return base.wallPosts[index];
	}
	@Override public void refresh()
	{
	}
    };

    static private final class Appearance implements ListArea.Appearance
    {
	private final Luwrain luwrain;
	private final Strings strings;
	Appearance(Luwrain luwrain, Strings strings)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(strings, "strings");
	    this.luwrain = luwrain;
	    this.strings = strings;
	}
	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof WallPostFull)
	    {
		final WallPostFull post = (WallPostFull)item;
	    	luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, post.getText(), null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof WallPostFull)
	    {
		final WallPostFull post = (WallPostFull)item;
		return post.getText();
	    }
	    return item.toString();
	}
	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}
	@Override public int getObservableRightBound(Object item)
	{
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }
}
