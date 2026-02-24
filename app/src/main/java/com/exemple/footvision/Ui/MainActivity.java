package com.exemple.footvision.Ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.exemple.footvision.Helpers.DBHelper;
import com.exemple.footvision.Adapters.MatchAdapter;
import com.exemple.footvision.Models.Match;
import com.exemple.footvision.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    List<Match> matchList = new ArrayList<>();
    String apiKey = "ad62ffea217c4a79abf842016dedc52d"; // I create d account and I have a key to access to d api

    Button btnLoginToolbar, btnLogout;
    TextView btnAvatar;
    DBHelper dbHelper;
    SharedPreferences prefs;
    boolean isLogoutVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchAdapter(matchList, match -> {
            Intent intent = new Intent(MainActivity.this, MatchDetailActivity.class);
            intent.putExtra("match_home", match.getHomeTeam());
            intent.putExtra("match_away", match.getAwayTeam());
            intent.putExtra("match_date", match.getDate());
            intent.putExtra("match_score", match.getScore());
            intent.putExtra("match_id", match.getId()); // passer l'ID pour les commentaires
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        btnLoginToolbar = findViewById(R.id.btnLoginToolbar);
        btnAvatar = findViewById(R.id.btnAvatar);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DBHelper(this);
        prefs = getSharedPreferences("footvision_prefs", MODE_PRIVATE);

        String loggedUser = prefs.getString("logged_user", null);
        if (loggedUser != null && !loggedUser.isEmpty()) {
            btnLoginToolbar.setVisibility(View.GONE);
            btnAvatar.setVisibility(View.VISIBLE);

            // to affiche two lettre in the avatar
            String firstTwoLetters = loggedUser.length() >= 2 ?
                    loggedUser.trim().substring(0, 2).toUpperCase() :
                    loggedUser.trim().substring(0, 1).toUpperCase();
            btnAvatar.setText(firstTwoLetters);

        } else {
            btnLoginToolbar.setVisibility(View.VISIBLE);
            btnAvatar.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }
        btnLoginToolbar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class))
        );

        btnAvatar.setOnClickListener(v -> {
            if (isLogoutVisible) {
                btnLogout.setVisibility(View.GONE);
                isLogoutVisible = false;
            } else {
                btnLogout.setVisibility(View.VISIBLE);
                isLogoutVisible = true;
            }
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
            prefs.edit().remove("logged_user").apply();
            btnLogout.setVisibility(View.GONE);
            btnAvatar.setVisibility(View.GONE);
            btnAvatar.setText("");
            btnLoginToolbar.setVisibility(View.VISIBLE);
            isLogoutVisible = false;
        });
        fetchMatches();
    }

    private void fetchMatches() {
        String url = "https://api.football-data.org/v4/matches"; // Api used to give d data

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray arr = response.getJSONArray("matches");
                        Gson gson = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Match match = gson.fromJson(obj.toString(), Match.class);
                            matchList.add(match);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur API", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("X-Auth-Token", apiKey);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}