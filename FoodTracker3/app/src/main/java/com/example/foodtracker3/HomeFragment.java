package com.example.foodtracker3;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.annotation.Nullable;;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


/**
 * The Home Fragment is rendered in the Main Activity on click of the Home navigation item,
 * and when the app starts initially.
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 * */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    Adapter adapter = null;
    DatabaseHelper dbh = null;
    ArrayList<Product> productList = null;
    androidx.appcompat.widget.SearchView searchView = null;
    private int pos;

    @Nullable
    @Override
    /**
     * This method creates and inflates the view when the Home Fragment is loaded into the MainActivity view port.
     * @param inflater the layout inflater for inflating the view.
     * @param container the container that will contain the inflated content- the view port.
     * @param savedInstanceState to load previous instances of the view
     * @return the resulting view.
     * */
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
        /**
         * This method houses the click listeners. Sets an on click listener object on the main adapter.
         * */
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener()
        {
            /**
             * Click event listener that deletes the item for which the delete icon is clicked.
             * @param position the ArrayList position of the item to delete.
             * */
            @Override
            public void onDeleteClick(final int position)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogStyle);
                //builder.setTitle("Attention!")
                        builder.setMessage("Do you want to delete the selected item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            /**
                             * The click event to be executed if the user confirms "yes".
                             * @param dialogInterface The popup interface object to be dismissed.
                             * */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                removeItem(position, productList, adapter, dbh);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            /**
                             * The click event to be executed if the user clicks "No" as in they do not want to delete the item.
                             * @param dialogInterface The popup interface object to be dismissed.
                             * */
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
            /**
             * The click event to be executed if the user clicks the tile.
             * @param position The position in the ArrayList of the item selected.
             * */
            @Override
            public void onItemClick(int position) {
                Product currentProduct = productList.get(position);
                currentProduct.setExpanded(!currentProduct.getExpanded());
                adapter.notifyItemChanged(position);
            }
            /**
             * The click event to be executed if the user clicks the edit icon.
             * @param position The position in the ArrayList of the item selected.
             * */
            @Override
            public void onEditClick(int position) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Product currentProduct = productList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("edit_product", currentProduct);
                AddFragment addFrag = new AddFragment();
                addFrag.setArguments(bundle);

                ft.replace(R.id.fragment_container, addFrag).commit();
            }

        });

        dbh.close();
        return view;
    }
    /**
     * Contains the logic for removing an item from the list and the database. This method is called when the user clicks "Yes" on the delete confirmation.
     * @param position The position in the ArrayList of the item to be deleted.
     * @param list The list of products to delete from.
     * @param adapter The adapter to notify of content change.
     * @param dbh Thr database helper object used to delete the item from the database.
     * */
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
    /**
     * Inflates the search view when the search icon is clicked.
     * @param menu the menu item being instantiated.
     * @param inflater the inflater object used to inflate the search view.
     * */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
         searchView= (androidx.appcompat.widget.SearchView)searchItem.getActionView();
        /**
         *Listener that looks to see if the text is being changed in the search box.
         * */
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            /**
             * Defines what to do when the text in the search box is changed.
             * */
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return false;
            }

        });
    }

    @Override
    /**
     * Defines what to do when a category filter drop down menu item is selected.
     * @param parent the parent view
     * @param view the current view
     * @param position the list position of the item selected.
     * */
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
        searchView.setQuery("", false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
