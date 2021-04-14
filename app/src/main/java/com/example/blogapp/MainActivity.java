package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar mainToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);


//        getSupportActionBar().setTitle("My Posts  ");
        getSupportActionBar().setTitle("My Posts  "+mAuth.getCurrentUser().getEmail());

    }

    public void showUser(){

        TextView textView = (TextView) findViewById(R.id.main_hello_text);

        String username = mAuth.getCurrentUser().getDisplayName();



        setTitle(username.toString());




    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if ( currentUser == null){

            //Logged User,

          sendToLoginPage();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //in Menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_account_settings_btn:
                sendToSettingPage();
                return true;

            default:
                return false;

        }


    }

    private void logOut() {


        mAuth.signOut();
        sendToLoginPage();

    }


    private void sendToLoginPage() {

        Intent login_intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(login_intent);
        finish();

    }

    private void sendToSettingPage() {

        Intent intent = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(intent);
        finish();

    }



}

