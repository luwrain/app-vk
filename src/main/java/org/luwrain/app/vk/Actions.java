
package org.luwrain.app.vk;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

import com.vk.api.sdk.queries.users.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.speech.*;

final class Actions
{
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

    boolean onUsersSearch()
    {
	//	final UsersSearchQuery query = ;
	try {
	    Log.debug("proba", "requesting");
	    final com.vk.api.sdk.objects.users.responses.SearchResponse resp = base.vk.users().search(base.actor).q("")
	    .offset(0)
	    .count(10)
	    .execute();
	    Log.debug("proba", "printing " + resp.getItems().size());
	    for(com.vk.api.sdk.objects.users.UserFull u: resp.getItems())
		Log.debug("proba", "" + u.getFirstName() + " " + u.getLastName());
	    	    	    Log.debug("proba", "done");
	}
	catch(Exception e)
	{
	    luwrain.message(e.getMessage(), Luwrain.MessageType.ERROR);
	}
	return true;
    }
}
