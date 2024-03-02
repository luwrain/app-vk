/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.vk2;

import java.util.*;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.base.BoolInt;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;

public final class ChatsAppearance extends ListUtils.AbstractAppearance<ConversationWithMessage>
{
    final App app;
    public ChatsAppearance(App app) { this.app = app;}

    @Override public void announceItem(ConversationWithMessage chat, Set<Flags> flags)
    {
	final var conv = chat.getConversation();
	final var message = chat.getLastMessage();
	final boolean noUnread;
	if (message.getOut() == BoolInt.YES)
	    noUnread = conv.getOutRead().equals(message.getId()); else
	    noUnread = conv.getInRead().equals(message.getId());
	final String title;
	if (message.getOut() == BoolInt.YES)
	    title = app.getUserCommonName(message.getPeerId()); else
	    title = app.getUserCommonName(message.getFromId());
	final Sounds sound;
	if (message.getOut() == BoolInt.YES)
	    sound = noUnread?Sounds.SELECTED:Sounds.LIST_ITEM; else
	    sound = noUnread?Sounds.SELECTED:Sounds.ATTENTION;
	app.setEventResponse(listItem(sound, title + ": " + message.getText(), null));
	return;
    }

    @Override public String getScreenAppearance(ConversationWithMessage chat, Set<Flags> flags)
    {
	final var conv = chat.getConversation();
	final var message = chat.getLastMessage();
	if (conv.getUnreadCount() != null)
	    return app.getUserCommonName(message.getFromId()) + " (" + conv.getUnreadCount() + "): " + message.getText(); else
	    return app.getUserCommonName(message.getFromId()) + ": " + message.getText();
    }
}
