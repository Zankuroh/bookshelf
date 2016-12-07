package com.example.maxime.bookshelf;

import com.loopj.android.http.*;

/**
 * Created by joly_i on 27/01/16.
 */
public class RequestAsync
{
    private static AsyncHttpClient client = new AsyncHttpClient();

    static public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl)
    {
        String baseUrl = "http://bookshelf.caolin.ovh:8000/api/";
        return baseUrl + relativeUrl;
    }
}
