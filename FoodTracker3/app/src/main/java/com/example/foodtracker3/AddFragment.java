package com.example.foodtracker3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // Inflate the xml which gives us a view
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        Button btn_add = view.findViewById(R.id.add_button);


        btn_add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DatabaseHelper dbh = new DatabaseHelper(v.getContext() );

                    // Add one test product
                    if (dbh.addTestProducts(1))
                    {
                        Toast.makeText(v.getContext(), "Record inserted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "Insert failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        // Get the item in the adapter





        return view;
    }
}
