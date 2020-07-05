package com.example.beautyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addNewItem(View view) {
        new DbManager(this, 2);
        startActivity (new Intent (this, AddEditProduct.class));
    }
    public void myBagProducts(View view) {
        new DbManager(this, 2);
        startActivity (new Intent (this, MyBagProducts.class));
    }

    public void showStats(View view) {
        new DbManager(this, 2);
        startActivity (new Intent (this, ShowStats.class));
    }
}
