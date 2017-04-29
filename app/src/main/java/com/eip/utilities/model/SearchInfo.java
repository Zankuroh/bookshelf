
package com.eip.utilities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchInfo
{
    @SerializedName("textSnippet")
    @Expose
    private String textSnippet;

    /**
     * 
     * @return
     *     The textSnippet
     */
    public String getTextSnippet() {
        return textSnippet;
    }

    /**
     * 
     * @param textSnippet
     *     The textSnippet
     */
    public void setTextSnippet(String textSnippet) {
        this.textSnippet = textSnippet;
    }
}
