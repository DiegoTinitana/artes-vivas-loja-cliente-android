package com.diegotinitana.artesvivas.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.diegotinitana.artesvivas.R;
import com.diegotinitana.artesvivas.adapters.EventAdapterRecyclerView;
import com.diegotinitana.artesvivas.data.Store;
import com.diegotinitana.artesvivas.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EventsActivity extends AppCompatActivity {
    private static final String CERO = "0";
    private static final String BARRA = "-";
    //Variables para obtener la fecha
    public final Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    final ArrayList<Event> events = Store.events;

    private Spinner spinner;
    private EventAdapterRecyclerView eventAdapterRecyclerView;
    private EditText date;
    private EditText name;
    private TextInputLayout dateLayout;
    private TextInputLayout nameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        name = findViewById(R.id.event_search_name);
        date = findViewById(R.id.event_search_date);
        dateLayout = findViewById(R.id.event_date);
        nameLayout = findViewById(R.id.event_name);

        RecyclerView eventRecycle = findViewById(R.id.event_recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        eventRecycle.setLayoutManager(linearLayoutManager);
        eventAdapterRecyclerView = new EventAdapterRecyclerView(events,R.layout.cardview_events, this);
        eventRecycle.setAdapter(eventAdapterRecyclerView);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        dateLayout.setVisibility(View.GONE);
                        nameLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        dateLayout.setVisibility(View.VISIBLE);
                        nameLayout.setVisibility(View.GONE);
                        getDate(null);
                        break;
                    case 2:
                        dateLayout.setVisibility(View.GONE);
                        nameLayout.setVisibility(View.GONE);
                        eventAdapterRecyclerView.setItems(events);
                        eventAdapterRecyclerView.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        List<String> categories = new ArrayList<String>();
        categories.add("Nombre");
        categories.add("Fecha");
        categories.add("Eventos");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")) {
                    eventAdapterRecyclerView.setItems(events);
                    eventAdapterRecyclerView.notifyDataSetChanged();
                }else {
                    filterEventForName(s.toString());
                }
            }
        });
    }

    public void filterEventForName(String title) {
        final ArrayList<Event> filterTitle = new ArrayList<>();
        for (Event event: events) {
            if(event.getTitle().length() >= title.length()) {
                String subString = event.getTitle().substring(0,title.length());
                String name = subString != null ? subString : "";
                if(title.equals(name)) {
                    filterTitle.add(event);
                }
            }
        }
        eventAdapterRecyclerView.setItems(filterTitle);
        eventAdapterRecyclerView.notifyDataSetChanged();
    }

    public void filterEventForDate(String date) {
        final ArrayList<Event> filterDate = new ArrayList<>();
        for (Event event: events) {
            if(event.getDate().equals(date)) {
                filterDate.add(event);
            }
        }
        eventAdapterRecyclerView.setItems(filterDate);
        eventAdapterRecyclerView.notifyDataSetChanged();
    }

    public void getDate(View view){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                String dateFormat = year + BARRA + mesFormateado + BARRA + diaFormateado;
                date.setText(dateFormat);
                filterEventForDate(dateFormat);
            }
        },anio, mes, dia);
        recogerFecha.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
