package com.firatllone.allone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class HomePage extends AppCompatActivity {

    Button profileButton,dersnotu,kulupbutton;
    private TextView fakulteAndBolumText, userName;
    private FirebaseFirestore db;
    private RecyclerView yemekList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);
        dersnotu = findViewById(R.id.dersnotu);
        profileButton = findViewById(R.id.profileButton);
        kulupbutton = findViewById(R.id.kulupbutton);
        yemekList = findViewById(R.id.yemekList);
        yemekList.setLayoutManager(new LinearLayoutManager(this));

        fakulteAndBolumText = findViewById(R.id.fakulteandbolumtext);
        userName = findViewById(R.id.userName);

        // Firestore referansı
        db = FirebaseFirestore.getInstance();
        // Veri çekme işlemi
        veriCek();
        textvericek();
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });
        kulupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(HomePage.this, KulupListMain.class);
                //startActivity(intent);
                //finish();
            }
        });
        dersnotu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, FileBrowserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void textvericek() {

            // Giriş yapan kullanıcının ID'sini al
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Giriş yapan kullanıcı ID'sini al

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Kullanıcı verilerini al
                                String name = document.getString("username");
                                String fakulte = document.getString("fakulte");
                                String bolum = document.getString("bolum");
                                String sinif = document.getString("sinif");

                                // TextView bileşenlerine yerleştir
                                userName.setText(name);
                                fakulteAndBolumText.setText(fakulte + "/" + bolum +"-" +sinif);
                            } else {
                                Log.d("Firestore", "No such document for user ID: " + userId);
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    });

    }
        private void veriCek() {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://unievi.firat.edu.tr/") // İlgili URL
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();

                        // Verileri parse etme
                        parseData(responseData);
                    }
                }
            });
        }

    private void parseData(String html) {

        // Örnek olarak basit bir regex ile
        String regex = "<p>(.*?)</p>"; // p tag'leri arasındaki verileri çekmek için
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        ArrayList<Yemek> yemekler = new ArrayList<>();

        while (matcher.find()) {
            String yemekAdi = matcher.group(1);
            // Burada "yemek detayı" olmadığı için sadece ad ekliyoruz
            yemekler.add(new Yemek(yemekAdi, ""));
        }

        // İlk 5 öğeyi almak için yeni bir liste oluşturma
        ArrayList<Yemek> ilkBesYemekler = new ArrayList<>();
        for (int i = 0; i < Math.min(5, yemekler.size()); i++) {
            ilkBesYemekler.add(yemekler.get(i));
        }

        runOnUiThread(() -> {
            // RecyclerView adaptörü kullanarak verileri yükle
            YemekAdapter yemekAdapter = new YemekAdapter(ilkBesYemekler);
            yemekList.setAdapter(yemekAdapter);
        });
    }




}

