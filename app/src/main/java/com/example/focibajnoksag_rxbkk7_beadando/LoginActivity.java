package com.example.focibajnoksag_rxbkk7_beadando;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();
    private static final String PREF_KEY = LoginActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        emailEditText = findViewById(R.id.editText_Email_Login);
        passwordEditText = findViewById(R.id.editText_Password_Login);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG,"onCreate");




    }

    public void login(View view) {

        String email = emailEditText.getText().toString();
        String jelszo = passwordEditText.getText().toString();
        if (email.isEmpty() || jelszo.isEmpty()){
            Log.e(LOG_TAG,"Hiányzó adatok!");
            Toast.makeText(LoginActivity.this,"Tölts ki minden adatot.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(LOG_TAG,"bejelentkezett: "+email+" jelszó:"+ jelszo);


        mAuth.signInWithEmailAndPassword(email,jelszo).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){


                    Log.d(LOG_TAG, " sikerült jelentkenzni.");
                    startindex();
                }else{
                    Log.d(LOG_TAG, "Nem sikerült jelentkenzni.");
                    Toast.makeText(LoginActivity.this,"Nem megfelelő adatokat adott meg.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void startindex(){
        Intent intent = new Intent(this,IndexActivity.class);
        startActivity(intent);

    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    public void loginAsGuest(View view) {

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(LOG_TAG, " sikerült jelentkenzni.");
                    startindex();
                }else{
                    Log.d(LOG_TAG, "Nem sikerült jelentkenzni.");
                    Toast.makeText(LoginActivity.this,"Nem sikerült bejelentkezni"+ task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email",emailEditText.getText().toString());
        editor.putString("jelszo",passwordEditText.getText().toString());
        editor.apply();

        Log.i(LOG_TAG,"onPause");
    }




    @Override
    protected void onStart() {
        super.onStart();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.loginbuttonanim);
        Button myButton = findViewById(R.id.guestLoginbutton);
        myButton.setAnimation(myAnim);

        final Animation regbanim = AnimationUtils.loadAnimation(this, R.anim.reg_login_buttonanim);
        Button regb = findViewById(R.id.registerButton);
        regb.setAnimation(regbanim);

        final Animation logbunanim = AnimationUtils.loadAnimation(this, R.anim.reg_login_buttonanim);
        Button logbbutt = findViewById(R.id.loginButton);
        logbbutt.setAnimation(logbunanim);





    }


}