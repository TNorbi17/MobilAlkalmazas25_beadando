package com.example.focibajnoksag_rxbkk7_beadando;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView profilnev = findViewById(R.id.profilnev);
        TextView profilemail = findViewById(R.id.profilemail);
        TextView profiltelo = findViewById(R.id.profiltelo);
        TextView profilliga = findViewById(R.id.profilliga);
        TextView profilcim = findViewById(R.id.profilcim);
        //Button editgomb = findViewById(R.id.editgomb);


        ImageView gifImageView = findViewById(R.id.gif);
        Glide.with(this)
                .load(R.drawable.sadcat)
                .into(gifImageView);



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }


    }

    private void loadUserData(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    TextView pnev = findViewById(R.id.profilnev);
                    TextView ptelo = findViewById(R.id.profiltelo);
                    TextView pemail = findViewById(R.id.profilemail);
                    TextView pliga = findViewById(R.id.profilliga);
                    TextView pcim = findViewById(R.id.profilcim);

                    String username = document.getString("felhasznaloNev");
                    String email = document.getString("email");
                    String phone = document.getString("telefonszam");
                    String league = document.getString("liga");
                    String address = document.getString("cim");

                    pnev.setText(username != null ? username : "Nincs megadva");
                    ptelo.setText(phone != null ? phone : "Nincs megadva");
                    pemail.setText(email != null ? email : "Nincs megadva");
                    pliga.setText(league != null ? league : "Nincs megadva");
                    pcim.setText(address != null ? address : "Nincs megadva");


                    Log.d(TAG, "User data loaded successfully");
                } else {
                    Log.d(TAG, "No such document");

                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());

            }
        });
    }
}