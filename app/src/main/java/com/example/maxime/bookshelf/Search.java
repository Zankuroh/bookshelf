package com.example.maxime.bookshelf;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bookshelf.api.GoogleBooksApi;
import com.bookshelf.model.Books;
import com.bookshelf.model.Item;
import com.google.zxing.integration.android.IntentIntegrator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jolyn on 08/12/2016.
 */

public class Search
{
    private View _rl;
    private MainActivity _act;

    public Search(View rl, MainActivity a)
    {
        _rl = rl;
        _act = a;
        Button manualSearch = (Button)_rl.findViewById(R.id.okSearch);
        manualSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searcheField = (EditText)_rl.findViewById(R.id.searchField);
                searchByISNB(searcheField.getText().toString());
            }
        });

        Button apnSearch = (Button)_rl.findViewById(R.id.searchApn);
        apnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                IntentIntegrator integrator = new IntentIntegrator(_act);

                integrator.setCaptureActivity(DecodeBarcode.class);
                integrator.setOrientationLocked(false);
                integrator.setPrompt("Scanner le code ISBN au dos du livre");
                integrator.setBeepEnabled(true);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.initiateScan();
            }
        });
    }

    public void searchByISNB(String isbn)
    {
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
                    Snackbar snackbar = Snackbar.make(_rl, titre, Snackbar.LENGTH_LONG);
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
