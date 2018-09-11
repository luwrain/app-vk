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

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

class MessagesArea extends ConsoleArea2
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final ActionLists actionLists;

    private DialogsArea dialogsArea = null;
    private Area defaultArea = null;

    MessagesArea(Luwrain luwrain, Strings strings, Base base,
	      Actions actions, ActionLists actionLists)
    {
	super(createParams(luwrain, strings, base));
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	this.actionLists = actionLists;
	setInputPrefix(strings.search() + ">");
	setConsoleClickHandler((area,index,obj)->{
		if (obj == null)
		    return false;
		return false;
	    });
	setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty() || base.isBusy())
		    return ConsoleArea2.InputHandler.Result.REJECTED;
		actions.onUsersSearch(text,
				      ()->{
					  area.refresh();
					  luwrain.onAreaNewBackgroundSound(area);
					  luwrain.playSound(base.users.length > 0?Sounds.OK:Sounds.ERROR);
				      },
				      ()->luwrain.onAreaNewBackgroundSound(area));
		luwrain.onAreaNewBackgroundSound(area);
		return ConsoleArea2.InputHandler.Result.OK;
	    });
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		if (defaultArea == null)
		    return false;
		luwrain.setActiveArea(defaultArea);
		return true;
	    case BACKSPACE:
		if (dialogsArea == null)
		    return false;
		luwrain.setActiveArea(dialogsArea);
		return true;
	    case ESCAPE:
		base.closeApp();
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case CLOSE:
	    base.closeApp();
	    return true;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.BACKGROUND_SOUND:
	    if (base.isBusy())
	    {
		((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
		return true;
	    }
	    return false;
	default:
	    return super.onAreaQuery(query);
	}
    }

    void setDialogsArea(DialogsArea dialogsArea)
    {
	NullCheck.notNull(dialogsArea, "dialogsArea");
	if (this.dialogsArea != null)
	    throw new RuntimeException("dialogsArea already set");
	this.dialogsArea = dialogsArea;
    }

    void setDefaultArea(Area defaultArea)
    {
	NullCheck.notNull(defaultArea, "defaultArea");
	if (this.defaultArea != null)
	    throw new RuntimeException("defaultArea already set");
	this.defaultArea = defaultArea;
    }

    static private ConsoleArea2.Params createParams(Luwrain luwrain, Strings strings, Base base)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(base, "base");
	final ConsoleArea2.Params params = new ConsoleArea2.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = new Model(base);
	params.appearance = new Appearance(luwrain);
	params.areaName = strings.appName();
	params.inputPos = ConsoleArea2.InputPos.TOP;
	return params;
    }

    static private final class Appearance implements ConsoleArea2.Appearance
    {
	private final Luwrain luwrain;
	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
 		luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, user.getFirstName() + " " + user.getLastName(), null));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.LIST_ITEM, item.toString(), null));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (item instanceof UserFull)
	    {
		final UserFull user = (UserFull)item;
		return user.getFirstName() + " " + user.getLastName();
	    }
	    return item.toString();
	}
    };

    static private final class Model implements ConsoleArea2.Model
    {
	private final Base base;
	Model(Base base)
	{
	    NullCheck.notNull(base, "base");
	    this.base = base;
	}
	@Override public int getConsoleItemCount()
	{
	    NullCheck.notNullItems(base.users, "base.users");
	    return base.users.length;
	}
	@Override public Object getConsoleItem(int index)
	{
	    NullCheck.notNullItems(base.users, "base.users");
	    return base.users[index];
	}
    };
}
