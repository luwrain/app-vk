package org.luwrain.app.vk.custom;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.EnumParam;
//import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Query for Newsfeed.get method
 */
public class NewsfeedGetPostsQuery extends AbstractQueryBuilder<NewsfeedGetPostsQuery, GetNewsfeedPostsResponse>
{
    /**
     * Creates a AbstractQueryBuilder instance that can be used to build api request with various parameters
     *
     * @param client VK API client
     * @param actor  actor with access token
     */
    public NewsfeedGetPostsQuery(VkApiClient client, UserActor actor) {
        super(client, "newsfeed.get", GetNewsfeedPostsResponse.class);
        accessToken(actor.getAccessToken());
    }

    /**
     * Filters to apply
     *
     * @param value value of "filters" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    /*
    public NewsfeedGetPostsQuery filters(NewsfeedGetFilter... value) {
        return unsafeParam("filters", value);
    }
    */

    /**
     * Filters to apply
     *
     * @param value value of "filters" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    /*
    public NewsfeedGetPostsQuery filters(List<NewsfeedGetFilter> value) {
        return unsafeParam("filters", value);
    }
    */

    /**
     * Return news items from banned sources
     *
     * @param value value of "return banned" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery returnBanned(Boolean value) {
        return unsafeParam("return_banned", value);
    }

    /**
     * Earliest timestamp (in Unix time) of a news item to return. By default, 24 hours ago.
     *
     * @param value value of "start time" parameter. Minimum is 0.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery startTime(Integer value) {
        return unsafeParam("start_time", value);
    }

    /**
     * Latest timestamp (in Unix time) of a news item to return. By default, the current time.
     *
     * @param value value of "end time" parameter. Minimum is 0.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery endTime(Integer value) {
        return unsafeParam("end_time", value);
    }

    /**
     * Maximum number of photos to return. By default, "5".
     *
     * @param value value of "max photos" parameter. Minimum is 0.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery maxPhotos(Integer value) {
        return unsafeParam("max_photos", value);
    }

    /**
     * Sources to obtain news from, separated by commas.
     * <p>
     * User IDs can be specified in formats "user_id" or "u"user_id""
     * where ""user_id"" is the user's friend ID.
     * <p>
     * Community IDs can be specified in formats "-"group_id"" or "g"group_id""
     * where "group_id" is the community ID.
     * <p>
     * If the parameter is not set, all of the user's friends and communities are returned, except for banned sources, which can be obtained with the newsfeed.getBanned method.
     *
     * @param value value of "source ids" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery sourceIds(String... value) {
        return unsafeParam("source_ids", value);
    }


    /**
     * Sources to obtain news from, separated by commas.
     * <p>
     * User IDs can be specified in formats "user_id" or "u"user_id""
     * where ""user_id"" is the user's friend ID.
     * <p>
     * Community IDs can be specified in formats "-"group_id"" or "g"group_id""
     * where "group_id" is the community ID.
     * <p>
     * If the parameter is not set, all of the user's friends and communities are returned, except for banned sources, which can be obtained with the newsfeed.getBanned method.
     *
     * @param value value of "source ids" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery sourceIds(List<String> value) {
        return unsafeParam("source_ids", value);
    }

    /**
     * Identifier required to get the next page of results.
     * Value for this parameter is returned in "next_from" field in a reply.
     *
     * @param value value of "start from" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery startFrom(String value) {
        return unsafeParam("start_from", value);
    }

    /**
     * Number of news items to return (default 50; maximum 100). For auto feed, you can use the "new_offset" parameter returned by this method.
     *
     * @param value value of "count" parameter. Minimum is 0.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery count(Integer value) {
        return unsafeParam("count", value);
    }

    /**
     * Set fields
     *
     * @param value value of "fields" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery fields(EnumParam... value) {
        return unsafeParam("fields", value);
    }

    /**
     * Set fields
     *
     * @param value value of "fields" parameter.
     * @return a reference to this {@code AbstractQueryBuilder} object to fulfill the "Builder" pattern.
     */
    public NewsfeedGetPostsQuery fields(List<EnumParam> value) {
        return unsafeParam("fields", value);
    }

    @Override
    protected NewsfeedGetPostsQuery getThis() {
        return this;
    }

    @Override
    protected List<String> essentialKeys() {
        return Arrays.asList("access_token");
    }
}
