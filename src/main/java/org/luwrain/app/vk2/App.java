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

import java.util.*;
import java.io.*;

//import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.oneofs.NewsfeedNewsfeedItemOneOf;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.events.InputEvent.*;
import org.luwrain.app.base.*;

import org.luwrain.app.vk.Strings;
import org.luwrain.app.vk.Settings;



public final class App extends AppBase<Strings>
{
    static final String
	LOG_COMPONENT = "vk";

    static final InputEvent
	HOT_KEY_FRIENDS = new InputEvent(Special.F8),
	HOT_KEY_HOME_WALL = new InputEvent(Special.F9),
	HOT_KEY_PERSONAL_INFO = new InputEvent(Special.F10, EnumSet.of(Modifiers.ALT));

    final ArrayList<UserFull>
	friends = new ArrayList<>(),
	frRequests = new ArrayList<>();

    final List<WallpostFull> homeWallPosts = new ArrayList<>();
    final List<ConversationWithMessage> chats = new ArrayList<>();
    final List <NewsfeedNewsfeedItemOneOf> news = new ArrayList<>();

        final Map<Integer, UserFull> userCache = new HashMap<>();

    //        final TransportClient transportClient = new HttpTransportClient();
    final VkApiClient vk = new VkApiClient(new HttpTransportClient());
    private UserActor actor = null;

    private Settings sett = null;
    private Operations operations = null;
    private AuthLayout authLayout = null;
    private MainLayout mainLayout = null;
    private HomeWallLayout homeWallLayout = null;
    private FriendsLayout friendsLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class, "luwrain.vk");
    }

    @Override protected AreaLayout onAppInit()
    {
	this.sett = Settings.create(getLuwrain());
	this.actor = new UserActor(sett.getUserId(0), sett.getAccessToken(""));
	this.operations = new Operations(this);
	this.authLayout = new AuthLayout(this);
	this.mainLayout = new MainLayout(this);
	this.homeWallLayout = new HomeWallLayout(this);
	this.friendsLayout = new FriendsLayout(this);
	setAppName(getStrings().appName());
	final var taskId = newTaskId();
	runTask(taskId, ()->{
		final var n = operations.getNews();
final var c = operations.getChats();
		finishedTask(taskId, ()->{
			news.addAll(n);
			chats.addAll(c);
			mainLayout.chatsArea.refresh();
		    });
	    });
	return this.mainLayout.getAreaLayout();
    }

    void onAuth(int userId, String accessToken)
    {
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

        String getUserCommonName(int userId)
    {
	if (userId < 0 || !userCache.containsKey(Integer.valueOf(userId)))
	    return String.valueOf(userId);
	final UserFull user = userCache.get(new Integer(userId));
	return user.getFirstName() + " " + user.getLastName();
    }

            boolean newFriendship(UserFull user)
    {
	if (user == null)
	    return false;
	final var taskId = newTaskId();
	return runTask(taskId, ()->{
operations.newFriendship(user.getId());
		finishedTask(taskId, ()->{
			getLuwrain().playSound(Sounds.OK);
		    });
	    });
    }



    Layouts layouts()
    {
	return new Layouts(){

	    @Override public boolean homeWall()
	    {
		final var taskId = newTaskId();
		return runTask(taskId, ()->{
			final var posts = operations.getWallPosts();
			finishedTask(taskId, ()->{
				homeWallPosts.clear();
				homeWallPosts.addAll(posts);
				homeWallLayout.wallArea.refresh();
				setAreaLayout(homeWallLayout);
			    });
		    });
	    }

	    	    @Override public boolean friends()
	    {
		final var taskId = newTaskId();
		return runTask(taskId, ()->{
			final var  f = operations.getFriends(null);
			finishedTask(taskId, ()->{
				friends.clear();
				friends.addAll(f);
				friendsLayout.friendsArea.refresh();
				setAreaLayout(friendsLayout);
			    });
		    });
	    }

	    	    	    @Override public boolean personalInfo()
	    {
		final var taskId = newTaskId();
		return runTask(taskId, ()->{
			final var  i = operations.getPersonalInfo();
			finishedTask(taskId, ()->{
				final PersonalInfoLayout layout = new PersonalInfoLayout(App.this, i);
				setAreaLayout(layout);
			    });
		    });
	    }


	};
    }

    UserActor getActor() { return actor; }
    Operations getOperations() { return operations; }

    interface Layouts
    {
	boolean homeWall();
	boolean friends();
	boolean personalInfo();
    }
}
