package com.example.foodtracker3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    Adapter adapter = null;
    DatabaseHelper dbh = null;
    ArrayList<Product> productList = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        

         dbh = new DatabaseHelper(this.getContext());
        productList = dbh.getAllProducts();
        ArrayList<Category> categoryList = dbh.getCategories();
        ArrayList<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("All Categories");
        for(Category i: categoryList)
        {
            categoryNameList.add(i.getName());
        }
        categoryNameList.remove(1);

        Spinner spinnerHome = view.findViewById(R.id.homeSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_home, categoryNameList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerHome.setAdapter(arrayAdapter);
        spinnerHome.setOnItemSelectedListener(this);

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

            @Override
            public void onEditClick(int position) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Product currentProduct = productList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("edit_product", currentProduct);
                EditFragment editFrag = new EditFragment();
                editFrag.setArguments(bundle);


                ft.replace(R.id.fragment_container, editFrag).commit();
            }

        });

        dbh.close();
        return view;
    }

    public void removeItem(int position, ArrayList<Product> list, RecyclerView.Adapter adapter, DatabaseHelper dbh)
    {
        dbh.removeProduct(list.get(position));


        if(list != Adapter.listFull)
        {
            int index = Adapter.listFull.indexOf(list.get(position));
            if(index != -1)
            {
                Adapter.listFull.remove(index);
            }
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

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String categoryName = parent.getItemAtPosition(position).toString();
        ArrayList<Product> newList;
        if(categoryName != "All Categories") {
            newList = dbh.getCategoryProducts(categoryName);
        }else{
            newList = dbh.getAllProducts();
        }
        productList.clear();
        productList.addAll(newList);
        adapter.notifyDataSetChanged();
        Adapter.listFull = newList;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
