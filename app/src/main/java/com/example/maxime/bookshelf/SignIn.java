package com.example.maxime.bookshelf;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Maxime on 02/12/2016.
 */

public class SignIn extends Activity {
    private static RelativeLayout _lp;
    private static Boolean status = false;

    public SignIn(String email, String pwd, RelativeLayout p)
    {
        _lp = p;
        RequestParams params = new RequestParams();
        params.put("password", pwd);
        params.put("email", email);
        RequestAsync.post("auth", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                try {
                    String resp = response.getString("token");
                    if (resp != null) {
                        Snackbar snackbar = Snackbar.make(_lp, "Connexion réussie !", Snackbar.LENGTH_LONG);
                        status = true;
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(_lp, "Échec de connexion :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
            {
                try {
                    String resp = response.getString("error");
                    if (resp != null) {
                        Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + resp, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(_lp, "Échec de connexion :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();
                }
            }
        });
    }

    public Boolean getStatus()
    {
        return status;
    }
}
