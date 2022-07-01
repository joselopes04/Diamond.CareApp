package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

public class Points extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    TextView textViewPoints;

    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        textViewPoints = findViewById(R.id.textViewPoints);

        setUpToolbar();

        //Menu de navegação
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_points);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intentHome = new Intent(Points.this, Home.class);
                        startActivity(intentHome);
                        break;

                    case  R.id.nav_myProfile:
                        Intent intentmyprofile = new Intent(Points.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        Intent intentsession = new Intent(Points.this, SessionsDate.class);
                        startActivity(intentsession);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(Points.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case  R.id.nav_services:
                        Intent intentServices = new Intent(Points.this, Services.class);
                        startActivity(intentServices);
                        break;

                    case R.id.nav_points:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(Points.this, Settings.class);
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

        //Mostrar os dados do user no ecrã
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        userID =  user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    int points = userProfile.points;
                    String pointstxt = String.valueOf(points);

                    textViewPoints.setText(pointstxt);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String toastcontent = getString(R.string.errorToast);
                ToastError(toastcontent);

            }
        });

    }

    //Butão para abrir o drawermenu
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.pointsLayout);
        Toolbar toolbar = findViewById(R.id.toolbarpoints);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //Mostrar mensagem de erro
    public void ToastError(String toastcontent){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_error, (ViewGroup) findViewById(R.id.toast_error_layout));
        TextView toastText = layout.findViewById(R.id.toast_error_txt);
        Toast toast = new Toast(getApplicationContext());
        toastText.setText(toastcontent);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
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