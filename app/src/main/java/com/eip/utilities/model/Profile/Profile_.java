
package com.eip.utilities.model.Profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile_ {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
