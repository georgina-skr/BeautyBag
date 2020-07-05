package com.example.beautyapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditProduct extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText prName;
    int prLike = 0; // neither liked nor disliked, -1: dislike  0:nothing  1:like
    EditText prPrice;
    TextView prPurchaseDate;
    TextView prExpiryDate;
    int prReminder = 0; //default: reminder off
    TextView prEndDate;
    // Product Category
    TextView categoryText;
    RadioGroup rg;
    RadioButton rbCategory;
    String prCategory;

    // Images (like,dislike,reminder)
    ImageView imLike;
    ImageView imDislike;
    ImageView imReminder;

    //Useful for edit product
    Button addEditBtn;
    boolean editProduct;

    int typedate= 0;
    boolean requiredEmpty = false; //checking for empty required fields
    String expDate;
    String endDate;

    Product product;

    Toolbar toolbar;

    DbManager sqLiteDatabase;

    //oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);
        
        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set arrow back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get references to view objects
        prName = findViewById(R.id.editTextName);
        prPrice =  findViewById(R.id.editTextPrice);
        prPurchaseDate = findViewById(R.id.TextViewPurchaseDate);
        prExpiryDate = findViewById(R.id.TextViewExpiryDate);
        prEndDate = findViewById(R.id.TextViewEnterEndDate);
        imLike = findViewById(R.id.imageViewLike);
        imDislike = findViewById(R.id.imageViewDislike);
        imReminder = findViewById(R.id.imageViewReminder);

        addEditBtn = findViewById(R.id.AddEdit);

        rg = findViewById(R.id.radioGroupCategory); //Radio Group
        categoryText = findViewById(R.id.TextViewCategory);

        //pop up - open calendar
        findViewById(R.id.ButtonPurDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typedate=1;
                showDatePickerDialog(); }
        });
        findViewById(R.id.ButtonExpDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typedate=2;
                showDatePickerDialog(); }
        });
        findViewById(R.id.ButtonEndDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typedate=3;
                showDatePickerDialog(); }
        });

        sqLiteDatabase = new DbManager(this, 2);

        //Check if edit product was pressed and not the add item
        if (getIntent().hasExtra("edit_product")){
            product = getIntent().getParcelableExtra("edit_product");
            editProduct = true;
            setTextsAndImages();
        }
    }

    // Back Arrow. Back to MyBagProducts when editProduct was pressed. Back to MainActivity when Add Item was pressed.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (editProduct) startActivity (new Intent(this, MyBagProducts.class));
                else startActivity (new Intent(this, MainActivity.class));
                return true;
        }
        return false;
    }

    //Set texts and images when edit was pressed
    void setTextsAndImages() {

        addEditBtn.setText("Update Product");
        toolbar.setTitle("Product Update");
        prName.setText(product.getName());

        prLike = 0;
        if (product.getButtonLike() == 1){
            prLike = 1;
            imLike.setImageResource(R.drawable.ic_thumb_up_like);
        }else if (product.getButtonLike() == -1){
            prLike = -1;
            imDislike.setImageResource(R.drawable.ic_thumb_down_dislike);
        }

        prPrice.setText(product.getPrice());

        prPurchaseDate.setText(product.getPurchaseDate());

        if (product.getExpiryDate() != null){
            prExpiryDate.setText(product.getExpiryDate());
        }

        if (product.getReminder() == 1){
            imReminder.setImageResource(R.drawable.ic_notifications_on);
            prReminder = 1;
        }

        if (product.getEndDate() != null){
            prEndDate.setText(product.getEndDate());
        }

        if (product.getCategory().equals("Face")){
            rg.check(R.id.radioButtonFace);
        }else if (product.getCategory().equals("Upper Body")){
            rg.check(R.id.radioButtonUpperBody);
        }else {
            rg.check(R.id.radioButtonBottomBody);
        }
        prCategory = product.getCategory();

    }

    //Add/Edit product to database
    public void addEditRecord(View view) {
        //Required fields: Name, Price, Purchase Date & Category
        requiredFields();
        if (requiredEmpty){ return; }

        expDate = null;
        endDate = null;
        //check if dates are selected
        if (prExpiryDate.getText().length() != 0){
            expDate = prExpiryDate.getText().toString();
        }
        if (prEndDate.getText().length() != 0){
            endDate = prEndDate.getText().toString();
        }

        if (!editProduct) addProduct();
        else updateProduct();
    }

    //Add product to database
    void addProduct() {
        product = new Product(prName.getText().toString(), prLike, prPrice.getText().toString(), prPurchaseDate.getText().toString(), expDate,
                prReminder, endDate, prCategory);
        String res = sqLiteDatabase.addRecord(product);
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
        //Reset
        prName.setText("");
        imLike.setImageResource(R.drawable.ic_no_thumb_up);
        imDislike.setImageResource(R.drawable.ic_no_thumb_down);
        prLike = 0;
        prPrice.setText("");
        prPurchaseDate.setText("");
        prExpiryDate.setText("");
        imReminder.setImageResource(R.drawable.ic_notifications_off);
        prReminder = 0;
        prEndDate.setText("");
        rg.clearCheck();
    }

    //check if expiry date has passed
    boolean passedExpiryDate(){
        boolean expired = false;
        String[] expDateParts = prExpiryDate.getText().toString().split("/");;

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String nowDate = sdf.format(now);
        String[] nowDateParts = nowDate.split("/");

        //check if expiry date has passed
        if (Integer.parseInt(expDateParts[2]) < Integer.parseInt(nowDateParts[2]))  expired = true;
        else if (Integer.parseInt(expDateParts[2]) == Integer.parseInt(nowDateParts[2]))  {
            if (Integer.parseInt(expDateParts[1]) < Integer.parseInt(nowDateParts[1]))  expired = true;
            else if (Integer.parseInt(expDateParts[1]) == Integer.parseInt(nowDateParts[1])){
                if (Integer.parseInt(expDateParts[0]) <= Integer.parseInt(nowDateParts[0]))  expired = true;
            }
        }
        return expired;
    }

    //Update product to database
    void updateProduct() {
        Product updatedProduct = new Product(prName.getText().toString(), prLike, prPrice.getText().toString(), prPurchaseDate.getText().toString(), expDate,
                prReminder, endDate, prCategory);
        int res = sqLiteDatabase.updateData(String.valueOf(product.getId()), updatedProduct);

        if(res==-1)
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show();
            startActivity (new Intent(this, MyBagProducts.class));
        }
    }

    //Required fields: Name, Price, Purchase Date & Category
    void requiredFields() {
        requiredEmpty = false; //initialize
        if(TextUtils.isEmpty(prName.getText().toString())) {
            prName.setError("Required");
            requiredEmpty = true;
        }
        if(TextUtils.isEmpty(prPrice.getText().toString())) {
            prPrice.setError("Required");
            requiredEmpty = true;
        }
        if(TextUtils.isEmpty(prPurchaseDate.getText().toString())) {
            prPurchaseDate.setError("Required");
            requiredEmpty = true;
        }
        if(rg.getCheckedRadioButtonId() == -1) {
            categoryText.setError("Required");
            requiredEmpty = true;
        }
    }

    //Dislike pressed
    public void dislikeProduct(View view) {
        if (prLike != -1) {
            imDislike.setImageResource(R.drawable.ic_thumb_down_dislike);
            //If like was pressed changed its image
            if (prLike == 1){
                imLike.setImageResource(R.drawable.ic_no_thumb_up);
            }
            prLike = -1;
            Toast.makeText(this, "Disliked", Toast.LENGTH_SHORT).show();
        }
        else {
            imDislike.setImageResource(R.drawable.ic_no_thumb_down);
            prLike = 0;
        }
    }
    //Like Pressed
    public void likeProduct(View view) {
        if (prLike != 1) {
            imLike.setImageResource(R.drawable.ic_thumb_up_like);
            //If dislike was pressed changed its image
            if (prLike == -1){
                imDislike.setImageResource(R.drawable.ic_no_thumb_down);
            }
            prLike = 1;
            Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
        }
        else {
            imLike.setImageResource(R.drawable.ic_no_thumb_up);
            prLike = 0;
        }
    }
    //Reminder Pressed
    public void setReminder(View view) {

        if(prExpiryDate.getText().toString().equals("")){
            Toast.makeText(this, "Set Expiry Date", Toast.LENGTH_SHORT).show();
        }
        else if (passedExpiryDate()){
            Toast.makeText(this, "Product already expired", Toast.LENGTH_SHORT).show();
        }
        else if(prReminder == 0) {
            imReminder.setImageResource(R.drawable.ic_notifications_on);
            prReminder = 1;
            Toast.makeText(this, "Reminder On", Toast.LENGTH_SHORT).show();
        }
        else {
            imReminder.setImageResource(R.drawable.ic_notifications_off);
            prReminder = 0;
            Toast.makeText(this, "Reminder Off", Toast.LENGTH_SHORT).show();
        }
    }
    //Radio button option selected
    public void categoryProduct(View view) {
        categoryText.setError(null); //initialize
        //radio button id
        int rbId = rg.getCheckedRadioButtonId();

        rbCategory = findViewById(rbId);
        prCategory = rbCategory.getText().toString();
    }

    //Date
    private void showDatePickerDialog() {
        DatePickerDialog dpd = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    //insert selected date in text field
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dayDate;
        String monthDate;
        if (dayOfMonth < 10) dayDate = "0" + dayOfMonth; else dayDate = String.valueOf(dayOfMonth);
        if (month + 1 < 10) monthDate = "0" + (month + 1); else monthDate = String.valueOf((month+1));

        String fulldate = dayDate + "/" + monthDate + "/" + year;
        if (typedate == 1) {
            prPurchaseDate.setError(null); //initialize
            prPurchaseDate.setText(fulldate);
        }if (typedate == 2){
            imReminder.setImageResource(R.drawable.ic_notifications_off);
            prReminder = 0;
            prExpiryDate.setText(fulldate);

        }if (typedate == 3) {
                prEndDate.setText(fulldate);
        }


    }
}