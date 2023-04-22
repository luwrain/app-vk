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

package org.luwrain.app.vk;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;
import org.luwrain.i18n.*;

import org.luwrain.app.vk2.Watching;

public final class Extension extends EmptyExtension
{
    static final String LOG_COMPONENT = "vk";

        private Watching watching = null;

    @Override public String init(Luwrain luwrain)
    {
	this.watching = new Watching(luwrain);
	return null;
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{
	    new SimpleShortcutCommand("vk"),
	    new SimpleShortcutCommand("vk2"),
	    };
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[]{
	    new SimpleShortcut("vk", App.class),
	    	    new SimpleShortcut("vk2", org.luwrain.app.vk2.App.class),
	};
    }

    @Override public void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt)
    {
	i18nExt.addCommandTitle(Lang.EN, "vk", "VK");
	i18nExt.addCommandTitle(Lang.RU, "vk", "ВКонтакте");
	try {
	    i18nExt.addStrings(Lang.EN, Strings.NAME, new ResourceStringsObj(luwrain, getClass().getClassLoader(), getClass(), "strings.properties").create(Lang.EN, Strings.class));
	    i18nExt.addStrings(Lang.RU, Strings.NAME, new ResourceStringsObj(luwrain, getClass().getClassLoader(), getClass(), "strings.properties").create(Lang.RU, Strings.class));
	}
	catch(java.io.IOException e)
	{
	    throw new RuntimeException(e);
	}
    }
}
