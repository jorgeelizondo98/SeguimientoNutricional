package com.example.seguimientonutricional;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String DIALOG_DATE = "DialogDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_signout)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(onItemClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_calendar:
                onCalendarSelected();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCalendarSelected(){
        //Despliega pop-up del calendario
        FragmentManager manager = getSupportFragmentManager();
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(manager,DIALOG_DATE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date dateTime = c.getTime();
        Log.d("checa",Integer.toString(year));
        String date = formatDate(dateTime);

        //TODO: Pasar string al fragmentHome para desplegar ahi la fecha.

    }

    private String formatDate(Date dateTime){
        SimpleDateFormat format = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy",
                new Locale("es","MEX"));
        String date = format.format(dateTime);
        return date;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private NavigationView.OnNavigationItemSelectedListener onItemClick = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            final DrawerLayout drawer = findViewById(R.id.drawer_layout);
            final NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
            if (menuItem.getItemId() == R.id.nav_signout){
                finish();
            }
            //This is for maintaining the behavior of the Navigation view
            NavigationUI.onNavDestinationSelected(menuItem, navController);
            //This is for closing the drawer after acting on it
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };



}
