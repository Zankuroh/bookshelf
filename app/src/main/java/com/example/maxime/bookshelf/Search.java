package com.example.maxime.bookshelf;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.bookshelf.api.GoogleBooksApi;
import com.bookshelf.model.Books;
import com.bookshelf.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jolyn on 08/12/2016.
 */

public class Search {

    private RelativeLayout _lp;

    public void searchByISNB(String isbn, RelativeLayout p)
    {
        _lp = p;
        GoogleBooksApi googleBooksApi = new Retrofit.Builder()
                .baseUrl(GoogleBooksApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksApi.class);

        Call<Books> call = googleBooksApi.searchByIsbn("isbn:"+isbn);
        call.enqueue(new Callback<Books>() {
            @Override
            public void onResponse(Call<Books> call, Response<Books> response) {
                if (response.isSuccessful()) {
                    Books book = response.body();
                    Log.d("RESEARCH", book.getTotalItems().toString());
                    Item item = book.getItems().get(0);
                    String titre = item.getVolumeInfo().getTitle();
                    Snackbar snackbar = Snackbar.make(_lp, titre, Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_lp, "Erreur !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
