
package org.luwrain.app.vk;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

final class UsersArea extends ConsoleArea2
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final Base base;
    private final Actions actions;
    private final ActionLists actionLists;

    UsersArea(Luwrain luwrain, Strings strings, Base base,
	      Actions actions, ActionLists actionLists)
    {
	super(createParams(luwrain, strings));

setConsoleClickHandler((area,index,obj)->{
		if (obj == null)
		    return false;
		//FIXME:
		return true;
	    });
setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty() || base.isBusy())
		    return ConsoleArea2.InputHandler.Result.REJECTED;
		//FIXME:
		return ConsoleArea2.InputHandler.Result.OK;
	    });
setInputPrefix(strings.appName() + ">");
this.luwrain = luwrain;
this.strings = strings;
this.base = base;
this.actions = actions;
this.actionLists = actionLists;
    }

    
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
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


    static private ConsoleArea2.Params createParams(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
final ConsoleArea2.Params params = new ConsoleArea2.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = null;
	params.appearance = null;
	params.areaName = strings.appName();
	params.inputPos = ConsoleArea2.InputPos.TOP;
	return params;
    }
}
