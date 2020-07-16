package com.raulomana.movies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raulomana.movies.model.Movie;
import com.raulomana.movies.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.MovieDetailHolder> {
    @NonNull
    private Movie movie;
    @NonNull
    private String userFriendlyReleaseDate;

    interface OnMovieClickListener {
        void onMovieClick(@NonNull Movie movie);
    }

    public MovieDetailAdapter(@NonNull Movie movie) {
        this.movie = movie;
        SimpleDateFormat releaseDateInputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat releaseDateOutputFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        this.userFriendlyReleaseDate = DateUtils.parseThenFormat(movie.getReleaseDate(), releaseDateInputFormat, releaseDateOutputFormat);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return R.layout.detail_item_header;
        }
        return R.layout.detail_item_description;
    }

    @NonNull
    @Override
    public MovieDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return new MovieDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDetailHolder holder, int position) {
        if(position == 0) {
            holder.bindHeader(movie);
        } else {
            holder.bindDescription(movie.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class MovieDetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @NonNull
        private ImageView image;
        @NonNull
        private TextView releaseDate;
        @NonNull
        private TextView duration;
        @NonNull
        private TextView rating;

        @NonNull
        private TextView description;

        public MovieDetailHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.detail_item_image);
            releaseDate = itemView.findViewById(R.id.detail_item_release_date);
            duration = itemView.findViewById(R.id.detail_item_duration);
            rating = itemView.findViewById(R.id.detail_item_rating);

            description = itemView.findViewById(R.id.detail_item_description);

            itemView.setOnClickListener(this);
        }

        public void bindHeader(@NonNull Movie movie) {
            if(BuildConfig.DEBUG) {
                Picasso.get().setIndicatorsEnabled(true);
            }

            Picasso.get()
                    .load(movie.getImage())
                    .placeholder(R.drawable.place_holder)
                    .into(image);

            releaseDate.setText(userFriendlyReleaseDate);
            rating.setText(rating.getResources().getString(R.string.detail_rating, movie.getRating()));
            if(movie.getRuntime() != null) {
                duration.setText(duration.getResources().getString(R.string.detail_duration, movie.getRuntime()));
            }
        }

        public void bindDescription(@NonNull String description) {
            this.description.setText(description);
        }

        @Override
        public void onClick(View v) {
//            if(listener != null) {
//                listener.onMovieClick(movies.get(getAdapterPosition()));
//            }
        }
    }


}
