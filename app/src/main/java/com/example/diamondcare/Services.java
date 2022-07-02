package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.diamondcare.Adapter.MyTimeSlotAdapter;
import com.example.diamondcare.Adapter.ServicesAdapter;
import com.example.diamondcare.Model.ServicesData;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Services extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView recyclerView;

    private ArrayList<ServicesData> servicesData;
    private ServicesAdapter.RecyclerViewClickListener listener;

    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        setUpToolbar();

        recyclerView = findViewById(R.id.recycler_Services);

        //Menu de navegação
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_services);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intentHome = new Intent(Services.this, Home.class);
                        startActivity(intentHome);
                        break;

                    case  R.id.nav_myProfile:
                        Intent intentmyprofile = new Intent(Services.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        Intent intentsession = new Intent(Services.this, SessionsDate.class);
                        startActivity(intentsession);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(Services.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case  R.id.nav_services:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case R.id.nav_points:
                        Intent intentPoints = new Intent(Services.this, Points.class);
                        startActivity(intentPoints);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(Services.this, Settings.class);
                        startActivity( intentsettings);
                        break;

                    case  R.id.nav_share:{
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody =  getString(R.string.shareBody) + " http://play.google.com/store/apps/detail?id=" + getPackageName();
                        String shareSub = getString(R.string.shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        String shareTitle = getString(R.string.shareTitle);
                        startActivity(Intent.createChooser(sharingIntent, shareTitle));

                    }
                    break;
                }
                return true;

            }
        });

        //Lista de serviços
        servicesData = new ArrayList<>();
        servicesData.add(new ServicesData(getString(R.string.hairCutTitle),getString(R.string.hairCutDesc),R.drawable.hair, "5 €"));
        servicesData.add(new ServicesData(getString(R.string.manicureTitle),getString(R.string.manicureDesc),R.drawable.nails, "5 €"));
        servicesData.add(new ServicesData(getString(R.string.pedicureTitle),getString(R.string.pedicureDesc),R.drawable.pedicure, "5 €"));
        servicesData.add(new ServicesData(getString(R.string.depilationTitle),getString(R.string.depilationDesc),R.drawable.hair_removal, "10 €"));
        servicesData.add(new ServicesData(getString(R.string.massageTitle),getString(R.string.massageDesc),R.drawable.massage, "20 €"));
        servicesData.add(new ServicesData(getString(R.string.goldTitle),getString(R.string.goldDescription),R.drawable.gold, "30 €"));
        servicesData.add(new ServicesData(getString(R.string.diamondTitle),getString(R.string.diamondDescription),R.drawable.diamond, "40 €"));
        setOnClickListner();

        ServicesAdapter adapter = new ServicesAdapter(listener, servicesData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    //Butão para abrir o drawermenu
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayoutServices);
        Toolbar toolbar = findViewById(R.id.toolbarServices);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //Quando se clica num serviço
    private void setOnClickListner() {
        listener = new ServicesAdapter.RecyclerViewClickListener(){
            @Override
            public void onRecyclerClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), SessionsDate.class);
                intent.putExtra("service", servicesData.get(position).getServiceTitle());
                intent.putExtra("price", servicesData.get(position).getPrice());
                startActivity(intent);
            }
        };
    }

    //Verificar ligação á internet do user qnd este ecrã é iniciado
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    //Parar de verificar ligação á internet do user qnd este ecrã é fechado
    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}