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

import java.util.*;

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

abstract class FriendsArea extends ListArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final Runnable closing;

    private Area defaultArea = null;
    private FriendshipRequestsArea friendshipRequestsArea = null;

    FriendsArea(Luwrain luwrain, Strings strings, Base base, Actions actions, Runnable closing)
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
			if (obj == null || !(obj instanceof UserFull))
			    return false;
			return onClick((UserFull)obj);
		    });
			actions.onFriendshipRequestsUpdate(()->{
		luwrain.setActiveArea(FriendsArea.this);
		refresh();
		friendshipRequestsArea.refresh();
	    });
    }

    abstract boolean onClick(UserFull user);

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		if (friendshipRequestsArea == null)
		    return false;
		luwrain.setActiveArea(friendshipRequestsArea);
		return true;
	    case BACKSPACE:
		if (defaultArea == null)
		    return false;
		luwrain.setActiveArea(defaultArea);
		return true;
	    case ESCAPE:
		closing.run();
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
	    if (ActionEvent.isAction(event, "message"))
		return onMessage();

	    	    if (ActionEvent.isAction(event, "delete"))
		return onDelete();

		    return false;
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
	return actions.lists.getFriendsActions();
    }

    private boolean onMessage()
    {
	final Object selected = selected();
	if (selected == null || !(selected instanceof UserFull))
	    return false;
	final UserFull user = (UserFull)selected;
	final String text = actions.conv.messageText();
	if (text == null || text.trim().isEmpty())
	    return true;
	return actions.onMessageSend(user.getId(), text, ()->luwrain.message(strings.messageSent(), Luwrain.MessageType.OK), ()->{});
    }

            private boolean onDelete()
    {
	final Object selected = selected();
	if (selected == null || !(selected instanceof UserFull))
	    return false;
	final UserFull user = (UserFull)selected;
	return actions.onFriendshipDelete(user.getId(), ()->{
		refresh();
		luwrain.playSound(Sounds.OK);
	    });
    }


    void setDefaultArea(Area defaultArea)
    {
	NullCheck.notNull(defaultArea, "defaultArea");
	if (this.defaultArea != null)
	    throw new RuntimeException("defaultArea already set");
	this.defaultArea = defaultArea;
    }

        void setFriendshipRequestsArea(FriendshipRequestsArea friendshipRequestsArea)
    {
	NullCheck.notNull(friendshipRequestsArea, "friendshipRequestsArea");
	if (this.friendshipRequestsArea != null)
	    throw new RuntimeException("friendshipRequestsArea already set");
	this.friendshipRequestsArea = friendshipRequestsArea;
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
	params.name = strings.friendsAreaName();
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
	    NullCheck.notNullItems(base.friends, "base.friends");
	    return base.friends.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.friends, "base.friends");
	    return base.friends[index];
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
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
		String extInfo = "";
		if (user.getStatus() != null && !user.getStatus().trim().isEmpty())
		    extInfo += ", " + user.getStatus().trim();
		if (user.getLastSeen() != null)
		{
		    final Date date = new Date(user.getLastSeen().getTime().longValue() * 1000);
		    extInfo += ", " + strings.lastSeen(luwrain.i18n().getPastTimeBrief(date));
		}
 		luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, user.getFirstName() + " " + user.getLastName() + extInfo, null));
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
		final String status;
		if (user.getStatus() != null && !user.getStatus().trim().isEmpty())
		    status = " (" + user.getStatus().trim() + ")"; else
		    status = "";
		return user.getFirstName() + " " + user.getLastName() + status;
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
