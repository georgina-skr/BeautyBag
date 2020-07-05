package com.example.beautyapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int id;
    private String name;
    private int buttonLike; // -1: dislike  0:nothing  1:like
    private String price;
    private String purchaseDate;
    private String expiryDate;
    private int reminder;
    private String endDate;
    private String category;


    //Constructors
    public  Product(String name, int buttonLike, String price, String purchaseDate, String expiryDate, int reminder, String endDate, String category){
        this.name = name;
        this.buttonLike = buttonLike;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.reminder = reminder;
        this.endDate = endDate;
        this.category = category;
    }

    public  Product(int id, String name, int buttonLike, String price, String purchaseDate, String expiryDate, int reminder, String endDate, String category){
        this.id = id;
        this.name = name;
        this.buttonLike = buttonLike;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.reminder = reminder;
        this.endDate = endDate;
        this.category = category;
    }


    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        buttonLike = in.readInt();
        price = in.readString();
        purchaseDate = in.readString();
        expiryDate = in.readString();
        reminder = in.readInt();
        endDate = in.readString();
        category = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getButtonLike() {
        return buttonLike;
    }

    public void setButtonLike(int buttonlike) {
        this.buttonLike = buttonlike;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public int getReminder() { return reminder; }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(buttonLike);
        dest.writeString(price);
        dest.writeString(purchaseDate);
        dest.writeString(expiryDate);
        dest.writeInt(reminder);
        dest.writeString(endDate);
        dest.writeString(category);
    }
}
