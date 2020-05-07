package com.example.foodtracker3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView.Adapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView;
        final RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;
        Button buttonRemove = view.findViewById(R.id.button_remove);
        final EditText editTextRemove = view.findViewById(R.id.edittext_remove);

        final DatabaseHelper dbh = new DatabaseHelper(this.getActivity() );// Possible memory leak? Store static 'context' in Application class?
        final ArrayList<Product> productList = dbh.getAllProducts();
        adapter = new Adapter(productList);
        buttonRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position, productList, adapter, dbh);
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity() );

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }
    public void removeItem(int position, ArrayList<Product> productList, RecyclerView.Adapter adapter, DatabaseHelper dbh){
        dbh.removeProduct(productList.get(position));
        productList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
