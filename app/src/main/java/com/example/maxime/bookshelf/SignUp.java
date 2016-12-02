package com.example.maxime.bookshelf;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jolyn on 23/11/2016.
 */

public class SignUp extends Activity{

    private static RelativeLayout _lp;
    public static String status;

    public SignUp(String name, String email, String password, RelativeLayout p) {
        _lp = p;
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("password", email);
        params.put("email", password);
        RequestAsync.post("register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    status = response.getString("success");
                    if (status != null) {
                        Snackbar snackbar = Snackbar.make(_lp, "Vous êtes connecté :)", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else
                    {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }


                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(_lp, "Echec de connexion :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int code, Header[] headers, Throwable m, JSONObject response) {
                Snackbar snackbar = Snackbar.make(_lp, "Echec de connexion :(", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public String getStatus()
    {
        return this.status;
    }

    public Boolean verifyEmail() {
        //TODO vérifier email
        return null;
    }

    public Boolean addUser() {
        //TODO ajouter à la base de donnée les inforamtions de l'utilisateur.
        return false;
    }

}
