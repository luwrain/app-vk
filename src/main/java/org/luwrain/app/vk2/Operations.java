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

//import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.users.UserFull;
//import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.Fields;

import org.luwrain.core.*;
import org.luwrain.app.base.*;

import org.luwrain.app.vk.Strings;
import org.luwrain.app.vk.Settings;

final class Operations
{
    private final VkApiClient vk;
    private final UserActor actor;

    Operations(App app)
    {
	this.vk = app.vk;
	this.actor = app.getActor();
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
	return res;
    }
}
