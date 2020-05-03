package com.example.foodtracker3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;
        ArrayList<Product> list = new ArrayList<>();
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));
        list.add(new Product(R.drawable.ic_delete,"Steak", "Expiration Date: 12/24/6793"));

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        //mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new Adapter(list);
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}
