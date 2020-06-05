package com.example.foodtracker3;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/*
    Class to represent a Category.
 */
public class Category
{
    private long id;
    private String name;
    private String description;

    // constructors
    public Category() { }

    public Category(long id, String name, String description)
    {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}

// For searching by category ID
class CategoryComparator implements Comparator<Category>
{
    @Override
    public int compare(Category cat1, Category cat2)
    {
        if (cat1.getId() > cat2.getId())        return 1;
        else if (cat1.getId() == cat2.getId())  return 0;
        else                                    return -1;
    }
}