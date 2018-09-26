package com.diegotinitana.artesvivas.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diegotinitana.artesvivas.R;
import com.diegotinitana.artesvivas.models.Event;
import com.diegotinitana.artesvivas.view.EventsActivity;
import com.diegotinitana.artesvivas.view.TestActivity;

import java.util.ArrayList;

public class EventAdapterRecyclerView extends RecyclerView.Adapter<EventAdapterRecyclerView.EventViewHolder> {
    private ArrayList<Event> events;
    private int resources;
    private Activity activity;

    public EventAdapterRecyclerView(ArrayList<Event> events, int resources, Activity activity) {
        this.events = events;
        this.resources = resources;
        this.activity = activity;
    }
    public void setItems(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resources, viewGroup, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        final Event event = events.get(i);
        eventViewHolder.titleEvent.setText(event.getTitle());
        eventViewHolder.placeEvent.setText(event.getPlace());
        eventViewHolder.dateEvent.setText(event.getDate());
        eventViewHolder.endEvent.setText(event.getEnd());
        eventViewHolder.startEvent.setText(event.getStart());
        eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToTest = new Intent(activity, TestActivity.class);
                goToTest.putExtra("title",event.getTitle());
                goToTest.putExtra("id",event.getId());
                activity.startActivity(goToTest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{

        private TextView titleEvent;
        private TextView placeEvent;
        private TextView dateEvent;
        private TextView startEvent;
        private TextView endEvent;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleEvent = itemView.findViewById(R.id.title_card_event);
            placeEvent = itemView.findViewById(R.id.place_card_event);
            dateEvent  = itemView.findViewById(R.id.date_card_event);
            startEvent = itemView.findViewById(R.id.start_card_event);
            endEvent   = itemView.findViewById(R.id.end_card_event);
        }
    }
}
