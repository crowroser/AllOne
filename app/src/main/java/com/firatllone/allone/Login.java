package com.firatllone.allone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private Button signIn;
    private FirebaseAuth mAuth;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Geri tuşuna basıldığında önceki aktiviteye dön
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        signIn = findViewById(R.id.signup_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("E-posta alanı boş olamaz.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Şifre alanı boş olamaz.");
            return;
        }

        // Firebase ile kullanıcı girişi
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Giriş başarılı, kullanıcı bilgilerini güncelle ve ana ekrana git
                            Log.d("LoginActivity", "signInWithEmail:success");
                            Toast.makeText(Login.this, "Giriş başarılı.",
                                    Toast.LENGTH_SHORT).show();
                            // Başarılı giriş olduğunda MainActivity'e geçiş yap
                            Intent intent = new Intent(Login.this, HomePage.class);
                            startActivity(intent);
                            finish(); // Bu aktiviteyi kapat
                        } else {
                            // Giriş başarısız ise hata mesajı göster
                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Giriş başarısız.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}