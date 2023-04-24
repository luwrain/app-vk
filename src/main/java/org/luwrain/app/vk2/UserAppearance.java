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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class UserAppearance extends ListUtils.AbstractAppearance<UserFull>
{
    final App app;
    UserAppearance(App app) { this.app = app;}

    @Override public void announceItem(UserFull user, Set<Flags> flags)
    {
	final var name = app.getUserCommonName(user.getId());
	app.setEventResponse(listItem(Sounds.LIST_ITEM, name, Suggestions.CLICKABLE_LIST_ITEM));
			    }

    @Override public String getScreenAppearance(UserFull user, Set<Flags> flags)
    {
	return app.getUserCommonName(user.getId());
	}
}
