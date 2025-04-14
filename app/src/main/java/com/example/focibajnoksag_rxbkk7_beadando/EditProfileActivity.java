package com.example.focibajnoksag_rxbkk7_beadando;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private EditText editUsername, editPhone, editAddress;
    private Spinner spinnerLeague;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        editUsername = findViewById(R.id.edit_username);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        spinnerLeague = findViewById(R.id.spinnerLiga);
        saveButton = findViewById(R.id.save_button);

        // Set up the Spinner with league options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.ligak,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeague.setAdapter(adapter);

        loadCurrentProfile();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
    }

    private void loadCurrentProfile() {
        if (userId != null) {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editUsername.setText(documentSnapshot.getString("felhasznaloNev"));
                            editPhone.setText(documentSnapshot.getString("telefonszam"));
                            editAddress.setText(documentSnapshot.getString("cim"));

                            // Set the selected league in spinner
                            String currentLeague = documentSnapshot.getString("liga");
                            if (currentLeague != null) {
                                int position = ((ArrayAdapter)spinnerLeague.getAdapter()).getPosition(currentLeague);
                                spinnerLeague.setSelection(position >= 0 ? position : 0);
                            }
                        }
                    });
        }
    }

    private void saveProfileChanges() {
        if (userId != null) {
            String username = editUsername.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String league = spinnerLeague.getSelectedItem().toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("felhasznaloNev", username);
            updates.put("telefonszam", phone);
            updates.put("liga", league);
            updates.put("cim", address);

            db.collection("users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Profil frissítve!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Hiba történt a mentés során", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}