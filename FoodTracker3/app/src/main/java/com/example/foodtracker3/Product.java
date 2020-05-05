package com.example.foodtracker3;

public class Product
{
    private int iconResource;
    private String foodName;
    private String foodQuantity;
    private String expirationDate;
    public Product(int iconResource, String foodName, String foodQuantity, String expirationDate)
    {
        this.iconResource = iconResource;
        this.foodName = foodName;
        this.foodQuantity = foodQuantity;
        this.expirationDate = expirationDate;
    }

    public int getIconResource()
    {
        return iconResource;
    }

    public String getFoodName()
    {
        return foodName;
    }

    public String getFoodQuantity()
    {
        return foodQuantity;
    }

    public String getExpirationDate()
    {
        return expirationDate;
    }

}
