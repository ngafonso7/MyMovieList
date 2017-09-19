package com.natanael.mymovielist.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.natanael.mymovielist.R;
import com.natanael.mymovielist.adapter.MovieAdapter;
import com.natanael.mymovielist.model.MovieDetails;
import com.natanael.mymovielist.model.MoviesList;
import com.natanael.mymovielist.utils.NetworkUtils;
import com.natanael.mymovielist.utils.Utils;

public class MainMovieListActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private static final int numberOfColums = 2;
    private static final int numberOfColums_land = 3;

    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingMoviesProgressBar;
    private TextView mErrorMessageTextView;
    private SwipeRefreshLayout mMovieListSwipeRefresh;

    private MoviesList allMoviesList;

    private static int selectedSortByParam = NetworkUtils.PARAM_SORT_BY_POPULAR;

    private static final String KEY_SELECTED_SORT_BY_PARAM = "selectedSortByParam";
    private static final String KEY_MOVIE_LIST = "movieList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_movie_list_layout);

        mLoadingMoviesProgressBar = (ProgressBar) findViewById(R.id.pb_loading_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager layoutManager = getLayoutManager();

        RecyclerView mMovieListView = (RecyclerView) findViewById(R.id.rc_movie_list);
        mMovieListView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(this);
        mMovieListView.setAdapter(mMovieAdapter);

        if(savedInstanceState == null) {
            refreshMovies(NetworkUtils.PARAM_SORT_BY_POPULAR);
        } else {
            if(savedInstanceState.containsKey(KEY_MOVIE_LIST)) {
                String movieListJson = savedInstanceState.getString(KEY_MOVIE_LIST);
                Gson gson = new Gson();
                allMoviesList = gson.fromJson(movieListJson, MoviesList.class);
                mMovieAdapter.setDataSource(allMoviesList);
                mMovieAdapter.notifyDataSetChanged();
            }
            if(savedInstanceState.containsKey(KEY_SELECTED_SORT_BY_PARAM)) {
                selectedSortByParam = savedInstanceState.getInt(KEY_SELECTED_SORT_BY_PARAM);
            }
        }

        mMovieListSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.sr_movie_list);
        mMovieListSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (selectedSortByParam == NetworkUtils.PARAM_SORT_BY_POPULAR) {
                    refreshMovies(NetworkUtils.PARAM_SORT_BY_POPULAR);
                } else {
                    refreshMovies(NetworkUtils.PARAM_SORT_BY_RATE);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_SORT_BY_PARAM, selectedSortByParam);
        Gson gson = new Gson();
        String movieListJson = gson.toJson(allMoviesList);
        outState.putString(KEY_MOVIE_LIST, movieListJson);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu sortByMenu) {
        getMenuInflater().inflate(R.menu.menu_main_movie_list_activity, sortByMenu);
        if (selectedSortByParam == NetworkUtils.PARAM_SORT_BY_POPULAR) {
            sortByMenu.findItem(R.id.action_sort_by_popular).setChecked(true);
        } else {
            sortByMenu.findItem(R.id.action_sort_by_rate).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                item.setChecked(true);
                refreshMovies(NetworkUtils.PARAM_SORT_BY_POPULAR);
                selectedSortByParam = NetworkUtils.PARAM_SORT_BY_POPULAR;
                break;
            case R.id.action_sort_by_rate:
                item.setChecked(true);
                refreshMovies(NetworkUtils.PARAM_SORT_BY_RATE);
                selectedSortByParam = NetworkUtils.PARAM_SORT_BY_RATE;
                break;
        }
        return true;
    }

    @Override
    public void onListItemClick(MovieDetails clickedMovie) {
        Intent movieDetailsIntent = new Intent(MainMovieListActivity.this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(Utils.KEY_EXTRA_MOVIE_DETAILS, clickedMovie);
        startActivity(movieDetailsIntent);
    }

    private class FetchMoviesTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            mLoadingMoviesProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... sort_by_type) {
            return NetworkUtils.getMovieDetailsJson(sort_by_type[0]);
        }

        @Override
        protected void onPostExecute(String queryMoviesJson) {

            mLoadingMoviesProgressBar.setVisibility(View.INVISIBLE);

            allMoviesList = null;
            if (queryMoviesJson != null) {
                if (!queryMoviesJson.startsWith("error")) {
                    Gson gson = new Gson();
                    allMoviesList = gson.fromJson(queryMoviesJson, MoviesList.class);
                    mErrorMessageTextView.setVisibility(View.INVISIBLE);
                } else {
                    showErrorMessage(NetworkUtils.ERROR_SYNC_FAILURE);
                }
            }
            mMovieListSwipeRefresh.setRefreshing(false);

            mMovieAdapter.setDataSource(allMoviesList);
            mMovieAdapter.notifyDataSetChanged();

        }
    }

    private void refreshMovies(int sortByParam) {
        if (NetworkUtils.isInternetConnected(this)) {
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sortByParam);
        } else {
            showErrorMessage(NetworkUtils.ERROR_NO_CONNECTION);
        }
    }

    private void showErrorMessage(int error_type) {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        switch (error_type) {
            case NetworkUtils.ERROR_NO_CONNECTION:
                mErrorMessageTextView.setText(R.string.error_message_no_connection);
                break;
            case NetworkUtils.ERROR_SYNC_FAILURE:
                mErrorMessageTextView.setText(R.string.error_message_offline_server);
                break;
        }
    }

    private GridLayoutManager getLayoutManager(){

        GridLayoutManager layoutManager;

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, numberOfColums);
        } else {
            layoutManager = new GridLayoutManager(this, numberOfColums_land);
        }

        return layoutManager;
    }


}
