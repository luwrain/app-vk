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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

final class ActionLists
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;

    ActionLists(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
    }

    Action[] getWallActions()
    {
	return new Action[]{
	    new Action("post", strings.actionWallPost(), new KeyboardEvent(KeyboardEvent.Special.INSERT)),
	    	    new Action("delete", strings.actionWallDelete(), new KeyboardEvent(KeyboardEvent.Special.DELETE)),
	    	    new Action("conversations", strings.actionConversations(), new KeyboardEvent(KeyboardEvent.Special.F5)),
	    	    	    new Action("friends", strings.actionFriends(), new KeyboardEvent(KeyboardEvent.Special.F6)),
	    //new Action("news", strings.actionNewsfeed(), new KeyboardEvent(KeyboardEvent.Special.F8)),
	    new Action("users", strings.actionUsers(), new KeyboardEvent(KeyboardEvent.Special.F9)),
	};
    }

        Action[] getUsersActions()
    {
	return new Action[] {
	    new Action("request-friendship", strings.actionRequestFriendship(), new KeyboardEvent(KeyboardEvent.Special.F6)),
	};
    }


    Action[] getConversationsActions()
    {
	return new Action[0];
    }

            Action[] getFriendsActions()
    {
	return new Action[]{
	    new Action("message", strings.actionMessage(), new KeyboardEvent(KeyboardEvent.Special.F5))
	};
    }


        Action[] getFriendshipRequestsActions()
    {
	return new Action[0];
    }
}
