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
import java.util.List;
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
    private static final String     COLUMN_PRODUCT_purchase_date    = "purchase_date";
    private static final String     COLUMN_PRODUCT_expiration_date  = "expiration_date";
    private static final String     COLUMN_PRODUCT_expired          = "expired";
    private static final String     COLUMN_PRODUCT_idCategory       = "idCategory";

    private static final String TABLE_ALIAS_Category = "cat";
    private static final String TABLE_Category = "Category";
    private static final String     COLUMN_CATEGORY_id          = "id";
    private static final String     COLUMN_CATEGORY_name        = "name";
    private static final String     COLUMN_CATEGORY_description = "description";


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
        // /*                                            <--- UNCOMMENT to disable DB wipe on new app instance
        if (first == true)
        {
            first = false;
            String dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Product + ";";
            db.execSQL(dropTableStatement);
            dropTableStatement = "DROP TABLE IF EXISTS " + TABLE_Category + ";";
            db.execSQL(dropTableStatement);
            this.onCreate(db);
        }
        // */
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
                + COLUMN_PRODUCT_purchase_date      + " DATE, "                 // Note: DATE == TEXT
                + COLUMN_PRODUCT_expiration_date    + " DATE, "
                + COLUMN_PRODUCT_expired            + " BOOLEAN, "              // Note: BOOLEAN == INTEGER
                + COLUMN_PRODUCT_idCategory         + " INTEGER REFERENCES " + TABLE_Category + "(" + COLUMN_CATEGORY_id + ") "
            + ");";
        db.execSQL(createTableStatement);

        // Create 'Category' table
        createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE_Category
            + " ("
                + COLUMN_CATEGORY_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "               // ES - removed NOT NULL
                + COLUMN_CATEGORY_name              + " TEXT, "
                + COLUMN_CATEGORY_description       + " TEXT "
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
    }

    // Called if the DB version number changes. It prevents previous user's apps from breaking when you change DB design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Implement data migration methods here
    }

    // Insert a product
    public boolean addOne(Product p)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_name, p.getName() );
        cv.put(COLUMN_PRODUCT_quantity, p.getQuantity() );
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
        {
            if (com.example.foodtracker3.BuildConfig.DEBUG)        // Only show toast if we are debugging. Try "BuildConfig.BUILD_TYPE.equals("debug")"
            {
                if (context != null)  Toast.makeText(context.get(), "addOne(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DatabaseHelper.addOne()", e.getMessage(), e );           // Log the error
        }

        if (insert == -1)   return false;
        else                return true;       // insert == rowID of newly inserted row
    }

    // Retrieve a list of all products
    public ArrayList<Product> selectAll()
    {
         ArrayList<Product> returnList = new ArrayList<>();

         // Build the SQL query. Note: we can find a more efficient & readable approach later.
        /*
            SELECT pro.name, pro.quantity, pro.purchase_date, pro.expiration_date, pro.expired, cat.name
            FROM product AS pro, category AS cat
            WHERE pro.idCategory = cat.id
            ORDER BY expiration_date;        // ASCENDING by default == earliest expiration dates on top
         */
        String P = TABLE_ALIAS_Product + ".";
        String C = TABLE_ALIAS_Category + ".";

        String queryString = //  [0]                          [1]                             [2]
            "SELECT " + P + COLUMN_PRODUCT_name + ", " + P + COLUMN_PRODUCT_quantity + ", " + P + COLUMN_PRODUCT_purchase_date + ", "
            //              [3]                                 [4]                             [5]
                    + P + COLUMN_PRODUCT_expiration_date + ", " + P + COLUMN_PRODUCT_expired + ", " + C + COLUMN_CATEGORY_name +
            " FROM " + TABLE_Product + " AS " + TABLE_ALIAS_Product + ", " + TABLE_Category + " AS " + TABLE_ALIAS_Category +
            " WHERE " + P + COLUMN_PRODUCT_idCategory + " = " + C + COLUMN_CATEGORY_id +
            " ORDER BY " + COLUMN_PRODUCT_expiration_date + ";" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst() )
        {
            do
            {
                Product item = new Product();
                item.setName(cursor.getString(0) );
                item.setQuantity(cursor.getInt(1) );
                item.setPurchase_date(Product.dbStr_toDate(cursor.getString(2)) );
                item.setExpiration_date(Product.dbStr_toDate(cursor.getString(3)) );
                item.setExpired((cursor.getInt(4) == 1) ? true : false);
                item.setIdCategory(cursor.getInt(5) );

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

            success = this.addOne(p);
        }

        return success;
    }


}
