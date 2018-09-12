/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.users.UserFull;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetDialogsResponse;
import com.vk.api.sdk.queries.users.UserField;

import org.luwrain.core.*;

final class Base
{
    private final Luwrain luwrain;
    private final Strings strings;
    private final TransportClient transportClient;
    final VkApiClient vk;
    final UserActor actor;
    final Settings sett;

    private FutureTask task = null;
    WallPostFull[] wallPosts = new WallPostFull[0];
    Dialog[] dialogs = new Dialog[0];
    Message[] messages = new Message[0];
    UserFull[] users = new UserFull[0];

    Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	this.sett = Settings.create(luwrain);
	this.transportClient = new HttpTransportClient();
	this.vk = new VkApiClient(transportClient);
	this.actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
    }

    boolean runTask(FutureTask task)
    {
	NullCheck.notNull(task, "task");
	if (isBusy())
	    return false;
	this.task = task;
	luwrain.executeBkg(this.task);
	return true;
    }

    void resetTask()
    {
	this.task = null;
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    void closeApp()
    {
	luwrain.closeApp();
    }

    void main(String[] args)
    {
	// TODO Auto-generated method stub
	String code="";
		UserAuthResponse authResponse=null;
		try {

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
