package com.app.blunder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
public class DashBoard extends AppCompatActivity {

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
    }
}