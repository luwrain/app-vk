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
import com.vk.api.sdk.objects.base.Sex;

import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.UserRelation;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class UserAppearance extends ListUtils.AbstractAppearance<UserFull>
{
    final App app;
    UserAppearance(App app) { this.app = app;}

    @Override public void announceItem(UserFull user, Set<Flags> flags)
    {
	final String age = app.birthdayUtils.getAge(user.getBdate());
	final StringBuilder b = new StringBuilder();
	b.append(app.getUserCommonName(user.getId()));
	if (!age.isEmpty())
	    b.append(", ").append(age);
	if (user.getRelation() != null && user.getRelation() != UserRelation.NOT_SPECIFIED)
	    b.append(", ").append(getRelationDescr(user));
	app.setEventResponse(listItem(Sounds.LIST_ITEM, new String(b), Suggestions.CLICKABLE_LIST_ITEM));
			    }

    @Override public String getScreenAppearance(UserFull user, Set<Flags> flags)
    {
	return app.getUserCommonName(user.getId());
	}

    static String getRelationDescr(UserFull user)
    {
	if (user.getRelation() == null)
	    return "";
	if (user.getSex() == Sex.MALE)
	    	switch(user.getRelation())
	{
	case SINGLE: return "Холост";
	case ENGAGED: return "Помолвлен";
	case MARRIED: return "Женат";
	case IN_LOVE: return "Влюблён";
    }
		if (user.getSex() == Sex.FEMALE)
	    	switch(user.getRelation())
	{
	case SINGLE: return "Не замужем";
	case ENGAGED: return "Помолвлена";
	case MARRIED: return "Замужем";
	case IN_LOVE: return "Влюблена";
    }
	switch(user.getRelation())
	{
	case NOT_SPECIFIED:
	    return "Не указано";
	case SINGLE:
	    return "Холост/не замужем";
	case IN_A_RELATIONSHIP:
	    return "В отношениях";
	case ENGAGED:
	    	    return "Помолвлен/помолвлена";
	case MARRIED:
	    	    return "Женат/замужем";
	case COMPLICATED:
	    return "Непонятно";
	case ACTIVELY_SEARCHING:
	    	    return "В активном поиске";
	case IN_LOVE:
	    	    return "Влюблён/влюблена";
	case IN_A_CIVIL_UNION:
	    	    return "В гражданском браке";
	default:
	    return user.getRelation().toString();
    }
}
}
