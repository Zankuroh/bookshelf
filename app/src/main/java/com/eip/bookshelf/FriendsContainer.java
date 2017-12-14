package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maxime on 25/04/2017.
 */

public class FriendsContainer extends Fragment implements View.OnClickListener
{
    private View _v;
    private ArrayList<AmisAdapter> _modelListFriend = new ArrayList<>();

    public FriendsContainer()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.amis_container, container, false);
        _v.findViewById(R.id.okSearchFriend).setOnClickListener(this);

        setAdapter();
        pushfakeFriends();
        return _v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.okSearchFriend:
                searchFriend();
                break;
            default:
                break;
        }
    }

    private void searchFriend() {
        Snackbar snackbar = Snackbar.make(_v, "Erreur utilisateur non trouvé", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void pushfakeFriends() {
        _modelListFriend.add(new AmisAdapter("Jean Jacque", "12"));
        _modelListFriend.add(new AmisAdapter("Jean Claude", "13"));

    }

    private void setAdapter()
    {
        GridView gvFriend = _v.findViewById(R.id.GVFriend);
        customAdapterAmis adapterFriend = new customAdapterAmis(_v, _modelListFriend);
        gvFriend.setAdapter(adapterFriend);
        _modelListFriend.clear();

        gvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idf = ((TextView) view.findViewById(R.id.TVIDAmis)).getText().toString();
                Bundle b = new Bundle();
                b.putString("idFriend", idf);
                b.putBoolean("isFriend", true);
                Profil profilFrag = new Profil();
                profilFrag.setArguments(b);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, profilFrag);
                fragmentTransaction.commit();
            }
        });
    }
}
