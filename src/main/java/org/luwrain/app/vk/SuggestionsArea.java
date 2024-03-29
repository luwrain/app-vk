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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

abstract class SuggestionsArea extends ListArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final Runnable closing;

    private FollowingsArea followingsArea = null;
    private Area defaultArea = null;

    SuggestionsArea(Luwrain luwrain, Strings strings, Base base, Actions actions, Runnable closing)
    {
	super(createParams(luwrain, strings, base));
	NullCheck.notNull(actions, "actions");
	NullCheck.notNull(closing, "closing");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	this.closing = closing;
	setListClickHandler((area,index,obj)->{
		NullCheck.notNull(obj, "obj");
		if (!(obj instanceof UserFull))
		    return false;
		final UserFull user = (UserFull)obj;
		return actions.onNewFriendship(user.getId(), ()->{
refresh();
followingsArea.refresh();
luwrain.playSound(Sounds.OK);
				    });
	    });
    }

        abstract boolean onProperties(UserFull user);

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		if (defaultArea == null)
		    return false;
		luwrain.setActiveArea(defaultArea);
		return true;
	    case BACKSPACE:
		if (followingsArea == null)
		    return false;
		luwrain.setActiveArea(followingsArea);
		return true;
	    case ESCAPE:
		closing.run();
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case PROPERTIES:
	    {
		final Object obj = selected();
		if (obj == null || !(obj instanceof UserFull))
		    return false;
		return onProperties((UserFull)obj);
		}
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
	return actions.lists.getSuggestionsActions();
    }

    void setFollowingsArea(FollowingsArea followingsArea)
    {
	NullCheck.notNull(followingsArea, "followingsArea");
	if (this.followingsArea != null)
	    throw new RuntimeException("followingsArea already set");
	this.followingsArea = followingsArea;
    }

    void setDefaultArea(Area defaultArea)
    {
	NullCheck.notNull(defaultArea, "defaultArea");
	if (this.defaultArea != null)
	    throw new RuntimeException("defaultArea already set");
	this.defaultArea = defaultArea;
    }

    static private ListArea.Params createParams(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain, strings, base);
	params.name = strings.suggestionsAreaName();
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
	    NullCheck.notNullItems(base.suggestions, "base.suggestions");
	    return base.suggestions.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.suggestions, "base.suggestions");
	    return base.suggestions[index];
	}
	@Override public void refresh()
	{
	}
    };

    static private final class Appearance implements ListArea.Appearance<Object>
    {
	private final Luwrain luwrain;
	private final Strings strings;
	private final Base base;
	Appearance(Luwrain luwrain, Strings strings, Base base)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(strings, "strings");
	    NullCheck.notNull(base, "base");
	    this.luwrain = luwrain;
	    this.strings = strings;
	    this.base = base;
	}
	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
 		luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, user.getFirstName() + " " + user.getLastName(), null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
		return user.getFirstName() + " " + user.getLastName();
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
