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

import com.vk.api.sdk.objects.wall.WallpostFull;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class WallAppearance extends ListUtils.AbstractAppearance<WallpostFull>
{
    final App app;
    WallAppearance(App app) { this.app = app;}

    @Override public void announceItem(WallpostFull post, Set<Flags> flags)
    {
		    	if (post.getText() != null)
	    app.setEventResponse(listItem(Sounds.LIST_ITEM, post.getText(), null)); else
	    	app.setEventResponse(listItem(Sounds.LIST_ITEM, post.toString(), null));
			    }

    @Override public String getScreenAppearance(WallpostFull post, Set<Flags> flags)
    {
	return post.getText() != null?post.getText():post.toString();
	}
}
