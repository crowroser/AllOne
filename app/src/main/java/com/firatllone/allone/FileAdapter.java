package com.firatllone.allone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<FileItem> fileList;
    private OnFileClickListener onFileClickListener;

    public interface OnFileClickListener {
        void onFileClick(FileItem fileItem);
    }

    public FileAdapter(List<FileItem> fileList, OnFileClickListener listener) {
        this.fileList = fileList;
        this.onFileClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false); // Özel layout dosyanızı kullanın
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = fileList.get(position);
        holder.textView.setText(fileItem.getName());
        holder.iconImageView.setImageResource(fileItem.getIconResId());
        holder.itemView.setOnClickListener(v -> onFileClickListener.onFileClick(fileItem));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView iconImageView; // ImageView ekleyin

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.fileName);
            iconImageView = itemView.findViewById(R.id.fileIcon); // ImageView için ID'yi güncelleyin
        }
    }
}
