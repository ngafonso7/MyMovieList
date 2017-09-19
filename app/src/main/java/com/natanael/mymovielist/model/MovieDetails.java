package com.natanael.mymovielist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetails implements Parcelable {

    //private int vote_count;
    //private int movieId;
    //private boolean video;
    private final float vote_average;
    private final String title;
    //private float popularity;
    private final String poster_path;
    //private String original_language;
    private final String original_title;
    //private int[] genre_ids;
    private final String backdrop_path;
    //private boolean adult;
    private final String overview;
    private final String release_date;

    private MovieDetails(Parcel in) {
        title = in.readString();
        original_title = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
        poster_path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(original_title);
        parcel.writeString(backdrop_path);
        parcel.writeString(overview);
        parcel.writeFloat(vote_average);
        parcel.writeString(release_date);
        parcel.writeString(poster_path);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR;

    static {
        CREATOR = new Creator<MovieDetails>() {
            @Override
            public MovieDetails createFromParcel(Parcel parcel) {
                return new MovieDetails(parcel);
            }

            @Override
            public MovieDetails[] newArray(int i) {
                return new MovieDetails[i];
            }
        };
    }


    public float getVote_average() {
        return vote_average;
    }


    public String getTitle() {
        return title;
    }


    public String getPoster_path() {
        return poster_path;
    }


    public String getOriginal_title() {
        return original_title;
    }


    public String getBackdrop_path() {
        return backdrop_path;
    }


    public String getOverview() {
        return overview;
    }


    public String getRelease_date() {
        return release_date;
    }

}
