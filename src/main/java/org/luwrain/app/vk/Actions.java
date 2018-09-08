
package org.luwrain.app.vk;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

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
