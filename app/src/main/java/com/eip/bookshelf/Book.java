package com.eip.bookshelf;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joly_i on 22/02/16.
 */

class Book
{
    private String  titre;
    private String  image;
    private String  auteur;
    private String  date;
    private String  genre;
    private String  resum;
    private Double  note;
    private Long    isbn;

    String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getResum() {
        return resum;
    }

    public void setResum(String resum) {
        this.resum = resum;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public static void getInfoBook(String isbn, final RelativeLayout _rl) {
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
                    Snackbar snackbar;
                    if (book.getTotalItems() > 0) {
                        Item item = book.getItems().get(0);
                        String titre = item.getVolumeInfo().getTitle();
                        snackbar = Snackbar.make(_rl, titre, Snackbar.LENGTH_LONG);
                    } else {
                        snackbar = Snackbar.make(_rl, "Aucun livre trouvé :(", Snackbar.LENGTH_LONG);
                    }
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Erreur !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

}
