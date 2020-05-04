package com.example.db_demo_1;              // Change package

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/*
    Class to represent a Product object.
 */
public class Product {
    private int idProduct;
    private String name;
    private int quantity;
    private Date purchase_date;
    private Date expiration_date;
    private boolean expired;
    private int idCategory;

    // (ES) - May not be an ideal location for these constants, but it's fine.
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String APP_DATE_FORMAT = "MM/dd/yyyy";

    // constructors
    public Product() { }

    public Product(int idProduct, String name, int quantity, Date purchase_date,
                   Date expiration_date, boolean expired, int idCategory) {
        this.idProduct = idProduct;
        this.name = name;
        this.quantity = quantity;
        this.purchase_date = purchase_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.idCategory = idCategory;
    }

    // getters & setters
    public int getIdProduct() { return idProduct; }

    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Format date into a string matching the current APP_DATE_FORMAT.
    public String getPurchase_date()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);
        return dateFormat.format(this.purchase_date);
    }

    public void setPurchase_date(String purchase_date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        try {
            this.purchase_date = dateFormat.parse(purchase_date);          // return type is Date
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Format date into a string matching the current APP_DATE_FORMAT.
    public String getExpiration_date()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);
        return dateFormat.format(this.expiration_date);
    }

    public void setExpiration_date(String expiration_date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        try {
            this.expiration_date = dateFormat.parse(expiration_date);          // return type is Date
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isExpired() { return expired; }

    // Since BOOLEAN == INTEGER in SQLite, we convert it w/ ternary statement
    public void setExpired(int expired) { this.expired = (expired == 1) ? true : false; }

    public int getIdCategory() { return idCategory; }

    public void setIdCategory(int idCategory) { this.idCategory = idCategory; }

    // toString method
    @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + idProduct +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", purchase_date=" + purchase_date +
                ", expiration_date=" + expiration_date +
                ", expired=" + expired +
                ", idCategory=" + idCategory +
                '}';
    }

}
