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
import java.util.regex.*;
import java.time.*;

import org.luwrain.core.*;

public final class BirthdayUtils
{
    static final Pattern PAT_FULL = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)$");
    
    final App app;
    BirthdayUtils(App app) { this.app = app; }

    String getAge(String birthday)
    {
	if (birthday == null || birthday.isEmpty())
	    return "";
	Log.debug("proba", birthday);
	final Matcher m = PAT_FULL.matcher(birthday);
	if (!m.find())
	    return "";
	final int year = Integer.parseInt(m.group(3));
	int age = Year.now().getValue() - year;
	final String suff;
	if (age % 100 > 10 && age % 100 < 20)
	    suff = "лет"; else
	if (age % 10 == 1)
	    suff = "год"; else
	    if (age % 10 >= 2 && age % 10 <= 4)
		suff = "года"; else
		suff = "лет";
	return String.valueOf(age) + " " + suff;
    }
}
