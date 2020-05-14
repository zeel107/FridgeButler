package com.example.foodtracker3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

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
        setHasOptionsMenu(true);
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        

        final DatabaseHelper dbh = new DatabaseHelper(this.getActivity() );// Possible memory leak? Store static 'context' in Application class?
        final ArrayList<Product> productList = dbh.getAllProducts();

        final Adapter adapter;
        adapter = new Adapter(productList);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener(){
            @Override
            public void onDeleteClick(int position) {
                removeItem(position, productList, adapter, dbh);
            }

            @Override
            public void onItemClick(int position) {
                adapter.notifyItemChanged(position);
            }
        });
        return view;
    }
    public void removeItem(int position, ArrayList<Product> list, RecyclerView.Adapter adapter, DatabaseHelper dbh) {
        dbh.removeProduct(list.get(position));
        list.remove(position);
        adapter.notifyItemRemoved(position);
    }
    public boolean onCreateOptionsMenu(Menu menu, final Adapter adapter)
    {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
