package com.raulomana.movies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raulomana.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesHolder> {
    @NonNull
    private List<Movie> movies;
    @Nullable
    private OnMovieClickListener listener;

    interface OnMovieClickListener {
        void onMovieClick(@NonNull Movie movie);
    }

    public MoviesAdapter(@NonNull List<Movie> movies, @Nullable OnMovieClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.movie_item;
    }

    @NonNull
    @Override
    public MoviesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return new MoviesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MoviesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private View descriptionContainer;
        private TextView rating;
        private TextView popularity;

        public MoviesHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.movie_item_image);
            descriptionContainer = itemView.findViewById(R.id.movie_description_container);
            rating = itemView.findViewById(R.id.movie_item_rating);
            popularity = itemView.findViewById(R.id.movie_item_popularity);
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Movie movie) {
            if(BuildConfig.DEBUG) {
                Picasso.get().setIndicatorsEnabled(true);
            }
            Picasso.get()
                    .load(movie.getImage())
                    .placeholder(R.drawable.place_holder)
                    .into(image);

            if(BuildConfig.DEBUG) {
                rating.setText("Rating: " + movie.getRating());
                popularity.setText("Popularity: " + movie.getPopularity());
                descriptionContainer.setVisibility(View.VISIBLE);
            } else {
                descriptionContainer.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if(listener != null) {
                listener.onMovieClick(movies.get(getAdapterPosition()));
            }
        }
    }


}
