package com.example.foodtracker3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    Class to represent a Product object.
 */
public class Product
{
    // Fields corresponding to 'Product' table attributes
    private long id;
    private String name;
    private int quantity;
    private long idUnit;
    private double unit_amount;
    private Date purchase_date;
    private Date expiration_date;
    private boolean expired;
    private long idCategory;

    // Additional fields for the app
    private Unit unit;
    private Category category;

    private boolean expanded;
    private int iconResource;

    // constructors
    public Product() { }

    public Product(long id, String name, int quantity, long idUnit, double unit_amount,Date purchase_date,
                   Date expiration_date, boolean expired, long idCategory, Unit unit, Category category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.idUnit = idUnit;
        this.unit_amount = unit_amount;
        this.purchase_date = purchase_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.idCategory = idCategory;

        this.unit = unit;
        this.category = category;

        this.iconResource = R.drawable.ic_delete;
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

    public Unit getUnit() { return unit; }

    public void setUnit(Unit unit) { this.unit = unit; }

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

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    public int getIconResource() { return this.iconResource; }

    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

    public boolean getExpanded() { return expanded; }

    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    @Override
    public String toString()
    {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", idUnit=" + idUnit +
                ", unit_amount=" + unit_amount +
                ", purchase_date=" + purchase_date +
                ", expiration_date=" + expiration_date +
                ", expired=" + expired +
                ", idCategory=" + idCategory +
                ", unit=" + unit +
                ", category=" + category +
                ", expanded=" + expanded +
                ", iconResource=" + iconResource +
                '}';
    }

}
