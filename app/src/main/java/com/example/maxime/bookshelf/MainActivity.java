package com.example.maxime.bookshelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout _lp;
    private RelativeLayout _lmain;
    private MenuItem _itm;
    private GridView _gvBiblio;
    private ListView _lvAutor;
    private ListView _lvCom;
    private customAdapterCom _adapterCom;
    private customAdapterBiblio _adapterBiblio;
    private customAdapterAutor _adapterAutor;
    private ArrayList<ComAdapter> _modelListCom = new ArrayList<ComAdapter>();
    private ArrayList<String> _modelListAutor = new ArrayList<String>();
    private ArrayList<BiblioAdapter> _modelListBiblio = new ArrayList<BiblioAdapter>();
    private int _currentViewID;
    private int _previousViewID;
    private List<Book> _infos;
    private boolean _co = false;
    private boolean _scan = false;
    private SignIn _connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        setAdapters();
        _lmain = (RelativeLayout) findViewById(R.id.RLMain);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        defineNameToolBar("Bibliothèque");
        _currentViewID = R.id.RLBiblio;
        prout();
        drawBiblio();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (_currentViewID == R.id.SVBook) {
            changeCurrentView(R.id.VFMain, _previousViewID, false);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.IAddBook) {
            _scan = true;
            Snackbar snackbar = Snackbar.make(_lmain, "Le livre a été ajouté à votre bibliothèque", Snackbar.LENGTH_LONG);
            snackbar.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        _itm = item;
        BiblioAdapter ba;
        verifyUser(id);

        switch (id)
        {
            case R.id.nav_biblio:
                defineNameToolBar("Bibliothèque");
                changeCurrentView(R.id.VFMain, R.id.RLBiblio, true);
                drawBiblio();
                break;
            case R.id.nav_search:
                changeCurrentView(R.id.VFMain, R.id.RLSearch, true);
                defineNameToolBar("Recherche");
                break;
            case R.id.nav_propo:
                defineNameToolBar("Suggestions");
                changeCurrentView(R.id.VFMain, R.id.RLBiblio, true);
                drawPropo();
                break;
            case R.id.nav_wish:
                changeCurrentView(R.id.VFMain, R.id.RLBiblio, true);
                ba = new BiblioAdapter("Les souhaits", R.drawable.test_book);
                _modelListBiblio.add(ba);
                defineNameToolBar("Liste de souhaits");
                break;
            case R.id.nav_autor:
                changeCurrentView(R.id.VFMain, R.id.RLAutor, true);
                defineNameToolBar("Auteurs suivis");
                break;
            case R.id.nav_co:
                navCo();
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

    private void drawBiblio()
    {
        for (int i = 0; i < _infos.size(); i++) {
            if (!_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") &&
                    !_infos.get(i).getTitre().equals("La damnation de Pythos") &&
                    !_infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre") &&
                    !_infos.get(i).getTitre().equals("Le trône de fer tome 5")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            } else if (_infos.get(i).getTitre().equals("La damnation de Pythos") && _scan) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            }
        }
    }

    private void drawPropo()
    {
        for (int i = 0; i < _infos.size(); i++) {
            if (_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") ||
                    _infos.get(i).getTitre().equals("Le trône de fer tome 5") ||
                    _infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            }
        }
    }

    private void setAdapters() {
        _gvBiblio = (GridView) findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(this, _modelListBiblio);
        _gvBiblio.setAdapter(_adapterBiblio);
        _gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Snackbar snackbar = Snackbar.make(_lmain, ((TextView) view.findViewById(R.id.TVAff)).getText(), Snackbar.LENGTH_LONG);
                //snackbar.show();
                changeCurrentView(R.id.VFMain, R.id.SVBook, false);
                moreDataBook(((TextView) view.findViewById(R.id.TVAff)).getText().toString());
            }
        });

        _lvAutor = (ListView) findViewById(R.id.LVAutor);
        _adapterAutor = new customAdapterAutor(this, _modelListAutor);
        _lvAutor.setAdapter(_adapterAutor);
        _lvAutor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Snackbar snackbar = Snackbar.make(_lmain, ((TextView) view.findViewById(R.id.TVAutor)).getText(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        _lvCom = (ListView) findViewById(R.id.LVCom);
        _adapterCom = new customAdapterCom(this, _modelListCom);
        _lvCom.setAdapter(_adapterCom);

        _modelListAutor.add("J. K. Rowling");
        _modelListAutor.add("R. R. Martin");
        _modelListCom.add(new ComAdapter("Maxime", "23/02/2016 à 13h42", "Super livre !"));

        _gvBiblio.setVisibility(View.GONE);
        _lvAutor.setVisibility(View.GONE);

        getTotalHeightofListView();
    }

    public void getTotalHeightofListView()
    {

        ListAdapter mAdapter = _lvCom.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            View mView = mAdapter.getView(i, null, _lvCom);

            mView.measure( View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = _lvCom.getLayoutParams();
        params.height = totalHeight + (_lvCom.getDividerHeight() * (mAdapter.getCount() - 1));
        _lvCom.setLayoutParams(params);
        _lvCom.requestLayout();

    }

    private void defineNameToolBar(String title)
    {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void changeCurrentView(int idFlipper, int idView, boolean clear)
    {
        if (clear) {
            _modelListBiblio.clear();
            _adapterBiblio.notifyDataSetChanged();
        }
        ViewFlipper vs = (ViewFlipper) findViewById(idFlipper);
        vs.setDisplayedChild(vs.indexOfChild(findViewById(idView)));
        Log.i("App", Integer.toString(idView));
        _previousViewID = _currentViewID;
        _currentViewID = idView;
    }

    public void sendCom(View v)
    {
        EditText et = (EditText) findViewById(R.id.ETCom);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH'h'mm");
        String currentDateandTime = sdf.format(new Date());
        Log.i("sendCom", currentDateandTime);
        _modelListCom.add(new ComAdapter("Nicolas", currentDateandTime, et.getText().toString()));
        et.setText("");
        InputMethodManager inputManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        getTotalHeightofListView();
    }

    public void openApn(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setCaptureActivity(DecodeBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scanner le code ISBN au dos du livre");
        integrator.setBeepEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 0x0000c0de) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (resultCode == RESULT_OK) {
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();
                    //we have a result
                    Log.i("App", "Format: " + scanFormat + "\nRésultat: " + scanContent);
                    changeCurrentView(R.id.VFMain, R.id.SVBook, false);
                    moreDataBook("La damnation de Pythos");
                    // Ici appeler la fonction moredatabook avec le titre du livre scanné
                }
                else {
                    Log.i("App", "Scan échec");
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("App", "Scan unsuccessful");
            }
        }
    }

    private void moreDataBook(String title)
    {
        int i;
        for (i = 0; i < _infos.size(); i++) {
            if (title.equals(_infos.get(i).getTitre()) == true) {
                Book b = _infos.get(i);
                TextView tv = (TextView) findViewById(R.id.TVInfoBook);
                TextView tvt = (TextView) findViewById(R.id.TVTitreBook);
                TextView tvr = (TextView) findViewById(R.id.TVResum);
                tvt.setText(b.getTitre());
                tv.setText("Date de sortie : " + b.getDate());
                tv.setText(tv.getText() + "\nAuteur : " + b.getAuteur());
                tv.setText(tv.getText() + "\nGenre : " + b.getGenre());
                tv.setText(tv.getText() + "\nNote : " + b.getNote().toString());
                tvr.setText(b.getResum());

                ImageView iv = (ImageView) findViewById(R.id.IVBook);
                iv.setImageBitmap(OptimizeBitmap.decodeSampledBitmapFromResource(getResources(), b.getImage(), 200, 200));
            }
        }
    }

    private void navAbout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changelog:\n-V0.00.1: Version alpha\n\n" +
                "L'application BookShelf vous permet de gérer votre bibliothèque !\n" +
                "Développée par Maxime, Geoffrey, Nicolas, Pierre, Victorien et Sergen.\n" +
                "D'après l'idée originale de Pierre.");
        builder.setPositiveButton("Merci !", null);
        builder.show();
    }

    private void navCo()
    {
        if (_itm.getTitle().toString().equals("Déconnexion")) {
            Snackbar snackbar = Snackbar.make(_lmain, "À bientôt !", Snackbar.LENGTH_LONG);
            snackbar.show();
            _itm.setTitle("Connexion");
            _co = false;
            _gvBiblio.setVisibility(View.GONE);
            _lvAutor.setVisibility(View.GONE);
            findViewById(R.id.TVCo).setVisibility(View.VISIBLE);
            return;
        }
        EditText ETlogin = new EditText(MainActivity.this);
        EditText ETpwd = new EditText(MainActivity.this);

        ETpwd.setId(R.id.popupPasswd);
        ETlogin.setId(R.id.popupLogin);

        ETlogin.setHint("Nom d'utilisateur");
        ETlogin.setInputType(InputType.TYPE_CLASS_TEXT);

        ETpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ETpwd.setHint("Mot de passe");

        _lp = new LinearLayout(MainActivity.this);
        _lp.setOrientation(LinearLayout.VERTICAL);
        _lp.addView(ETlogin);
        _lp.addView(ETpwd);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText login = (EditText) _lp.findViewById(R.id.popupLogin);
                EditText passwd = (EditText) _lp.findViewById(R.id.popupPasswd);
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        _connect = new SignIn(login.getText().toString(), passwd.getText().toString(), _lmain);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run()
                            {
                                _co = _connect.getStatus();
                                if (_co == true) {
                                    _gvBiblio.setVisibility(View.VISIBLE);
                                    _lvAutor.setVisibility(View.VISIBLE);
                                    findViewById(R.id.TVCo).setVisibility(View.GONE);
                                    _itm.setTitle("Déconnexion");
                                }
                            }
                        }, 3000);

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        changeCurrentView(R.id.VFMain, R.id.RLSignUp, false);
                        defineNameToolBar("Creation d'un compte");
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Veuillez vous connecter.").setPositiveButton("Connexion", dialogClickListener).setNegativeButton("Annuler", dialogClickListener).setNeutralButton("Créer un compte", dialogClickListener);
        builder.setView(_lp);
        builder.show();
      }

    private void verifyUser(int id)
    {
        TextView tv = (TextView) findViewById(R.id.TVCo);

        if ((id == R.id.nav_biblio || id == R.id.nav_propo || id == R.id.nav_wish) && _co &&
                (_gvBiblio.getVisibility() == View.GONE || _lvAutor.getVisibility() == View.GONE)) {
            _gvBiblio.setVisibility(View.VISIBLE);
            _lvAutor.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        } else if (!_co &&
                (_gvBiblio.getVisibility() == View.VISIBLE || _lvAutor.getVisibility() == View.VISIBLE)) {
            _gvBiblio.setVisibility(View.GONE);
            _lvAutor.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void CreateAccount(View v) {
        EditText name = (EditText) findViewById(R.id.SignUpFirstName);
        EditText password = (EditText) findViewById(R.id.SignUpPwd);
        EditText email = (EditText) findViewById(R.id.SignUpMail);
        final SignUp sign_up = new SignUp(name.getText().toString(), email.getText().toString(), password.getText().toString(), (RelativeLayout) findViewById(R.id.RLMain));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run()
            {
                _co = sign_up.getStatus();
                if (_co == true) {
                    _gvBiblio.setVisibility(View.VISIBLE);
                    _lvAutor.setVisibility(View.VISIBLE);
                    findViewById(R.id.TVCo).setVisibility(View.GONE);
                    _itm.setTitle("Déconnexion");
                    changeCurrentView(R.id.VFMain, R.id.RLBiblio, false);
                }
            }
        }, 3000);
    }

    public void CancelAccount(View v) {
        changeCurrentView(R.id.VFMain, R.id.RLBiblio, false);
    }

    private void prout()
    {
        /**
         * ouverture + creation du fichier
         */
        SharedPreferences sp = getSharedPreferences("Bibliothèque", Context.MODE_PRIVATE);
        /**
         *  edition du fichier
         */
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("biblio", "[\n" +
                "{\"titre\":\"Le trône de fer tome 5\",\n" +
                "\"image\":2130837592,\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"08/04/2015\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"Daenerys règne sur une cité de mort, entourée d'ennemis, tandis que Jon Snow doit affronter ses adversaires des deux côtés du Mur.\",\n" +
                "\"note\":4,\n" +
                "\"isbn\":9782290107096},\n" +
                "{\"titre\":\"Le trône de fer tome 1\",\n" +
                "\"image\":2130837573,\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 2\",\n" +
                "\"image\":2130837574,\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 3\",\n" +
                "\"image\":2130837575,\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 4\",\n" +
                "\"image\":2130837576,\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter à l'école des sorciers\",\n" +
                "\"image\":2130837577,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"16 novembre 1998\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"Le jour de ses onze ans, Harry Potter, un orphelin élevé par un oncle et une tante qui le détestent, voit son existence bouleversée. Un géant vient le chercher pour l'emmener à Poudlard, une école de sorcellerie ! Voler en balai, jeter des sorts, combattre les trolls : Harry Potter se révèle un sorcier doué. Mais un mystère entoure sa naissance et l'effroyable V..., le mage dont personne n'ose prononcer le nom.\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":2070518426\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et la Chambre des secrets\",\n" +
                "\"image\":2130837578,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et le Prisonnier d'Azkaban\",\n" +
                "\"image\":2130837579,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et la Coupe de feu\",\n" +
                "\"image\":2130837580,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et l'Ordre du phénix\",\n" +
                "\"image\":2130837581,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et le Prince de sang-mêlé\",\n" +
                "\"image\":2130837582,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et les reliques de la mort\",\n" +
                "\"image\":2130837593,\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":0\n" +
                "},\n" +
                "{\n" +
                "\"titre\":\"La damnation de Pythos\",\n" +
                "\"image\":2130837595,\n" +
                "\"auteur\":\"David Annandale\",\n" +
                "\"date\":\"16 novembre 2015\",\n" +
                "\"genre\":\"Science-Fiction\",\n" +
                "\"resum\":\"A la suite du Massacre du Dropsite d'Isstvan V, une force battue et ensanglantée d'Iron Hands, de Raven Guard et de Salamanders se regroupe sur ce qui ressemble à un monde mort insignifiant. En parant des attaques de toutes part commises par les créatures monstrueuses, ces alliés hargneux trouvent un espoir en la forme de réfugiés humains fuyant cette guerre grandissante et jetés à la dérive dans les courants du warp. Mais alors même que les Space Marines se taillent un sanctuaire pour se réfugier dans les jungles de Pythos, une ombre grandissante se rassemble et se prépare à les consumer tous.\",\n" +
                "\"note\":5,\n" +
                "\"isbn\":9781780302416\n" +
                "},\n" +
                "{\n" +
                "\"titre\":\"Percy Jackson - Tome 1 : Le voleur de foudre\",\n" +
                "\"image\":2130837594,\n" +
                "\"auteur\":\" Rick Riordan\",\n" +
                "\"date\":\" 3 juillet 2013\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"Percy Jackson n’est pas un garçon comme les autres. Ado perturbé, renvoyé de collège en pension, il découvre un jour le secret de sa naissance et de sa différence : son père, qu’il n’a jamais connu, n’est autre que Poséidon, le dieu de la mer dans la mythologie grecque. Placé pour sa protection dans un camp de vacances pour enfants « sangs mêlés » (mi-humains, mi-divins), Percy se voit injustement accusé d’avoir volé l’éclair de Zeus. Afin d’éviter une guerre fratricide entre les dieux de l’Olympe, il va devoir repartir dans le monde des humains, retrouver l’éclair et démasquer le vrai coupable… au péril de sa vie.\",\n" +
                "\"note\":4.5,\n" +
                "\"isbn\":2226249303\n" +
                "}\n" +
                "]");
        editor.commit();
        /**
         * récupération des donnée enregistré
         */
        String ret = sp.getString("biblio", null);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            _infos = objectMapper.readValue(ret, objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
