package com.example.foodtracker3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    Class to represent a Product object.
 */
public class Product {
    private long id;
    private String name;
    private int quantity;
    private long idUnit;
    private double unit_amount;
    private Date purchase_date;
    private Date expiration_date;
    private boolean expired;
    private long idCategory;



    private boolean expanded;

    private int iconResource;

    // (ES) - May not be an ideal location for these constants, but it's fine.
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String APP_DATE_FORMAT = "MM/dd/yyyy";

    // constructors
    public Product() { }

    public Product(long id, String name, int quantity, long idUnit, double unit_amount, Date purchase_date,
                   Date expiration_date, boolean expired, long idCategory, int iconResource) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.idUnit = idUnit;
        this.unit_amount = unit_amount;
        this.purchase_date = purchase_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.idCategory = idCategory;
        this.iconResource = iconResource;
        this.expanded = false;
    }

    // getters & setters
    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getIdUnit() { return idUnit; }

    public void setIdUnit(int idUnit) { this.idUnit = idUnit; }

    public double getUnit_amount() { return unit_amount; }

    public void setUnit_amount(double unit_amount) { this.unit_amount = unit_amount; }

    public Date getPurchase_date() { return this.purchase_date; }

    public void setPurchase_date(Date purchase_date) { this.purchase_date = purchase_date; }

    public Date getExpiration_date() { return this.expiration_date; }

    public void setExpiration_date(Date expiration_date) { this.expiration_date = expiration_date; }

    public boolean isExpired() { return expired; }

    public void setExpired(boolean expired) { this.expired = expired; }

    public long getIdCategory() { return idCategory; }

    public void setIdCategory(int idCategory) { this.idCategory = idCategory; }

    public int getIconResource() { return this.iconResource; }

    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

    public boolean getExpanded() { return expanded; }

    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    // toString method
    @Override
    public String toString() {
        return "Product{" +
                "idProduct=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", idUnit=" + idUnit +
                ", unit_amount=" + unit_amount +
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

    // Convert a APP_DATE_FORMAT String into a Date
    public static Date appStr_toDate(String dateStr)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);
        Date date = null;
        try { date = dateFormat.parse(dateStr); }
        catch (ParseException e) { e.printStackTrace(); }
        return date;                    // Note: May return null if DB date string is mis-formatted
    }

}
