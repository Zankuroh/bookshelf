
package com.eip.utilities.model.ModifAuthor;

import com.eip.utilities.model.Authors.Author;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModifAuthor {

    @SerializedName("data")
    @Expose
    private Author data;
    @SerializedName("errors")
    @Expose
    private Object errors;
    @SerializedName("meta")
    @Expose
    private Object meta;

    public Author getData() {
        return data;
    }

    public void setData(Author data) {
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
