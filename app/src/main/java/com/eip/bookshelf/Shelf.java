package com.eip.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxime on 02/03/2017.
 */

public class Shelf extends Fragment
{
    private List<Book> _infos;
    private ArrayList<BiblioAdapter> _modelListBiblio = new ArrayList<>();
    private customAdapterBiblio _adapterBiblio;
    private GridView _gvBiblio;
    private View _v;

    public Shelf()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.shelf, container, false);
        MainActivity.shelfType type = (MainActivity.shelfType)getArguments().getSerializable("type");
        hardTest();
        setAdapters();
        if (type == MainActivity.shelfType.MAINSHELF) {
            mainShelf(false);
        } else if (type == MainActivity.shelfType.PROPOSHELF) {
            propoShelf();
        } else if (type == MainActivity.shelfType.WISHSHELF) {
            wishShelf();
        }
        return _v;
    }

    public void mainShelf(boolean added)
    {
        //Todo: Appel à la BDD pour recup la vraie biblio
        for (int i = 0; i < _infos.size(); i++) {
            if (!_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") &&
                    !_infos.get(i).getTitre().equals("La damnation de Pythos") &&
                    !_infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre") &&
                    !_infos.get(i).getTitre().equals("Le trône de fer tome 5") &&
                    !_infos.get(i).getTitre().equals("L' Arbre des Souhaits")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage(), _infos.get(i).getIsbn().toString()));
            } else if (_infos.get(i).getTitre().equals("La damnation de Pythos") && added) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage(), _infos.get(i).getIsbn().toString()));
            }
        }
    }

    public void propoShelf()
    {
        //Todo: Appel à la BDD pour recup les vrais PROPOS
        for (int i = 0; i < _infos.size(); i++) {
            if (_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") ||
                    _infos.get(i).getTitre().equals("Le trône de fer tome 5") ||
                    _infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage(), _infos.get(i).getIsbn().toString()));
            }
        }
    }

    public void wishShelf()
    {
        //Todo: Appel à la BDD pour recup les vrais WISH
        BiblioAdapter ba = new BiblioAdapter("L' Arbre des Souhaits", "http://www.scholastic.ca/hipoint/648/?src=9781443155434.jpg&w=260", "1443155438");
        _modelListBiblio.add(ba);
    }

    private void setAdapters()
    {
        _gvBiblio = (GridView) _v.findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(_v, _modelListBiblio);
        _gvBiblio.setAdapter(_adapterBiblio);
        _gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isbn = ((TextView) view.findViewById(R.id.TVISBN)).getText().toString();
                HashMap<String, String> info = null;
                Bundle b = null;
                for (int i = 0; i < _infos.size(); i++) {
                    if (isbn.equals(_infos.get(i).getIsbn().toString())) {
                        info = new HashMap<String, String>();
                        b = new Bundle();
                        info.put("title", getValueOrDefault(_infos.get(i).getTitre()));
                        info.put("picture", _infos.get(i).getImage());
                        info.put("genre", getValueOrDefault(_infos.get(i).getGenre()));
                        info.put("author", getValueOrDefault(_infos.get(i).getAuteur()));
                        info.put("date", getValueOrDefault(_infos.get(i).getDate()));
                        info.put("note", getValueOrDefault(_infos.get(i).getNote().toString()));
                        info.put("resume", getValueOrDefault(_infos.get(i).getResum()));
                        b.putSerializable("info", info);
                        break;
                    }
                }
                if (info != null) {
                    Intent in = new Intent(getActivity(), InfoBook.class);
                    in.putExtras(b);
                    startActivity(in);
                }
            }
        });
    }

    public String getValueOrDefault(String value)
    {
        return (value != null && value != "") ? value : "N/A";
    }

    private void hardTest()
    {
        /**
         * ouverture + creation du fichier
         */
        SharedPreferences sp = _v.getContext().getSharedPreferences("Bibliothèque", Context.MODE_PRIVATE);
        /**
         *  edition du fichier
         */
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("biblio", "[\n" +
                "{\"titre\":\"Le trône de fer tome 5\",\n" +
                "\"image\":\"https://kbimages1-a.akamaihd.net/58f4c6b5-c790-4716-8dcd-10ffb6065dc0/1200/1200/False/le-trone-de-fer-l-integrale-5-tomes-13-a-15-1.jpg\",\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"08/04/2015\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"Daenerys règne sur une cité de mort, entourée d'ennemis, tandis que Jon Snow doit affronter ses adversaires des deux côtés du Mur.\",\n" +
                "\"note\":4,\n" +
                "\"isbn\":9782290107096},\n" +
                "{\"titre\":\"Le trône de fer tome 1\",\n" +
                "\"image\":\"https://www.images-booknode.com/book_cover/402/full/le-trone-de-fer---l-integrale,-tome-1-401890.jpg\",\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":1\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 2\",\n" +
                "\"image\":\"https://singesdelespace.files.wordpress.com/2012/11/trone-de-fer-lintc3a9grale-2.jpg\",\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":2\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 3\",\n" +
                "\"image\":\"http://images.gibertjoseph.com/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/i/408/9782756408408_1_75.jpg\",\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":3\n" +
                "},\n" +
                "{\"titre\":\"Le trône de fer tome 4\",\n" +
                "\"image\":\"http://www.georgerrmartin.fr/assets/integrales/Integrale4.jpg\",\n" +
                "\"auteur\":\"Georges R. R. Martin\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":4\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter à l'école des sorciers\",\n" +
                "\"image\":\"https://chacuneseschimeres.files.wordpress.com/2016/07/harry-potter-tome-1-harry-potter-a-l-ecole-des-sorciers-383225.jpg?w=636&h=913\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"16 novembre 1998\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"Le jour de ses onze ans, Harry Potter, un orphelin élevé par un oncle et une tante qui le détestent, voit son existence bouleversée. Un géant vient le chercher pour l'emmener à Poudlard, une école de sorcellerie ! Voler en balai, jeter des sorts, combattre les trolls : Harry Potter se révèle un sorcier doué. Mais un mystère entoure sa naissance et l'effroyable V..., le mage dont personne n'ose prononcer le nom.\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":2070518426\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et la Chambre des secrets\",\n" +
                "\"image\":\"https://media.senscritique.com/media/000006436455/source_big/Harry_Potter_et_la_Chambre_des_secrets_Harry_Potter_tome_2.jpg\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":5\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et le Prisonnier d'Azkaban\",\n" +
                "\"image\":\"http://image3.archambault.ca/2/D/A/4/ACH002991198.1381439502.580x580.jpg\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":6\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et la Coupe de feu\",\n" +
                "\"image\":\"http://media.paperblog.fr/i/745/7456211/harry-potter-coupe-feu-j-k-rowling-livre-vs-f-L-3UqSr3.jpeg\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":7\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et l'Ordre du phénix\",\n" +
                "\"image\":\"https://www.club.be/image.action?code=9782070643066&w=800&h=800\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":8\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et le Prince de sang-mêlé\",\n" +
                "\"image\":\"http://www.gallimard-jeunesse.fr/var/storage/images/product/43d/product_9782070643073_244x0.jpg\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":9\n" +
                "},\n" +
                "{\"titre\":\"Harry Potter et les reliques de la mort\",\n" +
                "\"image\":\"http://www.bdfi.info/couvs/f/folioj1479rr11.jpg\",\n" +
                "\"auteur\":\"J. K. Rowling\",\n" +
                "\"date\":\"\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"\",\n" +
                "\"note\":3,\n" +
                "\"isbn\":10\n" +
                "},\n" +
                "{\n" +
                "\"titre\":\"La damnation de Pythos\",\n" +
                "\"image\":\"https://images-na.ssl-images-amazon.com/images/I/51ev9F0yDeL.jpg\",\n" +
                "\"auteur\":\"David Annandale\",\n" +
                "\"date\":\"16 novembre 2015\",\n" +
                "\"genre\":\"Science-Fiction\",\n" +
                "\"resum\":\"A la suite du Massacre du Dropsite d'Isstvan V, une force battue et ensanglantée d'Iron Hands, de Raven Guard et de Salamanders se regroupe sur ce qui ressemble à un monde mort insignifiant. En parant des attaques de toutes part commises par les créatures monstrueuses, ces alliés hargneux trouvent un espoir en la forme de réfugiés humains fuyant cette guerre grandissante et jetés à la dérive dans les courants du warp. Mais alors même que les Space Marines se taillent un sanctuaire pour se réfugier dans les jungles de Pythos, une ombre grandissante se rassemble et se prépare à les consumer tous.\",\n" +
                "\"note\":5,\n" +
                "\"isbn\":9781780302416\n" +
                "},\n" +
                "{\n" +
                "\"titre\":\"L' Arbre des Souhaits\",\n" +
                "\"image\":\"http://images.amazon.com/images/P/1443155438.01.LZZZZZZZ.jpg\",\n" +
                "\"auteur\":\" Kyo Maclear\",\n" +
                "\"date\":\" 1 novembre 2016\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"Charles aimerait tant trouver un arbre de souhaits! Même si son frère et sa soeur ne croient pas qu'une telle chose existe, il saisit son toboggan et part l'aventure. Sa route sera semée d'expériences merveilleuses qui lui feront découvrir que les souhaits se réalisent parfois de façon inattendue.\",\n" +
                "\"note\":4.5,\n" +
                "\"isbn\":1443155438\n" +
                "},\n" +
                "{\n" +
                "\"titre\":\"Percy Jackson - Tome 1 : Le voleur de foudre\",\n" +
                "\"image\":\"https://images-eu.ssl-images-amazon.com/images/I/61q%2BUXymKHL.jpg\",\n" +
                "\"auteur\":\" Rick Riordan\",\n" +
                "\"date\":\" 3 juillet 2013\",\n" +
                "\"genre\":\"Fantastique\",\n" +
                "\"resum\":\"Percy Jackson n’est pas un garçon comme les autres. Ado perturbé, renvoyé de collège en pension, il découvre un jour le secret de sa naissance et de sa différence : son père, qu’il n’a jamais connu, n’est autre que Poséidon, le dieu de la mer dans la mythologie grecque. Placé pour sa protection dans un camp de vacances pour enfants « sangs mêlés » (mi-humains, mi-divins), Percy se voit injustement accusé d’avoir volé l’éclair de Zeus. Afin d’éviter une guerre fratricide entre les dieux de l’Olympe, il va devoir repartir dans le monde des humains, retrouver l’éclair et démasquer le vrai coupable… au péril de sa vie.\",\n" +
                "\"note\":4.5,\n" +
                "\"isbn\":2226249303\n" +
                "}\n" +
                "]");
        editor.apply();
        /**
         * récupération des données enregistrées
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
