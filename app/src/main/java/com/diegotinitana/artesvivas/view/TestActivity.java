package com.diegotinitana.artesvivas.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.diegotinitana.artesvivas.R;
import com.diegotinitana.artesvivas.data.Store;
import com.diegotinitana.artesvivas.models.Test;
import com.diegotinitana.artesvivas.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = EventsActivity.class.getName();
    private RatingBar ratingBar;
    private TextView testAsk;
    private int index = 0;
    private Map<String,Object> quiz = new HashMap<>();
    private ArrayList<Test> tests = Store.tests;
    private int value = 0;
    private CardView cardViewQuiz;
    private CardView cardViewForm;
    private EditText testName;
    private EditText testAge;
    private EditText testEmail;
    private AlertDialog alertDialog;
    private TextView title;
    private String idEvent;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ratingBar = findViewById(R.id.test_rating_bar);
        testAsk = findViewById(R.id.test_ask);
        cardViewQuiz = findViewById(R.id.card_test);
        cardViewForm = findViewById(R.id.card_form);
        testName = findViewById(R.id.test_name);
        testAge = findViewById(R.id.test_age);
        testEmail = findViewById(R.id.test_email);
        title = findViewById(R.id.title_test);

        user = Store.user.get(0);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                value = (int) rating;
            }
        });
        Intent myIntent = getIntent();
        idEvent = myIntent.getStringExtra("id");
        title.setText( myIntent.getStringExtra("title"));
        testAsk.setText(tests.get(index).getQuiz());
    }

    public void saveTest (View view) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Guardando");
        progress.setMessage("Esperar mientras se guarda el test");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        Map<String, String> client = new HashMap<>();
        client.put("name", testName.getText().toString());
        client.put("age", testAge.getText().toString());
        client.put("email", testEmail.getText().toString());
        Map<String, Object> c = new HashMap<>();
        c.put("client", client);

        db.collection("clients")
                .add(c)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        quiz.put("client", documentReference.getId());
                        quiz.put("createdAt", new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date()));
                        quiz.put("system","android");
                        quiz.put("eventId",idEvent);
                        quiz.put("createdBy", user.getId());
                        Map<String, Object> q = new HashMap<>();
                        q.put("quiz", quiz);
                        db.collection("quiz")
                                .add(q)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progress.hide();
                                        hideSoftKeyboard(testName);
                                        newTest();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void nextTest (View view) {
        if(index < tests.size() - 1) {
            quiz.put(tests.get(index).getId(), value);
            index = index + 1;
            ratingBar.setRating(Float.parseFloat("0"));
            testAsk.setText(tests.get(index).getQuiz());
        }else {
            quiz.put(tests.get(index).getId(), value);
            cardViewQuiz.setVisibility(View.GONE);
            cardViewForm.setVisibility(View.VISIBLE);
        }
    }

    public void backTest (View view) {
        if(index >= 1) {
            index = index - 1;
            ratingBar.setRating(Float.parseFloat("0"));
            testAsk.setText(tests.get(index).getQuiz());
        }
    }

    public void newTest() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Encuesta guardada satisfactoriamente");
        alertDialog.setMessage("Gracias por responder nuestra encuesta");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        index = 0;
                        quiz = new HashMap<>();
                        testName.setText("");
                        testEmail.setText("");
                        testAge.setText("");
                        ratingBar.setRating(Float.parseFloat("0"));
                        testAsk.setText(tests.get(index).getQuiz());
                        cardViewForm.setVisibility(View.GONE);
                        cardViewQuiz.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputManager =
                (InputMethodManager) this.
                        getSystemService(INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
