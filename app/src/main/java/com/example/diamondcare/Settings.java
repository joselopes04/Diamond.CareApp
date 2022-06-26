package com.example.diamondcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final String SAVEDLANGUAGE = "Language";

    TextView txtViewLanguage;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtViewLanguage = findViewById(R.id.txtViewLanguage);

        setUpToolbar();

        navigationView = findViewById(R.id.navigation_menu);
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

    //Mostrar o dialog para o user selecionar o idioma
    public void changeLanguageDialog(View v){
        String savedLanguage;
        int selectLang = 1;
        SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString(SAVEDLANGUAGE,"");

        //Selecionar a opção que o user tinha escolhida anteriormente
        if(savedLanguage.equals("en")){
            selectLang = 1;
        }else{
            selectLang = 0;
        }

        String[] languages = {"Portugês", "English"};
        new androidx.appcompat.app.AlertDialog.Builder(Settings.this)
                .setTitle(getString(R.string.languageDialog))
                .setSingleChoiceItems(languages, selectLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = languages[i];
                        if(selected.equals("Portugês")){
                            setLocal(Settings.this,"pt");
                            finish();
                            startActivity(getIntent());

                        }else if (selected.equals("English")){
                            setLocal(Settings.this, "en");
                            finish();
                            startActivity(getIntent());
                        }
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    //Selecionar o idioma
    public void setLocal(Activity activity, String lanCode){
        Locale locale = new Locale(lanCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String language = Locale.getDefault().getLanguage();
        editor.putString(SAVEDLANGUAGE, language);
        editor.commit();
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