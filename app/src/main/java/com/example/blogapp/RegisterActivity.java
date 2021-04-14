package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_email_field;
    private EditText register_pass_field;
    private EditText register_confirm_pass_field;

    private Button register_btn;
    private Button to_login_btn;

    private ProgressBar register_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        register_email_field = (EditText)findViewById(R.id.register_email);
        register_pass_field = (EditText)findViewById(R.id.register_pass);
        register_confirm_pass_field = (EditText)findViewById(R.id.register_confirm_pass);

        register_btn = (Button)findViewById(R.id.create_user_btn);
        to_login_btn =(Button)findViewById(R.id.to_login_btn);

        register_progress = (ProgressBar)findViewById(R.id.registration_progress);

        to_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginPage();
            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = register_email_field.getText().toString();
                String password = register_pass_field.getText().toString();
                String confirm_password = register_confirm_pass_field.getText().toString();
if (TextUtils.isEmpty(email)  && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirm_password)){
    Toast.makeText(RegisterActivity.this,"Заполните поле", Toast.LENGTH_LONG).show();

} else {


    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password)) {

        //Confirm Pass
        if (password.equals(confirm_password)) {

            register_progress.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
// Check if user created
                    if (task.isSuccessful()) {

                        sendToSetupPage();

                    } else {
                        String errorMessage = task.getException().getMessage(); // show error mesage
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }


                    register_progress.setVisibility(View.INVISIBLE);  //why?

                }
            });


        } else {
            Toast.makeText(RegisterActivity.this, "Birdey emes", Toast.LENGTH_LONG).show();
        }


    }

}
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            sendToSetupPage();
        }

    }

    private void sendToSetupPage() {

        Intent main_intent = new Intent(RegisterActivity.this,SetupActivity.class);
        startActivity(main_intent);
        finish();

    }

    private void sendToLoginPage() {

        Intent main_intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(main_intent);
        finish();

    }
}