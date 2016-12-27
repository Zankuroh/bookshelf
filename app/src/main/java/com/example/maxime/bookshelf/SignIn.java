package com.example.maxime.bookshelf;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.bookshelf.api.BookshelfApi;
import com.bookshelf.model.Auth;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 02/12/2016.
 */

public class SignIn extends Activity
{
    private RelativeLayout _lp;
    private Boolean _status;

    public SignIn(String email, String pwd, RelativeLayout p)
    {
        this._status = false;
        this._lp = p;
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Auth> call = bookshelfApi.Connexion(email,pwd);
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {
                    Auth auth = response.body();
                    String token = auth.getToken();
                    Snackbar snackbar = Snackbar.make(_lp, "Connexion réussie !", Snackbar.LENGTH_LONG);
                    _status = true;
                    snackbar.show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + jObjError.getString("error"), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public Boolean getStatus()
    {
        return _status;
    }
}
