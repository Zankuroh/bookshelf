package com.eip.bookshelf;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.eip.utilities.model.ASIN.ASIN;
import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.BooksLocal.BooksLocal;
import com.eip.utilities.model.Friend.WishList.Datum;
import com.eip.utilities.model.Friend.WishList.WishList;
import com.eip.utilities.model.IndustryIdentifier;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.Suggestion.Suggestion;
import com.eip.utilities.model.VolumeInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private String _status;
    private View _v;
    private MainActivity.shelfType _type;
    private customAdapterBiblio _adapterBiblio;
    private RequestDBLocal _req;
    private boolean _currentTab = false;
    private TextWatcher _tWatcher = null;
    private String _keywords;
    private int _startIndex;
    private int _nbFound;
    private GridView _gvBiblio;
    private List<String> _latestSugg;
    private List<String> _overallSugg;
    private List<String> _friendLatestSugg;
    private List<String> _friendSugg;
    private String _FriendId;

    public ShelfContainer()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _status = "-1";
        Bundle b = getArguments();
        if (b != null) {
            _type = (MainActivity.shelfType)b.getSerializable("type");
            _FriendId = b.getString("idFriend", null);
            if (_type == MainActivity.shelfType.MAINSHELF) {
                _status = b.getString("status");
            }
        } else {
            _type = null;
        }
        _req = new RequestDBLocal(_type, getContext());
//        _req.deletePrimaryInfo(null, null);
        if (_type == MainActivity.shelfType.MAINSHELF) {
            _v = inflater.inflate(R.layout.shelf_container, container, false); //Anciennement shelf_container !
            setAdapters();
            if (_currentTab) {
                setTextWatcher();
                mainShelf();
            }
        } else if (_type == MainActivity.shelfType.PROPOSHELF) {
            _v = inflater.inflate(R.layout.shelf_propo, container,false);
            setAdapters();
            propoShelf();
            setOnChangeSugg();
        } else if (_type == MainActivity.shelfType.WISHSHELF) {
            _v = inflater.inflate(R.layout.shelf_simple, container, false);
            if (_FriendId == null) {
                setTextWatcher();
            } else {
                _v.findViewById(R.id.ISearchB).setVisibility(View.GONE);
            }
            setAdapters();
        } else if (_type == MainActivity.shelfType.SEARCH) {
            _v = inflater.inflate(R.layout.shelf_search, container, false);
            _keywords = b.getString("keywords");
            _startIndex = 0;
            _nbFound = 0;
            initSearchInfo();
            setAdapters();
            searchBookByKeywords();
        }
        return _v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getFragmentManager() == null) {
            return;
        }
        Fragment myFragment = getFragmentManager().findFragmentByTag("SHELF");
        if (myFragment != null && myFragment.isVisible()) {
            if (getActivity() == null) {
                return;
            }
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            if (field == null) {
                field = _v.findViewById(R.id.ETkeyword);
            }
            if (field != null) {
                field.setText("");
            }
            if (_type == MainActivity.shelfType.WISHSHELF) {
                if (_FriendId == null) {
                    wishShelf();
                } else {
                    getFriendWishList();
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        _currentTab = isVisibleToUser;
        if (isVisibleToUser && _v != null) {
            setTextWatcher();
            if (_modelListBiblio.size() == 0) {
                mainShelf();
            }
        } else {
            unsetTextWatcher();
        }
    }

    private void setOnChangeSugg()
    {
        Spinner sp = _v.findViewById(R.id.SSuggest);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String value = ((Spinner)_v.findViewById(R.id.SSuggest)).getSelectedItem().toString();
                Log.i("Item", value);
                switch (value) {
                    case "Suggestions des 3 derniers livres":
                        loadSuggestions(_latestSugg);
                        break;
                    case "Suggestions de tous les livres":
                        loadSuggestions(_overallSugg);
                        break;
                    case "Suggestions des 3 derniers livres amis":
                        loadSuggestions(_friendLatestSugg);
                        break;
                    case "Suggestions de tous les livres amis":
                        loadSuggestions(_friendSugg);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void setTextWatcher()
    {
        if (getActivity() != null) {
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            if (field == null) {
                field = _v.findViewById(R.id.ETkeyword);
            }
            _tWatcher = new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String typeSearch = "";
                    String content = s.toString();
                    if (!content.equals("")) {
                        Spinner sp = getActivity().findViewById(R.id.Stype);
                        if (sp == null) {
                            sp = _v.findViewById(R.id.Stype);
                        }
                        typeSearch = sp.getSelectedItem().toString();
                    }
                    _modelListBiblio.clear();
                    searchBookInShelf(typeSearch, content);
                }
            };
            field.addTextChangedListener(_tWatcher);
        }
    }

    private void unsetTextWatcher()
    {
        if (_tWatcher != null && getActivity() != null) {
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            field.removeTextChangedListener(_tWatcher);
        }
    }

    private void initSearchInfo()
    {
        TextView tv = _v.findViewById(R.id.TVInfoSearch);
        final Button BPrev = _v.findViewById(R.id.BPrev);
        final Button BNext = _v.findViewById(R.id.BNext);

        tv.setText("Résultats pour: " + _keywords);
        BPrev.setEnabled(false);
        BPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_startIndex > 0) {
                    _startIndex -= 40;
                    if (_startIndex < _nbFound) {
                        BNext.setEnabled(true);
                    }
                    if (_startIndex == 0) {
                        BPrev.setEnabled(false);
                    }
                    searchBookByKeywords();
                }
            }
        });
        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_startIndex < _nbFound) {
                    _startIndex += 40;
                    if (_startIndex >= _nbFound) {
                        BNext.setEnabled(false);
                    }
                    if (_startIndex > 0) {
                        BPrev.setEnabled(true);
                    }
                    searchBookByKeywords();
                }
            }
        });
    }

    private void mainShelf()
    {
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
                    Map<String, String> map = new HashMap<>();

                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                        Log.i("isbn", book.getIsbn());
                        map.put(book.getIsbn(), book.getStatusId());
                    }
                    if (!isbns.isEmpty())
                        getBackBookInShelf(isbns, map);
                    else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.RLShelf), "Votre bibliothèque est vide", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                } else {
                    try {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.RLShelf), "Une erreur est survenue", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
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
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Suggestion> call = bookshelfApi.getSuggestion(MainActivity.token, false);
        call.enqueue(new Callback<Suggestion>() {
            @Override
            public void onResponse(Call<Suggestion> call, Response<Suggestion> response) {
                if (response.isSuccessful()) {
                    Suggestion bookshelf = response.body();
                    _latestSugg = bookshelf.getData().getLatestSuggestions();
                    _overallSugg = bookshelf.getData().getOverallSuggestions();
                    _friendLatestSugg = bookshelf.getData().getFriendsLatestBooks();
                    _friendSugg = bookshelf.getData().getFriendsSuggestions();

                    loadSuggestions(_latestSugg);
                } else {
                    Log.i("error", response.errorBody().toString());
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<Suggestion> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void loadSuggestions(List<String> sugg)
    {
        _modelListBiblio.clear();
        if (sugg == null || sugg.size() == 0) {
            _adapterBiblio.notifyDataSetChanged();
            return;
        }
        Lock l = new ReentrantLock();
        ListIterator<String> it = sugg.listIterator();
        while(it.hasNext()){
            String identifier = it.next();
            if (android.text.TextUtils.isDigitsOnly(identifier)) {
                Log.i("ISBN", identifier);
            } else {
                Log.i("ASIN", identifier);
                ASINBook(identifier, l);
            }
        }
    }

    private void ASINBook(final String asin, final Lock l)
    {
        Log.i("function", "asin passe");
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ASIN> call = bookshelfApi.searchFromASIN(MainActivity.token, asin);
        call.enqueue(new Callback<ASIN>() {
            @Override
            public void onResponse(Call<ASIN> call, Response<ASIN> response) {
                if (response.isSuccessful()) {
                    ASIN bookshelf = response.body();
                    List<com.eip.utilities.model.ASIN.Data> data = bookshelf.getData();
                    ListIterator<com.eip.utilities.model.ASIN.Data> it = data.listIterator();
                    while (it.hasNext()) {
                        com.eip.utilities.model.ASIN.Data asinData = it.next();
                        String title = asinData.getTitle();
                        String picUrl = asinData.getPicUrl();
                        l.lock();
                        _modelListBiblio.add(new BiblioAdapter(title, picUrl, null));
                        _adapterBiblio.notifyDataSetChanged();
                        l.unlock();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ASIN> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
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
                    Map<String, String> map = new HashMap<>();

                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                        map.put(book.getIsbn(), book.getStatusId());
                    }
                    if (!isbns.isEmpty())
                        getBackBookInShelf(isbns, map);
                    else {
                        Snackbar snackbar = Snackbar.make(_v, "Votre liste de souhaits est vide", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
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
        _gvBiblio = _v.findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(_v, _modelListBiblio);
        _gvBiblio.setAdapter(_adapterBiblio);
        _modelListBiblio.clear();

        _gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isbn = ((TextView) view.findViewById(R.id.TVISBN)).getText().toString();
                if (!isbn.equals("")) {
                    Bundle b = new Bundle();
                    b.putString("isbn", isbn);
                    Intent in = new Intent(getActivity(), InfoBook.class);
                    in.putExtra("book", b);
                    startActivity(in);
                } else {
                    Snackbar snackbar = Snackbar.make(_v, "Aucune information supplémentaire disponible", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void searchBookInShelf(final String typeSearch, final String content)
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Cursor cursor = _req.readFromSearch(typeSearch, content, _status);
                while(cursor.moveToNext()) {
                    String isbn = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_ISBN));
                    String title = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_TITLE));
                    String pic = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_PIC));
                    _modelListBiblio.add(new BiblioAdapter(title, pic, isbn));
                }
                cursor.close();
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

    private void getBackBookInShelf(final ArrayList<String> isbns, final Map<String, String> map)
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if (!_status.equals("-1")) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (!entry.getValue().equals(_status)) {
                            isbns.remove(entry.getKey());
                        }
                    }
                }

                if (isbns.size() > 0) {
                    Cursor cursor = _req.readPrimaryInfo(isbns, _status);
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
                        String genre = null;
                        String authors = null;
                        if (vi.getAuthors() != null) {
                            authors = TextUtils.join(", ", vi.getAuthors());
                        }
                        if (vi.getCategories() != null) {
                            genre = TextUtils.join(", ", vi.getCategories());
                        }
                        _modelListBiblio.add(new BiblioAdapter(vi.getTitle(), img, isbn));
                        if (_FriendId == null) {
                            _req.writePrimaryInfo(vi.getTitle(), img, isbn, authors, genre, map.get(isbn));
                        }
                    }
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
        Call<Books> call = gbi.searchByIsbn("isbn:" + isbn, "0", "1");
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

    public void searchBookByKeywords()
    {
        MainActivity.startLoading();

        Thread t = new Thread(new Runnable() {
            public void run() {
                GoogleBooksApi gbi = new Retrofit.Builder()
                        .baseUrl(GoogleBooksApi.APIPath)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(GoogleBooksApi.class);
                Call<Books> call = null;
                try {
                    call = gbi.searchByIsbn(URLEncoder.encode(_keywords, "UTF-8"), String.valueOf(_startIndex), "40");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                VolumeInfo vi = null;

                try {
                    Books b = call.execute().body();
                    _nbFound = b.getTotalItems();
                    if (b.getTotalItems() > 0) {
                        List items = b.getItems();
                        _modelListBiblio.clear();
                        for (int i = 0; i < items.size(); i++) {
                            vi = ((Item)items.get(i)).getVolumeInfo();
                            List identifier = vi.getIndustryIdentifiers();
                            if (identifier != null) {
                                String isbn = null;
                                for (int j = 0; j < identifier.size(); j++) {
                                    if (((IndustryIdentifier)identifier.get(j)).getType().equals("ISBN_13")) {
                                        isbn = ((IndustryIdentifier)identifier.get(j)).getIdentifier();
                                    }
                                }
                                if (isbn == null) {
                                    continue;
                                }
                                String img;
                                if (vi.getImageLinks() == null || vi.getImageLinks().getThumbnail() == null) {
                                    img = "https://puu.sh/wm9pR/adf0d3f814.jpg";
                                } else {
                                    img = vi.getImageLinks().getThumbnail();
                                }
                                _modelListBiblio.add(new BiblioAdapter(vi.getTitle(), img, isbn));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _adapterBiblio.notifyDataSetChanged();
                        TextView tv = _v.findViewById(R.id.TVSubInfoSearch);
                        tv.setText(String.valueOf(_nbFound) + " résultats - page " + String.valueOf(_startIndex / 40 + 1) + " / " + String.valueOf((int)Math.ceil((float)_nbFound / 40.0f)));
                        MainActivity.stopLoading();
                    }
                });
            }
        });
        t.start();
    }

    private void getFriendWishList() {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<WishList> call = bookshelfApi.getFriendWishList(MainActivity.token,  _FriendId);
        call.enqueue(new Callback<WishList>() {
            @Override
            public void onResponse(Call<WishList> call, Response<WishList> response) {
                _modelListBiblio.clear();
                if (response.isSuccessful()) {
                    List<Datum> list = response.body().getData();

                    ListIterator<Datum> it = list.listIterator();
                    ArrayList<String> isbns = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();

                    while(it.hasNext()){
                        Datum book = it.next();
                        isbns.add(book.getIsbn());
                        map.put(book.getIsbn(), "0");
                    }
                    if (!isbns.isEmpty())
                        getBackBookInShelf(isbns, map);
                    else {
                        Snackbar snackbar = Snackbar.make(_v, "Votre amis n'a pas de livre dans sa liste", Snackbar.LENGTH_LONG);
                        snackbar.show();
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
            public void onFailure(Call<WishList> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }
}
