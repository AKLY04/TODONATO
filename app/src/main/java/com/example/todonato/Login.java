package com.example.todonato;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText editText_email, editText_password;

    TextView txt_signup, txt_forgotPassword, txt_continueLogin;
    Button btn_login;

    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_email = findViewById(R.id.editTxt_emailLogin);
        editText_password = findViewById(R.id.editText_passwordLogin);
        txt_signup = findViewById(R.id.txt_signup);
        txt_forgotPassword = findViewById(R.id.txt_forgotPassword);
        txt_continueLogin = findViewById(R.id.txt_continueLogin);
        btn_login = findViewById(R.id.btn_login);
        fAuth = FirebaseAuth.getInstance();


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(Login.this, task_main.class));
            finish();
        }




        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Login.this, Registration.class);
                mIntent.putExtra("FROM_ACTIVITY", "B");
                startActivity(mIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editText_email.getText().toString().trim();
                String password = editText_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editText_email.setError("E-mail is Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editText_password.setError("Password is required!");
                    return;
                }
                if (password.length() < 6) {
                    editText_password.setError("Password not valid!");
                    return;
                }




                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, task_main.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        txt_continueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Logged in with temporary account", Toast.LENGTH_SHORT)
                                .show();
                        startActivity(new Intent(getApplicationContext(),task_main.class));
                        finish();
                    }
                });
            }
        });



        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

    }
}