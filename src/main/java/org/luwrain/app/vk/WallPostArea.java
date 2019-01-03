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
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

final class WallPostArea extends FormArea
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final ActionLists actionLists;
    private final Runnable closing;
    private int nextAttachmentNum = 0;

    WallPostArea(Luwrain luwrain, Strings strings, Base base,
		 Actions actions, ActionLists actionLists, Runnable closing)
    {
	super(new DefaultControlEnvironment(luwrain), strings.wallPostAreaName());
	NullCheck.notNull(actions, "actions");
	NullCheck.notNull(actionLists, "actionLists");
	NullCheck.notNull(closing, "closing");
	this.luwrain = luwrain;
	this.strings = strings;
	this.base = base;
	this.actions = actions;
	this.actionLists = actionLists;
	this.closing = closing;

	activateMultilineEdit("Введите текст новой записи ниже:", new String[0], true);
	
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ESCAPE:
		closing.run();
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
	case ACTION:
	    if (ActionEvent.isAction(event, "attach-photo"))
		return onAttachPhoto();
	case OK:
	    {
		final String text = getPostText();
		if (text.trim().isEmpty())
		    return false;
		if (!actions.onWallPost(text, new File[]{new File("")}, ()->{
			closing.run();
			}, ()->{}))
		    return false;
		luwrain.message(strings.sendingPost());
		return true;
	    }
	case CLOSE:
	    base.closeApp();
	    return true;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override public Action[] getAreaActions()
    {
	return actionLists.getWallPostActions();
    }

    private boolean onAttachPhoto()
    {
	final File file = actions.conv.attachPhoto();
	if (file == null)
	    	return true;
	addStatic("photo" + nextAttachmentNum, file.getName(), file);
	++nextAttachmentNum;
	return true;
    }

    private String getPostText()
    {
	final String[] lines = getMultilineEditTextVec();
	if (lines.length == 0)
	    return "";
	final StringBuilder b = new StringBuilder();
	b.append(lines[0]);
	for(int i = 1;i < lines.length - 1;++i)
	    b.append("\n" + lines[i]);
	return new String(b);
    }
}
