
package org.luwrain.app.vk;

import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.cpanel.*;

public final class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[]{
	    new Command(){
		@Override public String getName()
		{
		    return "vk";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("vk");
		}
	    }};
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[]{

	    new Shortcut() {
		@Override public String getExtObjName()
		{
		    return "vk";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    NullCheck.notNullItems(args, "args");
		    return new Application[]{new App()};
		}
	    },

	};
    }

}
