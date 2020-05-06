package com.example.foodtracker3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

    //public Product(, String foodName, String foodQuantity, String expirationDate)

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
    private int iconResource;

    // (ES) - May not be an ideal location for these constants, but it's fine.
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String APP_DATE_FORMAT = "MM/dd/yyyy";

    // constructors
    public Product() { }

    public Product(int idProduct, String name, int quantity, Date purchase_date,
                   Date expiration_date, boolean expired, int idCategory, int iconResource) {
        this.idProduct = idProduct;
        this.name = name;
        this.quantity = quantity;
        this.purchase_date = purchase_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.idCategory = idCategory;
        this.iconResource = iconResource;
    }

    // getters & setters
    public int getIdProduct() { return idProduct; }

    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Date getPurchase_date() { return this.purchase_date; }

    public void setPurchase_date(Date purchase_date) { this.purchase_date = purchase_date; }

    public Date getExpiration_date() { return this.expiration_date; }

    public void setExpiration_date(Date expiration_date) { this.expiration_date = expiration_date; }

    public boolean isExpired() { return expired; }

    public void setExpired(boolean expired) { this.expired = expired; }

    public int getIdCategory() { return idCategory; }

    public void setIdCategory(int idCategory) { this.idCategory = idCategory; }

    public int getIconResource() { return this.iconResource; }

    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

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

    // ---- Static Utility Methods ----
    // Maybe we could put these in their own Utility class or something. These are general and don't necessarily
    // have to be attached to the Product class.

    // Convert a Date to an APP_DATE_FORMAT String
    public static String date_toAppStr(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);
        return dateFormat.format(date);
    }

    // Convert a Date to an DB_DATE_FORMAT String
    public static String date_toDbStr(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        return dateFormat.format(date);
    }

    // Convert a DB_DATE_FORMAT String into a Date
    public static Date dbStr_toDate(String dateStr)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        Date date = null;
        try { date = dateFormat.parse(dateStr); }
        catch (ParseException e) { e.printStackTrace(); }
        return date;                    // Note: May return null if DB date string is mis-formatted
    }

}
