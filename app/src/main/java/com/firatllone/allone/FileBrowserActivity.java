package com.firatllone.allone;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivity extends AppCompatActivity implements FileAdapter.OnFileClickListener {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<FileItem> fileList;
    private StorageReference currentStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(fileList, this);
        recyclerView.setAdapter(fileAdapter);

        currentStorageRef = FirebaseStorage.getInstance().getReference(); // Başlangıç referansı
        loadFilesFromStorage(); // İlk yükleme
    }

    private void loadFilesFromStorage() {
        currentStorageRef.listAll().addOnSuccessListener(listResult -> {
            fileList.clear(); // Mevcut dosyaları temizle

            // Klasörleri ekle
            for (StorageReference folderRef : listResult.getPrefixes()) {
                fileList.add(new FileItem(folderRef.getName(), true, R.drawable.ic_folder)); // Klasör resmi
            }

            // Dosyaları ekle
            for (StorageReference fileRef : listResult.getItems()) {
                // Tüm dosyalar için varsayılan dosya simgesi kullanın
                fileList.add(new FileItem(fileRef.getName(), false, R.drawable.ic_file)); // Varsayılan dosya resmi
            }

            fileAdapter.notifyDataSetChanged(); // RecyclerView'u güncelle
        }).addOnFailureListener(e -> {
            Log.e("Hata", "Dosyalar yüklenirken hata: " + e.getMessage());
            Toast.makeText(this, "Dosyalar yüklenirken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onFileClick(FileItem fileItem) {
        if (fileItem.isDirectory()) {
            Toast.makeText(this, "Klasör açılıyor: " + fileItem.getName(), Toast.LENGTH_SHORT).show();
            // Yeni bir referans ayarla ve dosyaları yükle
            currentStorageRef = currentStorageRef.child(fileItem.getName());
            loadFilesFromStorage(); // Klasör içeriğini yükle
        } else {
            Toast.makeText(this, "Dosya açılıyor: " + fileItem.getName(), Toast.LENGTH_SHORT).show();
            // Dosyayı açmak için gerekli işlemleri yapabilirsiniz
        }
    }
}
