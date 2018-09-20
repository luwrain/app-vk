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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

final class FriendshipRequestsArea extends ListArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;

    private FriendsArea friendsArea = null;
    private Area defaultArea = null;

    FriendshipRequestsArea(Luwrain luwrain, Strings strings, Base base, Actions actions)
    {
	super(createParams(luwrain, strings, base));
	NullCheck.notNull(actions, "actions");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	setListClickHandler((area,index,obj)->{
		NullCheck.notNull(obj, "obj");
		if (!(obj instanceof UserFull))
		    return false;
		//FIXME:
		/*
		return actions.onMessagesHistory(dialog.getMessage().getUserId(), ()->{
			messagesArea.activateConv(dialog.getMessage().getUserId());
		    }, ()->{});
		*/
		return false;
	    });
	/*
	actions.onFriendshipRequestsUpdate(()->{
		luwrain.playSound(Sounds.CLICK);
		refresh();
	    }, ()->{});
	*/
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
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
		if (friendsArea == null)
		    return false;
		luwrain.setActiveArea(friendsArea);
		return true;
	    case ESCAPE:
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
	return actions.lists.getFriendshipRequestsActions();
    }

    void setFriendsArea(FriendsArea friendsArea)
    {
	NullCheck.notNull(friendsArea, "friendsArea");
	if (this.friendsArea != null)
	    throw new RuntimeException("friendsArea already set");
	this.friendsArea = friendsArea;
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
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain, strings, base);
	params.name = strings.friendshipRequestsAreaName();
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
	    NullCheck.notNullItems(base.friendshipRequests, "base.friendshipRequests");
	    return base.friendshipRequests.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.friendshipRequests, "base.friendshipRequests");
	    return base.friendshipRequests[index];
	}
	@Override public void refresh()
	{
	}
    };

    static private final class Appearance implements ListArea.Appearance
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
	    /*
	    if (item instanceof Dialog)
	    {
		final Dialog dialog = (Dialog)item;
		final Message message = dialog.getMessage();
		if (dialog.getUnread() != null)
		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.ATTENTION, base.getUserCommonName(message.getUserId()) + " " + dialog.getUnread() + " " + message.getBody(), null)); else
		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, base.getUserCommonName(message.getUserId()) + " " + message.getBody(), null));
		return;
	    }
	    */
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    /*
	    if (item instanceof Dialog)
	    {
		final Dialog dialog = (Dialog)item;
		final Message message = dialog.getMessage();
		if (dialog.getUnread() != null)
		    return base.getUserCommonName(message.getUserId()) + " (" + dialog.getUnread() + "): " + message.getBody(); else
		    return base.getUserCommonName(message.getUserId()) + ": " + message.getBody();
	    }
	    */
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
