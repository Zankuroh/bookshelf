
package com.bookshelf.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Register {

    @SerializedName("success")
    @Expose
    private String success;

    /**
     *
     * @return
     * The success
     */
    public String getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    public Register withSuccess(String success) {
        this.success = success;
        return this;
    }

}