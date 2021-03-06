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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;


public class DatabaseHelper extends SQLiteOpenHelper
{
    // Note: use WeakReference to prevent memory leaks. This reference won't prevent the context from
    // being sent to the garbage collector, if user switches views etc.
    WeakReference<Context> context;
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


    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = new WeakReference<Context>(context);
    }

    /*
        NOTE: This method is temporary, for testing purposes only. It ensures the database gets wiped
        when you reopen the app for the first time. So that we can test the add function without it
        getting cluttered. We should delete this override before releasing.
     */
    private static boolean first = true;
    @Override
    public SQLiteDatabase getReadableDatabase()
    {
        SQLiteDatabase db = super.getReadableDatabase();
                                                    //<--- to disable DB wipe on new app instance
        if (first == true)
        {
            first = false;
            String dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Product + ";";
            db.execSQL(dropTableStatement);
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Category + ";";
            db.execSQL(dropTableStatement);
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Unit + ";";
            db.execSQL(dropTableStatement);
            this.onCreate(db);
        }

        return db;
    }

    /*
        This gets called after a call to getReadableDatabase() or getWritableDatabase() ONLY if the
        DB doesn't already exist. If the DB already exists in data/data/com.example.foodtracker3/databases,
        then that DB will be opened and used, and this code will not be called.
    */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Drop tables for testing
        /*
        String dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Product + ";";
        db.execSQL(dropTableStatement);
        dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Category + ";";
        db.execSQL(dropTableStatement);
        */

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
                    + COLUMN_UNIT_abbrev    + " TEXT "
                + ");" ;
        db.execSQL(createTableStatement);

        // Add sample categories for testing
        String addCategories = "INSERT INTO Category (name, description)"
                +"VALUES ('Bag Snacks', 'Snacks that come in a bag.');";
        db.execSQL(addCategories);
        addCategories = "INSERT INTO Category (name, description)"
                +"VALUES ('Oral Hygiene', 'Products related to oral hygiene.');";
        db.execSQL(addCategories);
        addCategories = "INSERT INTO Category (name, description)"
                +"VALUES ('Cereal', 'Breakfast cereals.');";
        db.execSQL(addCategories);
        addCategories = "INSERT INTO Category (name, description)"
                +"VALUES ('Frozen Meals', 'Frozen entrees & side dishes.');";
        db.execSQL(addCategories);
        addCategories = "INSERT INTO Category (name, description)"
                +"VALUES ('Fruit', 'Fresh fruit.');";
        db.execSQL(addCategories);

        // Add sample units
        String addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (1, 'count', 'ct');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (2, 'ounce(s)', 'oz');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (3, 'liter(s)', 'lt');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (4, 'milliliter(s)', 'mL');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (5, 'box(es)', 'bx');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (6, 'bag(s)', 'bg');";
        db.execSQL(addUnits);
        addUnits = "INSERT INTO Unit (id, name, abbrev) VALUES (7, 'package(s)', 'pkg');";
        db.execSQL(addUnits);
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

        cv.put(COLUMN_PRODUCT_name, p.getName() );
        cv.put(COLUMN_PRODUCT_quantity, p.getQuantity() );
        cv.put(COLUMN_PRODUCT_idUnit, p.getIdUnit() );
        cv.put(COLUMN_PRODUCT_unit_amount, p.getUnit_amount() );
        cv.put(COLUMN_PRODUCT_purchase_date, Product.date_toDbStr(p.getPurchase_date()) );
        cv.put(COLUMN_PRODUCT_expiration_date, Product.date_toDbStr(p.getExpiration_date()) );
        cv.put(COLUMN_PRODUCT_expired, p.isExpired() );
        cv.put(COLUMN_PRODUCT_idCategory, p.getIdCategory() );

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
                Product item = new Product();
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setQuantity(cursor.getInt(2));
                item.setIdUnit(cursor.getInt(3));
                item.setUnit_amount(cursor.getDouble(4));
                item.setPurchase_date(Product.dbStr_toDate(cursor.getString(5)) );
                item.setExpiration_date(Product.dbStr_toDate(cursor.getString(6)) );
                item.setExpired(cursor.getInt(7) == 1);
                item.setIdCategory(cursor.getInt(8));
                item.setIconResource(R.drawable.ic_delete);

                returnList.add(item);
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

    // Retrieve a list of all units
    public ArrayList<Unit> getAllUnits()
    {
        /*
            SELECT id, name, abbrev
            FROM Unit
            ORDER BY id;
         */

        String queryString = //[0]                    [1]                       [2]
            "SELECT "       + COLUMN_UNIT_id + ", " + COLUMN_UNIT_name + ", " + COLUMN_UNIT_abbrev +
            " FROM " + TABLE_Unit +
            " ORDER BY " + COLUMN_UNIT_id + ";" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        ArrayList<Unit> returnList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do
            {
                Unit unit = new Unit();
                unit.setId(cursor.getInt(0));
                unit.setName(cursor.getString(1));
                unit.setAbbrev(cursor.getString(2));

                returnList.add(unit);
            } while (cursor.moveToNext());
        }
        else
        {
            // Do something if query returns no items?
        }

        // Close cursor and DB when finished
        cursor.close();
        db.close();
        return returnList;
    }

    // Testing method for adding x number of sample products
    public boolean addTestProducts(int count)
    {
        Random rand = new Random();
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        boolean success = true;

        for (int i = 0; success == true && i < count; i++)
        {
            Product p = new Product();

            p.setName("Sample Product #" + rand.nextInt(1000));
            p.setQuantity(rand.nextInt(100));
            p.setPurchase_date(date);

            calendar.add(Calendar.HOUR_OF_DAY, rand.nextInt(337));     // Random expiration_date within next 14 days
            date = calendar.getTime();

            p.setExpiration_date(date);
            p.setExpired(false);
            p.setIdCategory(rand.nextInt(6));

            success = this.addProduct(p);
        }

        return success;
    }

}
