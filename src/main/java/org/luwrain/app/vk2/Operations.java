/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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
import java.io.*;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.friends.GetOrder;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.oneofs.NewsfeedNewsfeedItemOneOf;
import com.vk.api.sdk.objects.newsfeed.Filters;

import org.luwrain.core.*;
import org.luwrain.app.base.*;

import org.luwrain.app.vk.Strings;
import org.luwrain.app.vk.Settings;

final class Operations
{
    final App app;
final VkApiClient vk;
final UserActor actor;

    Operations(App app)
    {
	this.app = app;
	this.vk = app.vk;
	this.actor = app.getActor();
    }

    List<NewsfeedNewsfeedItemOneOf> getNews()
    {
	try {
	    final var resp = vk.newsfeed().get(actor).filters(Filters.POST, Filters.PHOTO, /*Filters.WALL_PHOTO,*/ Filters.AUDIO, Filters.VIDEO).execute();
		    final var ids = new ArrayList<String>();
		    for(var i: resp.getItems())
		    {
			if (i.getOneOf1().getSourceId() != null)
			    ids.add(i.getOneOf1().getSourceId().toString());
		    }
getUsersForCache(ids);
return resp.getItems();
	}
		catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
    }

    List<WallpostFull> getWallPosts()
    {
	try {
			final var  resp = vk.wall().get(actor).execute();
			final var res = new ArrayList<WallpostFull>();
			if (resp != null && resp.getItems() != null)
			    res.addAll(resp.getItems());
			return res;
				}
		catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}

			
    }

	WallpostFull getWallPost(String id)
	{
	    try {
		final var resp = vk.wall().getByIdExtended(actor, id).execute();
		final var items = resp.getItems();
		if (items != null && items.size() == 1)
		    return items.get(0);
		return null;
	    	}
		catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
	}

    List<ConversationWithMessage> getChats()
    {
	try {
	    final var resp = vk.messages().getConversations(actor).execute();
	    final var list = resp.getItems();
	    final var userIds = new ArrayList<String>();
	    for(var d: list)
	    {
		userIds.add(d.getLastMessage().getFromId().toString());
		userIds.add(d.getLastMessage().getPeerId().toString());
	    }
	    final var users = getUsersForCache(userIds);
	    for(var u: users)
		app.userCache.put(u.getId(), u);
	    return list;
	}
	catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
    }

        List<UserFull> getFriends(Integer userId)
    {
	try {
	    final com.vk.api.sdk.objects.friends.responses.GetResponse resp;
	    if (userId != null)
		resp = vk.friends().get(actor).userId(userId).order(GetOrder.NAME).execute(); else
		resp = vk.friends().get(actor).order(GetOrder.NAME).execute();
	    final var l = resp.getItems();
	    final var ids = l.toArray(new Integer[l.size()]);
	    return getUsersForCache(ids);
	}
	catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
    }

    void newFriendship(int userId)
    {
	try {
		vk.friends().add(actor).userId(userId).execute();
		}
	catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
    }

        List<UserFull> getFriendshipRequests()
    {
	try {
	    final var requestsResp = vk.friends().getRequests(actor).execute();
	    final var requestsList = requestsResp.getItems();
	    final var requestsIds = requestsList.toArray(new Integer[requestsList.size()]);
	    return getUsersForCache(requestsIds);
	}
	catch(ApiException | ClientException e)
	{
	    throw new RuntimeException(e);
	}
    }

    List<UserFull> getUsersForCache(Integer[] ids) throws ApiException, ClientException
    {
	final var list = new ArrayList<String>();
	for(Integer i: ids)
	    list.add(i.toString());
	return getUsersForCache(list);
    }

    List<UserFull> getUsersForCache(List<String> ids)  throws ApiException, ClientException
    {
	final var resp = vk.users().get(actor).userIds(ids).fields(Fields.STATUS, Fields.LAST_SEEN, Fields.CITY, Fields.BDATE).execute();
	final var res = new ArrayList<UserFull>();
	res.addAll(resp);
		    for(var u: res)
		app.userCache.put(u.getId(), u);
	return res;
    }
}
