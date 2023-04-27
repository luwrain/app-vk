
package org.luwrain.app.vk2.api;

//https://vk.com/dev/audio.save

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

import java.util.Arrays;
import java.util.List;

public class AudioGetUploadServerQuery extends AbstractQueryBuilder<AudioGetUploadServerQuery, AudioGetUploadServerResponse>
{
    public AudioGetUploadServerQuery(VkApiClient client, UserActor actor)
    {
        super(client, "audio.getUploadServer", AudioGetUploadServerResponse.class);
        accessToken(actor.getAccessToken());
    }

    public AudioGetUploadServerQuery ownerId(Integer value)
    {
        return unsafeParam("owner_id", value);
    }

    @Override
    protected AudioGetUploadServerQuery getThis() {
        return this;
    }

    @Override
    protected List<String> essentialKeys() {
        return Arrays.asList("access_token");
    }
}
