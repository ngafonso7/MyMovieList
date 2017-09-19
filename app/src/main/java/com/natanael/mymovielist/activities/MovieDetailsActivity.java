package com.natanael.mymovielist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.natanael.mymovielist.R;
import com.natanael.mymovielist.model.MovieDetails;
import com.natanael.mymovielist.utils.NetworkUtils;
import com.natanael.mymovielist.utils.Utils;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView movieBannerImageView;
    private RatingBar movieRateRatingBar;
    private TextView movieOriginalTitleTextView;
    private TextView movieReleaseDateTextView;
    private TextView movieSynopsis;

    private static final String KEY_MOVIE_DETAILS = "keyMovieDetails";

    private MovieDetails movieDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);

        movieBannerImageView = (ImageView) findViewById(R.id.iv_movie_banner);
        movieRateRatingBar = (RatingBar) findViewById(R.id.rb_movie_rating);
        movieOriginalTitleTextView = (TextView) findViewById(R.id.tv_original_movie_title);
        movieReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        movieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(KEY_MOVIE_DETAILS)) {
                movieDetails = savedInstanceState.getParcelable(KEY_MOVIE_DETAILS);
                loadMovieDetails(movieDetails);
            }
        } else {
            Intent movieDetailsIntent = getIntent();
            movieDetails = movieDetailsIntent.getExtras().getParcelable(Utils.KEY_EXTRA_MOVIE_DETAILS);
            loadMovieDetails(movieDetails);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MOVIE_DETAILS, movieDetails);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieDetails(MovieDetails details) {
        String posterPath = details.getBackdrop_path();
        String posterUrl = NetworkUtils.MOVIE_POSTER_URL + posterPath;
        Picasso.with(this)
                .load(posterUrl)
                .placeholder(R.mipmap.loading)
                .into(movieBannerImageView);

        setTitle(details.getTitle());

        movieRateRatingBar.setRating(details.getVote_average() / 2);
        movieOriginalTitleTextView.setText(details.getOriginal_title());
        movieReleaseDateTextView.setText(details.getRelease_date());
        movieSynopsis.setText(details.getOverview());
    }
}
