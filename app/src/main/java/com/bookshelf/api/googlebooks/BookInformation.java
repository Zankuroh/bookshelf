package com.bookshelf.api.googlebooks;

import com.example.maxime.bookshelf.RequestAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jolyn on 26/11/2016.
 */

public class BookInformation {

    public void getInformation(String isbn) {
        RequestParams params = new RequestParams();
        params.put("isbn", isbn);
        RequestAsync.get("volume", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ObjectMapper objectMapper = new ObjectMapper();
                /*try {
                    List<Marks> infos = objectMapper.readValue(response.getJSONArray("notes").toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BookInfo.class));
                    Integer i = 0;
                    while (i < infos.size()) {
                        infos.get(i).affMarks(_almm, _adaMM, _s);
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

        });
    }
}
