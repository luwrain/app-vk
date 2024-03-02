/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;
import org.luwrain.app.base.*;

import static org.luwrain.app.vk2.App.*;

public class AppSection extends LayoutBase
{
    protected final App app;

    public AppSection(App app)
    {
	super(app);
	this.app = app;
    }

    @Override public Actions actions(ActionInfo ... a)
    {
	final List<ActionInfo> aa = new ArrayList<>();
	aa.addAll(Arrays.asList(a));

	aa.add(action("news",
		      "Новости",
		      HOT_KEY_MAIN_LAYOUT, app.layouts()::main));

	aa.add(action("chats",
		      "Чаты",
		      HOT_KEY_CHATS, app.layouts()::chats));

	aa.add(action("home-wall",
		      "Стена",
		      HOT_KEY_HOME_WALL, app.layouts()::homeWall));

	aa.add(action("friends",
		      "Друзья",
		      HOT_KEY_FRIENDS, app.layouts()::friends));

	aa.add(action("friendship-suggestions",
		      "Вероятные знакомые",
		      HOT_KEY_FRIENDSHIP_SUGGESTIONS, app.layouts()::friendshipSuggestions));

	aa.add(action("personal-info",
		      "Персональная информация",
		      App.HOT_KEY_PERSONAL_INFO, app.layouts()::personalInfo));

	return super.actions(aa.toArray(new ActionInfo[aa.size()]));
    }
    }
