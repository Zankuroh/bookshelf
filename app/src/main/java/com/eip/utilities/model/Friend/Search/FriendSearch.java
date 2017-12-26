
package com.eip.utilities.model.Friend.Search;

import java.util.List;

import com.eip.utilities.model.Friend.Friend;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendSearch {

    @SerializedName("data")
    @Expose
    private List<Friend> data = null;
    @SerializedName("errors")
    @Expose
    private Object errors;
    @SerializedName("meta")
    @Expose
    private Object meta;

    public List<Friend> getData() {
        return data;
    }

    public void setData(List<Friend> data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

}
