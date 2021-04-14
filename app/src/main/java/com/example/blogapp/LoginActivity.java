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

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button registerBtn;

    private FirebaseAuth mAuth;

    private ProgressBar LoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_confirm_pass);
        loginBtn = (Button)findViewById(R.id.create_user_btn);
        registerBtn = (Button)findViewById(R.id.to_login_btn);

        LoginProgress = (ProgressBar)findViewById(R.id.registration_progress);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterPage();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = email.getText().toString();
                String loginPassword = password.getText().toString();

                if (TextUtils.isEmpty(loginEmail) && TextUtils.isEmpty(loginPassword) ){
                    Toast.makeText(LoginActivity.this,"Заполните поле",Toast.LENGTH_SHORT).show();
                } else {




                // check поля пустой или не-пустой Empty

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword) ){
                    // proceed the process
                    LoginProgress.setVisibility(View.VISIBLE);


                    // Здесь пишем String и провереям Логинизацию

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()){
                                // Вход выполнен
                                    sendToMainPage();

                            } else {

                                String errorMessage = task.getException().getMessage(); // show error mesage
                                Toast.makeText(LoginActivity.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            LoginProgress.setVisibility(View.INVISIBLE);



                        }
                    });


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
            sendToMainPage();
        }




    }

    private void sendToMainPage() {


        Intent main_intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(main_intent);
        finish();

    }


    private void sendToRegisterPage() {

        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();

    }
}