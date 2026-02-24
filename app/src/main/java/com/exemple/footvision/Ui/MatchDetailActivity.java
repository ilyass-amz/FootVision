package com.exemple.footvision.Ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exemple.footvision.Adapters.CommentAdapter;
import com.exemple.footvision.Helpers.DBHelper;
import com.exemple.footvision.Models.Comment;
import com.exemple.footvision.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MatchDetailActivity extends AppCompatActivity {

    TextView homeTeam, awayTeam, date, score, duration;
    Button btnAddComment;
    RecyclerView rvComments;
    DBHelper dbHelper;
    CommentAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();
    SharedPreferences prefs;
    TextView btnAvatar;
    Button btnLoginToolbar, btnLogout;
    boolean isLogoutVisible = false;

    String matchId;
    String loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        homeTeam = findViewById(R.id.homeTeam);
        awayTeam = findViewById(R.id.awayTeam);
        date = findViewById(R.id.date);
        score = findViewById(R.id.score);
        duration = findViewById(R.id.duration);
        btnAddComment = findViewById(R.id.btnAddComment);
        rvComments = findViewById(R.id.rvComments);

        // le menu
        btnAvatar = findViewById(R.id.btnAvatar);
        btnLoginToolbar = findViewById(R.id.btnLoginToolbar);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DBHelper(this);
        prefs = getSharedPreferences("footvision_prefs", MODE_PRIVATE);

        loggedUser = prefs.getString("logged_user", null);

        // data from MainActivity
        String home = getIntent().getStringExtra("match_home");
        String away = getIntent().getStringExtra("match_away");
        String matchDate = getIntent().getStringExtra("match_date");
        String matchScore = getIntent().getStringExtra("match_score");
        matchId = String.valueOf(getIntent().getIntExtra("match_id", -1));

        // Teams and score
        homeTeam.setText(home != null ? home : "Home");
        awayTeam.setText(away != null ? away : "Away");
        score.setText(matchScore != null ? matchScore : "0 - 0");
        duration.setText("90 min");

        // date format like dd/MM/yyyy HH:mm
        if (matchDate != null && !matchDate.isEmpty()) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // API en UTC
                Date dateObj = isoFormat.parse(matchDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String formattedDate = displayFormat.format(dateObj);

                date.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                date.setText(matchDate);
            }
        } else {
            date.setText("");
        }

        // login and avatar user
        if (loggedUser != null && !loggedUser.isEmpty()) {
            btnLoginToolbar.setVisibility(View.GONE);
            btnAvatar.setVisibility(View.VISIBLE);

            String firstTwoLetters = loggedUser.length() >= 2 ?
                    loggedUser.trim().substring(0, 2).toUpperCase() :
                    loggedUser.trim().substring(0, 1).toUpperCase();
            btnAvatar.setText(firstTwoLetters);
        } else {
            btnLoginToolbar.setVisibility(View.VISIBLE);
            btnAvatar.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnAddComment.setVisibility(View.GONE);
        }

        btnLoginToolbar.setOnClickListener(v ->
                startActivity(new Intent(MatchDetailActivity.this, LoginActivity.class))
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
            btnAddComment.setVisibility(View.GONE);
        });

        // Comment
        btnAddComment.setOnClickListener(v -> showAddCommentDialog());

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, commentList, (comment, position, view) -> {
            showModifyDeleteDialog(comment);
        });
        rvComments.setAdapter(commentAdapter);

        loadComments();
    }

    // Add Comment
    private void showAddCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un commentaire");
        EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.insertComment(matchId, loggedUser, text);
                loadComments();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    // update Comment
    private void showEditCommentDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier le commentaire");
        EditText input = new EditText(this);
        input.setText(comment.getComment());
        builder.setView(input);
        builder.setPositiveButton("Modifier", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.updateComment(comment.getId(), text);
                loadComments();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    // charge of comment from db
    private void loadComments() {
        commentList.clear();
        Cursor cursor = dbHelper.getComments(matchId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String user = cursor.getString(cursor.getColumnIndexOrThrow("user"));
                String commentText = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                String mid = cursor.getString(cursor.getColumnIndexOrThrow("match_id"));
                commentList.add(new Comment(id, mid, user, commentText));
            }
            cursor.close();
        }
        commentAdapter.notifyDataSetChanged();
    }

    // Dialog to update or delete comment
    private void showModifyDeleteDialog(Comment comment) {
        if (!comment.getUser().equals(loggedUser)) {
            Toast.makeText(this, "Vous ne pouvez pas modifier ou supprimer ce commentaire", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ou supprimer ?");
        builder.setPositiveButton("Modifier", (dialog, which) -> showEditCommentDialog(comment));
        builder.setNegativeButton("Supprimer", (dialog, which) -> {
            dbHelper.getWritableDatabase().delete(
                    "comments",
                    "id=?",
                    new String[]{String.valueOf(comment.getId())}
            );
            loadComments();
        });
        builder.show();
    }
}