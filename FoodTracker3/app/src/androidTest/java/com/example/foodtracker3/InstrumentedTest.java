package com.example.foodtracker3;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class InstrumentedTest
{
    @Test
    public void useAppContext()
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.foodtracker3.debug", appContext.getPackageName());
    }

    @Test
    public void addProductTest()
    {
        // Setup
        Date testDate = new Date();
        DatabaseHelper testDbh = DatabaseHelper.createTempDB(ApplicationProvider.getApplicationContext());
        int testUnitId = 1;
        long testCategoryId = 1;

        Product testProduct = new Product(
                1
                , "Product 1"
                , 1
                , testUnitId
                , 1
                , testDate
                , testDate
                , false
                , testCategoryId
                , testDbh.getUnit(testUnitId)
                , testDbh.getCategory(testCategoryId));

        // Test
        boolean result = testDbh.addProduct(testProduct);
        assertEquals(true, result);

        // Teardown
        testDbh.close();
    }

    @Test
    public void addProductTestEmpty()
    {
        // Setup
        DatabaseHelper testDbh = DatabaseHelper.createTempDB(ApplicationProvider.getApplicationContext());
        Product testProduct = new Product();

        // Test
        boolean result = testDbh.addProduct(testProduct);
        assertEquals(true, result);

        // Teardown
        testDbh.close();
    }

    @Test
    public void removeProductTest()
    {
        // Setup
        DatabaseHelper dbh = DatabaseHelper.createTempDB(ApplicationProvider.getApplicationContext());
        Product testProduct = new Product();
        testProduct.setId(999);
        dbh.addProduct(testProduct);

        // Test
        boolean result = dbh.removeProduct(testProduct);
        assertEquals(true, result);

        // Teardown
        dbh.close();
    }
}

