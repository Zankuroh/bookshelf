
package com.eip.utilities.model.ChangeStatus;

import com.eip.utilities.model.BooksLocal.Book;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChangeStatus {

    @SerializedName("data")
    @Expose
    private Book data = null;
    @SerializedName("errors")
    @Expose
    private Object errors;
    @SerializedName("meta")
    @Expose
    private Object meta;

    public Book getData() {
        return data;
    }

    public void setData(Book data) {
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
