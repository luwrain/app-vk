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
import java.util.concurrent.atomic.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    final ListArea newsArea;
    final ListArea messagesArea;
    final ListArea requestsArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.newsArea = new ListArea(listParams((params)->{
		}));

		this.messagesArea = new ListArea(listParams((params)->{
			    		}));

				this.requestsArea = new ListArea(listParams((params)->{
			    		}));
    }
}
