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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.newsfeed.NewsfeedItem;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetDialogsResponse;
import com.vk.api.sdk.queries.users.UserField;

import org.luwrain.core.*;

final class Base implements Watching.Listener
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Watching watching;
    private final TransportClient transportClient;
    final VkApiClient vk;
    final UserActor actor;
    final Settings sett;

    private Area[] visibleAreas = new Area[0];
    final Map<Integer, UserFull> userCache = new HashMap();

    private FutureTask task = null;

    //Central area
    WallPostFull[] wallPosts = new WallPostFull[0];
    UserFull shownUser = null;
    WallPostFull[] shownUserWallPosts = new WallPostFull[0];

    
    Dialog[] dialogs = new Dialog[0];
    Message[] messages = new Message[0];
    UserFull[] users = new UserFull[0];
    UserFull[] friends = new UserFull[0];
    UserFull[] friendshipRequests = new UserFull[0];

        UserFull[] followings = new UserFull[0];
        UserFull[] suggestions = new UserFull[0];
    
    NewsfeedItem[] newsfeedItems = new NewsfeedItem[0];

    Base(Luwrain luwrain, Strings strings, Watching watching)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	this.watching = watching;
	this.sett = Settings.create(luwrain);
	this.transportClient = new HttpTransportClient();
	this.vk = new VkApiClient(transportClient);
	this.actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
	if (watching != null)
	    watching.addListener(sett.getUserId(0), this);
    }

    @Override public void onMessage(int messageId, int peerId, String messageText)
    {
	for(Area a: visibleAreas)
	    if (a instanceof NotificationNewMessage)
		luwrain.runUiSafely(()->{((NotificationNewMessage)a).onMessage(messageId, peerId, messageText);});
    }

    void setVisibleAreas(Area[] visibleAreas)
    {
	NullCheck.notNullItems(visibleAreas, "visibleAreas");
	this.visibleAreas = visibleAreas;
    }

    String getUserCommonName(int userId)
    {
	if (userId < 0)
	    return "" + userId;
	if (!userCache.containsKey(new Integer(userId)))
	    return "" + userId;
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

    void resetTask()
    {
	this.task = null;
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    void closeApp()
    {
	luwrain.closeApp();
    }
}

/*
		    UserField fields = null;
		    GetFieldsResponse l = vk.friends().get(actor, fields.ABOUT).execute();
			GetDialogsResponse x = vk.messages().getDialogs(actor).execute();
			System.out.println(x.getCount());
			System.out.println("Выводим последние сообщения из диалогов, если в друзьях, то выводиться его Фамилия+Имя+сообщение");
			for (int i=0;i<x.getItems().size();i++)
			{
				Dialog r = x.getItems().get(i);
				Message m = r.getMessage();
				int index=0;
				for (int j=0;j<l.getCount();j++) {
					int e2=m.getUserId();
					UserXtrLists e = l.getItems().get(j);
					int e3=l.getItems().get(j).getId();
					if (e2==e3) 
					{
						index=j;
					}
				}
				if (index>0)
				{
					System.out.println(l.getItems().get(index).getLastName()+" "+l.getItems().get(index).getFirstName()+" "+m.getBody());
				}
				else
				{
					System.out.println(m.getUserId()+" "+m.getBody());
				}
			}
    */
