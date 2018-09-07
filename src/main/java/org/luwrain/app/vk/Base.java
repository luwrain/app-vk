
package org.luwrain.app.vk;

import com.vk.api.sdk.actions.Messages;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.friends.responses.GetListsResponse;
import com.vk.api.sdk.objects.friends.responses.GetResponse;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetDialogsResponse;
import com.vk.api.sdk.queries.friends.FriendsGetListsQuery;
import com.vk.api.sdk.queries.friends.FriendsGetQuery;
import com.vk.api.sdk.queries.users.UserField;

//import org.eclipse.jetty.server.Request;
//import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.luwrain.core.*;

final class Base
{


    private final Luwrain luwrain;
    private final Strings strings;
        	private final TransportClient transportClient;
	final VkApiClient vk;
        final Settings sett;

    Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	this.sett = Settings.create(luwrain);
this.transportClient = new HttpTransportClient();
this.vk = new VkApiClient(transportClient);
    }

    

    void main(String[] args)
    {
	// TODO Auto-generated method stub
	String code="";
		UserAuthResponse authResponse=null;
		try {
		    UserActor actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
		    final int userId = 0;
			vk.messages().send(actor).message("fdgdfg").peerId(userId).execute();//userid-id получателя
		    UserField fields = null;
		    GetFieldsResponse l = vk.friends().get(actor, fields.ABOUT).execute();
			GetDialogsResponse x = vk.messages().getDialogs(actor).execute();
			System.out.println(x.getCount());
			System.out.println("Выводим последние сообщения из диалогов, если в друзьях, то выводиться его Фамилия+Имя+сообщение");
			for (int i=0;i<x.getItems().size();i++)
			{
				Dialog r = x.getItems().get(i);
				Message m = r.getMessage();
				int index=0;
				for (int j=0;j<l.getCount();j++) {
					int e2=m.getUserId();
					UserXtrLists e = l.getItems().get(j);
					int e3=l.getItems().get(j).getId();
					if (e2==e3) 
					{
						index=j;
					}
				}
				if (index>0)
				{
					System.out.println(l.getItems().get(index).getLastName()+" "+l.getItems().get(index).getFirstName()+" "+m.getBody());
				}
				else
				{
					System.out.println(m.getUserId()+" "+m.getBody());
				}
			}
			//вывод поста на своей стене
			vk.wall().post(actor).friendsOnly(true).message("dfgdfgd").execute();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
