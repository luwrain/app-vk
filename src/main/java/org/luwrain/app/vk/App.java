/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.io.*;

import com.vk.api.sdk.objects.users.UserFull;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application, MonoApp
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private Actions actions = null;
    private ActionLists actionLists = null;

    private WallArea defaultArea = null;
    private AreaLayoutHelper layout = null;

    //    private final Watching watching;

    public App(/*Watching watching*/)
    {
	//	this.watching = watching;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings/*, watching*/);
	this.actions = new Actions(base, this);
	this.actionLists = new ActionLists(luwrain, strings, base);
	createDefaultArea();
	base.setVisibleAreas(new Area[]{defaultArea});
	this.layout = new AreaLayoutHelper(()->{
		base.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
	    }, new AreaLayout(defaultArea));
	return new InitResult();
    }

        public void closeApp()
    {
	base.closeApp();
    }

    @Override public void onAppClose()
    {
    }

    @Override public AreaLayout getAreaLayout()
    {
	return layout.getLayout();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    private void createDefaultArea()
    {
	this.defaultArea = new WallArea(luwrain, strings, base, actions){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    {
				final Area[] areas = layout.getLayout().getAreas();
				if (areas.length > 1)
				{
				    luwrain.setActiveArea(areas[1]);
				    return true;
				}
				return false;
			    }
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "conversations"))
			    return onShowConversations();
			if (ActionEvent.isAction(event, "friends"))
			    return onShowFriends();
			if (ActionEvent.isAction(event, "post"))
			    return onNewWallPost(this);
						if (ActionEvent.isAction(event, "news"))
						    return actions.onNewsfeedUpdate(()->{});
												if (ActionEvent.isAction(event, "followings"))
												    return onShowFollowings();
												if (ActionEvent.isAction(event, "users"))
			    return onShowUsers();
			return super.onSystemEvent(event);
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    private boolean onNewWallPost(WallArea wallArea)
    {
	NullCheck.notNull(wallArea, "wallArea");
	layout.openTempArea(new WallPostArea(luwrain, strings, base, actions, actionLists, ()->{
		    layout.closeTempLayout();
		    luwrain.announceActiveArea();
		    wallArea.refresh();
	}));
	luwrain.announceActiveArea();
	return true;
    }

    private boolean onShowConversations()
    {
	final Runnable closing = ()->{
	    this.layout.setBasicLayout(new AreaLayout(defaultArea));
	    luwrain.announceActiveArea();
	};
	final ConversationsArea conversationsArea = new ConversationsArea(base, actions, closing);
	final MessagesArea messagesArea = new MessagesArea(luwrain, strings, base, actions, closing);
	layout.setBasicLayout(new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, defaultArea, conversationsArea, messagesArea));
	conversationsArea.setMessagesArea(messagesArea);
	messagesArea.setConversationsArea(conversationsArea);
	conversationsArea.setDefaultArea(defaultArea);
	messagesArea.setDefaultArea(defaultArea);
	//luwrain.setActiveArea(conversationsArea);
	return true;
    }

    private boolean onShowFriends()
    {
	final Runnable closing = ()->{
	    this.layout.setBasicLayout(new AreaLayout(defaultArea));
	    luwrain.announceActiveArea();
	};
	final FriendsArea friendsArea = new FriendsArea(luwrain, strings, base, actions, closing){
		@Override boolean onClick(UserFull user)
		{
		    NullCheck.notNull(user, "user");
		    return defaultArea.showUserInfo(user.getId());
		}
	    };
	final FriendshipRequestsArea friendshipRequestsArea = new FriendshipRequestsArea(luwrain, strings, base, actions, closing){
				@Override boolean onProperties(UserFull user)
		{
		    NullCheck.notNull(user, "user");
		    return defaultArea.showUserInfo(user.getId());
		}
	    };
	layout.setBasicLayout(new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, defaultArea, friendsArea, friendshipRequestsArea));
	friendsArea.setFriendshipRequestsArea(friendshipRequestsArea);
	friendshipRequestsArea.setFriendsArea(friendsArea);
	friendsArea.setDefaultArea(defaultArea);
	friendshipRequestsArea.setDefaultArea(defaultArea);
	return true;
    }

        private boolean onShowFollowings()
    {
	final Runnable closing = ()->{
	    this.layout.setBasicLayout(new AreaLayout(defaultArea));
	    luwrain.announceActiveArea();
	};
	final FollowingsArea followingsArea = new FollowingsArea(luwrain, strings, base, actions, closing){
		@Override boolean onClick(UserFull user)
		{
		    NullCheck.notNull(user, "user");
		    return defaultArea.showUserInfo(user.getId());
		}
	    };
	final SuggestionsArea suggestionsArea = new SuggestionsArea(luwrain, strings, base, actions, closing){
				@Override boolean onProperties(UserFull user)
		{
		    NullCheck.notNull(user, "user");
		    return defaultArea.showUserInfo(user.getId());
		}
	    };
	layout.setBasicLayout(new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, defaultArea, followingsArea, suggestionsArea));
	followingsArea.setSuggestionsArea(suggestionsArea);
	suggestionsArea.setFollowingsArea(followingsArea);
	followingsArea.setDefaultArea(defaultArea);
	suggestionsArea.setDefaultArea(defaultArea);
	return true;
    }


        private boolean onShowUsers()
    {
	final Runnable closing = ()->{
	    this.layout.setBasicLayout(new AreaLayout(defaultArea));
	    luwrain.announceActiveArea();
	};
	final UsersArea usersArea = new UsersArea(luwrain, strings, base, actions, closing){
		@Override boolean onClick(UserFull user)
		{
		    NullCheck.notNull(user, "user");
		    return defaultArea.showUserInfo(user.getId());
		}
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			    luwrain.setActiveArea(defaultArea);
			    return true;
			}
		    return super.onInputEvent(event);
		}
	    };
	layout.setBasicLayout(new AreaLayout(AreaLayout.LEFT_RIGHT, defaultArea, usersArea));
	luwrain.setActiveArea(usersArea);
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
