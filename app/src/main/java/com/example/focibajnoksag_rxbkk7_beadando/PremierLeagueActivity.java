package com.example.focibajnoksag_rxbkk7_beadando;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PremierLeagueActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_premier_league);

        tableLayout = findViewById(R.id.tableLayout);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();

        addTableHeader();
        loadTeamsFromFirestore();
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(R.color.table_header));
        addHeaderCell(headerRow, "Helyezés");
        addHeaderCell(headerRow, "Csapat");
        addHeaderCell(headerRow, "Gy");
        addHeaderCell(headerRow, "D");
        addHeaderCell(headerRow, "V");
        addHeaderCell(headerRow, "Pont");

        tableLayout.addView(headerRow);
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(16);
        row.addView(textView);
    }

    private void loadTeamsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        db.collection("teams")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            List<Team> teams = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Map<String, Object> data = document.getData();
                                Team team = new Team();
                                try {
                                    team.setName((String) data.get("name"));
                                    team.setWins(((Long) data.get("wins")).intValue());
                                    team.setDraws(((Long) data.get("draws")).intValue());
                                    team.setLosses(((Long) data.get("losses")).intValue());
                                    team.setPoints(((Long) data.get("points")).intValue());
                                    teams.add(team);
                                } catch (Exception e) {
                                    Log.e("Firestore", "Hiba a kézi konverzióban", e);
                                }
                            }

                            Collections.sort(teams, (t1, t2) -> t2.getPoints() - t1.getPoints());


                            runOnUiThread(() -> {
                                tableLayout.removeAllViews();
                                addTableHeader();
                                for (int i = 0; i < teams.size(); i++) {
                                    addTeamToTable(teams.get(i), i); // i = pozíció
                                }
                            });
                        } else {
                            Log.w("Firestore", "Nincsenek adatok");
                            ;
                        }
                    } else {
                        Log.e("Firestore", "Hiba a lekérdezéskor", task.getException());
                    }
                });
    }

    private void addTeamToTable(Team team, int position) {
        TableRow row = new TableRow(this);

        int bgColor;
        if (position < 3) {
            bgColor = getResources().getColor(R.color.light_blue);
        } else if (position < 5) {
            bgColor = getResources().getColor(R.color.yellow);
        } else if (position >= 15) {
            bgColor = getResources().getColor(R.color.red);
        } else {
            bgColor = (position % 2 == 0)
                    ? getResources().getColor(R.color.table_row_even)
                    : getResources().getColor(R.color.table_row_odd);
        }

        GradientDrawable border = new GradientDrawable();
        border.setColor(bgColor);
        border.setStroke(1, Color.BLACK);
        row.setBackground(border);

        addCell(row, String.valueOf(position + 1), Gravity.CENTER);
        addCell(row, team.getName(), Gravity.START);
        addCell(row, String.valueOf(team.getWins()), Gravity.CENTER);
        addCell(row, String.valueOf(team.getDraws()), Gravity.CENTER);
        addCell(row, String.valueOf(team.getLosses()), Gravity.CENTER);
        addCell(row, String.valueOf(team.getPoints()), Gravity.CENTER);

        tableLayout.addView(row);
    }

    private void addCell(TableRow row, String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 12, 8, 12);
        textView.setGravity(gravity);


        row.addView(textView);
    }

    public void profil(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void plgollovok(View view) {
        Intent intent = new Intent(this,PLGollovolistaActivity.class);
        startActivity(intent);
    }
}