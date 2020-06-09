package com.example.foodtracker3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.example.foodtracker3.databinding.FragmentAddBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * The Add Fragment is rendered in the Main Activity on click of the Add Navigation item.
 *
 *
 * @author Andrew Dineen
 * @author Aidan Fallstorm
 * @author Rick Patneaude
 * @author Eli Storlie
 * @author Marco Villafana
 * @version 1.0.0 Jun 7, 2020
 * */
public class AddFragment extends Fragment
{
    boolean EDIT_MODE;      // Flag to differentiate between adding and editing

    // View control objects
    Button btn_add;
    Button btn_save;
    Button btn_cancel;
    EditText et_productName;
    EditText et_productQuantity;
    EditText et_unitAmount;
    EditText et_expirationDate;
    EditText et_dateAdded;
    Spinner sp_unit;
    Spinner sp_category;
    TextView et_newCategory;
    ImageView iv_cancelNewCategory;
    ImageView iv_deleteCategory;

    FragmentAddBinding binding;     // auto-generated class based on "fragment_add.xml" file

    @Nullable
    @Override
    /**
     * This method creates and inflates the view when the Add Fragment is loaded into the MainActivity view port.
     * @param inflater the layout inflater for inflating the view.
     * @param container the container that will contain the inflated content- the view port.
     * @param savedInstanceState to load previous instances of the view
     * @return the resulting view.
     * */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        final Product editProduct;
        if (bundle != null)
        {
            EDIT_MODE = true;
            editProduct = (Product) bundle.getSerializable("edit_product");
        }
        else
        {
            EDIT_MODE = false;
            editProduct = null;
        }

        // Inflate the xml which gives us a view
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false);
        binding.setEditProduct(editProduct);
        View view = binding.getRoot();

        btn_add = binding.addButton;
        btn_save = binding.saveButton;
        btn_cancel = binding.cancelButton;
        et_productName = binding.nameInput;
        et_productQuantity = binding.quantityInput;
        et_unitAmount = binding.unitAmountInput;
        et_expirationDate = binding.tvExpirationDate;
        et_dateAdded = binding.tvDateAdded;
        sp_unit = binding.unitInput;
        sp_category = binding.categoryInput;
        et_newCategory = binding.etNewCategory;
        iv_cancelNewCategory = binding.ivCancelNewCategory;
        iv_deleteCategory = binding.ivDeleteCategory;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING); // fix keyboard/button issue (now button will stay put)

        final DatabaseHelper dbh = new DatabaseHelper(getContext());      // is getContext() reliable, or will it sometimes return null? Research it more

        /* UNITS SETUP */
        final ArrayList<Unit> units = dbh.getUnits();
        List<String> spList_unitAbbrevs = new ArrayList<>();

        for (Unit i : units)    // Add unit abbreviation strings to spinner list
        {
            spList_unitAbbrevs.add(i.getAbbrev());
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spList_unitAbbrevs);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_unit.setAdapter(unitAdapter);

        /* CATEGORIES SETUP */
        final ArrayList<Category> categories = dbh.getCategories();
        final List<String> spList_categoryNames = new ArrayList<>();

        for (Category i : categories)
        {
            spList_categoryNames.add(i.getName());
        }
        spList_categoryNames.add("(Create New Category)");

        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spList_categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(categoryAdapter);

        /* SET DEFAULT VALUES */
        sp_unit.setSelection((EDIT_MODE) ? editProduct.getIdUnit() : 0);    // unitAbbrevs[0] == "ct"
        sp_category.setSelection((EDIT_MODE) ? categories.indexOf(dbh.getCategory(editProduct.getIdCategory()) ) : 0);          // categories[0] == "None"
        /**
         * Defines what to do when a category drop down menu item is selected.
         * @param parent the parent view.
         * @param view the current view.
         * @param position the list position of the item selected.
         * */
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                if (pos == 0)       // if "None" selected
                {
                    showDeleteCategory(false);
                }
                else if (pos == sp_category.getCount() - 1)      // if selected "Create New Category"
                {
                    showNewCategory(true);
                }
                else                     // if any custom category is selected
                {
                    showDeleteCategory(true);
                }
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        /**
         * This method houses the click listener for the category delete button. Sets an on click listener object on the main adapter.
         * */
        iv_deleteCategory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogStyle);
                builder.setMessage("Delete category '" + (String)sp_category.getSelectedItem() + "'?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int deletedCatIndex = sp_category.getSelectedItemPosition();

                                if (dbh.removeCategory(categories.get(deletedCatIndex)) )
                                {
                                    spList_categoryNames.remove(deletedCatIndex);
                                    categoryAdapter.notifyDataSetChanged();
                                    sp_category.setSelection(0);                // Select "None"
                                    showDeleteCategory(false);
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        iv_cancelNewCategory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showNewCategory(false);
            }
        });

        /**
         *Shows date picker dialog when the Date Added input field is clicked.
         *
         * */
        et_dateAdded.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePickerDialog(et_dateAdded);
            }
        });
        /**
         *Shows date picker dialog when the Expiration Date input field is clicked.
         * */
        et_expirationDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               showDatePickerDialog(et_expirationDate);
            }
        });
        /**
         * The click event listener for the add button. Adds the record to the database and updates the appropriate tables.
         *
         * */
        View.OnClickListener onClick_addUpdate = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (validateInput() == false)   return;
                if (et_newCategory.isEnabled())         // insert new category if necessary
                {
                    Category category = new Category(-1, et_newCategory.getText().toString(), "");
                    if (dbh.addCategory(category))
                    {
                        categories.add(category);
                        spList_categoryNames.add(spList_categoryNames.size() - 1, category.getName());
                        categoryAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "Insert failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Date addDate = DatabaseHelper.appStr_toDate(et_dateAdded.getText().toString());
                Date expDate = DatabaseHelper.appStr_toDate(et_expirationDate.getText().toString());
                int unitId = units.get(sp_unit.getSelectedItemPosition()).getId();
                long categoryId = categories.get(sp_category.getSelectedItemPosition()).getId();

                Product prod = new Product
                        (
                                (EDIT_MODE) ? editProduct.getId() : -1,
                                et_productName.getText().toString(),
                                Integer.parseInt(et_productQuantity.getText().toString()),
                                unitId,
                                Double.parseDouble(et_unitAmount.getText().toString()),
                                addDate,
                                expDate,
                                (expDate == null) ? false : addDate.after(expDate),             // Determine if item is already expired
                                categoryId,
                                dbh.getUnit(unitId),
                                dbh.getCategory(categoryId)
                        );

                boolean success;
                if (EDIT_MODE)      success = dbh.updateProduct(prod);
                else                success = dbh.addProduct(prod);

                if (success && !EDIT_MODE)
                {
                    Toast.makeText(v.getContext(), "Item inserted.", Toast.LENGTH_SHORT).show();
                    // Clear text boxes after successful insert
                    et_productName.getText().clear();
                    et_productQuantity.setText("1");
                    et_unitAmount.setText("1");

                    sp_unit.setSelection(0);
                    sp_category.setSelection(0);
                    showNewCategory(false);
                    et_productName.requestFocus();
                }
                else if (success && EDIT_MODE)
                {
                    et_expirationDate.setText(" ");
                    Toast.makeText(v.getContext(), "Item updated.", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();     // new home fragment
                }
                else
                {
                    Toast.makeText(v.getContext(), "Operation failed.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btn_add.setOnClickListener(onClick_addUpdate);
        btn_save.setOnClickListener(onClick_addUpdate);

        /**
         * What to do when the cancel button is clicked. render a new home fragment.
         * */
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();     // new home fragment
            }
        });

        // Quantity default value of 1
        et_productQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    if (et_productQuantity.getText().toString().isEmpty())       et_productQuantity.setText("1");
                }
            }
        });

        // Unit Amount default value of 1
        et_unitAmount.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    if (et_unitAmount.getText().toString().isEmpty())       et_unitAmount.setText("1");
                }
            }
        });

        dbh.close();

        return view;
    }

    /**
     * Show/hide toggle between the category spinner and the newCategory EditText box.
     * @param show whether the new category should be shown or not.
     */
    private void showNewCategory(boolean show)
    {
        sp_category.setEnabled(!show);
        sp_category.setVisibility(show ? View.GONE : View.VISIBLE);
        et_newCategory.setEnabled(show);
        et_newCategory.setVisibility(show ? View.VISIBLE : View.GONE);
        iv_cancelNewCategory.setEnabled(show);
        iv_cancelNewCategory.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show)
        {
            showDeleteCategory(false);
            et_newCategory.requestFocus();
        }
        else        // cancel
        {
            et_newCategory.setText("");
            sp_category.setSelection(0);
            sp_category.requestFocus();
        }
    }

    /**
     * Show/hide toggle for the delete category icon.
     * @param show whether the category should be shown or not.
     */
    private void showDeleteCategory(boolean show)
    {
        iv_deleteCategory.setEnabled(show);
        iv_deleteCategory.setVisibility((show) ? View.VISIBLE : View.GONE);
    }

    /**
     * Display a Spinner-style DatePicker dialog for date input.
     * @param editText the EditText field into which the selected date-string will be inserted.
     */
    private void showDatePickerDialog(final EditText editText)
    {
        DatePickerDialog expDateDialog = new DatePickerDialog
        (
            getContext(),
            R.style.CustomDatePickerDialogTheme,
            new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth)
                {
                    String date = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year);
                    editText.setText(date);
                }
            },
             Calendar.getInstance().get(Calendar.YEAR),
             Calendar.getInstance().get(Calendar.MONTH),
             Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        expDateDialog.show();
    }

    /**
     * Check controls for invalid input.
     * @return whether or not the input is valid
     */
    public boolean validateInput()
    {
        boolean valid = true;

        // Check name
        if (TextUtils.isEmpty(et_productName.getText().toString()) )
        {
            et_productName.setError("Name cannot be blank.");
            valid = false;
        }

        // Check quantity
        if (TextUtils.isEmpty(et_productQuantity.getText().toString()) )
        {
            et_productQuantity.setError("Quantity cannot be blank.");
            valid = false;
        }

        // Check new category name
        if (et_newCategory.isEnabled() && TextUtils.isEmpty(et_newCategory.getText().toString()) )
        {
            et_newCategory.setError("Category name cannot be blank.");
            valid = false;
        }

        return valid;
    }

}
