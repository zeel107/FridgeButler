package com.example.foodtracker3;

import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UnitTest
{
    @Test
    public void date_toAppStrTest()
    {
        Product instance = new Product();
        GregorianCalendar cal = new GregorianCalendar(2020, 7 - 1, 10);
        long x = cal.getTimeInMillis();
        Date date = new Date(x);

        String result = DatabaseHelper.date_toAppStr(date);
        assertEquals("07/10/2020",result);
    }

    @Test
    public void date_toAppStrTestNull()
    {
        String result = DatabaseHelper.date_toAppStr(null);
        assertEquals("", result);
    }

    @Test
    public void date_toDbStrTest()
    {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2020, 10 - 1, 7);
        long x = cal.getTimeInMillis();
        Date date = new Date(x);

        String result = DatabaseHelper.date_toDbStr(date);
        assertEquals("2020-10-07", result);
    }

    @Test
    public void date_toDbStrTestNull()
    {
        String result = DatabaseHelper.date_toDbStr(null);
        assertEquals("", result);
    }

    @Test
    public void dbStr_toDateTest()
    {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2000, 10 - 1, 7);
        long x = cal.getTimeInMillis();
        Date expected = new Date(x);

        Date result = DatabaseHelper.dbStr_toDate("2000-10-07");
        assertEquals(expected, result);
    }

    @Test
    public void dbStr_toDateTestNull()
    {
        Date result = DatabaseHelper.dbStr_toDate("");
        assertNull(result);
    }

    @Test
    public void dbStr_toDateTestNull2()
    {
        Date result = DatabaseHelper.dbStr_toDate("what");
        assertNull(result);
    }

    @Test
    public void appStr_toDateTest()
    {
        Product instance = new Product();

        GregorianCalendar cal = new GregorianCalendar(2000, 7 - 1, 10);
        long x = cal.getTimeInMillis();
        Date expected = new Date(x);

        Date result = DatabaseHelper.appStr_toDate("07/10/2000");
        assertEquals(expected, result);
    }

    @Test
    public void appStr_toDateTestNull()
    {
        Date result = DatabaseHelper.appStr_toDate(null);
        assertNull(result);
    }

    @Test
    public void appStr_toDateTestNull2()
    {
        Date result = DatabaseHelper.appStr_toDate("what");
        assertNull(result);
    }

}
