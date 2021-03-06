package com.defaultapps.moviebase.ui.person.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.defaultapps.moviebase.R;
import com.defaultapps.moviebase.data.models.responses.person.Cast;
import com.defaultapps.moviebase.di.ActivityContext;
import com.defaultapps.moviebase.di.scope.PerFragment;
import com.defaultapps.moviebase.ui.person.CastCrewViewHolder;
import com.defaultapps.moviebase.utils.AppConstants;
import com.defaultapps.moviebase.utils.listener.OnMovieClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerFragment
public class CreditsCastAdapter extends RecyclerView.Adapter<CastCrewViewHolder> {

    private final Context context;
    private List<Cast> castCredits;
    private OnMovieClickListener listener;

    @Inject
    CreditsCastAdapter(@ActivityContext Context context) {
        this.context = context;
        castCredits = new ArrayList<>();
    }

    @Override
    public CastCrewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CastCrewViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cast_crew, parent, false));
    }

    @Override
    public void onBindViewHolder(CastCrewViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        Cast cast = castCredits.get(adapterPosition);
        holder.personJob.setText(cast.getCharacter());
        holder.itemView.setOnClickListener(view -> listener.onMovieClick(cast.getId()));
        Picasso
                .with(context)
                .load(AppConstants.POSTER_BASE_URL + cast.getPosterPath())
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return castCredits.size();
    }

    public void setData(List<Cast> castCredits) {
        this.castCredits.clear();
        this.castCredits.addAll(castCredits);
        notifyDataSetChanged();
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

}
