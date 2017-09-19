package com.natanael.mymovielist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.natanael.mymovielist.R;
import com.natanael.mymovielist.model.MovieDetails;
import com.natanael.mymovielist.model.MoviesList;
import com.natanael.mymovielist.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<MovieDetails> mMovieDetailsList;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(MovieDetails clickedMovie);
    }

    public MovieAdapter( ListItemClickListener clickListener){
        mOnClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForListItem, viewGroup, false);

        return new MovieViewHolder(view);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView moviePosterImageView;
        final TextView movieTitleTextView;

        final Context mContext;

        private MovieViewHolder(View itemView) {
            super(itemView);

            moviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            movieTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);

            mContext = itemView.getContext();
        }

        private void bind(MovieDetails details){
            String posterPath = details.getPoster_path();
            String posterUrl = NetworkUtils.MOVIE_POSTER_URL + posterPath;
            Picasso.with(mContext)
                    .load(posterUrl)
                    .placeholder(R.mipmap.loading)
                    .into(moviePosterImageView);
            //moviePosterImageView.setImageResource(details.getMoviePosterId());
            movieTitleTextView.setText(details.getTitle());
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovieDetailsList.get(clickedPosition));
        }
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (mMovieDetailsList != null){
            MovieDetails details = mMovieDetailsList.get(position);
            holder.bind(details);
        }

    }

    @Override
    public int getItemCount() {
        if (mMovieDetailsList != null){
            return mMovieDetailsList.size();
        }
        return 0;
    }

    public void setDataSource(MoviesList allMoviesList){
        if (allMoviesList != null) {
            mMovieDetailsList = allMoviesList.getResults();
        }
    }
}
