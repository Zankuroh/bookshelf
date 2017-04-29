
package com.eip.utilities.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pdf
{
    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;

    /**
     * 
     * @return
     *     The isAvailable
     */
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    /**
     * 
     * @param isAvailable
     *     The isAvailable
     */
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
