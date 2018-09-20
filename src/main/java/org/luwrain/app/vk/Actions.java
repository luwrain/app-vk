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
import java.util.concurrent.*;
import java.io.*;

import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.objects.users.UserFull;

import com.vk.api.sdk.queries.users.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.speech.*;

final class Actions
{
    static final int ANSWER_LIMIT = 100;

    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    final Conversations conv;
    final ActionLists lists;

    Actions(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.conv = new Conversations(luwrain, strings);
	this.lists = new ActionLists(luwrain, strings, base);
    }

    boolean onWallUpdate(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.wall.responses.GetResponse resp = base.vk.wall().get(base.actor)
			.execute();
			luwrain.runUiSafely(()->{
				final List<WallPostFull> list = resp.getItems();
				base.wallPosts = list.toArray(new WallPostFull[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onWallDelete(WallPostFull post, Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notNull(post, "post");
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
	return base.runTask(new FutureTask(()->{
		    try {
			base.vk.wall().delete(base.actor).postId(post.getId()).execute();
			final com.vk.api.sdk.objects.wall.responses.GetResponse resp = base.vk.wall().get(base.actor)
			.execute();
			luwrain.runUiSafely(()->{
				final List<WallPostFull> list = resp.getItems();
				base.wallPosts = list.toArray(new WallPostFull[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }


    boolean onWallPost(String text, Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notEmpty(text, "text");
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.wall.responses.PostResponse resp = base.vk.wall().post(base.actor)
			.message(text)
			.execute();
						final com.vk.api.sdk.objects.wall.responses.GetResponse respPosts = base.vk.wall().get(base.actor)
			.execute();
			luwrain.runUiSafely(()->{
				final List<WallPostFull> list = respPosts.getItems();
				base.wallPosts = list.toArray(new WallPostFull[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onDialogsUpdate(Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.messages.responses.GetDialogsResponse resp = base.vk.messages().getDialogs(base.actor).execute();
							final List<Dialog> list = resp.getItems();
							final List<String> userIds = new LinkedList();
							for(Dialog d: list)
							    userIds.add(d.getMessage().getUserId().toString());
							final UserFull[] users = getUsersForCache(userIds);
			luwrain.runUiSafely(()->{
				base.dialogs = list.toArray(new Dialog[list.size()]);
				base.cacheUsers(users);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

        void onDialogsUpdateNonInteractive(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	luwrain.executeBkg(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.messages.responses.GetDialogsResponse resp = base.vk.messages().getDialogs(base.actor).execute();
							final List<Dialog> list = resp.getItems();
							final List<String> userIds = new LinkedList();
							for(Dialog d: list)
							    userIds.add(d.getMessage().getUserId().toString());
							final UserFull[] users = getUsersForCache(userIds);
			luwrain.runUiSafely(()->{
				base.dialogs = list.toArray(new Dialog[list.size()]);
				base.cacheUsers(users);
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
				luwrain.crash(e);
		    }
	}, null));
    }

    boolean onMessagesHistory(int userId, Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.messages.responses.GetHistoryResponse resp = base.vk.messages().getHistory(base.actor)
			.userId(userId)
			.execute();
			luwrain.runUiSafely(()->{
				final List<Message> list = resp.getItems();
				base.messages = list.toArray(new Message[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onMessageSend(int userId, String text, Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notEmpty(text, "text");
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
	return base.runTask(new FutureTask(()->{
		    try {
			base.vk.messages().send(base.actor).message(text).peerId(userId).execute();
						final com.vk.api.sdk.objects.messages.responses.GetHistoryResponse resp = base.vk.messages().getHistory(base.actor)
			.userId(userId)
			.execute();
			luwrain.runUiSafely(()->{
				final List<Message> list = resp.getItems();
				base.messages = list.toArray(new Message[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onUsersSearch(String query, Runnable onSuccess)
    {
	NullCheck.notEmpty(query, "query");
	NullCheck.notNull(onSuccess, "onSuccess");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.users.responses.SearchResponse resp = base.vk.users().search(base.actor).q(query)
			.offset(0)
			.count(ANSWER_LIMIT)
			.execute();
			luwrain.runUiSafely(()->{
				final List<UserFull> list = resp.getItems();
				base.users = list.toArray(new UserFull[list.size()]);
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onFriendshipRequestsUpdate(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	return base.runTask(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.friends.responses.GetResponse friendsResp = base.vk.friends().get(base.actor).execute();
			final List<Integer> friendsList = friendsResp.getItems();
			final Integer[] friendsIds = friendsList.toArray(new Integer[friendsList.size()]);
			final UserFull[] friendsUsers = getUsersForCache(friendsIds);
			final com.vk.api.sdk.objects.friends.responses.GetRequestsResponse requestsResp = base.vk.friends().getRequests(base.actor).execute();
			final List<Integer> requestsList = requestsResp.getItems();
			final Integer[] requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
			final UserFull[] requestsUsers = getUsersForCache(requestsIds);
			luwrain.runUiSafely(()->{
				base.friends = friendsUsers;
				base.friendshipRequests = requestsUsers;
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

    boolean onNewFriendship(int userId, Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	return base.runTask(new FutureTask(()->{
		    try {
			base.vk.friends().add(base.actor, userId).execute();
			final com.vk.api.sdk.objects.friends.responses.GetResponse friendsResp = base.vk.friends().get(base.actor).execute();
			final List<Integer> friendsList = friendsResp.getItems();
			final Integer[] friendsIds = friendsList.toArray(new Integer[friendsList.size()]);
			final UserFull[] friendsUsers = getUsersForCache(friendsIds);
			final com.vk.api.sdk.objects.friends.responses.GetRequestsResponse requestsResp = base.vk.friends().getRequests(base.actor).execute();
			final List<Integer> requestsList = requestsResp.getItems();
			final Integer[] requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
			final UserFull[] requestsUsers = getUsersForCache(requestsIds);
			luwrain.runUiSafely(()->{
				base.friends = friendsUsers;
				base.friendshipRequests = requestsUsers;
				base.resetTask();
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			luwrain.runUiSafely(()->{
				base.resetTask();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }

            private UserFull[] getUsersForCache(Integer[] ids) throws ApiException, ClientException
    {
	final List<String> list = new LinkedList();
	for(Integer i: ids)
	    list.add(i.toString());
	return getUsersForCache(list);
    }

    private UserFull[] getUsersForCache(List<String> ids) throws ApiException, ClientException
    {
	//FIXME:Limit up to 1000
	final List<com.vk.api.sdk.objects.users.UserXtrCounters> resp = base.vk.users().get(base.actor).userIds(ids).execute();
	return resp.toArray(new UserFull[resp.size()]);
    }
}
