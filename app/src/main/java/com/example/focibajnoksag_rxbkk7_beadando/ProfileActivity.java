package com.example.focibajnoksag_rxbkk7_beadando;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private String userId;


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
        Button editButton = findViewById(R.id.editgomb);



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserData(userId);
        }
    }

    // Button click handler in the same style as your example
    public void editProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from edit activity
        if (userId != null) {
            loadUserData(userId);
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


    // Add this method to your ProfileActivity class

    public void deleteProfile(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Nincs bejelentkezett felhasználó", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Profil törlése")
                .setMessage("Biztos, hogy törölni szeretnéd a profilod? Minden adatod végleg elveszik!")
                .setPositiveButton("Törlés", (dialog, which) -> {
                    // Csak a megerősítés után fut le a törlés
                    db.collection("users").document(currentUser.getUid())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                currentUser.delete()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this,
                                                        "Profil sikeresen törölve",
                                                        Toast.LENGTH_SHORT).show();

                                                mAuth.signOut();
                                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.e(TAG,"Sikertelen törlés: " + task.getException().getMessage());
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG,"Adattörlés sikertelen: " + e.getMessage());
                            });
                })
                .setNegativeButton("Mégse", null)
                .show();
    }





}