package com.eip.utilities.model.ASIN;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Maxime on 28/12/2017.
 */

public class Data
{
    @SerializedName("book_title")
    @Expose
    private String title = null;
    @SerializedName("book_picture_url")
    @Expose
    private String picUrl = null;
    @SerializedName("book_amazon_url")
    @Expose
    private String amazonUrl = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAmazonUrl() {
        return amazonUrl;
    }

    public void setAmazonUrl(String amazonUrl) {
        this.amazonUrl = amazonUrl;
    }
}
