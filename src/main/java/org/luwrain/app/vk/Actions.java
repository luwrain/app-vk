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


import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.wall.WallPostFull;
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

    Actions(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.conv = new Conversations(luwrain, strings);
    }

    boolean onWallUpdate(Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
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
			luwrain.runUiSafely(()->{
				final List<Dialog> list = resp.getItems();
				base.dialogs = list.toArray(new Dialog[list.size()]);
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

    boolean onUsersSearch(String query, Runnable onSuccess, Runnable onFailure)
    {
	NullCheck.notEmpty(query, "query");
	NullCheck.notNull(onSuccess, "onSuccess");
	NullCheck.notNull(onFailure, "onFailure");
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
				onFailure.run();
				luwrain.crash(e);
			    });
		    }
	}, null));
    }
}
