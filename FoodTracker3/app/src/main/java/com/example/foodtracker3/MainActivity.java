package com.example.foodtracker3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.nav_list:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                            Button addButton = findViewById(R.id.add_button);
                            final EditText nameInput = (EditText) findViewById(R.id.name_input);
                            final EditText quanityInput = (EditText) findViewById(R.id.quantity_input);
                            final EditText expirationDateInput = (EditText) findViewById(R.id.expiration_input);
                            addButton.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    String name = nameInput.getText().toString();
                                    String expirationDate = expirationDateInput.getText().toString();
                                    String quantity = quanityInput.getText().toString();
                                    ((TextView) findViewById(R.id.name_input)).setText(name);
                                    ((TextView) findViewById(R.id.quantity_input)).setText(quantity);
                                    ((TextView) findViewById(R.id.expiration_input)).setText(expirationDate);
                                }
                            });
                            break;
                        case R.id.nav_add:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddFragment()).commit();

                            break;
                    }



                    return true;
                }
            };
}
