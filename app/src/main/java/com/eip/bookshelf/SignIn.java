package com.eip.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthLocal.AuthLocal;
import com.eip.utilities.model.SimpleResponse.SimpleResponse;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
    private LoginButton loginButton;

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
        _v.findViewById(R.id.fbConnect).setOnClickListener(this);

        _callbackManager = CallbackManager.Factory.create();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    MainActivity.token = null;
                    MainActivity.provider = null;
                }
            }
        };

        loginButton = _v.findViewById(R.id.fConnect);
        loginButton.setReadPermissions("public_profile email");
        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.registerCallback(_callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                MainActivity.provider = "FB";
                connectOauth(accessToken.getToken(), "facebook");
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException exception) { }
        });
        accessTokenTracker.startTracking();

        Button mGoogleSignInButton = _v.findViewById(R.id.gConnect);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id)) // R.string.server_client_id => nicolas // R.string.server_client_id_2 => maxime
                .requestServerAuthCode(getString(R.string.server_client_id))// R.string.server_client_id => nicolas // R.string.server_client_id_2 => maxime
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(_v.getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        deconnection();

        return _v;
    }

    private static final int RC_SIGN_IN = 9001;

    private void signInWithGoogle()
    {
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
                AskEmailForReset();
                break;
            case R.id.btnCreateAccount:
                startActivityForResult(new Intent(getActivity(), SignUp.class), 4242);
                break;
            case R.id.fbConnect:
                Log.i("Click", "FB");
                loginButton.performClick();
                break;
            default:
                break;
        }
    }

    private void clickSignIn()
    {
        MainActivity.hideSoftKeyboard(getActivity());
        EditText login = _v.findViewById(R.id.ETsignInMail);
        EditText passwd = _v.findViewById(R.id.ETsignInMdp);
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
        ShelfTab shelfFrag = new ShelfTab();
        shelfFrag.setArguments(arg);
        fragmentTransaction.replace(R.id.fragment_container, shelfFrag, "SHELF");
        fragmentTransaction.commit();
    }

    public void deconnection() {
        if (MainActivity.provider != null  && MainActivity.provider.equals("FB") && MainActivity.token != null) {
            loginButton.performClick();
        } else if (MainActivity.provider != null && MainActivity.provider.equals("Google") && MainActivity.token != null) {
            //mGoogleApiClient.connect();
            /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });*/
            /*Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });*/
            MainActivity.token = null;
            MainActivity.provider = null;
        } else if (MainActivity.token != null) {
            MainActivity.token = null;
            MainActivity.provider = null;
        }
    }

    public void AskEmailForReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(_v.getContext());
        builder.setMessage("Veuillez entrer votre adresse mail. Vous recevrez un token pour reset votre mot de passe");
        final EditText input = new EditText(_v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("Valider",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                resetPasswordSendToken(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public void resetPasswordSendToken(String email) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SimpleResponse> call = bookshelfApi.sendToken(email);
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()) {
                    SimpleResponse reset = response.body();
                    if (reset.getData().getSuccess().equals("true")) {
                        Snackbar snackbar = Snackbar.make(_v, "Un email avec un token vous a été envoyé", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        AskTokenForReset();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(_v, "Erreur : " + reset.getTitle(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        AskEmailForReset();
                    }
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
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    public void AskTokenForReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(_v.getContext());
        builder.setMessage("Veuillez entrer le token reçus par mail");
        final EditText input = new EditText(_v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("Valider",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                resetPasswordSendNewPassword(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public void resetPasswordSendNewPassword(String token) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SimpleResponse> call = bookshelfApi.validateToken(token);
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()) {
                    SimpleResponse reset = response.body();
                    if (reset.getData().getSuccess().equals("true")) {
                        Snackbar snackbar = Snackbar.make(_v, "Un email vous a été envoyé avec votre nouveau mot de passe", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(_v, "Erreur : " + reset.getTitle(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                        AskTokenForReset();
                    }
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
                MainActivity.stopLoading();
            }
            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }
}
