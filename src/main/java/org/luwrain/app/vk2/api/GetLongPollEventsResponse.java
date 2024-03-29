
package org.luwrain.app.vk2.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class GetLongPollEventsResponse {

    @SerializedName("ts")
    private Integer ts;

    @SerializedName("updates")
    private List<JsonArray> updates;

    public Integer getTs() {
        return ts;
    }

    public List<JsonArray> getUpdates() {
        return updates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetLongPollEventsResponse that = (GetLongPollEventsResponse) o;
        return Objects.equals(ts, that.ts) &&
                Objects.equals(updates, that.updates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ts, updates);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetLongPollEventsResponse{");
        sb.append("ts=").append(ts);
        sb.append(", updates=").append(updates);
        sb.append('}');
        return sb.toString();
    }
}

