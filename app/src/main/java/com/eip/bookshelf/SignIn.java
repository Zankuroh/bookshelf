package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.EditText;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthLocal.AuthLocal;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

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
    private GoogleApiClient mGoogleApiClient;

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
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        Log.i("FACEBOOK EMAIL",user.optString("email"));
                        Log.i("FACEBOOK NAME",user.optString("name"));
                        Log.i("FACEBOOK ID",user.optString("id"));
                    }
                }).executeAsync();

                //TODO gérer la création de compte
                //connect("","");

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

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        SignInButton mGoogleSignInButton = (SignInButton)_v.findViewById(R.id.gConnect);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        return _v;
    }

    private static final int RC_SIGN_IN = 9001;

    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestIdToken(getString(R.string.server_client_id))
                //.requestServerAuthCode(getString(R.string.server_client_id))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(_v.getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        this.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                Log.i("GOOGLE", "YEAHHHH");
                GoogleSignInAccount acct = result.getSignInAccount();
                //String idToken = acct.getIdToken();
                //String Authcode = acct.getServerAuthCode();

                /*String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                String token = acct.getServerAuthCode();

                Log.i("GOOGLE NAME", personName);
                Log.i("GOOGLE GIVENNAME", personGivenName);
                Log.i("GOOGLE FAMILY NAME", personFamilyName);
                Log.i("GOOGLE EMAIL", personEmail);
                Log.i("GOOGLE ID",personId);
                Log.i("GOOGLE TOKEN",token);*/

                //Log.i("GOOGLE IDTOKEN", idToken);
                //Log.i("GOOGLE AUTHCODE",Authcode);
                //connect("","");

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

            } else {

                //handleSignInResult(...);
            }
        } else {
            _callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnCo:
                clickSignIn();
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

    private void clickSignIn()
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

    private void connect(String email, String pwd)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<AuthLocal> call = bookshelfApi.Connexion(email,pwd);
        call.enqueue(new Callback<AuthLocal>() {
            @Override
            public void onResponse(Call<AuthLocal> call, Response<AuthLocal> response) {
                if (response.isSuccessful()) {
                    AuthLocal auth = response.body();

                    String token = auth.getData().getToken();
                    MainActivity.token = "bearer " + token;
                    Snackbar snackbar = Snackbar.make(_v, "Connexion réussie !", Snackbar.LENGTH_LONG);
                    MainActivity.co = true;
                    snackbar.show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(_v, "Erreur : " + jObjError.getString("title"), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthLocal> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
        //MainActivity.co = true;
    }
}
