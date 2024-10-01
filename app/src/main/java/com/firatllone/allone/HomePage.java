package com.firatllone.allone;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ArrayAdapter;

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


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class HomePage extends AppCompatActivity {

    Button profileButton,dersnotu;
    private ListView yemekList;
    private ArrayList<String> yemekler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_main);
        dersnotu = findViewById(R.id.dersnotu);
        profileButton = findViewById(R.id.profileButton);
        yemekList = findViewById(R.id.yemeklist);
        yemekler = new ArrayList<>();

        // Veri çekme işlemi
        veriCek();

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
                finish();
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

        while (matcher.find()) {
            String yemek = matcher.group(1);
            yemekler.add(yemek);
        }

        // İlk 5 öğeyi almak için yeni bir liste oluşturma
        ArrayList<String> ilkBesYemekler = new ArrayList<>();
        for (int i = 0; i < Math.min(5, yemekler.size()); i++) {
            ilkBesYemekler.add(yemekler.get(i));
        }

        runOnUiThread(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, ilkBesYemekler);
            yemekList.setAdapter(adapter);
        });
    }

}

