package com.app.blunder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private Button loginIN, signup;
    private TextView email, password;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        loginIN = findViewById(R.id.button);
        signup = findViewById(R.id.button2);
        loginIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }


    private void registerUser() {
        final String Email = email.getText().toString().trim();
        final String Password = email.getText().toString().trim();

        if(Email.isEmpty()) {
            email.setError("Email Name is required!! ");
            email.requestFocus();
            return;
        }

        if(Password.isEmpty()) {
            password.setError("Password is required!!");
            password.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }

        if(Password.length() < 6) {
            password.setError("Min password length should be 6 characters! ");
            password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "SignUp Unsuccessful, Please try Again!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "SignUp was Successful.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DetailsActivity.class));
                }
            }
        });


    }

    private void loginUser() {
        final String Email = email.getText().toString().trim();
        final String Password = email.getText().toString().trim();

        if(Email.isEmpty()) {
            email.setError("Email Name is required!! ");
            email.requestFocus();
            return;
        }

        if(Password.isEmpty()) {
            password.setError("Password is required!!");
            password.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }

        if(Password.length() < 6) {
            password.setError("Min password length should be 6 characters! ");
            password.requestFocus();
            return;
        }

        FirebaseUser mUser = mAuth.getCurrentUser();

        if(mUser != null) {
            Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    }