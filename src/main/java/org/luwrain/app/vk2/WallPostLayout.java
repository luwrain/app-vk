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
    static private final int
	MAX_LINE_LEN = 80;

    private final App app;
    private final WallpostFull wallPost;
    final NavigationArea textArea;
        final ListArea<UserFull> likesArea;

    private final List<Line> lines = new ArrayList<>();

    WallPostLayout(App app, WallpostFull wallPost, List<UserFull> likes, ActionHandler closing)
    {
	super(app);
	this.app = app;
	this.wallPost = wallPost;
	prepareText();
	this.textArea = new NavigationArea(getControlContext()) {
		@Override public int getLineCount() { return lines.size(); }
		@Override public String getLine(int index) { return lines.get(index).text; }
		@Override public String getAreaName() { return "Запись"; }
	    };
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

    void prepareText()
    {
	lines.add(new Line(""));
	if (wallPost.getViews() != null)
	{
	lines.add(new Line("Просмотров: " + wallPost.getViews().getCount()));
	lines.add(new Line(""));
	}
	if (wallPost.getAttachments() != null && !wallPost.getAttachments().isEmpty())
	{
	    lines.add(new Line("Прикреплений: " + wallPost.getAttachments().size()));
	lines.add(new Line(""));

	}
	for (var s: wallPost.getText().split("\n", -1))
	{
	    if (s.trim().isEmpty())
		continue;
var line = new StringBuilder();
	    for(var w: s.split(" ", -1))
	    {
		if (w.isEmpty())
		    continue;
		if (line.length() == 0)
		{
		    line.append(w);
		    continue;
		}
		if (line.length() + w.length() + 1 <= MAX_LINE_LEN)
		{
		    line.append(" ").append(w);
		    continue;
		}
		lines.add(new Line(new String(line)));
		line = new StringBuilder();
		line.append(w);
	    }
	    if (line.length() > 0)
		lines.add(new Line(new String(line)));
	    lines.add(new Line(""));
	}
    }

static final class Line
{
    final String text;
    Line(String text)
    {
	this.text = text;
    }
}
}
