package com.example.foodtracker3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A helper class for managing app-specific SQLite database operations.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 * */
public class DatabaseHelper extends SQLiteOpenHelper
{
    WeakReference<Context> context;     // Prevent potential memory leaks
    ArrayList<Unit> units;
    ArrayList<Category> categories;

    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String APP_DATE_FORMAT = "MM/dd/yyyy";
    /*
        ---- DATABASE STRING CONSTANTS ----
     */
    private static final String DB_NAME = "database.db";

    private static final String TABLE_ALIAS_Product = "pro";
    private static final String TABLE_Product = "Product";
    private static final String     COLUMN_PRODUCT_id               = "id";
    private static final String     COLUMN_PRODUCT_name             = "name";
    private static final String     COLUMN_PRODUCT_quantity         = "quantity";
    private static final String     COLUMN_PRODUCT_idUnit           = "idUnit";
    private static final String     COLUMN_PRODUCT_unit_amount      = "unit_amount";
    private static final String     COLUMN_PRODUCT_purchase_date    = "add_date";
    private static final String     COLUMN_PRODUCT_expiration_date  = "expiration_date";
    private static final String     COLUMN_PRODUCT_expired          = "expired";
    private static final String     COLUMN_PRODUCT_idCategory       = "idCategory";

    private static final String TABLE_ALIAS_Category = "cat";
    private static final String TABLE_Category = "Category";
    private static final String     COLUMN_CATEGORY_id          = "id";
    private static final String     COLUMN_CATEGORY_name        = "name";
    private static final String     COLUMN_CATEGORY_description = "description";

    private static final String TABLE_ALIAS_Unit = "uni";
    private static final String TABLE_Unit = "Unit";
    private static final String     COLUMN_UNIT_id      = "id";
    private static final String     COLUMN_UNIT_name    = "name";
    private static final String     COLUMN_UNIT_abbrev  = "abbrev";
    private static final String     COLUMN_UNIT_plural  = "plural";


    /**
     * Default constructor.
     * @param context The current context.
     */
    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = new WeakReference<Context>(context);
        this.units = this.getUnits();
        this.categories = this.getCategories();
    }

    /**
     * Private constructor that creates a database with an alternate name.
     * ** Currently only used to create in-memory database for unit testing. **
     * @param context The current context.
     * @param alternateDbName The name of the database being created.
     */
    private DatabaseHelper(@Nullable Context context, String alternateDbName)
    {
        super(context, alternateDbName, null, 1);
        this.context = new WeakReference<Context>(context);
        this.units = this.getUnits();
        this.categories = this.getCategories();
    }

    /**
     * Create an in-memory memory database, which ceases to exist once the connection is closed.
     * ** Currently only used for unit testing. **
     * @param context The current context.
     * @return A new DatabaseHelper object for an in-memory database.
     */
    public static DatabaseHelper createTempDB(@Nullable Context context)
    {
        return new DatabaseHelper(context, ":memory:");     // https://www.sqlite.org/inmemorydb.html
    }

    /**
     * Builds a SQLiteDatabase. Creates tables and inserts default records.
     * This gets called after a call to getReadableDatabase() or getWritableDatabase() ONLY if the database doesn't already exist.
     * If the database already exists, then that database will be opened and used, and this code will not be called.
     * @param db The SQLiteDatabase being affected.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create 'Product' table
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_Product
                + " ("
                + COLUMN_PRODUCT_id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + COLUMN_PRODUCT_name               + " TEXT, "
                + COLUMN_PRODUCT_quantity           + " INTEGER, "
                + COLUMN_PRODUCT_idUnit             + " INTEGER REFERENCES " + TABLE_Unit + "(" + COLUMN_UNIT_id + "), "
                + COLUMN_PRODUCT_unit_amount        + " REAL, "
                + COLUMN_PRODUCT_purchase_date      + " DATE, "                 // Note: DATE == TEXT
                + COLUMN_PRODUCT_expiration_date    + " DATE, "
                + COLUMN_PRODUCT_expired            + " BOOLEAN, "              // Note: BOOLEAN == INTEGER
                + COLUMN_PRODUCT_idCategory         + " INTEGER REFERENCES " + TABLE_Category + "(" + COLUMN_CATEGORY_id + ") ON DELETE SET DEFAULT"
                + ");";
        db.execSQL(createTableStatement);

        // Create 'Category' table
        createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_Category
                + " ("
                + COLUMN_CATEGORY_id            + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0, "            // ES - removed NOT NULL
                + COLUMN_CATEGORY_name          + " TEXT, "
                + COLUMN_CATEGORY_description   + " TEXT "
                + ");" ;
        db.execSQL(createTableStatement);

        // Create 'Unit' table
        createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_Unit
                + " ("
                + COLUMN_UNIT_id        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_UNIT_name      + " TEXT, "
                + COLUMN_UNIT_abbrev    + " TEXT, "
                + COLUMN_UNIT_plural    + " TEXT "
                + ");" ;
        db.execSQL(createTableStatement);

        String sampleInsert;

        // Add sample categories for testing
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (0, 'None', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (1, 'Meat', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (2, 'Fruits', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (3, 'Vegetables', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (4, 'Grains', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (5, 'Dairy', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (6, 'Other', '');";
        db.execSQL(sampleInsert);

        // Add sample units
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (0, 'count', 'ct', '');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (1, 'ounce', 'oz', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (2, 'gram', 'g', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (3, 'pound', 'lb', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (4, 'liter', 'lt', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (5, 'milliliter', 'mL', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (6, 'quart', 'qt', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (7, 'box', 'box', 'es');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (8, 'bag', 'bag', 's');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (9, 'package', 'pkg', 's');";
        db.execSQL(sampleInsert);
    }

    /**
     * Handles database configurations, called after database has been created.
     * @param db The SQLiteDatabase whose configurations are being affected.
     */
    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        // Enable foreign keys
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Handles any necessary data migration whenever database version number changes.
     * It prevents old app versions from breaking when you change database design.
     * @param db The SQLiteDatabase whose version has changed.
     * @param oldVersion Existing database version number.
     * @param newVersion New database version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Implement data migration methods here
    }

    /**
     * Insert a product into {@value #TABLE_Product} database table.
     * @param prod The product object being inserted.
     * @return TRUE if insert was successful, FALSE if not.
     */
    public boolean addProduct(Product prod)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_name, prod.getName());
        cv.put(COLUMN_PRODUCT_quantity, prod.getQuantity());
        cv.put(COLUMN_PRODUCT_idUnit, prod.getIdUnit());
        cv.put(COLUMN_PRODUCT_unit_amount, prod.getUnit_amount());
        cv.put(COLUMN_PRODUCT_purchase_date, date_toDbStr(prod.getAdd_date()) );
        cv.put(COLUMN_PRODUCT_expiration_date, date_toDbStr(prod.getExpiration_date()) );
        cv.put(COLUMN_PRODUCT_expired, prod.isExpired());
        cv.put(COLUMN_PRODUCT_idCategory, prod.getIdCategory());

        long insert = -1;
        try
        {
            insert = db.insertOrThrow(TABLE_Product, null, cv);
        }
        catch (SQLException e)
        {   // if error, Clean & Rebuild Project
            if (com.example.foodtracker3.BuildConfig.DEBUG)        // Only show toast if we are debugging.
            {
                if (context != null)  Toast.makeText(context.get(), "addOne(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DBH.addProduct()", e.getMessage(), e );           // Log the error
        }

        db.close();     // close database when finished

        if (insert == -1)   return false;
        else
        {
            prod.setId(insert);        // Store ROWID of the new product as its productID. This is safe because Product table declares an INTEGER PRIMARY KEY,
            return true;            // therefore making it an alias for the ROWID. (https://www.sqlite.org/rowidtable.html)
        }
    }

    /**
     * Update an existing product record in {@value #TABLE_Product} database table.
     * @param prod The product being updated.
     * @return TRUE if update was successful, FALSE if not.
     */
    public boolean updateProduct(Product prod)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_name, prod.getName());
        cv.put(COLUMN_PRODUCT_quantity, prod.getQuantity());
        cv.put(COLUMN_PRODUCT_idUnit, prod.getIdUnit());
        cv.put(COLUMN_PRODUCT_unit_amount, prod.getUnit_amount());
        cv.put(COLUMN_PRODUCT_purchase_date, date_toDbStr(prod.getAdd_date()) );
        cv.put(COLUMN_PRODUCT_expiration_date, date_toDbStr(prod.getExpiration_date()) );
        cv.put(COLUMN_PRODUCT_expired, prod.isExpired());
        cv.put(COLUMN_PRODUCT_idCategory, prod.getIdCategory());

        String whereClause = COLUMN_PRODUCT_id + " = " + prod.getId();

        int rowsAffected = db.update(TABLE_Product,  cv, whereClause, null);
        db.close();     // close database when finished

        if (rowsAffected > 0)   return true;        // One row should've been affected by the UPDATE
        else                    return false;
    }

    /**
     * Delete a product record from {@value #TABLE_Product} database table.
     * @param prod The product being deleted.
     * @return TRUE if delete was successful, FALSE if not.
     */
    public boolean removeProduct(Product prod)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_PRODUCT_id + " = " + prod.getId() + ";";

        int rowsAffected = db.delete(TABLE_Product, whereClause, null);
        db.close();     // Close database when finished

        if (rowsAffected > 0)      return true;    // One row should've been affected by the DELETE
        else                       return false;
    }

    /**
     * Delete a category record from {@value #TABLE_Category} database table.
     * @param cat The category being deleted.
     * @return TRUE if delete was successful, FALSE if not.
     */
    public boolean removeCategory(Category cat)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_CATEGORY_id + " = " + cat.getId() + ";";

        int rowsAffected = db.delete(TABLE_Category, whereClause, null);
        db.close();     // Close database when finished

        if (rowsAffected > 0)       // One row should've been affected by the DELETE
        {
            categories.remove(cat);
            return true;
        }
        else                       return false;
    }

    /**
     * Insert a category record to the {@value #TABLE_Category} database table.
     * @param cat The category object being inserted.
     * @return TRUE if insert was successful, FALSE if not.
     */
    public boolean addCategory(Category cat)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY_name, cat.getName());
        cv.put(COLUMN_CATEGORY_description, cat.getDescription());

        long insert = -1;
        try
        {
            insert = db.insertOrThrow(TABLE_Category, null, cv);
        }
        catch (SQLException e)
        {   // if error, Clean & Rebuild Project
            if (com.example.foodtracker3.BuildConfig.DEBUG)        // Only show toast if we are debugging.
            {
                if (context != null)  Toast.makeText(context.get(), "addCategory(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DBH.addCategory()", e.getMessage(), e );           // Log the error
        }

        db.close();     // Close database when finished

        if (insert == -1)   return false;
        else
        {
            cat.setId(insert);          // insert == rowID of newly inserted row.
            this.categories.add(cat);
            return true;                // https://www.sqlite.org/rowidtable.html
        }
    }

    /**
     * Retrieve all product from the {@value #TABLE_Product} database table.
     * @return An ArrayList with containing ALL of the products.
     */
    public ArrayList<Product> getAllProducts()
    {
        /*
            SELECT pro.id, pro.name, pro.quantity, pro.idUnit, pro.unit_amount, pro.purchase_date, pro.expiration_date, pro.expired, cat.name
            FROM product AS pro, category AS cat
            WHERE pro.idCategory = cat.id
            ORDER BY CASE
                WHEN pro.expiration_date = '' THEN (2) ELSE (1) END, pro.expiration_date;       -- expiration_date ASCENDING, blank dates last
         */
        String P = TABLE_ALIAS_Product + ".";

        String queryString = //[0]                           [1]                              [2]
                "SELECT "   + P + COLUMN_PRODUCT_id + ", " + P + COLUMN_PRODUCT_name + ", " + P + COLUMN_PRODUCT_quantity + ", "
                    //        [3]                                [4]                                     [5]
                        + P + COLUMN_PRODUCT_idUnit + ", " + P + COLUMN_PRODUCT_unit_amount + ", " + P + COLUMN_PRODUCT_purchase_date + ", "
                    //        [6]                                         [7]                                 [8]
                        + P + COLUMN_PRODUCT_expiration_date + ", " + P + COLUMN_PRODUCT_expired + ", " + P + COLUMN_PRODUCT_idCategory +
                " FROM "    + TABLE_Product + " AS " + TABLE_ALIAS_Product +
                " ORDER BY CASE " +
                    "WHEN " + COLUMN_PRODUCT_expiration_date + " = '' THEN (2) ELSE (1) END, " + P + COLUMN_PRODUCT_expiration_date + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ArrayList<Product> returnList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                Product p = new Product();
                p.setId(cursor.getInt(0));
                p.setName(cursor.getString(1));
                p.setQuantity(cursor.getInt(2));
                p.setIdUnit(cursor.getInt(3));
                p.setUnit_amount(cursor.getDouble(4));
                p.setAdd_date(dbStr_toDate(cursor.getString(5)) );
                p.setExpiration_date(dbStr_toDate(cursor.getString(6)) );
                p.setExpired(cursor.getInt(7) == 1);
                p.setIdCategory(cursor.getInt(8));
                p.setUnit(getUnit(p.getIdUnit()) );
                p.setCategory(getCategory(p.getIdCategory()) );
                p.setIconResource(R.drawable.ic_delete);

                returnList.add(p);
            } while (cursor.moveToNext());
        }
        else    // if query returned no items
        {

        }

        cursor.close();         // Close cursor and database when finished
        db.close();
        return returnList;
    }

    /**
     * Retrieve a list of all products from the {@value #TABLE_Product} database table contained in the specified category.
     * @param categoryName The name of the category to be retrieved.
     * @return An ArrayList containing all products in the category corresponding to {@param categoryName}.
     */
    public ArrayList<Product> getCategoryProducts(String categoryName)
    {
        /*
            SELECT pro.id, pro.name, pro.quantity, pro.idUnit, pro.unit_amount, pro.purchase_date, pro.expiration_date, pro.expired, cat.name
            FROM product AS pro, category AS cat
            WHERE pro.idCategory = cat.id
            ORDER BY CASE
                WHEN pro.expiration_date = '' THEN (2) ELSE (1) END, pro.expiration_date;       -- expiration_date ASCENDING, blank dates last
         */
        String P = TABLE_ALIAS_Product + ".";
        String C = TABLE_ALIAS_Category + ".";

        String queryString = //[0]                           [1]                              [2]
            "SELECT "   + P + COLUMN_PRODUCT_id + ", " + P + COLUMN_PRODUCT_name + ", " + P + COLUMN_PRODUCT_quantity + ", "
                    //        [3]                                [4]                                     [5]
                        + P + COLUMN_PRODUCT_idUnit + ", " + P + COLUMN_PRODUCT_unit_amount + ", " + P + COLUMN_PRODUCT_purchase_date + ", "
                    //        [6]                                         [7]                                 [8]
                        + P + COLUMN_PRODUCT_expiration_date + ", " + P + COLUMN_PRODUCT_expired + ", " + P + COLUMN_PRODUCT_idCategory +

            " FROM "    + TABLE_Product + " AS " + TABLE_ALIAS_Product + " INNER JOIN " +  TABLE_Category + " AS " + TABLE_ALIAS_Category + " ON " + C + COLUMN_CATEGORY_id + "=" +
                        P + COLUMN_PRODUCT_idCategory +
            " WHERE "  + C + COLUMN_CATEGORY_name + "=" + "\"" + categoryName + "\"" +
            " ORDER BY CASE" +
            " WHEN " + COLUMN_PRODUCT_expiration_date + " = '' THEN (2) ELSE (1) END, " + P + COLUMN_PRODUCT_expiration_date + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ArrayList<Product> returnList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                Product p = new Product();
                p.setId(cursor.getInt(0));
                p.setName(cursor.getString(1));
                p.setQuantity(cursor.getInt(2));
                p.setIdUnit(cursor.getInt(3));
                p.setUnit_amount(cursor.getDouble(4));
                p.setAdd_date(dbStr_toDate(cursor.getString(5)) );
                p.setExpiration_date(dbStr_toDate(cursor.getString(6)) );
                p.setExpired(cursor.getInt(7) == 1);
                p.setIdCategory(cursor.getInt(8));
                p.setUnit(getUnit(p.getIdUnit()) );
                p.setCategory(getCategory(p.getIdCategory()) );
                p.setIconResource(R.drawable.ic_delete);

                returnList.add(p);
            } while (cursor.moveToNext());
        }
        else    // if query returns no items
        {

        }

        cursor.close();         // Close cursor and database when finished
        db.close();
        return returnList;
    };

    /**
     * Retrieve a unit from the list by its id.
     * @param id The id of the unit to be retrieved.
     * @return The Unit for which {@value #TABLE_Unit}.{@value #COLUMN_UNIT_id} = {@param id}.
     */
    public Unit getUnit(long id)
    {
        if (units == null)  getUnits();
        return units.get((int)id);
    }

    /**
     * Retrieve all units from the {@value #TABLE_Unit} database table.
     * @return An ArrayList with containing all of the units.
     */
    public ArrayList<Unit> getUnits()
    {
        if (units != null)  return units;

        /*
            SELECT id, name, abbrev, plural
            FROM Unit
            ORDER BY id;
         */
        String queryString = //   [0]                     [1]                       [2]                         [3]
                "SELECT "       + COLUMN_UNIT_id + ", " + COLUMN_UNIT_name + ", " + COLUMN_UNIT_abbrev + ", " + COLUMN_UNIT_plural +
                " FROM "        + TABLE_Unit +
                " ORDER BY "    + COLUMN_UNIT_id + ";" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ArrayList<Unit> unitList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                Unit unit = new Unit();
                unit.setId(cursor.getInt(0));
                unit.setName(cursor.getString(1));
                unit.setAbbrev(cursor.getString(2));
                unit.setPlural(cursor.getString(3));

                unitList.add(unit);
            } while (cursor.moveToNext());
        }
        else    // if query returns no items
        {

        }

        cursor.close();         // Close cursor and database when finished
        db.close();

        this.units = unitList;
        return this.units;
    }

    /**
     * Retrieve a category from the list by its id.
     * @param id The id of the category to be retrieved.
     * @return The Category for which {@value #TABLE_Category}.{@value #COLUMN_CATEGORY_id} = {@param id}.
     */
    public Category getCategory(long id)
    {
        if (categories == null)     getCategories();

        int pos;
        for (pos = 0; pos < categories.size(); pos++)
        {
            if (categories.get(pos).getId() == id)  break;
        }

        return categories.get(pos);
    }

    /**
     * Retrieve all categories from the {@value #TABLE_Category} database table.
     * @return An ArrayList with containing ALL of the categories.
     */
    public ArrayList<Category> getCategories()
    {
        if (categories != null)     return categories;

        /*
            SELECT id, name, description
            FROM Category
            ORDER BY id;
         */
        String queryString = //[0]                        [1]                           [2]
            "SELECT "       + COLUMN_CATEGORY_id + ", " + COLUMN_CATEGORY_name + ", " + COLUMN_CATEGORY_description +
            " FROM "        + TABLE_Category +
            " ORDER BY "    + COLUMN_UNIT_id + ";" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ArrayList<Category> categoryList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                Category category = new Category();
                category.setId(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setDescription(cursor.getString(2));

                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        else    // if query returns no items
        {

        }

        cursor.close();         // Close cursor and database connection when finished
        db.close();

        this.categories = categoryList;
        return this.categories;
    }

    /*
        ---- Static Utility Methods ----
     */

    /**
     * Converts a Date object into a {@value #APP_DATE_FORMAT} string.
     * @param date The Date being converted.
     * @return A string in {@value #APP_DATE_FORMAT}, or an empty string if {@param date} is null.
     */
    public static String date_toAppStr(Date date)
    {
        String dateStr = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);

        try
        {
            if (date != null)       dateStr = dateFormat.format(date);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return dateStr;
    }

    /**
     * Converts a Date object into a {@value #DB_DATE_FORMAT} string.
     * @param date The Date being converted.
     * @return A string in {@value #DB_DATE_FORMAT}, or an empty string if {@param date} is null.
     */
    public static String date_toDbStr(Date date)
    {
        String dateStr = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        try
        {
            if (date != null)       dateStr = dateFormat.format(date);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return dateStr;
    }

    /**
     * Converts a string from {@value #DB_DATE_FORMAT} into a Date object.
     * @param dateStr The string being converted (should be in {@value #DB_DATE_FORMAT}).
     * @return A Date object holding the date specified by {@param dateStr}, or null if {@param dateStr} is empty/malformed.
     */
    public static Date dbStr_toDate(String dateStr)
    {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);

        try
        {
            if (!dateStr.isEmpty())      date = dateFormat.parse(dateStr);
        }
        catch (ParseException | NullPointerException e)
        {
            e.printStackTrace();
        }

        return date;        // Note: May return null
    }

    /**
     * Converts a string from {@value #APP_DATE_FORMAT} into a Date object.
     * @param dateStr The string being converted (should be in {@value #APP_DATE_FORMAT}).
     * @return A Date object holding the date specified by {@param dateStr}, or null if {@param dateStr} is empty/malformed.
     */
    public static Date appStr_toDate(String dateStr)
    {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);

        try
        {
            if (!dateStr.isEmpty())     date = dateFormat.parse(dateStr);
        }
        catch (ParseException | NullPointerException e)
        {
            e.printStackTrace();
        }

        return date;        // Note: May return null
    }

}