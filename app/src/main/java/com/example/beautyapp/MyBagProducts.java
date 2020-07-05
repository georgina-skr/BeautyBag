package com.example.beautyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyBagProducts extends AppCompatActivity implements ProductAdapter.OnProductListener {
    //Category Butoons
    Button allBtn;
    Button faceBtn;
    Button ubodyBtn;
    Button bbodyBtn;

    //Database
    DbManager sqLiteDatabase;
    //Recycler view, list of products
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product>products = new ArrayList<>();

    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bag_products);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set arrow back. Back to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allBtn = findViewById(R.id.buttonAll);
        faceBtn = findViewById(R.id.buttonFace);
        ubodyBtn = findViewById(R.id.buttonUBody);
        bbodyBtn = findViewById(R.id.buttonBBody);

        sqLiteDatabase = new DbManager(this, 2);

        try {
            dataToList();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(products,this, this);
        recyclerView.setAdapter(adapter);
    }

    void dataToList() throws ParseException {
        Cursor cursor = sqLiteDatabase.getAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "There aren't any products", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()){
                products.add(new Product(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), Integer.parseInt(cursor.getString(6)),
                        cursor.getString(7), cursor.getString(8)));
            }

            //Notification
            for (int i = 0; i < products.size() ; i++){
                if (products.get(i).getReminder() == 1){
                    boolean notif = false;
                    String[] expDateParts = products.get(i).getExpiryDate().split("/");

                    Date now = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String nowDate = sdf.format(now);
                    String[] nowDateParts = nowDate.split("/");

                    //check if expiry date has passed
                    if (Integer.parseInt(expDateParts[2]) < Integer.parseInt(nowDateParts[2]))  notif = true;
                    else if (Integer.parseInt(expDateParts[2]) == Integer.parseInt(nowDateParts[2]))  {
                        if (Integer.parseInt(expDateParts[1]) < Integer.parseInt(nowDateParts[1]))  notif = true;
                        else if (Integer.parseInt(expDateParts[1]) == Integer.parseInt(nowDateParts[1])){
                            if (Integer.parseInt(expDateParts[0]) <= Integer.parseInt(nowDateParts[0]))  notif = true;
                        }
                    }
                    if (notif){
                        //Notification
                        NotifyMe.Builder notifyMe = new NotifyMe.Builder(getApplicationContext());
                        notifyMe.title("Expired Product");
                        notifyMe.content(products.get(i).getName());
                        notifyMe.delay(2);
                        notifyMe.small_icon(R.drawable.ic_launcher_foreground);
                        notifyMe.color(170,58,95,255);//Color of notification header
                        notifyMe.led_color(0,255,255,255);
                        notifyMe.large_icon(R.drawable.ic_notifications_on);//Icon resource by ID
                        notifyMe.addAction(new Intent(), "Dismiss", true, false); //The action will call the intent when pressed
                        notifyMe.build();

                        products.get(i).setReminder(0);
                        sqLiteDatabase.updateData(String.valueOf(products.get(i).getId()),products.get(i));
                    }
                }
            }
        }
    }


    //Category Buttons onClick, change button
    public void categoryButtons(View view) {
        Button categoryBtn;
        String pressedCategory;
        categoryBtn = findViewById(view.getId());
        categoryBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_pressed)); //set pressed button background
        categoryBtn.setTextColor(Color.parseColor("#FFFFFF")); //set white text
        pressedCategory = categoryBtn.getText().toString().toLowerCase();

        if (pressedCategory.equals("upper bd")) pressedCategory = "upper body";
        else if (pressedCategory.equals("bottom bd")) pressedCategory = "bottom body";

        //show products from the right category
        if (!pressedCategory.equals(allBtn.getText().toString().toLowerCase())){
            adapter.getFilter().filter(pressedCategory);
        }else {
            adapter.getFilter().filter(null);
        }

        //set to default button the previous pressed button
        if (view.getId() != R.id.buttonAll && allBtn.getCurrentTextColor() == Color.WHITE){
            allBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            allBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (view.getId() != R.id.buttonFace && faceBtn.getCurrentTextColor() == Color.WHITE){
            faceBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            faceBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (view.getId() != R.id.buttonUBody && ubodyBtn.getCurrentTextColor() == Color.WHITE){
            ubodyBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            ubodyBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (view.getId() != R.id.buttonBBody && bbodyBtn.getCurrentTextColor() == Color.WHITE){
            bbodyBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            bbodyBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    // Enable/Disable category buttons
    void enableCategoryButtons(boolean enable){
        if (enable){
            allBtn.setClickable(true);
            faceBtn.setClickable(true);
            ubodyBtn.setClickable(true);
            bbodyBtn.setClickable(true);
        }else {
            allBtn.setClickable(false);
            faceBtn.setClickable(false);
            ubodyBtn.setClickable(false);
            bbodyBtn.setClickable(false);

            //Set All button as default
            allBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_pressed)); //set pressed button background
            allBtn.setTextColor(Color.parseColor("#FFFFFF")); //set white text
            faceBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            faceBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            ubodyBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            ubodyBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            bbodyBtn.setBackground(getResources().getDrawable(R.drawable.custom_button_default));
            bbodyBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    //Search by name or category
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(0);
        // When 'X' is  pressed, enable category buttons
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                enableCategoryButtons(true);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                enableCategoryButtons(false);
                adapter.getFilter().filter(query);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchView.clearFocus();
                enableCategoryButtons(true);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                    enableCategoryButtons(false);
                    adapter.getFilter().filter(newText);
                return false;
            }

        });
        return true;
    }

    //Click on a product -> starts new activity "ShowProductsInfo"
    @Override
    public void onProductClick(int position) {
        Intent intent = new Intent(this, ShowProductInfo.class);
        intent.putExtra("selected_product", products.get(position));
        startActivity(intent);
    }
}
