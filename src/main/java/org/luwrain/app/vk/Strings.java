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

interface Strings
{
    static final String NAME = "luwrain.vk";

    String actionConversations();
    String actionFriends();
    String actionNewsfeed();
    String actionRequestFriendship();
    String actionUsers();
    String actionWallDelete();
        String actionWallPost();
    String appName();
    String conversationsAreaName();
    String friendsAreaName();
    String friendshipRequestsAreaName();
    String friendshipRequestSent();
    String lastSeen(String value);
    String messagesAreaName();
        String sendingPost();
    String search();
    String usersAreaName();
    String wallAreaName();
    String wallPostAreaName();
}
