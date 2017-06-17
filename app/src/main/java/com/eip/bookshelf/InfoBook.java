package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.ModifBook.ModifBook;
import com.eip.utilities.model.VolumeInfo;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 25/04/2017.
 */

public class InfoBook extends AppCompatActivity
{
    private ListView _lvCom;
    private ArrayList<ComAdapter> _modelListCom = new ArrayList<>();
    private MainActivity.shelfType _type;
    private RelativeLayout _rl;
    private String _isbn;
    private VolumeInfo _vi;

    public InfoBook()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);
        _rl = (RelativeLayout)findViewById(R.id.RLBookInfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _lvCom = (ListView) findViewById(R.id.LVCom);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("book");
        _type = (MainActivity.shelfType)i.getSerializableExtra("shelf");
//        HashMap<String, String> info = (HashMap<String, String>)b.getSerializable("info");
        _isbn = b.getString("isbn");

        setAdapters();
        moreDataBook();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Snackbar snackbar;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.IAddBook:
                AddToBookShelf();
                break;
            case R.id.IRemoveBook:
                deleteToBookShelf();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        if (_type == MainActivity.shelfType.MAINSHELF) {
            menu.findItem(R.id.IAddBook).setVisible(false);
        } else {
            menu.findItem(R.id.IRemoveBook).setVisible(false);
        }
        if (_vi == null) {
            menu.findItem(R.id.IRemoveBook).setVisible(false);
            menu.findItem(R.id.IAddBook).setVisible(false);
        }
        return true;
    }


    private void setAdapters()
    {
        customAdapterCom _adapterCom = new customAdapterCom(this, _modelListCom);
        _lvCom.setAdapter(_adapterCom);
        //Todo: Récupérer les vrais commentaires :|
        _modelListCom.add(new ComAdapter("Maxime", "23/02/2016 à 13h42", "Super livre !"));
        getTotalHeightofListView();
    }

    private void moreDataBook()
    {
        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        TextView tvt = (TextView) findViewById(R.id.TVTitreBook);
        TextView tvr = (TextView) findViewById(R.id.TVResum);
        ImageView iv = (ImageView) findViewById(R.id.IVBook);
        Log.d("_isbn", _isbn);
        Thread t = new Thread(new Runnable() {
            public void run() {
                _vi = ShelfContainer.getInfoBook(_isbn);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (_vi == null) {
            return;
        }

        tvt.setText(_vi.getTitle());

        if (_vi.getPublishedDate() != null) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            Log.d("date", _vi.getPublishedDate());
            try {
                date = dt.parse(_vi.getPublishedDate());
            } catch (ParseException e) {
                dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                try {
                    date = dt.parse(_vi.getPublishedDate());
                } catch (ParseException e1) {
                    date = null;
                }
            }
            if (date != null) {
                SimpleDateFormat dt1 = new SimpleDateFormat("dd'/'MM'/'yyyy");
                tv.setText("Date de sortie : " + dt1.format(date));
            } else {
                tv.setText("Date de sortie : " + _vi.getPublishedDate());
            }
        } else {
            tv.setText("Date de sortie : -");
        }

        if (_vi.getAuthors() == null || _vi.getAuthors().size() == 0) {
            tv.setText(tv.getText() + "\nAuteur : -");
        } else {
            tv.setText(tv.getText() + "\nAuteur : ");
            for (int i = 0; i < _vi.getAuthors().size(); i++) {
                tv.setText(tv.getText() + _vi.getAuthors().get(i));
                if (i != _vi.getAuthors().size() - 1) {
                    tv.setText(tv.getText() + ", ");
                }
            }
        }

        if (_vi.getCategories() == null || _vi.getCategories().size() == 0) {
            tv.setText(tv.getText() + "\nGenre : -");
        } else {
            tv.setText(tv.getText() + "\nGenre : ");
            for (int i = 0; i < _vi.getCategories().size(); i++) {
                tv.setText(tv.getText() + _vi.getCategories().get(i));
                if (i != _vi.getCategories().size() - 1) {
                    tv.setText(", ");
                }
            }
        }

        if (_vi.getRatingsCount() == null) {
            tv.setText(tv.getText() + "\nNote : -");
        } else {
            tv.setText(tv.getText() + "\nNote : " + _vi.getRatingsCount());
        }

        if (_vi.getDescription() != null) {
            tvr.setText(_vi.getDescription());
        }

        if (_vi.getImageLinks() != null && _vi.getImageLinks().getThumbnail() != null) {
            Picasso.with(this).load(_vi.getImageLinks().getThumbnail()).fit().into(iv);
        }
//        tvt.setText(info.get("title"));
//        tv.setText("Date de sortie : " + info.get("date"));
//        tv.setText(tv.getText() + "\nAuteur : " + info.get("author"));
//        tv.setText(tv.getText() + "\nGenre : " + info.get("genre"));
//        tv.setText(tv.getText() + "\nNote : " + info.get("note"));
//        tvr.setText(info.get("resume"));
//        if (info.get("picture") != null && !info.get("picture").equals("")) {
//            Picasso.with(this).load(info.get("picture")).fit().into(iv);
//        }
    }

    private void getTotalHeightofListView()
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

    public void onClickSendCom(View v)
    {
        EditText et = (EditText) findViewById(R.id.ETCom);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.FRANCE);
        String currentDateandTime = sdf.format(new Date());
        _modelListCom.add(new ComAdapter("Nicolas", currentDateandTime, et.getText().toString()));
        et.setText("");
        MainActivity.hideSoftKeyboard(InfoBook.this);
        getTotalHeightofListView();
    }


    public void AddToBookShelf(){
        String isbn = "";
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddBook(MainActivity.token, isbn);
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été ajouté à votre bibliothèque", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void deleteToBookShelf(){
        String isbn = "";
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelBook(MainActivity.token, isbn, "deleted");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre bibliothèque", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void AddToWishList(){
        String isbn = "";
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddWishBook(MainActivity.token, isbn);
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été ajouté à votre liste de souhait", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void deleteToWishList(){
        String isbn = "";
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelWishBook(MainActivity.token, isbn, "deleted");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre liste de souhait", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
