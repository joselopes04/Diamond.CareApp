package com.example.diamondcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    Spinner spinner;
    String text = "Idioma-Language";

    Switch switch_theme;

    public static final String KEY_ISNIGHTMODE = "isNightMode";
    public static final String MyPREFERENCES = "nightModePrefs";
    SharedPreferences sharedPreferences;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = findViewById(R.id.languageSelector);

        setUpToolbar();

//        switch_theme = findViewById(R.id.switch_theme);
//        sharedPreferences = getSharedPreferences("night", 0);
//        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);
//        if(booleanValue){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            switch_theme.setChecked(true);
//        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            switch_theme.setChecked(false);
//        }

        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_settings);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intenthome = new Intent(Settings.this, Home.class);
                        startActivity( intenthome);
                        break;

                    case  R.id.nav_myProfile:
                        Intent intentmyprofile = new Intent(Settings.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        Intent intentsession = new Intent(Settings.this, SessionsDate.class);
                        startActivity(intentsession);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(Settings.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case R.id.nav_settings:
                        drawerLayout.closeDrawer(navigationView);
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



//        switch_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    switch_theme.setChecked(true);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("night_mode",true);
//                    editor.commit();
//                } else {
//                    getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    switch_theme.setChecked(false);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("night_mode",false);
//                    editor.commit();
//                }
//            }
//        });

        String[] languages = { text, "Portugês", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectLang = adapterView.getItemAtPosition(i).toString();
                if(selectLang.equals("Portugês")){
                    setLocal(Settings.this,"pt");
                    finish();
                    startActivity(getIntent());

                }else if (selectLang.equals("English")){
                    setLocal(Settings.this, "en");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        }

        public void setLocal(Activity activity, String lanCode){
        Locale locale = new Locale(lanCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.settingsLayout);
        Toolbar toolbar = findViewById(R.id.toolbarsettings);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //Chamar o alertDialog para mostrar os termos
    public void checkTerms(View v){
        showWarningDialog();
    }

    //Mostrar Termos e Condições
    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        View layout_dialog = LayoutInflater.from(Settings.this).inflate(R.layout.terms_and_conditions, null);
        builder.setView(layout_dialog);

        AppCompatButton btnAccept = layout_dialog.findViewById(R.id.btn_accept);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //Butão back
    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(Settings.this, Home.class);
        startActivity(intentHome);
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}