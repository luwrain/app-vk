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

//import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Conversation;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.messages.Message;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

final class ConversationsArea extends ListArea implements NotificationNewMessage
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final Runnable closing;

    private MessagesArea messagesArea = null;
    private Area defaultArea = null;

    ConversationsArea(Luwrain luwrain, Strings strings, Base base, Actions actions, Runnable closing)
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
		if (!(obj instanceof ConversationWithMessage))
		    return false;
		final ConversationWithMessage dialog = (ConversationWithMessage)obj;
		if (dialog.getLastMessage() == null || dialog.getLastMessage().getFromId() < 0)
		    return false;
		return actions.onMessagesHistory(dialog.getLastMessage().getFromId(), ()->{
			messagesArea.activateConv(dialog.getLastMessage().getFromId());
		    });
	    });
	actions.onConversationsUpdate(()->{
		//luwrain.playSound(Sounds.CLICK);
		luwrain.setActiveArea(ConversationsArea.this);
		refresh();
	    });
    }

    @Override public void onMessage(int messageId, int peerId, String messageText)
    {
	actions.onConversationsUpdateNonInteractive(()->refresh());
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		if (messagesArea == null)
		    return false;
		luwrain.setActiveArea(messagesArea);
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
	return actions.lists.getConversationsActions();
    }

    void setMessagesArea(MessagesArea messagesArea)
    {
	NullCheck.notNull(messagesArea, "messagesArea");
	if (this.messagesArea != null)
	    throw new RuntimeException("messagesArea already set");
	this.messagesArea = messagesArea;
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
	params.name = strings.conversationsAreaName();
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
	    NullCheck.notNullItems(base.dialogs, "base.dialogs");
	    return base.dialogs.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(base.dialogs, "base.dialogs");
	    return base.dialogs[index];
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
	    if (item instanceof ConversationWithMessage)
	    {
		final Conversation dialog = ((ConversationWithMessage)item).getConversation();
		final Message message = ((ConversationWithMessage)item).getLastMessage();
		if (dialog.getUnreadCount() != null)
		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.ATTENTION, base.getUserCommonName(message.getFromId()) + " " + dialog.getUnreadCount() + " " + message.getText(), null)); else
		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, base.getUserCommonName(message.getFromId()) + " " + message.getText(), null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof ConversationWithMessage)
	    {
		final Conversation dialog = ((ConversationWithMessage)item).getConversation();
		final Message message = ((ConversationWithMessage)item).getLastMessage();
		if (dialog.getUnreadCount() != null)
		    return base.getUserCommonName(message.getFromId()) + " (" + dialog.getUnreadCount() + "): " + message.getText(); else
		    return base.getUserCommonName(message.getFromId()) + ": " + message.getText();
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
