package com.example.diamondcare;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.IntentFilter;
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
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbar();

        //Menu de navegação
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

                    case  R.id.nav_myProfile:
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

                    case R.id.nav_points:
                        Intent intentPoints = new Intent(Home.this, Points.class);
                        startActivity(intentPoints);
                        break;

                    case  R.id.nav_services:
                        Intent intentServices = new Intent(Home.this, Services.class);
                        startActivity(intentServices);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(Home.this, Settings.class);
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        userID = user.getUid();

        final TextView welcomeTextView = findViewById(R.id.welcome);

        //Buscar nome do user na DB e consoante as horas do dia mostrar a saudação
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userprofile = snapshot.getValue(User.class);

                Calendar calendar = Calendar.getInstance();
                int Hours = calendar.get(Calendar.HOUR_OF_DAY);
                String greeting = getString(R.string.greetings);

                if (Hours > 0 && Hours<=4){
                    greeting = getString(R.string.night);
                }
                if (Hours > 4 && Hours<=12) {
                    greeting = getString(R.string.day);
                }
                if (Hours > 12 && Hours<=19) {
                    greeting = getString(R.string.noon);
                }
                if(Hours > 19){
                    greeting = getString(R.string.night);
                }

                if (userprofile != null){
                    String name = userprofile.name;
                    welcomeTextView.setText(  greeting+ " " + name +" !");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, getString(R.string.errorToast), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Cards de navegação
    public void cardProfileClicked(View v){
        Intent intentmyprofile = new Intent(Home.this, MyProfile.class);
        startActivity(intentmyprofile);
    }
    public void cardSessionsClicked(View v){
        Intent intentsession = new Intent(Home.this, SessionsServices.class);
        startActivity(intentsession);
    }
    public void cardMySessionsClicked(View v){
        Intent intentMySession = new Intent(Home.this, MySessions.class);
        startActivity(intentMySession);
    }
    public void cardServicesClicked(View v){
        Intent intentMySession = new Intent(Home.this, Services.class);
        startActivity(intentMySession);
    }
    public void cardPointsClicked(View v){
        Intent intentPoints = new Intent(Home.this, Points.class);
        startActivity(intentPoints);
    }
    public void cardSettingsClicked(View v){
        Intent intentsettings = new Intent(Home.this, Settings.class);
        startActivity( intentsettings);
    }

    //Butão para abrir o drawermenu
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbarhome);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //Butão back
    @Override
    public void onBackPressed() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_normal, (ViewGroup) findViewById(R.id.toast_normal_layout));
        TextView toastText = layout.findViewById(R.id.toast_normal_txt);
        toastText.setText(getString(R.string.backToast));
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