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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Conversation;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.newsfeed.NewsfeedItem;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;

import org.luwrain.core.*;
import org.luwrain.controls.*;

final class Base //implements Watching.Listener
{
    final Luwrain luwrain;
    final Strings strings;
    private final org.apache.commons.lang3.RandomUtils rand = new org.apache.commons.lang3.RandomUtils();
    //    private final Watching watching;
    private final TransportClient transportClient;
    final VkApiClient vk;
    final UserActor actor;
    final Settings sett;

    private Area[] visibleAreas = new Area[0];
    final Map<Integer, UserFull> userCache = new HashMap();

    final TaskCancelling taskCancelling = new TaskCancelling();
    private FutureTask task = null;

    WallpostFull[] wallPosts = new WallpostFull[0];
    UserFull shownUser = null;
    WallpostFull[] shownUserWallPosts = new WallpostFull[0];
    ConversationWithMessage[] conversations = new ConversationWithMessage[0];
    Message[] messages = new Message[0];
    UserFull[] users = new UserFull[0];
    UserFull[] friends = new UserFull[0];
    UserFull[] friendshipRequests = new UserFull[0];

    UserFull[] followings = new UserFull[0];
    UserFull[] suggestions = new UserFull[0];

    NewsfeedItem[] newsfeedItems = new NewsfeedItem[0];

    Base(Luwrain luwrain, Strings strings/*, Watching watching*/)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	//	this.watching = watching;
	this.sett = Settings.create(luwrain);
	this.transportClient = new HttpTransportClient();
	this.vk = new VkApiClient(transportClient);
	this.actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
	/*
	if (watching != null)
	    watching.addListener(sett.getUserId(0), this);
	*/
    }

    /*
    @Override public void onMessage(int messageId, int peerId, String messageText)
    {
	for(Area a: visibleAreas)
	    if (a instanceof NotificationNewMessage)
		luwrain.runUiSafely(()->{((NotificationNewMessage)a).onMessage(messageId, peerId, messageText);});
    }
    */

    String getUserCommonName(int userId)
    {
	if (userId < 0 || !userCache.containsKey(new Integer(userId)))
	    return String.valueOf(userId);
	final UserFull user = userCache.get(new Integer(userId));
	return user.getFirstName() + " " + user.getLastName();
    }

    void cacheUsers(UserFull[] users)
    {
	NullCheck.notNullItems(users, "users");
	for(UserFull u: users)
	    if (!userCache.containsKey(u.getId()))
		userCache.put(u.getId(), u);
    }

        void setVisibleAreas(Area[] visibleAreas)
    {
	NullCheck.notNullItems(visibleAreas, "visibleAreas");
	this.visibleAreas = visibleAreas.clone();
    }

    boolean runTask(FutureTask task)
    {
	NullCheck.notNull(task, "task");
	if (isBusy())
	    return false;
	this.task = task;
	luwrain.executeBkg(this.task);
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
	return true;
    }

    boolean runBkg(Operation op)
    {
	NullCheck.notNull(op, "op");
	return runTask(new FutureTask(()->{
		    try {
			try {
			    op.run();
			}
			catch(Exception e)
			{
			    onTaskError(e);
			}
		    }
		    finally {
			luwrain.runUiSafely(()->resetTask());
		    }
	}, null));
    }

    private void resetTask()
    {
	this.task = null;
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    void acceptTaskResult(TaskCancelling.TaskId taskId, Runnable runnable)
    {
	NullCheck.notNull(taskId, "taskId");
	NullCheck.notNull(runnable, "runnable");
	luwrain.runUiSafely(runnable);
    }

    void onTaskError(Exception e)
    {
	NullCheck.notNull(e, "e");
	if (e instanceof com.vk.api.sdk.exceptions.ApiPrivateProfileException)
	{
	    luwrain.message("Профиль пользователя закрыт для просмотра", Luwrain.MessageType.ERROR);
	    return;
	}
	luwrain.crash(e);
    }

    void closeApp()
    {
	/*
	if (watching != null)
	    watching.removeListener(this);
	*/
	luwrain.closeApp();
    }

    int nextRandomId()
    {
	return rand.nextInt();
    }

    private final class ConversationsListModel implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    NullCheck.notNullItems(conversations, "conversations");
	    return conversations.length;
	}
	@Override public Object getItem(int index)
	{
	    NullCheck.notNullItems(conversations, "conversations");
	    return conversations[index];
	}
	@Override public void refresh()
	{
	}
    }
    ListArea.Model getConversationsListModel()
    {
	return new ConversationsListModel();
    }

    interface Operation
    {
	void run() throws Exception;
    }
}
