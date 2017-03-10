package com.eip.bookshelf;


import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Register;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by jolyn on 23/11/2016.
 */

public class SignUp extends Activity {

    private RelativeLayout _lp;
    public Boolean _status = false;

    public SignUp(String name, String email, String password, RelativeLayout p) {
        _lp = p;
        _status = false;

        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Register> call = bookshelfApi.Register(name, password, email);
        call.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                if (response.isSuccessful()) {
                    Register auth = response.body();
                    String success = auth.getSuccess();
                    Snackbar snackbar = Snackbar.make(_lp, "Création réussie !", Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<Register> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public Boolean getStatus() {
        return this._status;
    }

    public static Boolean verifyEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Boolean checkPassword(String pwd1,  String pwd2) {
        if (pwd1.equals(pwd2))
            return true;
        return false;
    }

}