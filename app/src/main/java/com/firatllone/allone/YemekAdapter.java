package com.firatllone.allone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YemekAdapter extends RecyclerView.Adapter<YemekAdapter.YemekViewHolder> {

    private List<Yemek> yemekList;

    public YemekAdapter(List<Yemek> yemekList) {
        this.yemekList = yemekList;
    }

    @NonNull
    @Override
    public YemekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yemek_list, parent, false);
        return new YemekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YemekViewHolder holder, int position) {
        Yemek yemek = yemekList.get(position);
        holder.yemekAdi.setText(yemek.getAdi());

    }

    @Override
    public int getItemCount() {
        return yemekList.size();
    }

    public static class YemekViewHolder extends RecyclerView.ViewHolder {
        TextView yemekAdi;

        public YemekViewHolder(@NonNull View itemView) {
            super(itemView);
            yemekAdi = itemView.findViewById(R.id.yemek_adi);

        }
    }
}
