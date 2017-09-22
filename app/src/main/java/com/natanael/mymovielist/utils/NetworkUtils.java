package com.natanael.mymovielist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.natanael.mymovielist.BuildConfig;
import com.natanael.mymovielist.model.LoadMovieListCallBack;
import com.natanael.mymovielist.model.MoviesList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class NetworkUtils {

    public static final int ERROR_NO_CONNECTION = 0;
    public static final int ERROR_SYNC_FAILURE = 1;

    public static final int PARAM_SORT_BY_POPULAR = 0;
    public static final int PARAM_SORT_BY_RATE = 1;

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String THE_MOVIE_DB_BASE_URL =
            "http://api.themoviedb.org/3/";
    private static final String MOVIE_DB_SORT_BY_POPULAR = "popular";
    private static final String MOVIE_DB_SORT_BY_RATE = "top_rated";
    private static final String THE_MOVIE_DB_URL_POPULAR =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    private static final String THE_MOVIE_DB_URL_RATE=
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    public static final String MOVIE_POSTER_URL =
            "http://image.tmdb.org/t/p/w500";

    private static final String MOVIE_DB_LANGUAGE_PARAM = "language";
    private static final String MOVIE_DB_PAGE_PARAM = "page";

    private final TheMovieDBApi theMovieDBApi;

    private static NetworkUtils instanceNetworkUtils;

    public NetworkUtils() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(THE_MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        theMovieDBApi = retrofit.create(TheMovieDBApi.class);
    }


    public static NetworkUtils getInstance(){
        if (instanceNetworkUtils == null) {
            instanceNetworkUtils = new NetworkUtils();
        }
        return instanceNetworkUtils;
    }

    public void getMovieDetailsJson(int sortByType, int loadingPage, final LoadMovieListCallBack callback) {
        String deviceLanguage = Locale.getDefault().getLanguage() + "-" +
                Locale.getDefault().getCountry();

        try {
            if(sortByType == PARAM_SORT_BY_POPULAR) {
                theMovieDBApi.getMovieList(MOVIE_DB_SORT_BY_POPULAR, API_KEY, loadingPage, deviceLanguage)
                        .enqueue(new Callback<MoviesList>() {
                            @Override
                            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                                if(response.isSuccessful()) {
                                    callback.onMovieListRefresh(response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<MoviesList> call, Throwable t) {
                                callback.notifyRefreshFailure();
                            }
                        });
            } else {
                theMovieDBApi.getMovieList(MOVIE_DB_SORT_BY_RATE, API_KEY, loadingPage, deviceLanguage)
                        .enqueue(new Callback<MoviesList>() {
                            @Override
                            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                                if(response.isSuccessful()) {
                                    callback.onMovieListRefresh(response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<MoviesList> call, Throwable t) {
                                callback.notifyRefreshFailure();
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static URL getQueryMovieUrl(int sortByType, int loadingPage){

        URL url = null;
        Uri builtUri;

        String deviceLanguage = Locale.getDefault().getLanguage() + "-" +
                Locale.getDefault().getCountry();

        try {
            if(sortByType == PARAM_SORT_BY_POPULAR) {
                builtUri = Uri.parse(THE_MOVIE_DB_URL_POPULAR).buildUpon()
                        .appendQueryParameter(MOVIE_DB_LANGUAGE_PARAM, deviceLanguage)
                        .appendQueryParameter(MOVIE_DB_PAGE_PARAM, String.valueOf(loadingPage))
                        .build();
            } else {
                builtUri = Uri.parse(THE_MOVIE_DB_URL_RATE).buildUpon()
                        .appendQueryParameter(MOVIE_DB_LANGUAGE_PARAM, deviceLanguage)
                        .appendQueryParameter(MOVIE_DB_PAGE_PARAM, String.valueOf(loadingPage))
                        .build();
            }
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /*public static String getMovieDetailsJson(int sortByType, int loadingPage) {
        HttpURLConnection urlConnection;
        String jsonResult = null;
        URL queryUrl = getQueryMovieUrl(sortByType, loadingPage);
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
    }*/

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



    public interface TheMovieDBApi {
        @GET("movie/{sort}")
        Call<MoviesList> getMovieList(
                @Path("sort") String sort_by,
                @Query("api_key") String api_key,
                @Query("page") int page,
                @Query("language") String language);
    }


}
