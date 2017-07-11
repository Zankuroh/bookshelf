package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthLocal.AuthLocal;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

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

                AccessToken accessToken = loginResult.getAccessToken();
               /* GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        Log.i("FACEBOOK EMAIL",user.optString("email"));
                        Log.i("FACEBOOK NAME",user.optString("name"));
                        Log.i("FACEBOOK ID",user.optString("id"));
                    }
                }).executeAsync();*/
                MainActivity.provider = "FB";
                connectOauth(accessToken.getToken(), "facebook");

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        SignInButton mGoogleSignInButton = (SignInButton)_v.findViewById(R.id.gConnect);
        /*if (MainActivity.token != null) {
            for (int i = 0; i < mGoogleSignInButton.getChildCount(); i++) {
                View v = mGoogleSignInButton.getChildAt(i);

                // if the view is instance of TextView then change the text SignInButton
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.setText("Se déconnecter");
                    //TODO faire la déconnexion si l'envie t'en prend @nicolas
                }
            }
        }*/

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        return _v;
    }

    private static final int RC_SIGN_IN = 9001;

    private void signInWithGoogle()
    {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id)) // R.string.server_client_id => nicolas // R.string.server_client_id_2 => maxime
                .requestServerAuthCode(getString(R.string.server_client_id))// R.string.server_client_id => nicolas // R.string.server_client_id_2 => maxime
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(_v.getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == 4242) { // SignUp successfull -> Auto connect
            if (resultCode == RESULT_OK) {
                String login = data.getStringExtra("login");
                String passwd = data.getStringExtra("pwd");
                connect(login, passwd, _v);
            }
        }

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String Authcode = null;
                if (acct != null) {
                    Authcode = acct.getServerAuthCode();
                }

                Snackbar snackbar = Snackbar.make(_v, Authcode, 5000);
                snackbar.show();
                MainActivity.provider = "Google";
                connectOauth(Authcode, "google");

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
                startActivityForResult(new Intent(getActivity(), SignUp.class), 4242);
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
        connect(login.getText().toString(), passwd.getText().toString(), _v);
    }

    private void connect(String email, String pwd, final View v)
    {
        MainActivity.startLoading();
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
                    MainActivity.userID = auth.getData().getUserId();
                    MainActivity.token = "bearer " + token;
                    Snackbar snackbar = Snackbar.make(v, "Connexion réussie !", Snackbar.LENGTH_LONG);
                    MainActivity.co = true;
                    snackbar.show();
                    switchFragment();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(v, "Erreur : " + jObjError.getString("title"), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<AuthLocal> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void connectOauth(String token, String provider)
    {
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<AuthLocal> call = bookshelfApi.Oauth(token,provider);
        call.enqueue(new Callback<AuthLocal>() {
            @Override
            public void onResponse(Call<AuthLocal> call, Response<AuthLocal> response) {
                if (response.isSuccessful()) {
                    AuthLocal auth = response.body();

                    String token = auth.getData().getToken();
                    MainActivity.userID = auth.getData().getUserId();
                    MainActivity.token = "bearer " + token;
                    Snackbar snackbar = Snackbar.make(_v, "Connexion réussie !", Snackbar.LENGTH_LONG);
                    MainActivity.co = true;
                    snackbar.show();
                    switchFragment();
                } else {
                    MainActivity.provider = null;
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
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<AuthLocal> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void switchFragment()
    {
        MainActivity.MenuItemCo.setTitle("Déconnexion");
        MainActivity.MenuItemBiblio.setChecked(true);
        MainActivity.defineNameToolBar("Bibliothèque");
        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Bundle arg = new Bundle();
        arg.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        // A GARDER (TAB DANS SHELF)
//        ShelfTab shelfFrag = new ShelfTab();
//        shelfFrag.setArguments(arg);
//        fragmentTransaction.replace(R.id.fragment_container, shelfFrag);
//        fragmentTransaction.commit();
        ShelfContainer shelfFrag = new ShelfContainer();
        shelfFrag.setArguments(arg);
        fragmentTransaction.replace(R.id.fragment_container, shelfFrag);
        fragmentTransaction.commit();
    }
}
