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

public final class UserProfileLayout extends LayoutBase
{
    private final App app;
        final ListArea<UserFull> friendsArea;

    public UserProfileLayout(App app, List<UserFull> friends, ActionHandler closing)
    {
	super(app);
	this.app = app;
		this.friendsArea = new ListArea<UserFull>(listParams((params)->{
			    params.name = "Друзья";//FIXME:
			    params.model = new ListModel<>(friends);
			    params.appearance = new UserAppearance(app);
			    //			    params.clickHandler = this::onNewsClick;
			}));
		setCloseHandler(closing);
		setAreaLayout(friendsArea, actions(
						   action("new-friendship", "Добавить подписку", new InputEvent(InputEvent.Special.INSERT), ()->app.newFriendship(friendsArea.selected()))
						   ));
    }

    /*
    private boolean onNewsClick(ListArea<NewsfeedNewsfeedItemOneOf> area, int index, NewsfeedNewsfeedItemOneOf item)
    {
	final var post = item.getOneOf0();
	if (post.getSourceId() == null || post.getPostId() == null)
	    return false;
	final var taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final var wallPost = app.getOperations().getWallPost(post.getSourceId().toString() + "_" + post.getPostId());
		app.finishedTask(taskId, ()->{
			final WallPostLayout layout = new WallPostLayout(app, wallPost, ()->{
				app.setAreaLayout(this);
				setActiveArea(newsArea);
				return true;
			});
			app.setAreaLayout(layout);
			getLuwrain().announceActiveArea();
		    });
	    });
    }
    */
}
