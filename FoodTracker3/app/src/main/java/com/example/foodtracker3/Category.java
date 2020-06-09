package com.example.foodtracker3;

/**
 * Class which represents a category, tied to the Category database table.
 * A list of Category objects is stored in the DatabaseHelper class to prevent redundant database queries.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */
public class Category
{
    private long id;
    private String name;
    private String description;

    // constructors
    public Category() { }

    /**
     * Parameterized constructor which initializes a Category object.
     * Information corresponds to an entry in the "Category" database table.
     * @param id The key to an entry in the "Category" database table.
     * @param name The name of a Category.
     * @param description The description of a Category.
     */
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