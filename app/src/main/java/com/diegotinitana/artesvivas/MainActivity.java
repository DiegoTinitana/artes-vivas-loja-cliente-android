package com.diegotinitana.artesvivas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diegotinitana.artesvivas.data.Store;
import com.diegotinitana.artesvivas.models.Event;
import com.diegotinitana.artesvivas.models.Place;
import com.diegotinitana.artesvivas.models.Test;
import com.diegotinitana.artesvivas.models.User;
import com.diegotinitana.artesvivas.view.EventsActivity;
import com.diegotinitana.artesvivas.view.TestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = EventsActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button login;
    private ProgressDialog progress;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Cargando....");
        progress.setMessage("Esperar mientras se actualizan los datos");
        progress.setCancelable(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email.getText().toString());
                if(matcher.matches() && password.getText().toString().length() > 6) {
                    progress.show();
                    login();
                }else {
                    alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("El Email o Password incorrectos");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    progress.hide();
                                    email.setText("");
                                    password.setText("");
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    public void login() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getData();
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            final ArrayList<User> usr = new ArrayList<>();
                            User u = new User(user.getDisplayName(), user.getUid());
                            usr.add(u);
                            Store.user = usr ;
                        } else {
                            progress.hide();
                            alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage(task.getException().getMessage());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        // ...
                    }
                });

    }

    public void goToEventsList() {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
        finish();
    }

    public  void getData () {
        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<Place> places = new ArrayList<>();
        final ArrayList<Test> tests = new ArrayList<>();
        db.collection("places").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Place place = new Place(document.getData().get("address").toString(),
                                document.getData().get("capacity").toString(),
                                document.getData().get("name").toString(), document.getId());
                        places.add(place);
                    }
                    db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(QueryDocumentSnapshot document: task.getResult()) {
                                    String placeName = "";
                                    for (Place place: places) {
                                        if(place.getId().equals(document.getData().get("place").toString())) {
                                            placeName = place.getName().toString();
                                        }
                                    }
                                    Event event = new Event(document.getData().get("name").toString(),
                                            document.getData().get("date").toString(),
                                            document.getData().get("init").toString(),
                                            document.getData().get("end").toString(),
                                            document.getId(),placeName
                                    );
                                    events.add(event);
                                }
                                db.collection("test").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Test test = new Test(document.getData().get("ask").toString(), document.getId());
                                                tests.add(test);
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        progress.dismiss();
                                        Store.events = events;
                                        Store.tests = tests;
                                        Store.places = places;
                                        goToEventsList();
                                    }
                                });
                            }else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
