/*
   Copyright 2012-2023 Michael Pozhidaev <msp@luwrain.org>

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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;

import static org.luwrain.controls.ListUtils.*;

final class FriendsLayout extends AppSection
{
        final ListArea<UserFull> friendsArea;

    FriendsLayout(App app)
    {
	super(app);
		this.friendsArea = new ListArea<UserFull>(listParams((params)->{
			    params.name = "Друзья";//FIXME:
			    params.model = new ListModel<>(app.friends);
			    params.appearance = new UserAppearance(app);
			    			    params.clickHandler = this::openUser;
			}));
		setAreaLayout(friendsArea, actions());
		    }

    private boolean openUser(ListArea<UserFull> area, int index, UserFull user)
    {
	final var taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
	final var f = app.getOperations().getFriends(user.getId());
		app.finishedTask(taskId, ()->{
			final UserProfileLayout layout = new UserProfileLayout(app, f, ()->{
				app.setAreaLayout(this);
				setActiveArea(friendsArea);
				return true;
			});
			app.setAreaLayout(layout);
			getLuwrain().announceActiveArea();
		    });
	    });
    }
}
