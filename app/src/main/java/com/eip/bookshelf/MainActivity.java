package com.eip.bookshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static boolean co = false;
    public static String token = null;
    public static MenuItem MenuItemCo;
    enum shelfType {
        MAINSHELF,
        PROPOSHELF,
        WISHSHELF,
        SEARCH
    }
    private SignIn _signFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set the fragment initially
        Disconnected fragment = new Disconnected();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        defineNameToolBar("Bibliothèque");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Log.d("Select", "...");

        switch (id) {
            case R.id.nav_biblio:
                defineNameToolBar("Bibliothèque");
                if (MainActivity.co) {
                    Bundle arg = setArgs(shelfType.MAINSHELF);
                    ShelfTab shelfFrag = new ShelfTab();
                    shelfFrag.setArguments(arg);
                    fragmentTransaction.replace(R.id.fragment_container, shelfFrag);
                    Log.d("Select biblio", "PUTAIN !");
                    fragmentTransaction.commit();
                } else {
                    accessDenied();
                }
                break;
            case R.id.nav_search:
                defineNameToolBar("Recherche");
                Search searchFrag = new Search();
                fragmentTransaction.replace(R.id.fragment_container, searchFrag);
                fragmentTransaction.commit();
                break;
            case R.id.nav_propo:
                defineNameToolBar("Propositions");
                if (MainActivity.co) {
                    Bundle arg = setArgs(shelfType.PROPOSHELF);
                    ShelfContainer shelfFrag = new ShelfContainer();
                    shelfFrag.setArguments(arg);
                    fragmentTransaction.replace(R.id.fragment_container, shelfFrag);
                    fragmentTransaction.commit();
                } else {
                    accessDenied();
                }
                break;
            case R.id.nav_wish:
                defineNameToolBar("Liste de souhaits");
                if (MainActivity.co) {
                    Bundle arg = setArgs(shelfType.WISHSHELF);
                    ShelfContainer shelfFrag = new ShelfContainer();
                    shelfFrag.setArguments(arg);
                    fragmentTransaction.replace(R.id.fragment_container, shelfFrag);
                    fragmentTransaction.commit();
                } else {
                    accessDenied();
                }
                break;
            case R.id.nav_author:
                defineNameToolBar("Auteurs suivis");
                if (MainActivity.co) {
                    FollowAuthor authorFrag = new FollowAuthor();
                    fragmentTransaction.replace(R.id.fragment_container, authorFrag);
                    fragmentTransaction.commit();
                } else {
                    accessDenied();
                }
                break;
            case R.id.nav_profil:
                defineNameToolBar("Profil");
                if (MainActivity.co) {
                    Profil profilFrag = new Profil();
                    fragmentTransaction.replace(R.id.fragment_container, profilFrag);
                    fragmentTransaction.commit();
                } else {
                    accessDenied();
                }
                break;
            case R.id.nav_co:
                if (!MainActivity.co) {
                    MenuItemCo = item;
                    defineNameToolBar("Connexion");
                    _signFrag = new SignIn();
                    fragmentTransaction.replace(R.id.fragment_container, _signFrag);
                    fragmentTransaction.commit();
                } else {
                    MainActivity.co = false;
                    item.setTitle("Connexion");
                    accessDenied();
                }
                break;
            case R.id.nav_param:
                break;
            case R.id.nav_about:
                navAbout();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Bundle setArgs(shelfType st)
    {
        Bundle args = new Bundle();
        args.putSerializable("type", st);

        return args;
    }

    private void accessDenied()
    {
        Disconnected decoFrag = new Disconnected();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, decoFrag);
        fragmentTransaction.commit();
    }

    private void defineNameToolBar(String title)
    {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void navAbout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changelog:\n-V0.45: Refonte de l'app. Nouvelles fonctionnalités !\n\n" +
                "L'application BookShelf vous permet de gérer votre bibliothèque !\n" +
                "Développée par Nicolas et Maxime.\n" +
                "D'après l'idée originale de Pierre.");
        builder.setPositiveButton("Merci !", null);
        builder.show();
    }

    public static void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View current_focus = activity.getCurrentFocus();
        if (current_focus != null) {
            inputMethodManager.hideSoftInputFromWindow(current_focus.getWindowToken(), 0);
        }
    }
}
