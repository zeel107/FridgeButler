package com.example.foodtracker3;

/**
 * Class which represents a unit, tied to the Unit database table.
 * A list of Unit objects is stored in the DatabaseHelper class to prevent redundant database queries.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 */
public class Unit
{
    private int id;
    private String name;
    private String abbrev;
    private String plural;

    // constructors
    public Unit() { }

    /**
     * Parameterized constructor which initializes a Unit object.
     * Information corresponds to an entry in the "Unit" database table.
     * @param id The key to an entry in the "Unit" database table.
     * @param name The name of a Unit.
     * @param abbrev The abbreviation for a Unit.
     * @param plural The suffix which makes a Unit name plural.
     */
    public Unit(int id, String name, String abbrev, String plural)
    {
        this.id = id;
        this.name = name;
        this.abbrev = abbrev;
        this.plural = plural;
    }

    // getters & setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAbbrev() { return abbrev; }

    public void setAbbrev(String abbrev) { this.abbrev = abbrev; }

    public String getPlural() { return plural; }

    public void setPlural(String plural) { this.plural = plural; }
}
