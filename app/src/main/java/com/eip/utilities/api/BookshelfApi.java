package com.eip.utilities.api;

<<<<<<< HEAD:app/src/main/java/com/bookshelf/api/BookshelfApi.java
import com.bookshelf.model.Auth;
import com.bookshelf.model.LocalBook;
import com.bookshelf.model.SimpleResponse;
=======
import com.eip.utilities.model.Auth;
import com.eip.utilities.model.Register;
>>>>>>> dev:app/src/main/java/com/eip/utilities/api/BookshelfApi.java

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
    Call<SimpleResponse> Register(@Query("name") String name, @Query("password") String password, @Query("email") String email);

    @GET("books")
    Call<LocalBook> getBookshelf(@Query("token") String token);

    @POST("books")
    Call<SimpleResponse> AddBooks(@Query("token") String token, @Query("Isbn") String isbn);

    @DELETE("books")
    Call<SimpleResponse> SimpleResponse(@Query("token") String token, @Query("Isbn") String isbn);

}
