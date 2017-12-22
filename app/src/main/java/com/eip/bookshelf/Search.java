package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jolyn on 08/12/2016.
 */

public class Search extends Fragment implements View.OnClickListener
{
    private View _v;

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
        if (scanResult != null && resultCode == RESULT_OK) {
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
        MainActivity.hideSoftKeyboard(getActivity());
        String keywords = ((EditText)_v.findViewById(R.id.searchField)).getText().toString();
        if (!keywords.equals("")) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Bundle arg = new Bundle();
            arg.putSerializable("type", MainActivity.shelfType.SEARCH);
            arg.putString("keywords", keywords);
            ShelfContainer shelfFrag = new ShelfContainer();
            shelfFrag.setArguments(arg);
            fragmentTransaction.replace(R.id.fragment_container, shelfFrag, "SHELF");
            fragmentTransaction.commit();
        }
    }
}
