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
import java.util.concurrent.atomic.*;
import java.io.*;

import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.oneofs.NewsfeedNewsfeedItemOneOf;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;

import static org.luwrain.controls.ListUtils.*;

final class MainLayout extends LayoutBase
{
    private final App app;
        final ListArea<NewsfeedNewsfeedItemOneOf> newsArea;
    final ListArea<ConversationWithMessage> chatsArea;
    //    final ListArea requestsArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
		this.newsArea = new ListArea<NewsfeedNewsfeedItemOneOf>(listParams((params)->{
			    params.name = "Новости";//FIXME:
			    params.model = new ListModel(app.news);
			    params.appearance = new NewsAppearance(app);
			    params.clickHandler = this::onNewsClick;
			}));

		this.chatsArea = new ListArea<ConversationWithMessage>(listParams((params)->{
			    params.name = app.getStrings().conversationsAreaName();
			    params.model = new ListModel(app.chats);
			    params.appearance = new ChatsAppearance(app);
			    		}));
		
		//				this.requestsArea = new ListArea(listParams((params)->{}));
    final ActionInfo
    actionHomeWall = action("home-wall", "Стена", App.HOT_KEY_HOME_WALL, app.layouts()::homeWall);
		setAreaLayout(AreaLayout.LEFT_RIGHT,
			      newsArea, actions(actionHomeWall),
		  chatsArea, null);
    }

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
}
