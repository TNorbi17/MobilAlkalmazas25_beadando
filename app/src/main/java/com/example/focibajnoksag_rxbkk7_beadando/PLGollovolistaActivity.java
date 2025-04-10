package com.example.focibajnoksag_rxbkk7_beadando;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PLGollovolistaActivity extends AppCompatActivity {

    private TableLayout topScorersTable;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plgollovolista);

        topScorersTable = findViewById(R.id.topScorersTable);
        db = FirebaseFirestore.getInstance();

        loadTopScorers();
    }

    private void loadTopScorers() {
        db.collection("top_scorers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Gollovok> scorers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Gollovok scorer = document.toObject(Gollovok.class);
                            scorers.add(scorer);
                        }

                        Collections.sort(scorers, (s1, s2) -> s2.getGoals() - s1.getGoals());
                        displayScorers(scorers);
                    }
                });
    }

    private void displayScorers(List<Gollovok> scorers) {


        for (int i = 0; i < scorers.size(); i++) {
            Gollovok scorer = scorers.get(i);

            TableRow row = new TableRow(this);
            int bgColor = getRowBackgroundColor(i);
            row.setBackgroundColor(bgColor);
            addCell(row, scorer.getName(), Gravity.START);
            addCell(row, scorer.getTeam(), Gravity.START);
            addCell(row, String.valueOf(scorer.getGoals()), Gravity.START);
            topScorersTable.addView(row);
        }
    }


    private int getRowBackgroundColor(int position) {
        if (position == 0) {
            return getResources().getColor(R.color.gold);
        } else if (position == 1) {
            return getResources().getColor(R.color.silver);
        } else if (position == 2) {
            return getResources().getColor(R.color.bronze);
        } else {
            return position % 2 == 0
                    ? getResources().getColor(R.color.table_row_even)
                    : getResources().getColor(R.color.table_row_odd);
        }
    }

    private void addCell(TableRow row, String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 12, 8, 12);
        textView.setGravity(gravity);
        row.addView(textView);
    }


}