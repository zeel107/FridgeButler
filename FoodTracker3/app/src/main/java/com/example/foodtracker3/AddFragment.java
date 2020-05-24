package com.example.foodtracker3;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddFragment extends Fragment {

    // View control objects --- Defining them here instead of in onCreateView(), so they can be accessed by validateInput() method.
    Button btn_add;
    EditText et_productName;
    EditText et_productQuantity;
    EditText et_unitAmount;
    MaskEditText et_expirationDate;
    Spinner sp_unit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Inflate the xml which gives us a view
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING); // fix keyboard/button issue (now button will stay put)

        // ES - I'm not sure what's ideal, as far as where to declare and initialize these variables, but this works.
        btn_add = view.findViewById(R.id.add_button);
        et_productName = view.findViewById(R.id.name_input);
        et_productQuantity = view.findViewById(R.id.quantity_input);
        et_unitAmount = view.findViewById(R.id.unitAmount_input);
        et_expirationDate = view.findViewById(R.id.expiration_input);
        sp_unit = view.findViewById(R.id.unit_input);
        //et_expirationDate.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE); // fix no slash in keyboard? didn't work

        final DatabaseHelper dbh = new DatabaseHelper(getContext());      // is getContext() reliable, or will it sometimes return null? Research it more

        // Units setup
        final ArrayList<Unit> units = dbh.getUnits();
        List<String> spList_unitAbbrevs = new ArrayList<>();

        for (Unit i : units)    // Add unit abbreviation strings to spinner list
        {
            spList_unitAbbrevs.add(i.getAbbrev());
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, spList_unitAbbrevs);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_unit.setAdapter(unitAdapter);
        sp_unit.setSelection(1);    // units[0] == "n/a", units[1] == "ct" (the default)

        btn_add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (validateInput() == false)   return;

                    Date pur = new Date();
                    Date exp = Product.appStr_toDate(et_expirationDate.getText().toString());
                    long unitId = units.get(sp_unit.getSelectedItemPosition()).getId();

                    Product p = new Product
                    (
                        -1,
                        et_productName.getText().toString(),
                        Integer.parseInt(et_productQuantity.getText().toString()),
                        unitId,
                        Double.parseDouble(et_unitAmount.getText().toString()),
                        pur,
                        exp,
                        pur.after(exp),             // Determine if item is already expired
                        1,
                        dbh.getUnit(unitId)
                    );

                    // Insert the record
                    if (dbh.addProduct(p) )                 // dbh.addTestProducts(1))
                    {
                        Toast.makeText(v.getContext(), "Record inserted", Toast.LENGTH_SHORT).show();
                        // Clear text boxes after successful insert
                        et_productName.getText().clear();
                        et_productQuantity.getText().clear();
                        et_expirationDate.getText().clear();
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "Insert failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

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
            }
        );

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
            }
        );

        dbh.close();

        return view;
    }

    // However we decide to validate input later on, we can add it here. Or we can add it up above
    // to onCreateView(), but this seemed less cluttered.
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

        // Check expiration_date
        if (TextUtils.isEmpty(et_expirationDate.getText().toString()) )
        {
            et_expirationDate.setError("Expiration date cannot be blank.");
            valid = false;
        }
        // Check date format (there's probably a more efficient way to do this)
        else
        {
            String dateStr = et_expirationDate.getText().toString();
            Date date = Product.appStr_toDate(dateStr);
            if (date == null)
            {
                et_expirationDate.setError("Invalid date format. (eg. 12/25/2020)");
                valid = false;
            }
            else if (Integer.parseInt(dateStr.substring(0,2)) > 12 || Integer.parseInt(dateStr.substring(3,5)) > 31
            || (Integer.parseInt(dateStr.substring(6)) < Calendar.getInstance().get(Calendar.YEAR)) )
            {
                et_expirationDate.setError("Invalid date.");
                valid = false;
            }
        }

        return valid;
    }

}
