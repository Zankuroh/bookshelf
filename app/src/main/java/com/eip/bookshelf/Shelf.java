package com.eip.bookshelf;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime on 02/03/2017.
 */

public class Shelf
{
    private List<Book> _infos;
    private MainActivity _co;
    private ArrayList<BiblioAdapter> _modelListBiblio = new ArrayList<>();
    private customAdapterBiblio _adapterBiblio;
    private GridView _gvBiblio;
    public enum shelfType {
        MAINSHELF,
        PROPOSHELF,
        WISHSHELF
    };

    public Shelf(MainActivity context, shelfType type, boolean added)
    {
        _co = context;
        setAdapters();
        hardTest();
        if (type == shelfType.MAINSHELF) {
            mainShelf(added);
        } else if (type == shelfType.PROPOSHELF) {
            propoShelf();
        } else if (type == shelfType.WISHSHELF) {
            whishShelf();
        }
    }

    public void mainShelf(boolean added)
    {
        for (int i = 0; i < _infos.size(); i++) {
            if (!_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") &&
                    !_infos.get(i).getTitre().equals("La damnation de Pythos") &&
                    !_infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre") &&
                    !_infos.get(i).getTitre().equals("Le trône de fer tome 5")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            } else if (_infos.get(i).getTitre().equals("La damnation de Pythos") && added) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            }
        }
    }

    public void propoShelf()
    {
        for (int i = 0; i < _infos.size(); i++) {
            if (_infos.get(i).getTitre().equals("Harry Potter et les reliques de la mort") ||
                    _infos.get(i).getTitre().equals("Le trône de fer tome 5") ||
                    _infos.get(i).getTitre().equals("Percy Jackson - Tome 1 : Le voleur de foudre")) {
                _modelListBiblio.add(new BiblioAdapter(_infos.get(i).getTitre(), _infos.get(i).getImage()));
            }
        }
    }

    public void whishShelf()
    {
        BiblioAdapter ba = new BiblioAdapter("Les souhaits", R.drawable.test_book);
        _modelListBiblio.add(ba);
    }

    private void setAdapters()
    {
        _gvBiblio = (GridView) _co.findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(_co, _modelListBiblio);
        _gvBiblio.setAdapter(_adapterBiblio);
        _gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _co.changeCurrentView(R.id.VFMain, R.id.SVBook, false);
                _co.moreDataBook(((TextView) view.findViewById(R.id.TVAff)).getText().toString(), _infos);
            }
        });
    }

    public void dataUpdated()
    {
        _modelListBiblio.clear();
        _adapterBiblio.notifyDataSetChanged();
    }

    private void hardTest()
    {
        /**
         * ouverture + creation du fichier
         */
        SharedPreferences sp = _co.getSharedPreferences("Bibliothèque", Context.MODE_PRIVATE);
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
