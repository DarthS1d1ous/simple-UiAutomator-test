package com.borschevskydenis.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {

    private static final String CORRECT_STRING_LOGIN = "test";
    private static final String CORRECT_STRING_PASSWORD = "test";

    EditText etLogin;
    EditText etPassword;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etLogin.getText().toString().equals(CORRECT_STRING_LOGIN) && etPassword.getText().toString().equals(CORRECT_STRING_PASSWORD)) {
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    startActivity(intent);
                } else Toast.makeText(getApplicationContext(),"Введён неправильный логин или пароль!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
