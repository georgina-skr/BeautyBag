package com.example.beautyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowProductInfo extends AppCompatActivity {
    TextView prName;
    ImageView prLike;
    ImageView prReminder;
    TextView prPrice;
    TextView prPDate;
    TextView prExDate;
    TextView prEnDate;
    TextView prCategory;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product_info);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set arrow back. Back to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get references to view objects
        prName = findViewById(R.id.textViewPrName);
        prLike = findViewById(R.id.imageViewLike);
        prReminder = findViewById(R.id.imageViewReminder);
        prPrice = findViewById(R.id.textViewPrPrice);
        prPDate = findViewById(R.id.textViewPrPurDate);
        prExDate = findViewById(R.id.textViewPrExpDate);
        prEnDate = findViewById(R.id.textViewPrEndDate);
        prCategory = findViewById(R.id.textViewPrCategory);

        if (getIntent().hasExtra("selected_product")){
            product = getIntent().getParcelableExtra("selected_product");
            setTextsAndImages();
        }

    }

    //Set texts to TextViews
    void setTextsAndImages() {
        prName.setText(product.getName());

        if (product.getButtonLike() == 1){
            prLike.setImageResource(R.drawable.ic_thumb_up_like);
        }else if (product.getButtonLike() == -1){
            prLike.setImageResource(R.drawable.ic_thumb_down_dislike);
        }

        if (product.getReminder() == 1){
            prReminder.setImageResource(R.drawable.ic_notifications_on);
        }

        prPrice.setText(product.getPrice());

        prPDate.setText(product.getPurchaseDate());

        if (product.getExpiryDate() != null){
            prExDate.setText(product.getExpiryDate());
        }

        if (product.getEndDate() != null){
            prEnDate.setText(product.getEndDate());
        }

        prCategory.setText(product.getCategory());

    }

    public void deleteProduct(View view) {

        // Delete Confirmation Dialog Box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Delete this?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        deleteConfirmed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    void deleteConfirmed(){
        DbManager sqLiteDatabase = new DbManager(this, 2);

        int res = sqLiteDatabase.deleteData(String.valueOf(product.getId()));

        if(res==-1)
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyBagProducts.class));
        }
    }

    public void editProduct(View view) {
        Intent intent = new Intent(this, AddEditProduct.class);
        intent.putExtra("edit_product", product);
        startActivity(intent);
    }
}
