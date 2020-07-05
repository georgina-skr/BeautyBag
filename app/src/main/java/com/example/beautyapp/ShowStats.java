package com.example.beautyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowStats extends AppCompatActivity {

    //Barchart
    BarChart barChart;
    ArrayList<BarEntry> totalmoney = new ArrayList<>();
    ArrayList<BarEntry> totalproducts = new ArrayList<>();
    BarDataSet barDataSetMoney;
    BarData barData;
    BarDataSet barDataSetProd;

    //Database
    DbManager sqLiteDatabase;
    int yearNow;
    int monthNow;

    //Total money lists per year and per month
    ArrayList<Float> moneyY = new ArrayList<>();
    ArrayList<Float> moneyM = new ArrayList<>();

    //Total products lists per year and per month
    ArrayList<Integer> productsY = new ArrayList<>();
    ArrayList<Integer> productsM = new ArrayList<>();

    //List with products from Database
    List<Product> products = new ArrayList<>();

    //Buttons
    Button yearBtn;
    Button monthBtn;

    TextView title;

    Boolean dataexist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set arrow back. Back to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barChart = findViewById(R.id.barChart);
        yearBtn = findViewById(R.id.buttonYear);
        monthBtn = findViewById(R.id.buttonMonth);
        title = findViewById(R.id.textViewTitle);

        title.setText("For the last 5 years");

        //Calculate current year and month
        yearNow = Calendar.getInstance().get(Calendar.YEAR);
        monthNow = Calendar.getInstance().get(Calendar.MONTH) + 1;

        sqLiteDatabase = new DbManager(this, 2);

        //Initialize money and products sums. Years for last 5 years. 12 Months
        for (int i = 0; i < 5; i++){
            moneyY.add(0f);
            productsY.add(0);
        }
        for (int i = 0; i < 12; i++){
            moneyM.add(0f);
            productsM.add(0);
        }

        //Get data from Database
        dataToList();
    }

    private void setBarChart(String period) {
        if (period.toLowerCase().equals("year")) {
            //Chart for the last 5 years
            ArrayList<String> years = new ArrayList();
            years.add(Integer.toString(yearNow - 4));
            years.add(Integer.toString(yearNow - 3));
            years.add(Integer.toString(yearNow - 2));
            years.add(Integer.toString(yearNow - 1));
            years.add(Integer.toString(yearNow));

            //For money
            totalmoney = new ArrayList<>();
            totalmoney.add(new BarEntry(0, moneyY.get(0)));
            totalmoney.add(new BarEntry(1, moneyY.get(1)));
            totalmoney.add(new BarEntry(2, moneyY.get(2)));
            totalmoney.add(new BarEntry(3, moneyY.get(3)));
            totalmoney.add(new BarEntry(4, moneyY.get(4)));

            //For products
            totalproducts = new ArrayList<>();
            totalproducts.add(new BarEntry(0, productsY.get(0)));
            totalproducts.add(new BarEntry(1, productsY.get(1)));
            totalproducts.add(new BarEntry(2, productsY.get(2)));
            totalproducts.add(new BarEntry(3, productsY.get(3)));
            totalproducts.add(new BarEntry(4, productsY.get(4)));

            //For money
            barDataSetMoney = new BarDataSet(totalmoney, "Total Money");
            barDataSetMoney.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSetMoney.setValueTextColor(Color.BLACK);
            barDataSetMoney.setValueTextSize(16f);

            //For products
            barDataSetProd = new BarDataSet(totalproducts, "Total Products");
            barDataSetProd.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSetProd.setValueTextColor(Color.BLACK);
            barDataSetProd.setValueTextSize(16f);

            barData = new BarData(barDataSetMoney, barDataSetProd);
            barData.setValueFormatter(new LargeValueFormatter());
            barChart.setFitBars(true);
            barChart.setData(barData);
            float groupSpace = 0.4f;
            float barSpace = 0.02f;
            float barWidth = 0.25f;

            barChart.getBarData().setBarWidth(barWidth);
            barChart.groupBars(0, groupSpace, barSpace);
            barChart.getDescription().setText("1st: Money 2nd: Products");

            //X-axis
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawLabels(true);
            xAxis.setLabelCount(years.size());
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(years));

            barChart.animateY(2000);
            barChart.invalidate();
        } else {
            //Chart for the months of this year
            ArrayList<String> months = new ArrayList();
            months.add("Jan");
            months.add("Feb");
            months.add("Mar");
            months.add("Apr");
            months.add("May");
            months.add("Jun");
            months.add("Jul");
            months.add("Aug");
            months.add("Sep");
            months.add("Oct");
            months.add("Nov");
            months.add("Dec");


            //Money & products
            totalmoney = new ArrayList<>();
            totalproducts = new ArrayList<>();
            for (int i = 0; i < monthNow; i++){
                totalmoney.add(new BarEntry(i, moneyM.get(i)));
                totalproducts.add(new BarEntry(i, productsM.get(i)));
            }


            //For money
            barDataSetMoney = new BarDataSet(totalmoney, "Total Money");
            barDataSetMoney.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSetMoney.setValueTextColor(Color.BLACK);
            barDataSetMoney.setValueTextSize(16f);

            //For products
            barDataSetProd = new BarDataSet(totalproducts, "Total Products");
            barDataSetProd.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSetProd.setValueTextColor(Color.BLACK);
            barDataSetProd.setValueTextSize(16f);

            barData = new BarData(barDataSetMoney, barDataSetProd);
            barData.setValueFormatter(new LargeValueFormatter());
            barChart.setFitBars(true);
            barChart.setData(barData);
            float groupSpace = 0.4f;
            float barSpace = 0.02f;
            float barWidth = 0.25f;

            barChart.getBarData().setBarWidth(barWidth);
            barChart.groupBars(0, groupSpace, barSpace);
            barChart.getDescription().setText("1st: Money 2nd: Products");

            //X-axis
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawLabels(true);
            xAxis.setLabelCount(months.size());
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

            barChart.animateY(2000);
            barChart.invalidate();
        }


    }



    //Calculate money and products per year for the last 5 years and per month for the current year
    void calculateSums() {
        for (int i = 0; i < products.size(); i++) {
            String[] purDateParts = products.get(i).getPurchaseDate().split("/");
            if (Integer.parseInt(purDateParts[2]) == yearNow) {
                moneyY.set(4, moneyY.get(4) + Float.parseFloat(products.get(i).getPrice()));
                productsY.set(4,productsY.get(4)+1);
                for (int j = 0; j < monthNow; j++){
                    if (Integer.parseInt(purDateParts[1]) == j+1){
                        moneyM.set(j, moneyM.get(j) + Float.parseFloat(products.get(i).getPrice()));
                        productsM.set(j, productsM.get(j)+1);
                    }
                }
            }else if (Integer.parseInt(purDateParts[2]) == yearNow - 1){
                moneyY.set(3, moneyY.get(3) + Float.parseFloat(products.get(i).getPrice()));
                productsY.set(3,productsY.get(3)+1);
            }else if (Integer.parseInt(purDateParts[2]) == yearNow - 2){
                moneyY.set(2, moneyY.get(2) + Float.parseFloat(products.get(i).getPrice()));
                productsY.set(2,productsY.get(2)+1);
            }else if (Integer.parseInt(purDateParts[2]) == yearNow - 3){
                moneyY.set(1, moneyY.get(1) + Float.parseFloat(products.get(i).getPrice()));
                productsY.set(1,productsY.get(1)+1);
            }else if (Integer.parseInt(purDateParts[2]) == yearNow - 4){
                moneyY.set(0, moneyY.get(0) + Float.parseFloat(products.get(i).getPrice()));
                productsY.set(0,productsY.get(0)+1);
            }
        }
    }

    //Get data from Database
    void dataToList() {
        Cursor cursor = sqLiteDatabase.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "There aren't any products", Toast.LENGTH_SHORT).show();
            dataexist = false;
        } else {
            while (cursor.moveToNext()) {
                products.add(new Product(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), Integer.parseInt(cursor.getString(6)),
                        cursor.getString(7), cursor.getString(8)));
            }

            dataexist = true;
            calculateSums();

            setBarChart("YEAR");
        }
    }

    public void periodButtons(View view) {
        Button pressedBtn;
        pressedBtn = findViewById(view.getId());
        pressedBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_pressed)); //set pressed button background
        pressedBtn.setTextColor(Color.parseColor("#FFFFFF")); //set white text

        //show right bar chart
        if (dataexist) setBarChart(pressedBtn.getText().toString().toLowerCase());

        //set to default button the previous pressed button
        if (view.getId() != R.id.buttonYear && yearBtn.getCurrentTextColor() == Color.WHITE){
            title.setText("For the months of this year");
            yearBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            yearBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            title.setText("For the last 5 years");
            monthBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            monthBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
