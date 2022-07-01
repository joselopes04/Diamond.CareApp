package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diamondcare.Model.Appointment;
import com.example.diamondcare.Model.User;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.example.diamondcare.Utility.ReminderBroadcast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Sessions_step3 extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    long maxId=0;

    ArrayList<Appointment> appointments;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    DatabaseReference databaseReference;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @BindView(R.id.step_view3)
    StepView stepView;
    @BindView(R.id.btn_prev)
    Button btn_prev;
    @BindView(R.id.btn_next)
    Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions_step3);

        TextView txtData = findViewById(R.id.textViewData);
        TextView txtHora = findViewById(R.id.textViewHora);

        String date = "Erro";
        String hour = "Erro";

        //obter os dados que o user preencheu no ecrã anterior
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            date = extras.getString("date");
            hour = extras.getString("hour");
        }

        txtData.setText(date);
        txtHora.setText(hour);

        //Steps no topo do ecrã
        ButterKnife.bind(Sessions_step3.this);

        setUpToolbar();
        setUpSetView();
        setUpColorButton();

        //Contar quantas marcações o user já têm na DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        databaseReference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxId=(snapshot.child("marcacoes").getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast2 = Toast.makeText(Sessions_step3.this, "Erro na DB", Toast.LENGTH_SHORT);
                toast2.show();
            }
        });

        //Menu
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_sessions);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intentHome = new Intent(Sessions_step3.this, Home.class);
                        startActivity(intentHome);
                        break;

                    case  R.id.nav_myProfile:
                        Intent intentmyprofile = new Intent(Sessions_step3.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case  R.id.nav_services:
                        Intent intentServices = new Intent(Sessions_step3.this, Services.class);
                        startActivity(intentServices);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(Sessions_step3.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case R.id.nav_points:
                        Intent intentPoints = new Intent(Sessions_step3.this, Points.class);
                        startActivity(intentPoints);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(Sessions_step3.this, Settings.class);
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
    }

    //Butão para abrir o drawermenu
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.sessionsStep3_Layout);
        Toolbar toolbar = findViewById(R.id.toolbarsessionsStep3);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    //Defenir os steps no topo do ecrã
    private void setUpSetView() {
        List<String> stepList = new ArrayList<>();
        stepList.add(getString(R.string.date));
        stepList.add(getString(R.string.confirm));
        stepView.setSteps(stepList);
        stepView.go(1,true);
    }

    //Ajustar a cor dos botões
    private void setUpColorButton() {

        if (btn_next.isEnabled()){
            btn_next.setBackgroundResource(R.color.purple_700);
        }else{
            btn_next.setBackgroundResource(R.color.darker_grey);
        }
        if (btn_prev.isEnabled()){
            btn_prev.setBackgroundResource(R.color.purple_700);
        }else{
            btn_prev.setBackgroundResource(R.color.darker_grey);
        }

    }

    //Butão para voltar ao ecrã anterior
    public void btn_prev3Clicked(View v){
        Intent intentNext = new Intent(Sessions_step3.this, SessionsDate.class);
        startActivity(intentNext);
    }

    //Butão para realizar a marcação
    public void btn_concluir3Clicked(View v){
        new AlertDialog.Builder(Sessions_step3.this)
                .setTitle(getString(R.string.bookingTitle))
                .setMessage(getString(R.string.bookingMessage))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = "Erro";
                        String hour = "Erro";

                        //obter os dados que o user preencheu no ecrã anterior
                        Bundle extras = getIntent().getExtras();
                        if(extras != null){
                            date = extras.getString("date");
                            hour = extras.getString("hour");
                        }

                        Appointment appointments = new Appointment(date, hour);
                        HashMap User = new HashMap();
                        databaseReference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()){
                                    databaseReference.child(userID).child("marcacoes").child("M-Id"+String.valueOf(maxId+1)).setValue(appointments);

                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_sucess, (ViewGroup) findViewById(R.id.toast_sucess_layout));
                                    TextView toastText = layout.findViewById(R.id.toast_sucess_txt);
                                    toastText.setText(getString(R.string.AppointmentSuccess));
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

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
                                                int totalpoints = points + 15;
                                                updateData(totalpoints);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            String toastcontent = getString(R.string.errorToast);
                                            ToastError(toastcontent);

                                        }
                                    });


                                    createNotification();
                                    Toast.makeText(Sessions_step3.this, "Reminder Set", Toast.LENGTH_SHORT).show();


                                    Intent intentHome = new Intent(Sessions_step3.this, ReminderBroadcast.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Sessions_step3.this,0,intentHome,PendingIntent.FLAG_IMMUTABLE);

                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                    long timeAtButtonClick = System.currentTimeMillis();
                                    long tenSecondsInMillis = 1000 * 10;

                                    alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick + tenSecondsInMillis,pendingIntent);

                                }else{
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast_error, (ViewGroup) findViewById(R.id.toast_error_layout));
                                    TextView toastText = layout.findViewById(R.id.toast_error_txt);
                                    toastText.setText(getString(R.string.errorToast));
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                    Toast.makeText(Sessions_step3.this, (getString(R.string.errorToast)), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification(){
            CharSequence name = "Diamond Care";
            String description = "Lembrete de marcação";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Diamond Care", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
    }

    //Salvar os dados que o utilizador introduziu na DB
    public void updateData(int totalpoints){
        HashMap User = new HashMap();
        User.put("points",totalpoints);
        userID =  user.getUid();
        databaseReference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        databaseReference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()){

                    String toastcontent = getString((R.string.successChangesToast));
                    ToastSuccess(toastcontent);

                }else{
                    String toastcontent = getString((R.string.errorToast));
                    ToastError(toastcontent);

                }
            }
        });
    }

    //Mostrar mensagem de sucesso
    public void ToastSuccess(String toastcontent) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_sucess, (ViewGroup) findViewById(R.id.toast_sucess_layout));
        TextView toastText = layout.findViewById(R.id.toast_sucess_txt);
        Toast toast = new Toast(getApplicationContext());
        toastText.setText(toastcontent);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    //Mostrar mensagem de erro
    public void ToastError(String toastcontent) {
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