package com.eip.utilities.api;


import com.eip.utilities.model.Auth.Auth;
import com.eip.utilities.model.Authors.Authors;
import com.eip.utilities.model.BooksLocal.BooksLocal;
import com.eip.utilities.model.DelProfile.DelProfile;
import com.eip.utilities.model.ModifAuthor.ModifAuthor;
import com.eip.utilities.model.ModifBook.ModifBook;
import com.eip.utilities.model.Profile.Profile;
import com.eip.utilities.model.ProfileModification.ProfileModification;
import com.eip.utilities.model.Register.Register;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jolyn on 08/12/2016.
 */

public interface BookshelfApi
{
    public static final String APIPath = "http://79.137.87.198/api/";

    @POST("auth")
    Call<Auth> Connexion(@Query("email") String email, @Query("password") String password);

    @POST("register")
    Call<Register> Register(@Query("name") String name, @Query("password") String password, @Query("email") String email);

    @POST("book")
    Call<ModifBook> AddBook(@Header("Authorization") String token, @Query("Isbn") String isbn);

    @GET("book")
    Call<BooksLocal> getBookshelf(@Header("Authorization") String token);

    @FormUrlEncoded
    @DELETE("book")
    Call<ModifBook> DelBook(@Header("Authorization") String token, @Field("Isbn") String isbn, @Field("deleted") String deleted);

    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @POST("profile/password")
    Call<ProfileModification> ChangePwd(@Header("Authorization") String token, @Query("password") String password, @Query("new_password") String new_password);

    @POST("profile/email")
    Call<ProfileModification> ChangeEmail(@Header("Authorization") String token, @Query("password") String password, @Query("email") String email);

    @POST("profile/name")
    Call<ProfileModification> ChangeName(@Header("Authorization") String token, @Query("password") String password, @Query("name") String name);

    @POST("author")
    Call<ModifAuthor> AddAuthor(@Header("Authorization") String token, @Query("first_name") String first_name, @Query("last_name") String last_name);

    @FormUrlEncoded
    @DELETE("profile")
    Call<DelProfile> DelProfil(@Header("Authorization") String token, @Field("password") String password, @Field("deleted") String deleted);

    @GET("author")
    Call<Authors> getAuthors(@Header("Authorization") String token);

    @FormUrlEncoded
    @DELETE("author")
    Call<ModifAuthor> DelAuthor(@Header("Authorization") String token, @Field("id") String id);

    @POST("wish/book")
    Call<ModifBook> AddWishBook(@Header("Authorization") String token, @Query("Isbn") String isbn);

    @GET("wish/book")
    Call<BooksLocal> getWishBookshelf(@Header("Authorization") String token);

    @FormUrlEncoded
    @DELETE("wish/book")
    Call<ModifBook> DelWishBook(@Header("Authorization") String token, @Field("Isbn") String isbn, @Field("deleted") String deleted);

}
