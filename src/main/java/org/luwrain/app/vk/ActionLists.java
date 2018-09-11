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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

final class ActionLists
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;

    ActionLists(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
    }

    Action[] getWallActions()
    {
	return new Action[]{
	    new Action("post", strings.actionPost(), new KeyboardEvent(KeyboardEvent.Special.INSERT)),
	    	    new Action("dialogs", strings.actionDialogs()),
	    new Action("users", strings.actionUsers()),
	};
    }

    Action[] getDialogsActions()
    {
	return new Action[0];
    }
}
