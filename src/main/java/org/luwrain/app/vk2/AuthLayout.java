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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.app.base.*;
import org.luwrain.nlp.*;
import org.luwrain.controls.WizardArea.*;

import static org.luwrain.controls.ListUtils.*;


final class AuthLayout extends LayoutBase
{
    private final App app;
    final WizardArea wizardArea;

    AuthLayout(App app)
    {
	super(app);
	this.app = app;
	this.wizardArea = new WizardArea(getControlContext());
	this.wizardArea.setAreaName(app.getStrings().appName());
	final Frame frame = this.wizardArea.newFrame();
	frame.addText(app.getStrings().authIntro());
	frame.addInput(app.getStrings().authUserId(), "");
	frame.addInput(app.getStrings().authAccessToken(), "");
	frame.addClickable(app.getStrings().authConnect(), this::connect);
	this.wizardArea.show(frame);
	setAreaLayout(wizardArea, null);
	    }

    private boolean connect(WizardValues values)
    {
	final String
	userId = values.getText(0).trim(),
	accessToken = values.getText(1).trim();
	if (userId.isEmpty())
	{
	    app.message(app.getStrings().authInvalidUserId(), Luwrain.MessageType.ERROR);
	    return true;
	}
	final int userIdValue;
	try {
	    userIdValue = Integer.parseInt(userId);
	}
	catch(NumberFormatException e)
	{
	    	    app.message(app.getStrings().authInvalidUserId(), Luwrain.MessageType.ERROR);
		    return true;
	}
	if (userIdValue <= 0)
	{
	    	    	    app.message(app.getStrings().authInvalidUserId(), Luwrain.MessageType.ERROR);
			    return true;
	}
	if (accessToken.isEmpty())
	{
	    app.message(app.getStrings().authInvalidAccessToken(), Luwrain.MessageType.ERROR);
	    return true;
	}
	app.onAuth(userIdValue, accessToken);
	return true;
    }
}
