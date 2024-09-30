package com.firatllone.allone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class SignUp extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signUpButton;
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
        setContentView(R.layout.signup_main);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        signUpButton = findViewById(R.id.button_signup);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(SignUp.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                    // Kayıt başarılı
                                    Toast.makeText(SignUp.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                                    // İstediğiniz bir aktiviteye geçiş yapabilirsiniz
                                } else {
                                    // Kayıt başarısız
                                    Toast.makeText(SignUp.this, "Kayıt başarısız. Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}