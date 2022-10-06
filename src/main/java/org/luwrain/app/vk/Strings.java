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

interface Strings
{
    static final String
	NAME = "luwrain.vk";

    String actionAttachPhoto();
    String actionConversations();
    String actionDelete();
    String actionFollowings();
    String actionFriends();
    String actionMessage();
    String actionNewsfeed();
    String actionRequestFriendship();
    String actionUsers();
    String actionWallDelete();
        String actionWallPost();
    String appName();
    String attachPhotoPopupName();
    String attachPhotoPopupPrefix();
    String conversationsAreaName();
    String followingsAreaName();
    String friendsAreaName();
    String friendshipRequestsAreaName();
    String friendshipRequestSent();
    String lastSeen(String value);
    String messagesAreaName();
    String messageTextPopupName();
    String messageTextPopupPrefix();
    String messageSent();
        String sendingPost();
    String search();
    String suggestionsAreaName();
    String usersAreaName();
    String wallAreaName();
    String wallPostAreaName();
}
