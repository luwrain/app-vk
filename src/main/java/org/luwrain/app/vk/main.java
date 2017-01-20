
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

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TransportClient transportClient = new HttpTransportClient();
		VkApiClient vk = new VkApiClient(transportClient); 
		String code="";
		UserAuthResponse authResponse=null;
		try {
			UserActor actor=new UserActor(id, "access-token");//id-id пользователя, https://vk.com/pages?oid=-1&p=%D0%90%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F_%D0%BA%D0%BB%D0%B8%D0%B5%D0%BD%D1%82%D1%81%D0%BA%D0%B8%D1%85_%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D0%B9
			vk.messages().send(actor).message("fdgdfg").peerId(userid).execute();//userid-id получателя
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
