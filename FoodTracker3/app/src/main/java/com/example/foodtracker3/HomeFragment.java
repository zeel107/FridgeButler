package com.example.foodtracker3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    Adapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        

        final DatabaseHelper dbh = new DatabaseHelper(this.getActivity() );// Possible memory leak? Store static 'context' in Application class?
        final ArrayList<Product> productList = dbh.getAllProducts();


        adapter = new Adapter(productList);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener(){
            @Override
            public void onDeleteClick(int position) {
                removeItem(position, productList, adapter, dbh );
            }

            @Override
            public void onItemClick(int position) {
                Product currentProduct = productList.get(position);
                currentProduct.setExpanded(!currentProduct.getExpanded());
                adapter.notifyItemChanged(position);
            }

        });
        return view;
    }
    public void removeItem(int position, ArrayList<Product> list, RecyclerView.Adapter adapter, DatabaseHelper dbh) {
        dbh.removeProduct(list.get(position));


        if(list != Adapter.listFull)
        {
            int index = Adapter.listFull.indexOf(list.get(position));
            System.out.println("This is the index: " + index);
            Adapter.listFull.remove(index);
        }
        list.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)searchItem.getActionView();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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
    }
}
