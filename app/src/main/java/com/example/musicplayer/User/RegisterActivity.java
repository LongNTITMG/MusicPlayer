package com.example.musicplayer.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextName,editTextEmail, editTextPassword, editTextPhone;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPhone = findViewById(R.id.phone);
        editTextName = findViewById(R.id.name);
        buttonReg = findViewById(R.id.btn_reg);
        progressBar = findViewById(R.id.progressbar);
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                progressBar.setVisibility(View.VISIBLE);
                String name,email, password,phone,conPass;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                phone = String.valueOf(editTextPhone.getText());
                name = String.valueOf(editTextName.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;}

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this,"Enter PhoneNumber",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"Enter Name",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterActivity.this, "Your account Have Been Created",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed to register",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
        });
    }
}