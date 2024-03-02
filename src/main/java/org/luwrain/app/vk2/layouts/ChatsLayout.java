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

package org.luwrain.app.vk2.layouts;

import java.util.*;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;

import org.luwrain.controls.*;
import org.luwrain.app.vk2.*;

import static org.luwrain.controls.ListUtils.*;

public final class ChatsLayout extends AppSection
{
    public final ListArea<ConversationWithMessage> chatsArea;

    public ChatsLayout(App app)
    {
	super(app);
	this.chatsArea = new ListArea<ConversationWithMessage>(listParams((params)->{
		    params.name = "Чаты";//FIXME:
		    params.model = new ListModel<>(app.chats);
		    params.appearance = new ChatsAppearance(app);
		    //			    			    params.clickHandler = this::openUser;
		}));
	setAreaLayout(chatsArea, actions());
    }
}
