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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;

import static org.luwrain.controls.ListUtils.*;

final class NewWallPostLayout extends LayoutBase
{
    private final App app;
    final SpellCheckingEditArea editArea;

    NewWallPostLayout(App app, ActionHandler closing)
    {
	super(app);
	this.app = app;
	this.editArea = new SpellCheckingEditArea(getLuwrain(), editParams((params)->{
		    params.name = "Новое сообщение";
		}));
	setCloseHandler(closing);
	setAreaLayout(editArea, null);
    }
}
