
package org.luwrain.app.vk2.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.Validable;
import java.net.URI;
import java.util.Objects;

public class AudioGetUploadServerResponse implements Validable {
    @SerializedName("album_id")
    private Integer albumId;

    @SerializedName("upload_url")
    private URI uploadUrl;

    @SerializedName("fallback_upload_url")
    private URI fallbackUploadUrl;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("group_id")
    private Integer groupId;

    public Integer getAlbumId() {
        return albumId;
    }

    public AudioGetUploadServerResponse setAlbumId(Integer albumId) {
        this.albumId = albumId;
        return this;
    }

    public URI getUploadUrl() {
        return uploadUrl;
    }

    public AudioGetUploadServerResponse setUploadUrl(URI uploadUrl) {
        this.uploadUrl = uploadUrl;
        return this;
    }

    public URI getFallbackUploadUrl() {
        return fallbackUploadUrl;
    }

    public AudioGetUploadServerResponse setFallbackUploadUrl(URI fallbackUploadUrl) {
        this.fallbackUploadUrl = fallbackUploadUrl;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public AudioGetUploadServerResponse setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public AudioGetUploadServerResponse setGroupId(Integer groupId) {
        this.groupId = groupId;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fallbackUploadUrl, uploadUrl, groupId, albumId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioGetUploadServerResponse getUploadServerResponse = (AudioGetUploadServerResponse) o;
        return Objects.equals(userId, getUploadServerResponse.userId) &&
                Objects.equals(groupId, getUploadServerResponse.groupId) &&
                Objects.equals(uploadUrl, getUploadServerResponse.uploadUrl) &&
                Objects.equals(fallbackUploadUrl, getUploadServerResponse.fallbackUploadUrl) &&
                Objects.equals(albumId, getUploadServerResponse.albumId);
    }

    @Override
    public String toString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toPrettyString() {
        final StringBuilder sb = new StringBuilder("GetUploadServerResponse{");
        sb.append("userId=").append(userId);
        sb.append(", groupId=").append(groupId);
        sb.append(", uploadUrl=").append(uploadUrl);
        sb.append(", fallbackUploadUrl=").append(fallbackUploadUrl);
        sb.append(", albumId=").append(albumId);
        sb.append('}');
        return sb.toString();
    }
}
