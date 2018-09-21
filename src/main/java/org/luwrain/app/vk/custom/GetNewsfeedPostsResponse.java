package org.luwrain.app.vk.custom;

import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.newsfeed.ItemWallpost;
import com.vk.api.sdk.objects.users.UserFull;

import java.util.List;
import java.util.Objects;

/**
 * GetResponse object
 */
public class GetNewsfeedPostsResponse {
    @SerializedName("items")
    private List<ItemWallpost> items;

    @SerializedName("profiles")
    private List<UserFull> profiles;

    @SerializedName("groups")
    private List<GroupFull> groups;

    public List<ItemWallpost> getItems() {
        return items;
    }

    public List<UserFull> getProfiles() {
        return profiles;
    }

    public List<GroupFull> getGroups() {
        return groups;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profiles, groups, items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
	    return false;
        GetNewsfeedPostsResponse getResponse = (GetNewsfeedPostsResponse) o;
        return Objects.equals(items, getResponse.items) &&
                Objects.equals(profiles, getResponse.profiles) &&
                Objects.equals(groups, getResponse.groups);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetResponse{");
        sb.append("items=").append(items);
        sb.append(", profiles=").append(profiles);
        sb.append(", groups=").append(groups);
        sb.append('}');
        return sb.toString();
    }
}
