package com.diegotinitana.artesvivas.adapters;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diegotinitana.artesvivas.R;
import com.diegotinitana.artesvivas.models.Event;
import com.diegotinitana.artesvivas.models.Test;

import java.util.ArrayList;

public class TestAdapterRecycleView extends RecyclerView.Adapter<TestAdapterRecycleView.TestViewHolder> {
    private ArrayList<Test> tests;
    private int resources;
    private Activity activity;

    public TestAdapterRecycleView(ArrayList<Test> tests, int resources, Activity activity) {
        this.tests = tests;
        this.resources = resources;
        this.activity = activity;
    }

    public void setItems(ArrayList<Test> tests) {
        this.tests = tests;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resources, viewGroup, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder testViewHolder, int i) {
        Test test = tests.get(i);
        testViewHolder.ask.setText(test.getQuiz());
    }



    @Override
    public int getItemCount() { return tests.size(); }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView ask;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            ask = itemView.findViewById(R.id.test_ask);
        }
    }

}
