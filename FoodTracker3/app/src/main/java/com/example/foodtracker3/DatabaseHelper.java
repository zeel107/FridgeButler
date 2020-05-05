package com.example.db_demo_1;          // Change package here

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
import java.util.List;


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
        This gets called the first time the DB gets accessed. It ensures that the necessary tables
        exist.
        Note: If we are closing and reopening DB connections often, we may want to move this code to
        another method that only executes on app startup. Maybe.
    */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Drop and recreate tables for testing
        //String dropTableStatement = "DROP TABLE " + TABLE_PRODUCT + ";DROP TABLE " + TABLE_CATEGORY + ";";
        //db.execSQL(dropTableStatement);

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
        String addCategories =
            "INSERT INTO category (name, description)"
                +"VALUES ('Bag Snacks', 'Snacks that come in a bag.');"
            +"INSERT INTO category (name, description)"
                +"VALUES ('Oral Hygiene', 'Products related to oral hygiene.');"
            +"INSERT INTO category (name, description)"
                +"VALUES ('Cereal', 'Breakfast cereals.');"
            +"INSERT INTO category (name, description)"
                +"VALUES ('Frozen Meals', 'Frozen entrees & side dishes.');"
            +"INSERT INTO category (name, description)"
                +"VALUES ('Fruit', 'Fresh fruit.');";
        db.execSQL(addCategories);
    }

    // Called if the DB version number changes. It prevents previous user's apps from breaking when you change DB design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

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
            if (/*com.example.db_demo_1.*/BuildConfig.DEBUG)        // Only show toast if we are debugging. Try "BuildConfig.BUILD_TYPE.equals("debug")"
            {
                if (context != null)  Toast.makeText(context.get(), "addOne(): " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e("DatabaseHelper.addOne()", e.getMessage(), e );           // Log the error
        }

        if (insert == -1)   return false;
        else                return true;       // insert == rowID of newly inserted row
    }

    // Retrieve a list of all products
    public List<Product> selectAll()
    {
         List<Product> returnList = new ArrayList<>();

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


}
