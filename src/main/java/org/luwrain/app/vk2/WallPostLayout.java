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

import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;

import static org.luwrain.controls.ListUtils.*;

final class WallPostLayout extends LayoutBase
{
    private final App app;
    private final WallpostFull wallPost;
    final SimpleArea textArea;
        final ListArea<UserFull> likesArea;

    WallPostLayout(App app, WallpostFull wallPost, List<UserFull> likes, ActionHandler closing)
    {
	super(app);
	this.app = app;
	this.wallPost = wallPost;
	this.textArea = new SimpleArea(getControlContext(), "", prepareText(wallPost.getText()));
	this.likesArea = new ListArea<UserFull>(listParams((params)->{
		    params.name = "Лайки";
		    params.model = new ListModel<>(likes);
		    params.appearance = new UserAppearance(app);
		}));
	setCloseHandler(closing);
	setAreaLayout(AreaLayout.LEFT_RIGHT, textArea, null, likesArea, actions(
		      action("new-friendship", "Добавить в друзья", new InputEvent(InputEvent.Special.INSERT), ()->app.newFriendship(likesArea.selected()))
										));
    }

    static String[] prepareText(String text)
    {
	final var res = new ArrayList<String>();
	for (var s: text.split("\n", -1))
	    res.add(s);
	return res.toArray(new String[res.size()]);
    }

}
