
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

    private EditArea editArea = null;
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
	//createArea();
	this.layout = new AreaLayoutHelper(()->{
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, editArea);
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
}
