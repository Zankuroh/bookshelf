package com.eip.utilities.api;


import com.eip.utilities.model.ModifReview.ModifReview;
import com.eip.utilities.model.AuthLocal.AuthLocal;
import com.eip.utilities.model.Authors.Authors;
import com.eip.utilities.model.BooksLocal.BooksLocal;
import com.eip.utilities.model.DelProfile.DelProfile;
import com.eip.utilities.model.ModifAuthor.ModifAuthor;
import com.eip.utilities.model.ModifBook.ModifBook;
import com.eip.utilities.model.Profile.Profile;
import com.eip.utilities.model.ProfileModification.ProfileModification;
import com.eip.utilities.model.Register.Register;
import com.eip.utilities.model.Reviews.Reviews;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jolyn on 08/12/2016.
 */

public interface BookshelfApi
{
    public static final String APIPath = "http://79.137.87.198/api/";

    @FormUrlEncoded
    @POST("auth")
    Call<AuthLocal> Connexion(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("oauth")
    Call<AuthLocal> Oauth(@Field("token") String token, @Field("provider") String provider);


    @FormUrlEncoded
    @POST("register")
    Call<Register> Register(@Field("name") String name, @Field("password") String password, @Field("email") String email);

    @FormUrlEncoded
    @POST("book")
    Call<ModifBook> AddBook(@Header("Authorization") String token, @Field("isbn") String isbn);

    @GET("book")
    Call<BooksLocal> getBookshelf(@Header("Authorization") String token);

    @DELETE("book/")
    Call<ModifBook> DelBook(@Header("Authorization") String token, @Query("isbn") String isbn, @Query("deleted") String deleted);

    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("profile/password")
    Call<ProfileModification> ChangePwd(@Header("Authorization") String token, @Field("password") String password, @Field("new_password") String new_password);

    @FormUrlEncoded
    @POST("profile/email")
    Call<ProfileModification> ChangeEmail(@Header("Authorization") String token, @Field("password") String password, @Field("email") String email);

    @FormUrlEncoded
    @POST("profile/name")
    Call<ProfileModification> ChangeName(@Header("Authorization") String token, @Field("password") String password, @Field("name") String name);

    @FormUrlEncoded
    @POST("author")
    Call<ModifAuthor> AddAuthor(@Header("Authorization") String token, @Field("first_name") String first_name, @Field("last_name") String last_name);

    @DELETE("profile/")
    Call<DelProfile> DelProfil(@Header("Authorization") String token, @Query("password") String password, @Query("delete") String deleted);

    @GET("author")
    Call<Authors> getAuthors(@Header("Authorization") String token);

    @DELETE("author/")
    Call<ModifAuthor> DelAuthor(@Header("Authorization") String token, @Query("id") String id);

    @FormUrlEncoded
    @POST("wish/book")
    Call<ModifBook> AddWishBook(@Header("Authorization") String token, @Field("isbn") String isbn);

    @GET("wish/book")
    Call<BooksLocal> getWishBookshelf(@Header("Authorization") String token);

    @DELETE("wish/book/")
    Call<ModifBook> DelWishBook(@Header("Authorization") String token, @Query("isbn") String isbn, @Query("deleted") String deleted);

    @GET("review")
    Call<Reviews> getReview(@Header("Authorization") String token, @Query("isbn") String isbn);

    @FormUrlEncoded
    @POST("review")
    Call<ModifReview> AddReview(@Header("Authorization") String token, @Field("isbn") String isbn, @Field("content") String content, @Field("rate") String rate);

    @PUT("review/{reviewId}")
    Call<ModifReview> ChangeReview(@Header("Authorization") String token, @Path("reviewId") int reviewId, @Query("content") String content, @Query("rate") String rate);

    @DELETE("review/{reviewId}")
    Call<ModifReview> DelReview(@Header("Authorization") String token, @Path("reviewId") int reviewId, @Query("validation") String validation);
}
