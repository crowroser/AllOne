package com.firatllone.allone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private EditText usernameEditText;
    private Spinner fakultespinner, bolumspinner, sinifspinner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<String> fakulteList = new ArrayList<>();
    private List<String> bolumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        usernameEditText = findViewById(R.id.usernameEditText);
        fakultespinner = findViewById(R.id.fakultespinner);
        bolumspinner = findViewById(R.id.bolumspinner);
        sinifspinner = findViewById(R.id.sinifspinner);

        // Fakülteleri ve bölümleri yükle
        loadFakulteler();

        // Firestore'dan mevcut kullanıcı bilgilerini al
        if (currentUser != null) {
            Toast.makeText(this, "Profil bilgileri yükleniyor...", Toast.LENGTH_SHORT).show();

            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {

                            if (document.exists()) {
                                String username = document.getString("username");
                                String fakulte = document.getString("fakulte");
                                String bolum = document.getString("bolum");
                                String sinif = document.getString("sinif");

                                usernameEditText.setText(username);

                                // Fakülteleri yükle ve ardından spinner değerini ayarlayın
                                setSpinnerValue(fakultespinner, fakulte);
                                loadBolumler(fakulte, () -> setSpinnerValue(bolumspinner, bolum)); // Bölümleri yükle

                                setSpinnerValue(sinifspinner, sinif);
                            } else {
                                Toast.makeText(Profile.this, "Kullanıcı bilgileri bulunamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this, "Veritabanına erişim sağlanamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Profil bilgilerini güncelleme butonu
        Button updateProfileButton = findViewById(R.id.updateProfileButton);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Fakülte seçildiğinde bölümleri yükle
        fakultespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFakulte = fakulteList.get(position);
                loadBolumler(selectedFakulte, null); // Seçilen fakülteye göre bölümleri yükle
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Seçim yapılmadığında yapılacak işlemler (isteğe bağlı)
            }
        });
    }

    @Override
    public void onBackPressed() {
        // HomePage aktivitesine dönmek için Intent oluştur
        Intent intent = new Intent(Profile.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Mevcut aktiviteyi kapat
    }

    // Firestore'dan fakülteleri yükle
    // Firestore'dan fakülteleri yükle
    private void loadFakulteler() {
        db.collection("fakulteler").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fakulteList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String fakulte = document.getId();
                        if (fakulte == null) {
                            fakulteList.add("null");
                        } else {
                            fakulteList.add(fakulte);
                        }
                    }
                    if (fakulteList.isEmpty()) {
                        fakulteList.add("Fakülte bulunamadı");
                    }
                    ArrayAdapter<String> fakulteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fakulteList);
                    fakulteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fakultespinner.setAdapter(fakulteAdapter);

                    // Fakülteler yüklendikten sonra spinner değerini set et
                    if (currentUser != null) {
                        db.collection("users").document(currentUser.getUid())
                                .get()
                                .addOnSuccessListener(document -> {
                                    if (document.exists()) {
                                        String fakulte = document.getString("fakulte");
                                        String bolum = document.getString("bolum");
                                        String sinif = document.getString("sinif");

                                        usernameEditText.setText(document.getString("username"));
                                        setSpinnerValue(fakultespinner, fakulte);

                                        // Fakülte seçimi tamamlandıktan sonra bölümleri yükle
                                        loadBolumler(fakulte, () -> setSpinnerValue(bolumspinner, bolum));
                                        setSpinnerValue(sinifspinner, sinif);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Fakülteler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Firestore'dan bölümleri yükle
    private void loadBolumler(String selectedFakulte, Runnable callback) {
        db.collection("fakulteler").document(selectedFakulte).collection("bolumler").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bolumList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String bolum = document.getString("name");
                        if (bolum == null) {
                            bolumList.add("null");
                        } else {
                            bolumList.add(bolum);
                        }
                    }
                    if (bolumList.isEmpty()) {
                        bolumList.add("Bölüm bulunamadı");
                    }
                    ArrayAdapter<String> bolumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bolumList);
                    bolumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bolumspinner.setAdapter(bolumAdapter);

                    // Bölümler yüklendikten sonra spinner değerini set et
                    if (callback != null) {
                        callback.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Bölümler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Spinner'da Firestore'dan gelen veriyi seçili yapmak için yardımcı fonksiyon
    private void setSpinnerValue(Spinner spinner, String value) {
        if (value == null) {
            value = "null"; // Eğer value null ise "null" yaz
        }
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    // Profil bilgilerini güncelleme metodu
    private void updateProfile() {
        String username = usernameEditText.getText().toString().trim();
        String fakulte = fakultespinner.getSelectedItem() != null ? fakultespinner.getSelectedItem().toString() : "null";
        String bolum = bolumspinner.getSelectedItem() != null ? bolumspinner.getSelectedItem().toString() : "null";
        String sinif = sinifspinner.getSelectedItem() != null ? sinifspinner.getSelectedItem().toString() : "null";

        if (!username.isEmpty()) {
            // Kullanıcı verilerini Firestore'a güncelle
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("fakulte", fakulte);
            user.put("bolum", bolum);
            user.put("sinif", sinif);

            db.collection("users").document(currentUser.getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Profil bilgileri güncellendi", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Profile.this, "Veritabanına erişim sağlanamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Lütfen kullanıcı adını girin", Toast.LENGTH_SHORT).show();
        }
    }
}
