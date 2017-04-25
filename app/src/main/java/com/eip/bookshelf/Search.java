package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.Item;
import com.google.zxing.integration.android.IntentIntegrator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jolyn on 08/12/2016.
 */

public class Search extends Fragment implements View.OnClickListener
{
    private View _v;

    public Search()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.start_search, container, false);

        _v.findViewById(R.id.okSearch).setOnClickListener(this);
        _v.findViewById(R.id.searchApn).setOnClickListener(this);
        return _v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.okSearch:
                searchByISBN();
                break;
            case R.id.searchApn:
                searchByCB();
                break;
            default:
                break;
        }
    }

    public void searchByCB()
    {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());

        integrator.setCaptureActivity(DecodeBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scanner le code ISBN au dos du livre");
        integrator.setBeepEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    public void searchByISBN()
    {
        EditText isbn = (EditText)_v.findViewById(R.id.searchField);

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
                        snackbar = Snackbar.make(_v, titre, Snackbar.LENGTH_LONG);
                    } else {
                        snackbar = Snackbar.make(_v, "Aucun livre trouvé :(", Snackbar.LENGTH_LONG);
                    }
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Erreur !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
