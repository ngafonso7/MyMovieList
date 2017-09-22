package com.natanael.mymovielist.model;

/**
 * Created by natanael.afonso on 22/09/2017.
 */

public interface LoadMovieListCallBack {

    void onMovieListRefresh(MoviesList movieList);

    void notifyRefreshFailure();
}
