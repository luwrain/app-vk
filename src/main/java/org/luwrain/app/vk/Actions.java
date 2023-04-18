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
import java.util.concurrent.*;
import java.io.*;

import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.objects.messages.Conversation;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.queries.users.*;
import com.vk.api.sdk.objects.newsfeed.NewsfeedItem;
import com.vk.api.sdk.objects.newsfeed.ItemWallpost;
import com.vk.api.sdk.objects.newsfeed.NewsfeedItemType;
import com.vk.api.sdk.objects.photos.Photo;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.speech.*;

import org.luwrain.app.vk.custom.*;
import org.luwrain.app.vk.TaskCancelling.TaskId;

final class Actions
{
    static final int ANSWER_LIMIT = 100;

    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final App app;
    final Conversations conv;
    final ActionLists lists;

    Actions(Base base, App app)
    {
	NullCheck.notNull(base, "base");
	NullCheck.notNull(app, "app");
	this.luwrain = base.luwrain;
	this.strings = base.strings;
	this.base = base;
	this.conv = new Conversations(luwrain, strings);
	this.lists = new ActionLists(luwrain, strings, base);
	this.app = app;
    }

    boolean onHomeWallUpdate(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final com.vk.api.sdk.objects.wall.responses.GetResponse resp = base.vk.wall().get(base.actor).execute();
		base.acceptTaskResult(taskId, ()->{
			final List<WallpostFull> list = resp.getItems();
			base.wallPosts = list.toArray(new WallpostFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onUserInfoUpdate(int userId, Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final List<com.vk.api.sdk.objects.users.UserXtrCounters> userResp = base.vk.users().get(base.actor).userIds(new Integer(userId).toString())
		.fields(Fields.STATUS, Fields.LAST_SEEN, Fields.OCCUPATION, Fields.INTERESTS, Fields.BDATE)
		.execute();
		if (userResp.isEmpty())
		    return;
		final UserFull shownUser = userResp.get(0);
		final com.vk.api.sdk.objects.wall.responses.GetResponse resp = base.vk.wall().get(base.actor)
		.ownerId(userId)
		.count(30)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			base.shownUser = shownUser;
			final List<WallpostFull> list = resp.getItems();
			base.shownUserWallPosts = list.toArray(new WallpostFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onWallDelete(WallpostFull post, Runnable onSuccess)
    {
	NullCheck.notNull(post, "post");
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		base.vk.wall().delete(base.actor).postId(post.getId()).execute();
		final com.vk.api.sdk.objects.wall.responses.GetResponse resp = base.vk.wall().get(base.actor)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			final List<WallpostFull> list = resp.getItems();
			base.wallPosts = list.toArray(new WallpostFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onWallPost(String text, File[] photos, Runnable onSuccess)
    {
	NullCheck.notEmpty(text, "text");
	NullCheck.notNullItems(photos, "photos");
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final List<String> attachments = new LinkedList();
		for(File f: photos)
		{
		    final com.vk.api.sdk.objects.photos.PhotoUpload server = base.vk.photos().getWallUploadServer(base.actor).execute();
		    final com.vk.api.sdk.objects.photos.responses.WallUploadResponse upload = base.vk.upload().photoWall(server.getUploadUrl().toString(), f).execute();
		    for(Photo p: base.vk.photos().saveWallPhoto(base.actor, upload.getPhoto()).server(upload.getServer()).hash(upload.getHash()).execute())
			attachments.add("photo" + p.getOwnerId() + "_" + p.getId());
		}
		final com.vk.api.sdk.objects.wall.responses.PostResponse resp = base.vk.wall().post(base.actor)
		.message(text)
		.attachments(attachments)
		.execute();
		final com.vk.api.sdk.objects.wall.responses.GetResponse respPosts = base.vk.wall().get(base.actor)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			final List<WallpostFull> list = respPosts.getItems();
			base.wallPosts = list.toArray(new WallpostFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onConversationsUpdate(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final com.vk.api.sdk.objects.messages.responses.GetConversationsResponse resp = base.vk.messages().getConversations(base.actor).execute();
		final List<ConversationWithMessage> list = resp.getItems();
		final List<String> userIds = new LinkedList();
		for(ConversationWithMessage d: list)
		{
		    userIds.add(d.getLastMessage().getFromId().toString());
		    		    userIds.add(d.getLastMessage().getPeerId().toString());
		}
		final UserFull[] users = getUsersForCache(userIds);
		base.acceptTaskResult(taskId, ()->{
			base.conversations = list.toArray(new ConversationWithMessage[list.size()]);
			base.cacheUsers(users);
			onSuccess.run();
		    });
	    });
    }

    void onConversationsUpdateNonInteractive(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	luwrain.executeBkg(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.messages.responses.GetConversationsResponse resp = base.vk.messages().getConversations(base.actor).execute();
			final List<ConversationWithMessage> list = resp.getItems();
			final List<String> userIds = new LinkedList();
			for(ConversationWithMessage d: list)
			    userIds.add(d.getLastMessage().getFromId().toString());
			final UserFull[] users = getUsersForCache(userIds);
			luwrain.runUiSafely(()->{
				base.conversations = list.toArray(new ConversationWithMessage[list.size()]);
				base.cacheUsers(users);
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			base.onTaskError(e);
		    }
	}, null));
    }

    boolean onMessagesHistory(ConversationWithMessage conv, Runnable onSuccess)
    {
	NullCheck.notNull(conv, "conv");
	NullCheck.notNull(onSuccess, "onSuccess");
	final Message message = conv.getLastMessage();
	if (message == null)
	    return false;
	final int peerId;
	if (message.getOut() == BoolInt.YES)
	    peerId = message.getPeerId(); else
	    peerId = message.getFromId();
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		base.vk.messages().markAsRead(base.actor)
		.peerId(peerId)
		.startMessageId(message.getId())
		.execute();
		final com.vk.api.sdk.objects.messages.responses.GetHistoryResponse resp = base.vk.messages().getHistory(base.actor)
		.userId(peerId)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			final List<Message> list = resp.getItems();
			base.messages = list.toArray(new Message[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    void onMessagesHistoryNonInteractive(int userId, Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	luwrain.executeBkg(new FutureTask(()->{
		    try {
			final com.vk.api.sdk.objects.messages.responses.GetHistoryResponse resp = base.vk.messages().getHistory(base.actor)
			.userId(userId)
			.execute();
			luwrain.runUiSafely(()->{
				final List<Message> list = resp.getItems();
				base.messages = list.toArray(new Message[list.size()]);
				onSuccess.run();
			    });
			return;
		    }
		    catch(Exception e)
		    {
			base.onTaskError(e);
		    }
	}, null));
    }

    boolean onMessageSend(int userId, String text, Runnable onSuccess)
    {
	NullCheck.notEmpty(text, "text");
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		base.vk.messages().send(base.actor)
		.message(text)
		.randomId(base.nextRandomId())
		.peerId(userId)
		.execute();
		final com.vk.api.sdk.objects.messages.responses.GetHistoryResponse resp = base.vk.messages().getHistory(base.actor)
		.userId(userId)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			final List<Message> list = resp.getItems();
			base.messages = list.toArray(new Message[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onUsersSearch(String query, Runnable onSuccess)
    {
	NullCheck.notEmpty(query, "query");
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		if (query.trim().toLowerCase().matches("id[0-9]+"))
		{
		    final List<String> ids = new LinkedList();
		    ids.add(query.trim().substring(2));
		    final List<com.vk.api.sdk.objects.users.UserXtrCounters> resp = base.vk.users().get(base.actor).userIds(ids).fields(Fields.STATUS, Fields.CITY, Fields.LAST_SEEN).execute();
		    base.acceptTaskResult(taskId, ()->{
			    base.users = resp.toArray(new UserFull[resp.size()]);
			    onSuccess.run();
			});
		    return;
		}
		final com.vk.api.sdk.objects.users.responses.SearchResponse resp = base.vk.users().search(base.actor).q(query)
		.offset(0)
		.count(100)
		.fields(Fields.STATUS, Fields.CITY, Fields.LAST_SEEN)
		.execute();
		base.acceptTaskResult(taskId, ()->{
			final List<UserFull> list = resp.getItems();
			base.users = list.toArray(new UserFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    boolean onFriendshipRequestsUpdate(Runnable onSuccess)
    {
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final var friendsResp = base.vk.friends().get(base.actor).order(com.vk.api.sdk.objects.enums.FriendsOrder.NAME).execute();
		final var friendsList = friendsResp.getItems();
		final var friendsIds = friendsList.toArray(new Integer[friendsList.size()]);
		final var friendsUsers = getUsersForCache(friendsIds);
		final var requestsResp = base.vk.friends().getRequests(base.actor).execute();
		final var requestsList = requestsResp.getItems();
		final var requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
		final var requestsUsers = getUsersForCache(requestsIds);
		base.acceptTaskResult(taskId, ()->{
			base.friends = friendsUsers;
			base.friendshipRequests = requestsUsers;
			onSuccess.run();
		    });
	    });
    }

    boolean onFollowingsUpdate(Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final com.vk.api.sdk.objects.friends.responses.GetRequestsResponse requestsResp = base.vk.friends().getRequests(base.actor).out(true).execute();
		final List<Integer> requestsList = requestsResp.getItems();
		final Integer[] requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
		final UserFull[] requestsUsers = getUsersForCache(requestsIds);
		final com.vk.api.sdk.objects.friends.responses.GetSuggestionsResponse resp = base.vk.friends().getSuggestions(base.actor).execute();
		final List<UserFull> list = resp.getItems();
		base.acceptTaskResult(taskId, ()->{
			base.followings = requestsUsers;
			base.suggestions = list.toArray(new UserFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    //FIXME:refresh friends and friendsrequests
    boolean onFriendshipDelete(int userId, Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId();
	return base.runBkg(()->{
		final com.vk.api.sdk.objects.friends.responses.DeleteResponse deleteResp = base.vk.friends().delete(base.actor).userId(userId).execute();
		final com.vk.api.sdk.objects.friends.responses.GetRequestsResponse requestsResp = base.vk.friends().getRequests(base.actor).out(true).execute();
		final List<Integer> requestsList = requestsResp.getItems();
		final Integer[] requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
		final UserFull[] requestsUsers = getUsersForCache(requestsIds);
		final com.vk.api.sdk.objects.friends.responses.GetSuggestionsResponse resp = base.vk.friends().getSuggestions(base.actor).execute();
		final List<UserFull> list = resp.getItems();
		base.acceptTaskResult(taskId, ()->{
			base.followings = requestsUsers;
			base.suggestions = list.toArray(new UserFull[list.size()]);
			onSuccess.run();
		    });
	    });
    }

    //FIXME:request followings and suggestions
    boolean onNewFriendship(int userId, Runnable onSuccess)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	final TaskId taskId = base.taskCancelling.newTaskId(); 
	return base.runBkg(()->{
		base.vk.friends().add(base.actor).userId(userId).execute();
		final com.vk.api.sdk.objects.friends.responses.GetResponse friendsResp = base.vk.friends().get(base.actor).execute();
		final List<Integer> friendsList = friendsResp.getItems();
		final Integer[] friendsIds = friendsList.toArray(new Integer[friendsList.size()]);
		final UserFull[] friendsUsers = getUsersForCache(friendsIds);
		final com.vk.api.sdk.objects.friends.responses.GetRequestsResponse requestsResp = base.vk.friends().getRequests(base.actor).execute();
		final List<Integer> requestsList = requestsResp.getItems();
		final Integer[] requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
		final UserFull[] requestsUsers = getUsersForCache(requestsIds);
		base.acceptTaskResult(taskId, ()->{
			base.friends = friendsUsers;
			base.friendshipRequests = requestsUsers;
			onSuccess.run();
		    });
	    });
    }

    boolean onNewsfeedUpdate(Runnable onSuccess)
    {
	/*
	  NullCheck.notNull(onSuccess, "onSuccess");
	  return base.runTask(new FutureTask(()->{
		    try {
			final GetNewsfeedPostsResponse resp = new NewsfeedGetPostsQuery (base.vk, base.actor).filters(NewsfeedGetFilter.POST).execute();
			final List<ItemWallpost> items = resp.getItems();
			luwrain.runUiSafely(()->{
				//							base.newsfeedItems = list.toArray(new NewsfeedItem[list.size()]);
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
	*/
	return false;
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
	final List<com.vk.api.sdk.objects.users.UserXtrCounters> resp = base.vk.users().get(base.actor).userIds(ids).fields(Fields.STATUS, Fields.LAST_SEEN, Fields.CITY, Fields.BDATE).execute();
	return resp.toArray(new UserFull[resp.size()]);
    }
}
