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

abstract class UsersArea extends ConsoleArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final Runnable closing;

    UsersArea(Luwrain luwrain, Strings strings, Base base, Actions actions, Runnable closing)
    {
	super(createParams(luwrain, strings, base));
	NullCheck.notNull(actions, "actions");
	NullCheck.notNull(closing, "closing");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	this.closing = closing;
	setInputPrefix(strings.search() + ">");
	setConsoleClickHandler((area,index,obj)->{
		if (obj == null || !(obj instanceof UserFull))
		    return false;
		return onClick((UserFull)obj);
	    });
	setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty() || base.isBusy())
		    return ConsoleArea.InputHandler.Result.REJECTED;
		if (!actions.onUsersSearch(text, ()->{
			    area.refresh();
			    luwrain.playSound(base.users.length > 0?Sounds.OK:Sounds.ERROR);
			}))
		    return ConsoleArea.InputHandler.Result.REJECTED;
		return ConsoleArea.InputHandler.Result.OK;
	    });
    }

    abstract boolean onClick(UserFull user);

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
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
	case ACTION:
	    if (ActionEvent.isAction(event, "request-friendship"))
		return onRequestFriendship();
	    	    if (ActionEvent.isAction(event, "message"))
		return onMessage();
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
	    return actions.lists.getUsersActions();
	}

    private boolean onRequestFriendship()
    {
	final Object selected = selected();
	if (selected == null || !(selected instanceof UserFull))
	    return false;
	final UserFull user = (UserFull)selected;
	return  actions.onNewFriendship(user.getId(), ()->luwrain.message(strings.friendshipRequestSent(), Luwrain.MessageType.OK));
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
	return actions.onMessageSend(user.getId(), text, ()->luwrain.message(strings.messageSent(), Luwrain.MessageType.OK));
    }


    static private ConsoleArea.Params createParams(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain, strings);
	params.name = strings.usersAreaName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	return params;
    }

    static private final class Appearance implements ConsoleArea.Appearance
    {
	private final Luwrain luwrain;
	private final Strings strings;
	Appearance(Luwrain luwrain, Strings strings)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(strings, "strings");
	    this.luwrain = luwrain;
	    this.strings = strings ; 
	}
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
String extInfo = getExtInfo(user);
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
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
		return user.getFirstName() + " " + user.getLastName() + getExtInfo(user);
	    }
	    return item.toString();
	}
	    private String getExtInfo(UserFull user)
    {
	NullCheck.notNull(user, "user");
	final StringBuilder b = new StringBuilder();
	if (user.getCity() != null && user.getCity().getTitle() != null && !user.getCity().getTitle().trim().isEmpty())
	    b.append(", ").append(user.getCity().getTitle().trim());
	if (user.getStatus() != null && !user.getStatus().trim().isEmpty())
	    b.append(", ").append(user.getStatus().trim());
	    return new String(b);
    }
	    };

    static private final class Model implements ConsoleArea.Model
    {
	private final Base base;
	Model(Base base)
	{
	    NullCheck.notNull(base, "base");
	    this.base = base;
	}
	@Override public int getItemCount()
	{
	    NullCheck.notNullItems(base.users, "base.users");
	    return base.users.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.users, "base.users");
	    return base.users[index];
	}
    };
}
