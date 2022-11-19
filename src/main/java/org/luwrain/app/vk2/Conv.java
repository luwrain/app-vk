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

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

import org.luwrain.app.vk.Strings;

final class Conv
{
    private final Luwrain luwrain;
    private final Strings strings;

    Conv(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    File attachPhoto()
    {
	return Popups.existingFile(luwrain, strings.attachPhotoPopupPrefix(), luwrain.getFileProperty("luwrain.dir.userhome"), new String[]{"jpg", "jpeg"});
    }

    String messageText()
    {
	return Popups.text(luwrain, strings.messageTextPopupName(), strings.messageTextPopupPrefix(), "");
    }
}
