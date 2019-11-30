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

import com.vk.api.sdk.objects.messages.Message;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

class MessagesArea extends ConsoleArea implements NotificationNewMessage
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final Runnable closing;

    private int activeUserId = -1;

    private ConversationsArea conversationsArea = null;
    private Area defaultArea = null;

    MessagesArea(Luwrain luwrain, Strings strings, Base base, Actions actions, Runnable closing)
    {
	super(createParams(luwrain, strings, base));
	NullCheck.notNull(actions, "actions");
	NullCheck.notNull(closing, "closing");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	this.closing = closing;
	setInputPrefix(">");
	setConsoleClickHandler((area,index,obj)->{
		if (obj == null)
		    return false;
		return false;
	    });
	setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (activeUserId < 0 || text.trim().isEmpty() || base.isBusy())
		    return ConsoleArea.InputHandler.Result.REJECTED;
		if (!actions.onMessageSend(activeUserId, text, ()->{
			    refresh();
			    luwrain.onAreaNewBackgroundSound(MessagesArea.this);
			    luwrain.playSound(base.users.length > 0?Sounds.OK:Sounds.OK);
			}))
		    return ConsoleArea.InputHandler.Result.REJECTED;
		luwrain.onAreaNewBackgroundSound(area);
		return ConsoleArea.InputHandler.Result.CLEAR_INPUT;
	    });
    }

    void activateConv(int userId)
    {
	if (userId < 0)
	    throw new IllegalArgumentException("userId (" + userId + ") may not be negative");
	this.activeUserId = userId;
	setInputPrefix(base.getUserCommonName(userId) + ">");
	refresh();
	luwrain.setActiveArea(this);
    }

        @Override public void onMessage(int messageId, int peerId, String messageText)
    {
	if (activeUserId < 0)
	    return;
	actions.onMessagesHistoryNonInteractive(activeUserId, ()->refresh());
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

    void setConversationsArea(ConversationsArea conversationsArea)
    {
	NullCheck.notNull(conversationsArea, "conversationsArea");
	if (this.conversationsArea != null)
	    throw new RuntimeException("conversationsArea already set");
	this.conversationsArea = conversationsArea;
    }

    void setDefaultArea(Area defaultArea)
    {
	NullCheck.notNull(defaultArea, "defaultArea");
	if (this.defaultArea != null)
	    throw new RuntimeException("defaultArea already set");
	this.defaultArea = defaultArea;
    }

    static private ConsoleArea.Params createParams(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain);
	params.areaName = strings.messagesAreaName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	return params;
    }

    static private final class Appearance implements ConsoleArea.Appearance
    {
	private final Luwrain luwrain;
	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof Message)
	    {
		final Message message = (Message)item;
 		luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, message.getText(), null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof Message)
	    {
		final Message message = (Message)item;
		return message.getText();
	    }
	    return item.toString();
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
	@Override public int getConsoleItemCount()
	{
	    NullCheck.notNullItems(base.messages, "base.messages");
	    return base.messages.length;
	}
	@Override public Object getConsoleItem(int index)
	{
	    NullCheck.notNullItems(base.messages, "base.messages");
	    return base.messages[index];
	}
    };
}
