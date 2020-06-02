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


public class DatabaseHelper extends SQLiteOpenHelper
{
    // (ES) - May not be an ideal location for these constants, but it's fine.
    // Note: use WeakReference to prevent memory leaks. This reference won't prevent the context from
    // being sent to the garbage collector, if user switches views etc.
    WeakReference<Context> context;
    ArrayList<Unit> units;
    ArrayList<Category> categories;

    private static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    private static final String APP_DATE_FORMAT = "MM/dd/yyyy";

    // ----- DATABASE STRING CONSTANTS -----
    private static final String DB_NAME = "database.db";            // Change database name here

    private static final String TABLE_ALIAS_Product = "pro";
    private static final String TABLE_Product = "Product";
    private static final String     COLUMN_PRODUCT_id               = "id";
    private static final String     COLUMN_PRODUCT_name             = "name";
    private static final String     COLUMN_PRODUCT_quantity         = "quantity";
    private static final String     COLUMN_PRODUCT_idUnit           = "idUnit";
    private static final String     COLUMN_PRODUCT_unit_amount      = "unit_amount";
    private static final String     COLUMN_PRODUCT_purchase_date    = "purchase_date";
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
    private static final String     COLUMN_UNIT_plural  = "plural"; // Necessary if at any point we want to display the full unit name instead of just the abbrev


    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = new WeakReference<Context>(context);
        this.units = this.getUnits();
        this.categories = this.getCategories();
    }

    // Private constructor that creates a DB with an alternate name
    private DatabaseHelper(@Nullable Context context, String alternateDbName)
    {
        super(context, alternateDbName, null, 1);
        this.context = new WeakReference<Context>(context);
        this.units = this.getUnits();
        this.categories = this.getCategories();
    }

    // Currently used in test class to create temporary (in-memory) database, for testing.
    // This DB ceases to exist once the connection to it is closed. --> https://www.sqlite.org/inmemorydb.html
    public static DatabaseHelper createTempDB(@Nullable Context context)
    {
        return new DatabaseHelper(context, ":memory:");
    }

    /*
        NOTE: This method is temporary, for testing purposes only. It ensures the database gets wiped
        when you reopen the app for the first time. So that we can test the add function without it
        getting cluttered. We should delete this override before releasing.
     */

   /* 
    private static boolean first = false;   //<--- set FALSE to disable DB wipe on new app instance

    @Override
    public SQLiteDatabase getReadableDatabase()
    {
        SQLiteDatabase db = super.getReadableDatabase();

        if (first == true)
        {
            first = false;
            String dropTableStatement;
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Product + ";";
            db.execSQL(dropTableStatement);
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Category + ";";
            db.execSQL(dropTableStatement);
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Unit + ";";
            db.execSQL(dropTableStatement);
            this.onCreate(db);
        }

        return db;
    }*/

    /*
        This gets called after a call to getReadableDatabase() or getWritableDatabase() ONLY if the
        DB doesn't already exist. If the DB already exists in data/data/com.example.foodtracker3/databases,
        then that DB will be opened and used, and this code will not be called.
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
                + COLUMN_PRODUCT_idCategory         + " INTEGER REFERENCES " + TABLE_Category + "(" + COLUMN_CATEGORY_id + ") "
            + ");";
        db.execSQL(createTableStatement);

        // Create 'Category' table
        createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_Category
            + " ("
                + COLUMN_CATEGORY_id            + " INTEGER PRIMARY KEY AUTOINCREMENT, "            // ES - removed NOT NULL
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
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (1, 'Bag Snacks', 'Snacks that come in a bag.');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (2, 'Oral Hygiene', 'Products related to oral hygiene.');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (3, 'Cereal', 'Breakfast cereals.');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (4, 'Frozen Meals', 'Frozen entrees & side dishes.');";
        db.execSQL(sampleInsert);
        sampleInsert = "INSERT INTO Category (id, name, description) VALUES (5, 'Fruit', 'Fresh fruit.');";
        db.execSQL(sampleInsert);

        // Add sample units
        //sampleInsert = "INSERT INTO Unit (id, name, abbrev, plural) VALUES (0, 'n/a', '', '');";
        //db.execSQL(sampleInsert);
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

    // Called if the DB version number changes. It prevents previous user's apps from breaking when you change DB design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Implement data migration methods here
    }

    // Insert one record to the Product table
    public boolean addProduct(Product p)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_name, p.getName());
        cv.put(COLUMN_PRODUCT_quantity, p.getQuantity());
        cv.put(COLUMN_PRODUCT_idUnit, p.getIdUnit());
        cv.put(COLUMN_PRODUCT_unit_amount, p.getUnit_amount());
        cv.put(COLUMN_PRODUCT_purchase_date, date_toDbStr(p.getPurchase_date()) );
        cv.put(COLUMN_PRODUCT_expiration_date, date_toDbStr(p.getExpiration_date()) );
        cv.put(COLUMN_PRODUCT_expired, p.isExpired());
        cv.put(COLUMN_PRODUCT_idCategory, p.getIdCategory());

        long insert = -1;
        try
        {
            insert = db.insertOrThrow(TABLE_Product, null, cv);
        }
        catch (SQLException e)
        {   // NOTE: if this line shows up as an error for you, Build --> Clean Project then Build --> Rebuild Project should fix it
            if (com.example.foodtracker3.BuildConfig.DEBUG)        // Only show toast if we are debugging. Try "BuildConfig.BUILD_TYPE.equals("debug")"
            {
                if (context != null)  Toast.makeText(context.get(), "addOne(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DBH.addProduct()", e.getMessage(), e );           // Log the error
        }

        if (insert == -1)   return false;
        else
        {
            p.setId(insert);        // insert == rowID of newly inserted row. Not a good practice but it is safe in our case.
            return true;            // https://www.sqlite.org/rowidtable.html
        }
    }

    // Insert one record to the Product table
    public boolean updateProduct(Product p)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_name, p.getName());
        cv.put(COLUMN_PRODUCT_quantity, p.getQuantity());
        cv.put(COLUMN_PRODUCT_idUnit, p.getIdUnit());
        cv.put(COLUMN_PRODUCT_unit_amount, p.getUnit_amount());
        cv.put(COLUMN_PRODUCT_purchase_date, date_toDbStr(p.getPurchase_date()) );
        cv.put(COLUMN_PRODUCT_expiration_date, date_toDbStr(p.getExpiration_date()) );
        cv.put(COLUMN_PRODUCT_expired, p.isExpired());
        cv.put(COLUMN_PRODUCT_idCategory, p.getIdCategory());

        int update = -1;
        try
        {
            update = db.update(TABLE_Product,  cv, null, null);
        }
        catch (SQLException e)
        {   // NOTE: if this line shows up as an error for you, Build --> Clean Project then Build --> Rebuild Project should fix it
            if (com.example.foodtracker3.BuildConfig.DEBUG)        // Only show toast if we are debugging. Try "BuildConfig.BUILD_TYPE.equals("debug")"
            {
                if (context != null)  Toast.makeText(context.get(), "addOne(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DBH.addProduct()", e.getMessage(), e );           // Log the error
        }

        if (update == -1)   return false;
        else
        {
            p.setId(update);        // insert == rowID of newly inserted row. Not a good practice but it is safe in our case.
            return true;            // https://www.sqlite.org/rowidtable.html
        }
    }

    // Delete one record from the Product table
    public boolean removeProduct(Product p)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_PRODUCT_id + " = " + p.getId() + ";";

        int rowsAffected = db.delete(TABLE_Product, whereClause, null);

        if (rowsAffected > 0)      return true;    // One row should've been affected by the DELETE
        else                       return false;
    }


    // Retrieve a list of all products
    public ArrayList<Product> getAllProducts()
    {
         // Build the SQL query. Note: we can find a more efficient & readable approach later.
        /*
            SELECT pro.id, pro.name, pro.quantity, pro.idUnit, pro.unit_amount, pro.purchase_date, pro.expiration_date, pro.expired, cat.name
            FROM product AS pro, category AS cat
            WHERE pro.idCategory = cat.id
            ORDER BY expiration_date;        // ASCENDING by default == earliest expiration dates on top
         */
        String P = TABLE_ALIAS_Product + ".";
        //String C = TABLE_ALIAS_Category + ".";

        // Note: Table aliases are now unnecessary because we are only accessing a single table. I left them here in case that changes next sprint.
        String queryString = //[0]                           [1]                              [2]
            "SELECT "   + P + COLUMN_PRODUCT_id + ", " + P + COLUMN_PRODUCT_name + ", " + P + COLUMN_PRODUCT_quantity + ", "
                    //        [3]                                [4]                                     [5]
                        + P + COLUMN_PRODUCT_idUnit + ", " + P + COLUMN_PRODUCT_unit_amount + ", " + P + COLUMN_PRODUCT_purchase_date + ", "
                    //        [6]                                         [7]                                 [8]
                        + P + COLUMN_PRODUCT_expiration_date + ", " + P + COLUMN_PRODUCT_expired + ", " + P + COLUMN_PRODUCT_idCategory +

            " FROM "    + TABLE_Product + " AS " + TABLE_ALIAS_Product +
            " ORDER BY " + COLUMN_PRODUCT_expiration_date + ";" ;

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
                p.setPurchase_date(dbStr_toDate(cursor.getString(5)) );
                p.setExpiration_date(dbStr_toDate(cursor.getString(6)) );
                p.setExpired(cursor.getInt(7) == 1);
                p.setIdCategory(cursor.getInt(8));
                p.setUnit(getUnit(p.getIdUnit()) );
                p.setCategory(getCategory(p.getIdCategory()) );
                p.setIconResource(R.drawable.ic_delete);

                returnList.add(p);
            } while (cursor.moveToNext());
        }
        else
        {
            // Query failed/returned no items?
        }

        // Close cursor and DB when finished
        cursor.close();
        db.close();
        return returnList;
    }

    public ArrayList<Product> getCategoryProducts(String categoryName)
    {
        // Build the SQL query. Note: we can find a more efficient & readable approach later.
        /*
            SELECT pro.id, pro.name, pro.quantity, pro.idUnit, pro.unit_amount, pro.purchase_date, pro.expiration_date, pro.expired, cat.name
            FROM product AS pro, category AS cat
            WHERE pro.idCategory = cat.id
            ORDER BY expiration_date;        // ASCENDING by default == earliest expiration dates on top
         */
        String P = TABLE_ALIAS_Product + ".";
        String C = TABLE_ALIAS_Category + ".";

        // Note: Table aliases are now unnecessary because we are only accessing a single table. I left them here in case that changes next sprint.
        String queryString = //[0]                           [1]                              [2]
                "SELECT "   + P + COLUMN_PRODUCT_id + ", " + P + COLUMN_PRODUCT_name + ", " + P + COLUMN_PRODUCT_quantity + ", "
                        //        [3]                                [4]                                     [5]
                        + P + COLUMN_PRODUCT_idUnit + ", " + P + COLUMN_PRODUCT_unit_amount + ", " + P + COLUMN_PRODUCT_purchase_date + ", "
                        //        [6]                                         [7]                                 [8]
                        + P + COLUMN_PRODUCT_expiration_date + ", " + P + COLUMN_PRODUCT_expired + ", " + P + COLUMN_PRODUCT_idCategory +

                        " FROM "    + TABLE_Product + " AS " + TABLE_ALIAS_Product + " INNER JOIN " +  TABLE_Category + " AS " + TABLE_ALIAS_Category + " ON " + C + COLUMN_CATEGORY_id + "=" +
                          P + COLUMN_PRODUCT_idCategory
                        +" WHERE "  + C + COLUMN_CATEGORY_name + "=" + "\"" + categoryName + "\""
                        +" ORDER BY " + P + COLUMN_PRODUCT_expiration_date + ";" ;

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
                p.setPurchase_date(dbStr_toDate(cursor.getString(5)) );
                p.setExpiration_date(dbStr_toDate(cursor.getString(6)) );
                p.setExpired(cursor.getInt(7) == 1);
                p.setIdCategory(cursor.getInt(8));
                p.setUnit(getUnit(p.getIdUnit()) );
                p.setCategory(getCategory(p.getIdCategory()) );
                p.setIconResource(R.drawable.ic_delete);

                returnList.add(p);
            } while (cursor.moveToNext());
        }
        else
        {
            // Query failed/returned no items?
        }

        // Close cursor and DB when finished
        cursor.close();
        db.close();
        return returnList;
    };

    // Retrieve a unit by ID from 'units' list
    public Unit getUnit(long id)
    {
        if (units == null)  getUnits();
        return units.get((int)id);
    }

    // Retrieve a list of all units
    public ArrayList<Unit> getUnits()
    {
        if (units != null)  return units;       // May want to force requery in the future, if we allow customizing units.

        /*
            SELECT id, name, abbrev
            FROM Unit
            ORDER BY id;
         */

        String queryString = //[0]                    [1]                       [2]
            "SELECT "       + COLUMN_UNIT_id + ", " + COLUMN_UNIT_name + ", " + COLUMN_UNIT_abbrev +
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

                unitList.add(unit);
            } while (cursor.moveToNext());
        }
        else
        {
            // Do something if query returns no items?
        }

        // Close cursor and DB when finished
        cursor.close();
        db.close();

        this.units = unitList;
        return this.units;
    }

    // Retrieve a category by ID from 'categories' list.
    // Note: when user is allowed to create and delete their own categories, will
    // change this to hash map using category ID as key-- because we won't be able to rely on
    // the index in this array being the correct category.
    public Category getCategory(long id)
    {
        if (categories == null)     getCategories();
        return categories.get((int)id);
    }

    // Retrieve a list of all categories
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
        else
        {
            // Do something if query returns no items?
        }

        // Close cursor and DB connection when finished
        cursor.close();
        db.close();

        this.categories = categoryList;
        return this.categories;
    }

    /*
        ---- Static Utility Methods ----
     */

    // Convert a Date to an APP_DATE_FORMAT String
    public static String date_toAppStr(Date date)
    {
        String dateStr = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);

        try
        {
            dateStr = dateFormat.format(date);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return dateStr;
    }

    // Convert a Date to an DB_DATE_FORMAT String
    public static String date_toDbStr(Date date)
    {
        String dateStr = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        try
        {
            dateStr = dateFormat.format(date);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return dateStr;
    }

    // Convert a DB_DATE_FORMAT String into a Date
    public static Date dbStr_toDate(String dateStr)
    {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);

        try
        {
            date = dateFormat.parse(dateStr);
        }
        catch (ParseException | NullPointerException e)
        {
            e.printStackTrace();
        }

        return date;                    // Note: May return null if DB date string is mis-formatted
    }

    // Convert a APP_DATE_FORMAT String into a Date
    public static Date appStr_toDate(String dateStr)
    {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(APP_DATE_FORMAT);

        try
        {
            date = dateFormat.parse(dateStr);
        }
        catch (ParseException | NullPointerException e)
        {
            e.printStackTrace();
        }

        return date;                    // Note: May return null if DB date string is mis-formatted
    }

}
