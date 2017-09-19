package com.natanael.mymovielist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.natanael.mymovielist.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

public class NetworkUtils {

    public static final int ERROR_NO_CONNECTION = 0;
    public static final int ERROR_SYNC_FAILURE = 1;

    public static final int PARAM_SORT_BY_POPULAR = 0;
    public static final int PARAM_SORT_BY_RATE = 1;

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String THE_MOVIE_DB_URL_POPULAR =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    private static final String THE_MOVIE_DB_URL_RATE=
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    public static final String MOVIE_POSTER_URL =
            "http://image.tmdb.org/t/p/w500";

    private static final String MOVIE_DB_LANGUAGE_PARAM = "language";

    private static URL getQueryMovieUrl(int sortByType){

        URL url = null;
        Uri builtUri;

        String deviceLanguage = Locale.getDefault().getLanguage() + "-" +
                Locale.getDefault().getCountry();

        try {
            if(sortByType == PARAM_SORT_BY_POPULAR) {
                builtUri = Uri.parse(THE_MOVIE_DB_URL_POPULAR).buildUpon()
                        .appendQueryParameter(MOVIE_DB_LANGUAGE_PARAM, deviceLanguage)
                        .build();
            } else {
                builtUri = Uri.parse(THE_MOVIE_DB_URL_RATE).buildUpon()
                        .appendQueryParameter(MOVIE_DB_LANGUAGE_PARAM, deviceLanguage)
                        .build();
            }
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMovieDetailsJson(int sortByType) {
        HttpURLConnection urlConnection;
        String jsonResult = null;
        URL queryUrl = getQueryMovieUrl(sortByType);
        if (queryUrl != null) {
            try {
                urlConnection = (HttpURLConnection) queryUrl.openConnection();
                int connectionResultCode = urlConnection.getResponseCode();
                if (connectionResultCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = urlConnection.getInputStream();

                    Scanner scanner = new Scanner(input);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        jsonResult = scanner.next();
                        urlConnection.disconnect();
                    }
                } else {
                    return "error:" + ERROR_SYNC_FAILURE;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonResult;
    }

    public static boolean isInternetConnected(Context context) {
        boolean isOnline = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if(netInfo != null) {
                if(netInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                        netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isOnline = true;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return isOnline;
    }

}
