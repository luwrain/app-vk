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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

import static org.luwrain.controls.ListUtils.*;

final class SuggestionsLayout extends AppSection
{
    final ListArea<UserFull> suggestionsArea;

    SuggestionsLayout(App app, List<UserFull> suggestions)
    {
	super(app);
	this.suggestionsArea = new ListArea<UserFull>(listParams((params)->{
		    params.name = "Вероятные знакомые";
		    params.model = new ListModel<>(suggestions);
		    params.appearance = new UserAppearance(app);
		}));
	setAreaLayout(suggestionsArea, actions(
					       action("new-friendship", "Добавить в друзья", new InputEvent(InputEvent.Special.INSERT), ()->app.newFriendship(suggestionsArea.selected()))
					       ));
    }
}
