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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    TextView txt_login, txt_continueRegistration;
    EditText editText_fullname, editText_email, editText_password, editText_confirmPass;
    Button btn_register;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        txt_login = findViewById(R.id.txt_login);
        txt_continueRegistration = findViewById(R.id.txt_continueRegistration);
        editText_fullname = findViewById(R.id.editTxt_registrationFullName);
        editText_email = findViewById(R.id.editTxt_registrationEmail);
        editText_password = findViewById(R.id.editTxt_registrationPassword);
        editText_confirmPass = findViewById(R.id.editTxt_registrationConfirm);
        btn_register = findViewById(R.id.btn_register);

         fAuth = FirebaseAuth.getInstance();

         btn_register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String fullname = editText_fullname.getText().toString().trim();
                 String email =  editText_email.getText().toString().trim();
                 String password = editText_password.getText().toString().trim();
                 String confirmPass = editText_confirmPass.getText().toString().trim();
                 String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                 if(TextUtils.isEmpty(fullname)){
                     editText_fullname.setError("Full name is required!");
                     return;
                 }

                 if(TextUtils.isEmpty(email)){
                     editText_email.setError("E-mail is Required!");
                     return;
                 }
                 if(!email.matches(emailPattern)){
                     editText_email.setError("Check e-mail format!");
                     return;
                 }

                 if(TextUtils.isEmpty(password)){
                     editText_password.setError("Password is Required!");
                     return;
                 }
                 if(password.length() < 6){
                     editText_password.setError("Password must be greater than 6 chars ");
                     return;
                 }

                 if(!password.equals(confirmPass)){
                     editText_confirmPass.setError("Password doesn't match!");
                     return;
                 }

Intent mIntent = getIntent();
                 String previousActivity = mIntent.getStringExtra("FROM_ACTIVITY");

if(previousActivity.equals("A")){
    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
    fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            Toast.makeText(Registration.this, "User Synced", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), task_main.class));
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(Registration.this, "Failed to connect.", Toast.LENGTH_SHORT).show();
        }
    });
}
                 if(previousActivity.equals("B")){
        //Register in the Firebase
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(Registration.this, "User Created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration.this, Login.class));
                }
                else{
                    Toast.makeText(Registration.this, "User Not Created: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
    }
         });


        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });

        txt_continueRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Registration.this, "Logged in with temporary account", Toast.LENGTH_SHORT)
                                .show();
                        startActivity(new Intent(getApplicationContext(),task_main.class));
                        finish();
                    }
                });
            }
        });



    }

}