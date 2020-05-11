package com.example.foodtracker3;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UnitTest {

    /*
    @Test
    public void validateInputTest() {
        AddFragment instance = new AddFragment();

        boolean result = instance.validateInput();
        assertEquals(true, result);
    }

     */
    /*
    @Test
    public void addProductTest() {
        DatabaseHelper instance = new DatabaseHelper(new Context());
        boolean result = instance.addProduct(new Product(100, "Test", 1, new Date(1000), new Date(100000), false, 1, 0));
        assertEquals(true, result);
    }
*/
    @Test
    public void date_toAppStrTest() {
        Product instance = new Product();
        Date date = new Date(2020, 10, 7);
        String result = instance.date_toAppStr(date);
        assertEquals("7/10/2020",result);
    }

    @Test
    public void date_toDbStrTest() {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2020, 10, 7);
        long x = cal.getTimeInMillis();
        Date date = new Date(x);

        String result = instance.date_toDbStr(date);
        assertEquals("2020-10-07", result);
    }

    @Test
    public void dbStr_toDateTest() {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2000, 10, 7);
        long x = cal.getTimeInMillis();
        Date expected = new Date(x);

        Date result = instance.dbStr_toDate("2000-10-07");
        assertEquals(expected, result);
    }

    @Test
    public void appStr_toDateTest() {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2000, 10, 7);
        long x = cal.getTimeInMillis();
        Date expected = new Date(x);

        Date result = instance.appStr_toDate("7/10/2000");
        assertEquals(expected, result);

    }

    @Test
    public void appStr_toDateTestNull() {

        Date result = Product.appStr_toDate("what");
        assertNull(result);

    }

    @Test
    public void appStr_toDateTestNull2() {
        Date result = Product.appStr_toDate(null);
        assertNull(result);
    }

    @Test
    public void dbStr_toDateTestNull() {
        Date result = Product.dbStr_toDate("");
        assertNull(result);
    }

    @Test
    public void date_toAppStrTestNull() {
        String result = Product.date_toAppStr(null);
        assertNull(result);
    }

}
