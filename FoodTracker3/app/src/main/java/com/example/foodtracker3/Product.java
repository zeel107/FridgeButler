package com.example.foodtracker3;

import java.io.Serializable;
import java.util.Date;

/*
    Class to represent a Product object.
 */
public class Product implements Serializable
{
    // Fields corresponding to 'Product' table attributes
    private long id;
    private String name;
    private int quantity;
    private int idUnit;
    private double unit_amount;
    private Date add_date;
    private Date expiration_date;
    private boolean expired;
    private long idCategory;

    // Additional fields for the app
    private Unit unit;
    private Category category;

    private boolean expanded;
    private int iconResource;

    private boolean edit;

    // constructors
    public Product() { }

    public Product(long id, String name, int quantity, int idUnit, double unit_amount,Date add_date,
                   Date expiration_date, boolean expired, long idCategory, Unit unit, Category category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.idUnit = idUnit;
        this.unit_amount = unit_amount;
        this.add_date = add_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.idCategory = idCategory;

        this.unit = unit;
        this.category = category;

        this.iconResource = R.drawable.ic_delete2;
        this.expanded = false;
        this.edit = false;
    }

    // getters & setters
    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getIdUnit() { return idUnit; }

    public void setIdUnit(int idUnit) { this.idUnit = idUnit; }

    public Unit getUnit() { return unit; }

    public void setUnit(Unit unit) { this.unit = unit; }

    public double getUnit_amount() { return unit_amount; }

    public void setUnit_amount(double unit_amount) { this.unit_amount = unit_amount; }

    public Date getAdd_date() { return this.add_date; }

    public void setAdd_date(Date add_date) { this.add_date = add_date; }

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

    public boolean isEdit() { return edit; }

    public void setEdit(boolean edit) { this.edit = edit; }

    @Override
    public String toString()
    {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", idUnit=" + idUnit +
                ", unit_amount=" + unit_amount +
                ", purchase_date=" + add_date +
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
