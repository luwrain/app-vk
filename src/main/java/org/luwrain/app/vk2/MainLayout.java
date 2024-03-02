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

import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.oneofs.NewsfeedNewsfeedItemOneOf;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

import static org.luwrain.controls.ListUtils.*;

public final class MainLayout extends AppSection
{
    final ListArea<NewsfeedNewsfeedItemOneOf> newsArea;
    final ListArea<ConversationWithMessage> chatsArea;
    //    final ListArea requestsArea;

    public MainLayout(App app)
    {
	super(app);
	this.newsArea = new ListArea<NewsfeedNewsfeedItemOneOf>(listParams((params)->{
		    params.name = "Новости";//FIXME:
		    params.model = new ListModel<>(app.news);
		    params.appearance = new NewsAppearance(app);
		    params.clickHandler = this::onNewsClick;
		})){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
			{
			case SAVE:
			return onAddLike();
			}
		    return super.onSystemEvent(event);
		}
	    };

		this.chatsArea = new ListArea<ConversationWithMessage>(listParams((params)->{
			    params.name = app.getStrings().conversationsAreaName();
			    params.model = new ListModel<>(app.chats);
			    params.appearance = new ChatsAppearance(app);
			    		}));
		
		setAreaLayout(AreaLayout.LEFT_RIGHT,
			      newsArea, actions(),
		  chatsArea, actions());
    }

    private boolean onNewsClick(ListArea<NewsfeedNewsfeedItemOneOf> area, int index, NewsfeedNewsfeedItemOneOf item)
    {
	final var post = item.getOneOf0();
	if (post.getSourceId() == null || post.getPostId() == null)
	    return false;
	final var taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final var wallPost = app.getOperations().getWallPost(post.getSourceId().toString() + "_" + post.getPostId());
		final var likes = app.getOperations().getLikesWallPost(wallPost.getOwnerId(), wallPost.getId());
		app.finishedTask(taskId, ()->{
			final WallPostLayout layout = new WallPostLayout(app, wallPost, likes, ()->{
				app.setAreaLayout(this);
				setActiveArea(newsArea);
				return true;
			});
			app.setAreaLayout(layout);
			getLuwrain().announceActiveArea();
		    });
	    });
    }

        private boolean onAddLike()
    {
	final NewsfeedNewsfeedItemOneOf item = newsArea.selected();
	if (item == null)
	    return false;
	final var post = item.getOneOf0();
	if (post.getSourceId() == null || post.getPostId() == null)
	    return false;
	final var taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final var wallPost = app.getOperations().getWallPost(post.getSourceId().toString() + "_" + post.getPostId());
		app.getOperations().addLikeWallPost(wallPost);
		app.finishedTask(taskId, ()->{
			getLuwrain().playSound(Sounds.OK);
		    });
	    });
    }

    
}
