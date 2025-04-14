package com.example.focibajnoksag_rxbkk7_beadando;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    EditText fhnevEditText;
    EditText emailEditText;
    EditText jelszoEditText;
    EditText telefonszamedittext;
    EditText jelszomegegyzserEditText;

    EditText adddressedittext;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fhnevEditText = findViewById(R.id.felhasznalonevedittext);
        emailEditText = findViewById(R.id.emailedittext);
        jelszoEditText = findViewById(R.id.jelszoEditText);
        jelszomegegyzserEditText = findViewById(R.id.jelszoujraEditText);
        spinner = findViewById(R.id.spinnerLiga);
        telefonszamedittext  = findViewById(R.id.telefonszamedittext);
        adddressedittext = findViewById(R.id.adddressedittext);





        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String email = preferences.getString("email","");
        String jelszo = preferences.getString("jelszo","");

        emailEditText.setText(email);
        jelszoEditText.setText(jelszo);
        jelszomegegyzserEditText.setText(jelszo);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ligak, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG,"onCreate");
    }

    public void megse(View view) {
        finish();
    }

    public void register(View view) {


        String fhnev = fhnevEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String jelszo = jelszoEditText.getText().toString();
        String jelszomegegyszer = jelszomegegyzserEditText.getText().toString();


        if(!jelszo.equals(jelszomegegyszer)){
            Log.e(LOG_TAG,"Nem egyenlő a 2 jelszó!");
            return;
        }
        if (jelszo.length() < 6 ){

            Toast.makeText(RegisterActivity.this,"Legalább 6 karakter legyen a jelszó", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.contains("@") ){

            Toast.makeText(RegisterActivity.this,"Nem jó email formátum", Toast.LENGTH_LONG).show();
            return;
        }

        if (fhnev.isEmpty()) {
            Toast.makeText(this, "Add meg a felhasználóneved!", Toast.LENGTH_SHORT).show();
            return;
        }
        String telefonszam = telefonszamedittext.getText().toString();
        String cim = adddressedittext.getText().toString();
        String liga = spinner.getSelectedItem().toString();




        Log.i(LOG_TAG,"Regisztrált: "+fhnev+" email: "+email+" jelszó:"+jelszo+" Jelszó mégegyszer: "+jelszomegegyszer+cim); //logcat

        mAuth.createUserWithEmailAndPassword(email, jelszo)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sikeres regisztráció
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserDataToFirestore(user.getUid(), fhnev, email, telefonszam, liga, cim);
                    } else {
                        // Sikertelen regisztráció
                        Toast.makeText(RegisterActivity.this,
                                "Regisztráció sikertelen: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void saveUserDataToFirestore(String userId, String felhasznaloNev, String email, String telefonszam, String liga, String cim)
    {

        Map<String, Object> user = new HashMap<>();
        user.put("felhasznaloNev", felhasznaloNev);
        user.put("email", email);
        user.put("telefonszam", telefonszam);
        user.put("liga", liga);
        user.put("cim", cim);
        user.put("votesLeft",5);
        user.put("regisztracioIdopont", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "Felhasználói adatok sikeresen mentve");
                    startindex();
                })
                .addOnFailureListener(e -> {
                    Log.w(LOG_TAG, "Hiba a felhasználói adatok mentésekor", e);
                    Toast.makeText(RegisterActivity.this,
                            "Hiba az adatok mentésekor", Toast.LENGTH_SHORT).show();
                });

    }
    private void startindex(/*userdata*/){
        Intent intent = new Intent(this,IndexActivity.class);
        startActivity(intent);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selecteditem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selecteditem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}