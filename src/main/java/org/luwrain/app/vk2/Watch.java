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

import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import org.luwrain.core.*;
import org.luwrain.app.vk2.api.*;

import static org.luwrain.core.NullCheck.*;

public final class Watch implements Runnable
{
    static private final String LOG_COMPONENT = "vk";
    static private final int WAIT_TIME = 20;

    private final Luwrain luwrain;
    final int userId;
    final VkApiClient vk;
    final UserActor actor;
    final List<WatchingListener> listeners = new ArrayList<>();

    Watch(Luwrain luwrain, int userId, VkApiClient vk, UserActor actor)
    {
	notNull(luwrain, "luwrain");
	notNull(vk, "vk");
	notNull(actor, "actor");
	this.luwrain = luwrain;
	this.userId = userId;
	this.vk = vk;
	this.actor = actor;
    }

    private void onMessage(int messageId, int peerId, String messageText)
    {
	for(WatchingListener l: listeners)
	    l.onMessage(messageId, peerId, messageText);
	luwrain.announcement(messageText, Luwrain.AnnouncementType.CHAT, "vk", "message", null);
    }

    @Override public void run()
    {
	while(true)
	{
	    try {
		final com.vk.api.sdk.objects.messages.responses.GetLongPollServerResponse params = vk.messages().getLongPollServer(actor).needPts(true).execute();
		Integer ts = params.getTs();
		while(true)
		{
		    		    final GetLongPollEventsQuery query = new GetLongPollEventsQuery(vk, "https://" + params.getServer(), params.getKey(), ts);
				    final var resp = query.waitTime(WAIT_TIME).execute();


				    /*
				    //				    		    		    final GetLongPollEventsQuery query = vk.longPoll().getEvents("https://" + params.getServer(), params.getKey(), ts);
		    //		    final var resp = query.waitTime(WAIT_TIME).execute();
				    final var resp = vk.longPoll().getEvents("https://" + params.getServer(), params.getKey(), ts).waitTime(WAIT_TIME).execute();
				    */
		    
		    ts = resp.getTs();
		    final var updates = resp.getUpdates();
		    /*
		    for(var u: updates)
		    */
		    final List<JsonArray> objs = resp.getUpdates();
		    for(JsonArray a: objs)
		    {
			if (a.size() == 0 || a.get(0).getAsInt() != 4)
			    continue;
			if (a.size() < 7)
			    continue;
			final int messageId = a.get(1).getAsInt();
			final int peerId = a.get(3).getAsInt();
			final String messageText = a.get(6).getAsString();
			if (messageId < 0 || peerId < 0 || messageText == null)
			    continue;
			onMessage(messageId, peerId, messageText);
		    }
		}
	    }
	    catch(ApiException | ClientException e)
	    {
		e.printStackTrace();
		try {
		    Thread.sleep(5000);
		}
		catch(InterruptedException ee)
		{
		    Thread.currentThread().interrupt();
		}
		continue;
	    }
	}
    }
}
