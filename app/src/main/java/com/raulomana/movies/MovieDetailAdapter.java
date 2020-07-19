package com.raulomana.movies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Review;
import com.raulomana.movies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.MovieDetailHolder> {
    @NonNull
    private Movie movie;
    @Nullable
    private OnDetailClickListener listener;
    @NonNull
    private SparseIntArray positionToLayout;
    @NonNull
    private SparseArray<Object> positionToModel;

    interface OnDetailClickListener {
        void onFavoriteClick(@NonNull Movie movie);
        void onVideoClick(@NonNull Movie movie, @NonNull Video video);
    }

    public MovieDetailAdapter(@NonNull Movie movie, @Nullable OnDetailClickListener listener) {
        this.movie = movie;
        this.listener = listener;
        positionToLayout = new SparseIntArray();
        positionToModel = new SparseArray<>();
        int currentPosition = 0;
        positionToLayout.put(currentPosition++, R.layout.detail_item_header);
        positionToLayout.put(currentPosition++, R.layout.detail_item_description);
        List<Video> videos = this.movie.getVideos();
        if(!videos.isEmpty()) {
            positionToLayout.put(currentPosition++, R.layout.detail_item_videos_header);
            for(Video item: videos) {
                positionToLayout.put(currentPosition, R.layout.detail_item_video_item);
                positionToModel.put(currentPosition++, item);
            }
        }
        List<Review> reviews = this.movie.getReviews();
        if(!reviews.isEmpty()) {
            positionToLayout.put(currentPosition++, R.layout.detail_item_reviews_header);
            for(Review item: reviews) {
                positionToLayout.put(currentPosition, R.layout.detail_item_review);
                positionToModel.put(currentPosition++, item);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return positionToLayout.get(position);
    }

    @NonNull
    @Override
    public MovieDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return new MovieDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDetailHolder holder, int position) {
        int viewType = positionToLayout.get(position);
        if(R.layout.detail_item_header == viewType) {
            holder.bindHeader(movie);
        } else if(R.layout.detail_item_description == viewType) {
            if(movie.getDescription() != null) {
                holder.bindDescription(movie.getDescription());
            }
        } else if(R.layout.detail_item_videos_header == viewType) {
            // nothing to do, everything is done via xml
        } else if(R.layout.detail_item_video_item == viewType) {
            Object video = positionToModel.get(position);
            if(video instanceof Video) {
                holder.bindVideo((Video) video);
            }
        } else if(R.layout.detail_item_reviews_header == viewType) {
            // nothing to do, everything is done via xml
        } else if(R.layout.detail_item_review == viewType) {
            Object review = positionToModel.get(position);
            if(review instanceof Review) {
                holder.bindReview((Review) review);
            }
        }
    }

    @Override
    public int getItemCount() {
        return positionToLayout.size();
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
        private Button favoriteAction;

        @NonNull
        private TextView description;

        /**
         * Video
         */
        @NonNull
        private View videoContainer;
        @NonNull
        private TextView videoName;

        @NonNull
        private TextView reviewAuthor;
        @NonNull
        private TextView reviewContent;

        private MovieDetailHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.detail_item_image);
            releaseDate = itemView.findViewById(R.id.detail_item_release_date);
            duration = itemView.findViewById(R.id.detail_item_duration);
            rating = itemView.findViewById(R.id.detail_item_rating);
            favoriteAction = itemView.findViewById(R.id.detail_item_favorite_action);

            description = itemView.findViewById(R.id.detail_item_description);

            videoContainer = itemView.findViewById(R.id.detail_item_video_item_container);
            videoName = itemView.findViewById(R.id.detail_item_video_item_name);

            reviewAuthor = itemView.findViewById(R.id.detail_item_review_author);
            reviewContent = itemView.findViewById(R.id.detail_item_review_content);

            itemView.setOnClickListener(this);
        }

        private void bindHeader(@NonNull Movie movie) {
            if(BuildConfig.DEBUG) {
                Picasso.get().setIndicatorsEnabled(true);
            }

            Picasso.get()
                    .load(movie.getImage())
                    .placeholder(R.drawable.place_holder)
                    .into(image);

            releaseDate.setText(movie.getDisplayReleaseDate());
            rating.setText(rating.getResources().getString(R.string.detail_rating, movie.getRating()));
            if(movie.getRuntime() != null) {
                duration.setText(duration.getResources().getString(R.string.detail_duration, movie.getRuntime()));
            }

            favoriteAction.setOnClickListener(this);
            favoriteAction.setSelected(movie.isFavorite());
            if(movie.isFavorite()) {
                favoriteAction.setText(R.string.detail_item_un_favorite_action);
            } else {
                favoriteAction.setText(R.string.detail_item_favorite_action);
            }
        }

        private void bindDescription(@NonNull String description) {
            this.description.setText(description);
        }

        private void bindVideo(@NonNull final Video video) {
            this.videoName.setText(video.getName());
            this.videoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onVideoClick(movie, video);
                    }
                }
            });
        }

        private void bindReview(@NonNull final Review review) {
            reviewAuthor.setText(review.getAuthor());
            reviewContent.setText(review.getContent());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(R.id.detail_item_favorite_action == id) {
                if(listener != null) {
                    listener.onFavoriteClick(movie);
                    notifyItemChanged(getAdapterPosition());
                }
            }
        }
    }


}
