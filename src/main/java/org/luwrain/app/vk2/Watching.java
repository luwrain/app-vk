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
import java.util.concurrent.*;

import com.google.gson.JsonObject;

import com.vk.api.sdk.exceptions.*;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import org.luwrain.core.*;

final class Watching
{
    static private final String LOG_COMPONENT = "vk";

    final Luwrain luwrain;
    final List<Watch> watches = new ArrayList<>();
    Watching(Luwrain luwrain) { this.luwrain = luwrain; }

    void run()
    {
	Log.debug(LOG_COMPONENT, "starting " + watches.size() + " watch(es)");
	for(Watch w: watches)
	{
	    luwrain.executeBkg(new FutureTask(w, null));
	}
    }

    void loadWatches()
    {
		final Settings sett = Settings.create(luwrain);
		if (sett.getUserId(0) == 0 || sett.getAccessToken("").isEmpty())
		{
		    Log.debug(LOG_COMPONENT, "no auth data to run watches");
		    return;
		}
	final VkApiClient vk = new VkApiClient(new HttpTransportClient());
	final UserActor actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
	watches.add(new Watch(luwrain, sett.getUserId(0), vk, actor));
    }

    void addListener(int userId, WatchingListener listener)
    {
	NullCheck.notNull(listener, "listener");
	for(Watch w: watches)
	    if (w.userId == userId)
	    {
		w.listeners.add(listener);
		return;
	    }
    }

    void removeListener(WatchingListener listener)
    {
	NullCheck.notNull(listener, "listener");
	for(Watch w: watches)
	{
	    final Iterator<WatchingListener> it = w.listeners.iterator();
	    while(it.hasNext())
	    {
		if (it.next() == listener)
		{
		    it.remove();
		    break;
		}
	    }
	}
    }
}
