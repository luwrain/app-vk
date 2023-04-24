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
import com.vk.api.sdk.objects.newsfeed.ItemWallpost;

import com.vk.api.sdk.oneofs.NewsfeedNewsfeedItemOneOf;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;
import static org.luwrain.app.vk2.App.*;

final class NewsAppearance extends ListUtils.AbstractAppearance<NewsfeedNewsfeedItemOneOf>
{
    final App app;
    NewsAppearance(App app) { this.app = app;}

    @Override public void announceItem(NewsfeedNewsfeedItemOneOf item, Set<Flags> flags)
    {
	try {
	    final var post = item.getOneOf0();
	    final var userName = app.getUserCommonName(post.getSourceId());
	    switch(post.getType())
	    {
	    case POST:
		if (post.getText() != null)
		    app.setEventResponse(listItem(Sounds.LIST_ITEM, userName + " " + post.getText(), Suggestions.CLICKABLE_LIST_ITEM)); else
		    app.setEventResponse(hint(Hint.EMPTY_LINE));
		break;
	    case FRIEND:
		app.setEventResponse(listItem(Sounds.LIST_ITEM, userName, null));
		break;
	    default:
		app.setEventResponse(listItem(Sounds.LIST_ITEM, post.getType().toString(), null));
	    }
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to parse news feed item: " + e.getClass().getName() + ": " + e.getMessage());
	    app.setEventResponse(listItem(Sounds.ALERT, item.getRaw().toString(), null));
	}
    }

    @Override public String getScreenAppearance(NewsfeedNewsfeedItemOneOf item, Set<Flags> flags)
    {
	try {
	    final var post = item.getOneOf0();
	    return post.getText() != null?post.getText().trim():"";
	}
	catch(Exception e)
	{
	    return item.getRaw().toString();
	}
    }
}
