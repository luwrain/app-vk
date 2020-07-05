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
	    new Action("post", strings.actionWallPost(), new InputEvent(InputEvent.Special.INSERT)),
	    	    new Action("delete", strings.actionWallDelete(), new InputEvent(InputEvent.Special.DELETE)),
	    	    new Action("conversations", strings.actionConversations(), new InputEvent(InputEvent.Special.F5)),
	    	    	    new Action("friends", strings.actionFriends(), new InputEvent(InputEvent.Special.F6)),
	    	    	    	    new Action("followings", strings.actionFollowings(), new InputEvent(InputEvent.Special.F7)),
	    //new Action("news", strings.actionNewsfeed(), new InputEvent(InputEvent.Special.F8)),
	    new Action("users", strings.actionUsers(), new InputEvent(InputEvent.Special.F9)),
	};
    }

        Action[] getWallPostActions()
    {
	return new Action[]{
	    new Action("attach-photo", strings.actionAttachPhoto(), new InputEvent(InputEvent.Special.F5)),
	};
    }


        Action[] getUsersActions()
    {
	return new Action[] {
	    new Action("message", strings.actionMessage(), new InputEvent(InputEvent.Special.F5)),
	    new Action("request-friendship", strings.actionRequestFriendship(), new InputEvent(InputEvent.Special.F6)),
	};
    }


    Action[] getConversationsActions()
    {
	return new Action[0];
    }

            Action[] getFriendsActions()
    {
	return new Action[]{
	    new Action("delete", strings.actionDelete(), new InputEvent(InputEvent.Special.DELETE)),
	    new Action("message", strings.actionMessage(), new InputEvent(InputEvent.Special.F5))
	};
    }

        Action[] getFriendshipRequestsActions()
    {
	return new Action[0];
    }

        Action[] getFollowingsActions()
    {
	return new Action[]{
	    	    new Action("message", strings.actionMessage(), new InputEvent(InputEvent.Special.F5)),
	    new Action("delete", strings.actionDelete(), new InputEvent(InputEvent.Special.DELETE)),
	};
    }


        Action[] getSuggestionsActions()
    {
	return new Action[0];
    }

    
}
