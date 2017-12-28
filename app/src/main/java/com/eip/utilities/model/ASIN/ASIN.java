package com.eip.utilities.model.ASIN;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Maxime on 28/12/2017.
 */

public class ASIN
{
    @SerializedName("data")
    @Expose
    private List<Data> data;
    @SerializedName("errors")
    @Expose
    private Object errors;
    @SerializedName("meta")
    @Expose
    private Object meta;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
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
