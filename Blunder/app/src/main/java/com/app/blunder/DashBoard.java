package com.app.blunder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wenchao.cardstack.CardStack;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class DashBoard extends AppCompatActivity {

    public CardStack mCardStack;
    public CardsDataAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.background));

        PieChart mPieChart = findViewById(R.id.piechart);
        mPieChart.addPieSlice(new PieModel("Likes", 20, Color.parseColor("#007afe")));
        mPieChart.addPieSlice(new PieModel("Matches",30, Color.parseColor("#08a045")));
        mPieChart.addPieSlice(new PieModel("Deceased",40, Color.parseColor("#F6404F")));
         mPieChart.startAnimation();

        mCardStack = findViewById(R.id.container);

        mCardStack.setContentResource(R.layout.card_layout);
        mCardStack.setStackMargin(20);

        mCardAdapter = new CardsDataAdapter(getApplicationContext(),0);
        mCardAdapter.add("test1");
        mCardAdapter.add("test2");
        mCardAdapter.add("test3");
        mCardAdapter.add("test4");
        mCardAdapter.add("test5");

        mCardStack.setAdapter(mCardAdapter);

    }

}