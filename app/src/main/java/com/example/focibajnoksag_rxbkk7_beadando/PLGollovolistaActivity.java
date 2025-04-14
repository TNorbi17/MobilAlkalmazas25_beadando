package com.example.focibajnoksag_rxbkk7_beadando;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PLGollovolistaActivity extends AppCompatActivity {

    private static final String TAG = "PLGollovolistaActivity";
    private static final int INITIAL_VOTES = 5;

    private TableLayout topScorersTable;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Gollovok> scorers;
    private Gollovok selectedPlayer;
    private Button voteButton;
    private int remainingVotes = INITIAL_VOTES;
    private NotificationHandler mNotification;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plgollovolista);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        topScorersTable = findViewById(R.id.topScorersTable);
        progressBar = findViewById(R.id.progressBar);
        voteButton = findViewById(R.id.voteButton);





        loadUserData();

        loadTopScorers();

        mNotification = new NotificationHandler(this);

        voteButton.setOnClickListener(v -> {
            if (remainingVotes <= 0) {
                Toast.makeText(this, "Elfogytak a szavazataid!", Toast.LENGTH_SHORT).show();
                mNotification.send("Nem szavazhatsz többet!");
                return;
            }

            if (selectedPlayer != null) {
                submitVote();

            } else {

                Toast.makeText(this, "Válassz ki egy játékost a szavazáshoz!", Toast.LENGTH_SHORT).show();
                mNotification.send("Nem választottál Játékost!");
            }
        });

    }

    private void loadUserData() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            updateVoteButton();
                            Log.d(TAG, "User data loaded. Remaining votes: " + remainingVotes);
                        } else {
                            Log.d(TAG, "No such user document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
    }

    private void loadTopScorers() {
        progressBar.setVisibility(View.VISIBLE);
        topScorersTable.setVisibility(View.GONE);

        db.collection("top_scorers")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    topScorersTable.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        scorers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Gollovok scorer = document.toObject(Gollovok.class);
                            scorer.setId(document.getId());
                            scorers.add(scorer);
                        }

                        Collections.sort(scorers, (s1, s2) -> s2.getGoals() - s1.getGoals());
                        displayScorers(scorers);
                    } else {
                        Toast.makeText(this, "Hiba történt az adatok betöltésekor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayScorers(List<Gollovok> scorers) {
        int childCount = topScorersTable.getChildCount();
        if (childCount > 1) {
            topScorersTable.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < scorers.size(); i++) {
            Gollovok scorer = scorers.get(i);
            TableRow row = new TableRow(this);
            row.setBackgroundColor(getRowBackgroundColor(i));

            row.setOnClickListener(v -> {
                if (remainingVotes > 0) {
                    resetAllRowsBackground();
                    selectedPlayer = scorer;
                    row.setBackgroundColor(getResources().getColor(R.color.secondary));
                } else {
                    Toast.makeText(this, "Nincs több szavazatod!", Toast.LENGTH_SHORT).show();
                }
            });

            addCell(row, scorer.getName(), Gravity.START);
            addCell(row, scorer.getTeam(), Gravity.START);
            addCell(row, String.valueOf(scorer.getGoals()), Gravity.CENTER);
            addCell(row, String.valueOf(scorer.getVotes()), Gravity.CENTER);

            topScorersTable.addView(row);
        }
    }

    private void updateVoteButton() {
        voteButton.setText("Szavazás (" + remainingVotes + " maradt)");
        voteButton.setEnabled(remainingVotes > 0);

    }

    private void resetAllRowsBackground() {
        for (int i = 1; i < topScorersTable.getChildCount(); i++) {
            View child = topScorersTable.getChildAt(i);
            if (child instanceof TableRow) {
                child.setBackgroundColor(getRowBackgroundColor(i - 1));
            }
        }
    }

    private int getRowBackgroundColor(int position) {
        if (position == 0) return getResources().getColor(R.color.gold);
        if (position == 1) return getResources().getColor(R.color.silver);
        if (position == 2) return getResources().getColor(R.color.bronze);
        return position % 2 == 0
                ? getResources().getColor(R.color.table_row_even)
                : getResources().getColor(R.color.table_row_odd);
    }

    private void addCell(TableRow row, String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 16, 8, 16);
        textView.setGravity(gravity);
        row.addView(textView);
    }

    private void submitVote() {
        if (selectedPlayer == null || remainingVotes <= 0) return;

        progressBar.setVisibility(View.VISIBLE);
        voteButton.setEnabled(false);

        // Játékos szavazatainak növelése
        db.collection("top_scorers").document(selectedPlayer.getId())
                .update("votes", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    // Felhasználó szavazatainak csökkentése
                    db.collection("users").document(currentUserId)
                            .update("votesLeft", FieldValue.increment(-1))
                            .addOnSuccessListener(aVoid1 -> {
                                remainingVotes--;
                                updateVoteButton();
                                mNotification.send("Sikeres Szavazás!\n Erre a játékosra szavaztál: "+selectedPlayer.getName());

                                Toast.makeText(this,
                                        "Szavazat leadva: " + selectedPlayer.getName() +
                                                " (" + remainingVotes + " szavazatod maradt)",
                                        Toast.LENGTH_SHORT).show();

                                loadTopScorers();
                                selectedPlayer = null;
                                progressBar.setVisibility(View.GONE);


                            })
                            .addOnFailureListener(e -> {
                                mNotification.send("A szavazáshoz be kell jelentkeznie!");
                                handleVoteError();

                            });
                })
                .addOnFailureListener(e -> {
                    mNotification.send("A szavazáshoz be kell jelentkeznie!");
                    handleVoteError();

                });


    }

    private void handleVoteError() {
        Toast.makeText(this, "Hiba történt a szavazás során", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        voteButton.setEnabled(true);
    }
}