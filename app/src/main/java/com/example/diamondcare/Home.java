package com.example.diamondcare;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diamondcare.Model.User;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Home extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    public static final String KEY_ISNIGHTMODE = "isNightMode";
    public static final String MyPREFERENCES = "nightModePrefs";

    SharedPreferences sharedPreferences;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbar();

        // Selecionar o tema escolhido pelo utilizador
        /*
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(KEY_ISNIGHTMODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
         }
*/
        //Menu
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case  R.id.nav_myprofile:
                        Intent intentmyprofile = new Intent(Home.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        Intent intentsession = new Intent(Home.this, SessionsDate.class);
                        startActivity(intentsession);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(Home.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(Home.this, Settings.class);
                        startActivity( intentsettings);
                        break;

                    case  R.id.nav_share:{

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody =  "Hey eu estou a adorar usar a app Diamond Care. Com ela consigo muitos DESCONTOS em cortes de cabelo. Aqui está o link para download http://play.google.com/store/apps/detail?id=" + getPackageName();
                        String shareSub = "Try now";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Partilhar com"));

                    }
                    break;
                }
                return true;


            }
        });



        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        userID = user.getUid();

        final TextView welcomeTextView = (TextView) findViewById(R.id.welcome);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userprofile = snapshot.getValue(User.class);

                Calendar calendar = Calendar.getInstance();
                int Hours = calendar.get(Calendar.HOUR_OF_DAY);
                String greeting = "Olá ";

                if (Hours > 0 && Hours<=4){
                    greeting = "Boa noite ";
                }
                if (Hours > 4 && Hours<=12) {
                    greeting = "Bom dia ";
                }
                if (Hours > 12 && Hours<=19) {
                    greeting = "Boa tarde ";
                }
                if(Hours > 19){
                    greeting = "Boa noite ";
                }

                if (userprofile != null){
                    String name = userprofile.name;
                    welcomeTextView.setText(  greeting + name +" !");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Algo correu mal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Butão para abrir o drawermenu
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbarhome);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_normal, (ViewGroup) findViewById(R.id.toast_normal_layout));
        TextView toastText = layout.findViewById(R.id.toast_normal_txt);
        toastText.setText("Pressione novamente para sair");
        Toast backToast = new Toast(getApplicationContext());
        backToast.setGravity(Gravity.CENTER, 0, 700);
        backToast.setDuration(Toast.LENGTH_SHORT);
        backToast.setView(layout);

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
        } else {
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
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