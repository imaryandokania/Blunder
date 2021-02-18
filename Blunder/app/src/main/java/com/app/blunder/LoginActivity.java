package com.app.blunder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private Button loginIN, signup;
    private EditText email, password;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        loginIN = findViewById(R.id.button);
        signup = findViewById(R.id.button2);

        mAuth = FirebaseAuth.getInstance();

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Please Login!", Toast.LENGTH_SHORT).show();
            }
        };

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
        String front = Email .substring(0,Email .indexOf("@"));
        String domain = Email .substring(Email .indexOf("@")+1);
        Log.i("front",front);
        Log.i("domain",domain);
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
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                .url("https://ajith-verify-email-address-v1.p.rapidapi.com/varifyEmail?email="+front+"%40"+domain)
                .get()
                .addHeader("x-rapidapi-key", "dff7e330e3msh0a11bbc62a0b719p10baa8jsn956a1929fdfa")
                .addHeader("x-rapidapi-host", "ajith-Verify-email-address-v1.p.rapidapi.com")
                .build();
        String ver = "";

        try {
            Response response = client.newCall(request).execute();
            String h=response.body().string();
            JSONObject r=new JSONObject(h);
            ver=r.getString("exist");
            Log.i("status",ver);
        }catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if(ver.equals("false")) {
            email.setError("Wrong Email detected by AI");
            email.requestFocus();
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

        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login error! Please try again!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "You cannot go back!", Toast.LENGTH_SHORT).show();
    }
}