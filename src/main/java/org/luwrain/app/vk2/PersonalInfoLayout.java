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

import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.users.UserSettingsXtr;
import com.vk.api.sdk.objects.account.SaveProfileInfoRelation;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.controls.edit.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
//import org.luwrain.nlp.*;

import static org.luwrain.controls.ListUtils.*;

final class PersonalInfoLayout extends AppSection
{
static private final String
    LAST_NAME = "last-name";

    final FormArea formArea;
    final EditArea statusArea;

    PersonalInfoLayout(App app, UserSettingsXtr userSett, String status)
    {
	super(app);
	this.formArea = new FormArea(getControlContext(), "Персональная информация"){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case SAVE:
			return onSave();
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
	statusArea = new EditArea(editParams((params)->{
		    params.name = "Статус";
		    params.content = new MutableMarkedLinesImpl(status.split("\n", 1));
		}));
	formArea.addEdit(LAST_NAME, "Имя:", userSett.getLastName());
	formArea.addEdit("relation", "Статус личной жизни:", userSett.getRelation().toString());
	setAreaLayout(AreaLayout.TOP_BOTTOM, formArea, actions(), statusArea, actions());
	    }

    private boolean onSave()
    {
	final var taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		app.getOperations().setStatus(statusArea.getText(" "));
		app.getOperations().savePersonalInfo(SaveProfileInfoRelation.NOT_SPECIFIED);
		app.finishedTask(taskId, ()->{
			app.getLuwrain().playSound(Sounds.DONE);
		    });
	    });
    }

        }
