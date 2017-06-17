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
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.VolumeInfo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jolyn on 08/12/2016.
 */

public class Search extends Fragment implements View.OnClickListener
{
    private View _v;
    private VolumeInfo _vi;

    public Search()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.start_search, container, false);

        _v.findViewById(R.id.okSearch).setOnClickListener(this);
        _v.findViewById(R.id.searchApn).setOnClickListener(this);
        return _v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.okSearch:
                searchByISBN();
                break;
            case R.id.searchApn:
                searchByCB();
                break;
            default:
                break;
        }
    }

    private void searchByCB()
    {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.forSupportFragment(Search.this)
        .setCaptureActivity(DecodeBarcode.class)
        .setOrientationLocked(false)
        .setPrompt("Scanner le code ISBN au dos du livre")
        .setBeepEnabled(true)
        .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d("scan result", scanResult.getContents());
            Bundle b = new Bundle();
            b.putString("isbn", scanResult.getContents());
            Intent in = new Intent(getActivity(), InfoBook.class);
            in.putExtra("book", b);
            in.putExtra("shelf", MainActivity.shelfType.SEARCH);
            startActivity(in);
        }
    }


    private void searchByISBN()
    {
        String isbn = ((EditText)_v.findViewById(R.id.searchField)).getText().toString();
        Bundle b = new Bundle();
        b.putString("isbn", isbn);
        Intent in = new Intent(getActivity(), InfoBook.class);
        in.putExtra("book", b);
        in.putExtra("shelf", MainActivity.shelfType.SEARCH);
        startActivity(in);
    }


}
