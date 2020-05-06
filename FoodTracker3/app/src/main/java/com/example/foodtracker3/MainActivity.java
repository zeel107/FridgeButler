package com.example.foodtracker3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

    // ES - I added all of the Log Debug statements to help with fixing the bug that makes the app crash
    // when you try to select the Home tab from the Home tab. We probably just need to check which fragment
    // is currently active, and do nothing if selected_fragment == current_fragment.
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.nav_list:
                            Log.d("MainActivity", "Line 43");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                            Log.d("MainActivity", "Line 46");
                            Button addButton = findViewById(R.id.add_button);
                            Log.d("MainActivity", "Line 48");
                            final EditText nameInput = (EditText) findViewById(R.id.name_input);
                            Log.d("MainActivity", "Line 50");
                            final EditText quanityInput = (EditText) findViewById(R.id.quantity_input);
                            Log.d("MainActivity", "Line 52");
                            final EditText expirationDateInput = (EditText) findViewById(R.id.expiration_input);
                            Log.d("MainActivity", "Line 54");
                            addButton.setOnClickListener(new View.OnClickListener()
                            {
                                public void onClick(View v)
                                {
                                    String name = nameInput.getText().toString();
                                    Log.d("MainActivity", "Line 59");
                                    String expirationDate = expirationDateInput.getText().toString();
                                    Log.d("MainActivity", "Line 61");
                                    String quantity = quanityInput.getText().toString();
                                    Log.d("MainActivity", "Line 63");
                                    ((TextView) findViewById(R.id.name_input)).setText(name);
                                    Log.d("MainActivity", "Line 65");
                                    ((TextView) findViewById(R.id.quantity_input)).setText(quantity);
                                    Log.d("MainActivity", "Line 67");
                                    ((TextView) findViewById(R.id.expiration_input)).setText(expirationDate);
                                    Log.d("MainActivity", "Line 69");
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
