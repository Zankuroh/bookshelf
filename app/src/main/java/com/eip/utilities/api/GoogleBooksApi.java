package com.eip.utilities.api;

import com.eip.utilities.model.Books;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jolyn on 08/12/2016.
 */

public interface GoogleBooksApi
{
    String APIPath = "https://www.googleapis.com/books/v1/";

    @GET("volumes")
    Call<Books> searchByIsbn(@Query("q") String isbn,  @Query("startIndex") String index, @Query("maxResults") String maxRes);
}
