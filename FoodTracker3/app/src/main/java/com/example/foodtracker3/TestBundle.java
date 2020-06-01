package com.example.foodtracker3;

import java.io.Serializable;

public class TestBundle implements Serializable {
    long productIndex;
    TestBundle(long i) {
        productIndex = i;
    }
}
