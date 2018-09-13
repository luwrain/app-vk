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
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;

    private Area defaultArea = null;
    private AreaLayoutHelper layout = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	this.actions = new Actions(luwrain, strings, base);
	this.actionLists = new ActionLists(luwrain, strings, base);
	createDefaultArea();
	this.layout = new AreaLayoutHelper(()->{
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, defaultArea);
	return new InitResult();
    }

        @Override public void closeApp()
    {
	base.closeApp();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return layout.getLayout();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    private void createDefaultArea()
    {
	this.defaultArea = new WallArea(luwrain, strings, base, actions){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    {
				final Area[] areas = layout.getLayout().getAreas();
				if (areas.length > 1)
				{
				    luwrain.setActiveArea(areas[1]);
				    return true;
				}
				return false;
			    }
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
		    case ACTION:
						if (ActionEvent.isAction(event, "conversations"))
			    return onShowConversations(this);
			if (ActionEvent.isAction(event, "post"))
			    return onNewWallPost(this);
			return super.onSystemEvent(event);
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    private boolean onNewWallPost(WallArea wallArea)
    {
	NullCheck.notNull(wallArea, "wallArea");
	layout.openTempArea(new WallPostArea(luwrain, strings, base, actions, actionLists, ()->{
		    layout.closeTempLayout();
		    wallArea.refresh();
	}));
	return true;
    }

    private boolean onShowConversations(WallArea wallArea)
    {
	NullCheck.notNull(wallArea, "wallArea");
	final Runnable closing = ()->{
	    layout.setBasicArea(defaultArea);
	};
	final ConversationsArea conversationsArea = new ConversationsArea(luwrain, strings, base, actions, actionLists);
		final MessagesArea messagesArea = new MessagesArea(luwrain, strings, base, actions, actionLists);
	layout.setBasicLayout(new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, defaultArea, conversationsArea, messagesArea));
	conversationsArea.setMessagesArea(messagesArea);
	messagesArea.setConversationsArea(conversationsArea);
	conversationsArea.setDefaultArea(defaultArea);
	messagesArea.setDefaultArea(defaultArea);
	luwrain.setActiveArea(conversationsArea);
	return true;
    }
}
