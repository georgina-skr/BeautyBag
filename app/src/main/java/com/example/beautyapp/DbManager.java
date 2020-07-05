package com.example.beautyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class DbManager extends SQLiteOpenHelper {
    private static final String dbname="ProductDB.db";
    public  DbManager(Context context, int version){
        super(context, dbname, null, version);
    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qry="create table tb_product (id integer primary key autoincrement, name string, buttonLike integer, price string, purchaseDt string, expiryDt string, " +
                "reminder integer, endDt string, category string)";
        sqLiteDatabase.execSQL(qry);
    }
    //drop table and recreate it
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tb_product");
        onCreate(sqLiteDatabase);
    }
    //insert values to database - add new product
    public String addRecord(Product product){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv=new ContentValues();


        cv.put("name", product.getName());
        cv.put("buttonLike", product.getButtonLike());
        cv.put("price", product.getPrice());
        cv.put("purchaseDt", product.getPurchaseDate());
        cv.put("expiryDt", product.getExpiryDate());
        cv.put("reminder", product.getReminder());
        cv.put("endDt", product.getEndDate());
        cv.put("category", product.getCategory());
        long res=sqLiteDatabase.insert("tb_product", null, cv);

        if(res==-1)
            return "Failed";
        else
            return "Successfully Inserted";
    }

    public Cursor getAllData() {
        String query = "SELECT * FROM tb_product";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;
        if (sqLiteDatabase != null){
            cursor = sqLiteDatabase.rawQuery(query, null);
        }
        return cursor;
    }

    public Integer updateData(String id, Product product) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", product.getName());
        cv.put("buttonLike", product.getButtonLike());
        cv.put("price", product.getPrice());
        cv.put("purchaseDt", product.getPurchaseDate());
        cv.put("expiryDt", product.getExpiryDate());
        cv.put("reminder", product.getReminder());
        cv.put("endDt", product.getEndDate());
        cv.put("category", product.getCategory());

        return sqLiteDatabase.update("tb_product", cv, "ID = ?",new String[] { id });

    }

    public Integer deleteData (String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete("tb_product", "ID = ?",new String[] {id});
    }


}
