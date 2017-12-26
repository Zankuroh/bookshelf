
package com.eip.utilities.model.Friend.Modif;

import com.eip.utilities.model.Friend.Friend;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendModif {

    @SerializedName("data")
    @Expose
    private Friend data = null;
    @SerializedName("errors")
    @Expose
    private Object errors;
    @SerializedName("meta")
    @Expose
    private Object meta;
    @SerializedName("title")
    @Expose
    private String title;

    public Friend getData() {
        return data;
    }

    public void setData(Friend data) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
