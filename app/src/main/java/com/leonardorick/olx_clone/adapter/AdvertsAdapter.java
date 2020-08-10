package com.leonardorick.olx_clone.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardorick.olx_clone.R;
import com.leonardorick.olx_clone.model.Advert;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdvertsAdapter extends RecyclerView.Adapter<AdvertsAdapter.AdvertsViewHolder> {
    private List<Advert> adverts;

    public AdvertsAdapter(List<Advert> adverts) {
        this.adverts = adverts;
    }

    @NonNull
    @Override
    public AdvertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advert_list_item, parent, false);
        return new AdvertsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertsViewHolder holder, int position) {
        Advert advert = adverts.get(position);

        holder.desc.setText(advert.getDesc());

        holder.value.setText(advert.getValue());
        Picasso.get()
                .load(Uri.parse(advert.getImages().get(0)))
                .into(holder.mainAdImage);
    }

    @Override
    public int getItemCount() {
        return adverts.size();
    }

    public class AdvertsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mainAdImage;
        private TextView desc;
        private TextView value;

        public AdvertsViewHolder(@NonNull View itemView) {
            super(itemView);
            mainAdImage = itemView.findViewById(R.id.adListMainImage);
            desc = itemView.findViewById(R.id.adDescOnList);
            value = itemView.findViewById(R.id.adValueOnList);
        }
    }
}
