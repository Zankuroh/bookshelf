package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Handler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Auth;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;
import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 02/12/2016.
 */

public class SignIn extends Fragment implements View.OnClickListener
{
    private View _v;
    private CallbackManager _callbackManager;

    public SignIn()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.sign_in, container, false);
        _v.findViewById(R.id.btnCo).setOnClickListener(this);
        _v.findViewById(R.id.btnForgetPass).setOnClickListener(this);
        _v.findViewById(R.id.btnCreateAccount).setOnClickListener(this);


        _callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) _v.findViewById(R.id.fConnect);
        loginButton.setReadPermissions("public_profile email");
        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FACEBOOK", "YEAHHHH");
                //TODO gérer la création de compte
                connect("","");
                //_v.findViewById(R.id.nav_biblio).performClick();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
        // Other app specific specialization

        return _v;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnCo:
                clickSignIn(v);
                break;
            case R.id.btnForgetPass:
                Snackbar snackbar = Snackbar.make(_v, "Non implémenté.", Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case R.id.btnCreateAccount:
                startActivity(new Intent(getActivity(), SignUp.class));
                break;
            default:
                break;
        }
    }

    public void clickSignIn(View v)
    {
        MainActivity.hideSoftKeyboard(getActivity());
        EditText login = (EditText) _v.findViewById(R.id.ETsignInMail);
        EditText passwd = (EditText) _v.findViewById(R.id.ETsignInMdp);
        connect(login.getText().toString(), passwd.getText().toString());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run()
            {
                if (MainActivity.co) {
                    MainActivity.MenuItemCo.setTitle("Déconnexion");
                    //Todo: launch fragment shelf
                }
            }
        }, 3000);
    }

    public void connect(String email, String pwd)
    {
//        BookshelfApi bookshelfApi = new Retrofit.Builder()
//                .baseUrl(BookshelfApi.APIPath)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(BookshelfApi.class);
//        Call<Auth> call = bookshelfApi.Connexion(email,pwd);
//        call.enqueue(new Callback<Auth>() {
//            @Override
//            public void onResponse(Call<Auth> call, Response<Auth> response) {
//                if (response.isSuccessful()) {
//                    Auth auth = response.body();
//                    String token = auth.getToken();
//                    Snackbar snackbar = Snackbar.make(_v, "Connexion réussie !", Snackbar.LENGTH_LONG);
//                    MainActivity.co = true;
//                    snackbar.show();
//                } else {
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Snackbar snackbar = Snackbar.make(_v, "Erreur : " + jObjError.getString("error"), Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    } catch (Exception e) {
//                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Auth> call, Throwable t) {
//                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
//                snackbar.show();
//                t.printStackTrace();
//            }
//        });
        MainActivity.co = true;
    }
}
