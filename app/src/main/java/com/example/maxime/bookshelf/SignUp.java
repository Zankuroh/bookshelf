package com.example.maxime.bookshelf;


import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jolyn on 23/11/2016.
 */

public class SignUp extends Activity {

    private RelativeLayout _lp;
    public Boolean _status = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public SignUp(String name, String email, String password, RelativeLayout p) {
        _lp = p;
        _status = false;

        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("password", password);
        params.put("email", email);

        RequestAsync.post("register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String ret  = response.getString("success");
                    if (ret.equals("User has been created")) {
                        Snackbar snackbar = Snackbar.make(_lp, ret, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        _status = true;
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

    public Boolean getStatus() {
        return this._status;
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