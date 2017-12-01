package com.eip.bookshelf;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.BooksLocal.BooksLocal;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.VolumeInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 02/03/2017.
 */

public class ShelfContainer extends Fragment
{
    private ArrayList<BiblioAdapter> _modelListBiblio = new ArrayList<>();
    private View _v;
    private MainActivity.shelfType _type;
    private customAdapterBiblio _adapterBiblio;
    private RequestDBLocal _req;
    private boolean _currentTab = false;
    private TextWatcher _tWatcher = null;

    public ShelfContainer()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle b = getArguments();
        if (b != null) {
            _type = (MainActivity.shelfType)b.getSerializable("type");
        } else {
            _type = null;
        }
        _req = new RequestDBLocal(_type, getContext());
        //_req.deletePrimaryInfo(null, null);
        if (_type == MainActivity.shelfType.MAINSHELF) {
            _v = inflater.inflate(R.layout.shelf_container, container, false); //Anciennement shelf_container !
            setAdapters();
            if (_currentTab) {
                setTextWatcher();
            }
        } else if (_type == MainActivity.shelfType.PROPOSHELF) {
            _v = inflater.inflate(R.layout.shelf_simple, container, false);
            setTextWatcher();
            setAdapters();
        } else if (_type == MainActivity.shelfType.WISHSHELF) {
            _v = inflater.inflate(R.layout.shelf_simple, container, false);
            setTextWatcher();
            setAdapters();
        }

        Log.d("why", "entrance");
        return _v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        _modelListBiblio.clear();
        _adapterBiblio.notifyDataSetChanged();
        if (_type == MainActivity.shelfType.MAINSHELF) {
            if (_currentTab) {
                mainShelf();
            }
        } else if (_type == MainActivity.shelfType.PROPOSHELF) {
            propoShelf();
        } else if (_type == MainActivity.shelfType.WISHSHELF) {
            wishShelf();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        _currentTab = isVisibleToUser;
        if (isVisibleToUser && _v != null) {
            setTextWatcher();
            mainShelf();
        } else {
            unsetTextWatcher();
        }
        Log.d("current tab", String.valueOf(_currentTab));
    }

    private void setTextWatcher()
    {
        if (getActivity() != null) {
            EditText field = (EditText) getActivity().findViewById(R.id.ETkeyword);
            if (field == null) {
                field = (EditText) _v.findViewById(R.id.ETkeyword);
            }
            _tWatcher = new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("text", s.toString());
                }
            };
            field.addTextChangedListener(_tWatcher);
        }
    }

    private void unsetTextWatcher()
    {
        if (_tWatcher != null && getActivity() != null) {
            EditText field = (EditText) getActivity().findViewById(R.id.ETkeyword);
            field.removeTextChangedListener(_tWatcher);
        }
    }

//    private void loadMain()
//    {
//        if (ShelfTab.in_use) {
//            return;
//        } else {
//            ShelfTab.in_use = true;
//        }
//        TabLayout tabLayout = (TabLayout)getActivity().findViewById(R.id.TLTab);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Log.d("shelf", "loadmain");
//                mainShelf();
//                // Appeler seulement la BDD au lieu de la full request.
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {}
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {}
//        });
//        //mainShelf();
//    }

    private void mainShelf()
    {
        Log.d("Entrée !", "chargement mainShelf !");
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<BooksLocal> call = bookshelfApi.getBookshelf(MainActivity.token);
        call.enqueue(new Callback<BooksLocal>() {
            @Override
            public void onResponse(Call<BooksLocal> call, Response<BooksLocal> response) {
                _modelListBiblio.clear();
                if (response.isSuccessful()) {
                    BooksLocal bookshelf = response.body();
                    List<com.eip.utilities.model.BooksLocal.Book> list = bookshelf.getData();
                    ListIterator<com.eip.utilities.model.BooksLocal.Book> it = list.listIterator();
                    ArrayList<String> isbns = new ArrayList<>();

                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                    }
                    if (!isbns.isEmpty())
                        searchBookInShelf(isbns);
                    else {
                        Snackbar snackbar = Snackbar.make(_v, "Votre bibliothèque est vide", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
                ShelfTab.in_use = false;
            }

            @Override
            public void onFailure(Call<BooksLocal> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void propoShelf()
    {
        //MainActivity.startLoading();
        //Todo: Appel à la BDD pour recup les vrais PROPOS
        //MainActivity.stopLoading();
    }

    private void wishShelf()
    {
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<BooksLocal> call = bookshelfApi.getWishBookshelf(MainActivity.token);
        call.enqueue(new Callback<BooksLocal>() {
            @Override
            public void onResponse(Call<BooksLocal> call, Response<BooksLocal> response) {
                _modelListBiblio.clear();
                if (response.isSuccessful()) {
                    BooksLocal bookshelf = response.body();
                    List<com.eip.utilities.model.BooksLocal.Book> list = bookshelf.getData();

                    ListIterator<com.eip.utilities.model.BooksLocal.Book> it = list.listIterator();
                    ArrayList<String> isbns = new ArrayList<>();
                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                    }
                    if (!isbns.isEmpty())
                        searchBookInShelf(isbns);
                    else {
                        Snackbar snackbar = Snackbar.make(_v, "Votre liste de souhaits est vide", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
                ShelfTab.in_use = false;
            }

            @Override
            public void onFailure(Call<BooksLocal> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void setAdapters()
    {
        GridView gvBiblio = (GridView) _v.findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(_v, _modelListBiblio);
        gvBiblio.setAdapter(_adapterBiblio);
        _modelListBiblio.clear();

        gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isbn = ((TextView) view.findViewById(R.id.TVISBN)).getText().toString();
                Bundle b = new Bundle();
                b.putString("isbn", isbn);
                Intent in = new Intent(getActivity(), InfoBook.class);
                in.putExtra("book", b);
                startActivity(in);
            }
        });
    }

    private void searchBookInShelf(final ArrayList<String> isbns)
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Cursor cursor = _req.readPrimaryInfo(isbns);
                while(cursor.moveToNext()) {
                    String isbn = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_ISBN));
                    String title = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_TITLE));
                    String pic = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_PIC));
                    _modelListBiblio.add(new BiblioAdapter(title, pic, isbn));
                    isbns.remove(isbn);
                }
                cursor.close();
                for (String isbn : isbns) {
                    VolumeInfo vi = getInfoBook(isbn);
                    String img;
                    if (vi.getImageLinks() == null || vi.getImageLinks().getThumbnail() == null) {
                        img = "https://puu.sh/wm9pR/adf0d3f814.jpg";
                    } else {
                        img = vi.getImageLinks().getThumbnail();
                    }
                    _modelListBiblio.add(new BiblioAdapter(vi.getTitle(), img, isbn));
                    _req.writePrimaryInfo(vi.getTitle(), img, isbn);
                }
            }
        });
        t.start();
        try {
            t.join();
            _adapterBiblio.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static VolumeInfo getInfoBook(String isbn)
    {
        GoogleBooksApi gbi = new Retrofit.Builder()
                .baseUrl(GoogleBooksApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksApi.class);
        Call<Books> call = gbi.searchByIsbn("isbn:"+isbn);
        VolumeInfo vi = null;

        try {
            Books b = call.execute().body();
            if (b.getTotalItems() > 0) {
                Item item = b.getItems().get(0);
                vi = item.getVolumeInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vi;
    }
}
