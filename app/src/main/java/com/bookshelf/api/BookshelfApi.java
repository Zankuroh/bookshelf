package com.bookshelf.api;

import com.bookshelf.model.Auth;
import com.bookshelf.model.Register;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jolyn on 08/12/2016.
 */

public interface BookshelfApi {
    public static final String APIPath = "http://bookshelf.caolin.ovh:8000/api/";

    @POST("auth")
    Call<Auth> Connexion(@Query("email") String email, @Query("password") String password);

    @POST("register")
    Call<Register> Register(@Query("name") String name, @Query("password") String password, @Query("email") String email);
}
