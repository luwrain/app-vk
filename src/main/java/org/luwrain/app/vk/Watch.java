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

import com.google.gson.JsonObject;

import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.callback.longpoll.responses.GetLongPollEventsResponse;

import org.luwrain.core.*;

final class Watch implements Runnable
{
    static private final String LOG_COMPONENT = Extension.LOG_COMPONENT;
    
    final VkApiClient vk;
    final UserActor actor;

    Watch(VkApiClient vk, UserActor actor)
    {
	NullCheck.notNull(vk, "vk");
	NullCheck.notNull(actor, "actor");
	this.vk = vk;
	this.actor = actor;
    }

    @Override public void run()
    {
	try {
	    final com.vk.api.sdk.objects.messages.LongpollParams params = vk.messages().getLongPollServer(actor).needPts(true).execute();
	    Log.debug(LOG_COMPONENT, "starting watch for " + params.getServer());
	    while(true)
	    {
	    final GetLongPollEventsResponse resp = vk.longPoll().getEvents("https://" + params.getServer(), params.getKey(), params.getTs()).waitTime(10).execute();
	    final List<JsonObject> objs = resp.getUpdates();
	    Log.debug(LOG_COMPONENT, "watch get " + objs.size() + " update(s)");
	    //	    final com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse resp = vk.messages().getLongPollHistory(actor).pts(params.getPts()).execute();
	    }
	}
	catch(ApiException | ClientException e)
	{
	    Log.error(LOG_COMPONENT, "watch failed:" + e.getClass().getName() + ":" + e.getMessage());
	}
    }
}
