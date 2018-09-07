
package org.luwrain.app.vk;

import org.luwrain.core.*;

interface Settings
{
    static final String PATH = "/org/luwrain/app/vk";

    int getUserId(int defValue);
    String getAccessToken(String defValue);

    static Settings create(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return RegistryProxy.create(luwrain.getRegistry(), PATH, Settings.class);
    }
}
