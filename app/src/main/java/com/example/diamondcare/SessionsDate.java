package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.diamondcare.Adapter.MyTimeSlotAdapter;
import com.example.diamondcare.Model.TimeSlotData;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.material.navigation.NavigationView;
import com.shuhart.stepview.StepView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SessionsDate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    int Year, Month, Day;
    String sessiondate;

    private ArrayList<TimeSlotData> timeSlotData;
    private RecyclerView recyclerView;
    private MyTimeSlotAdapter.RecyclerViewClickListener listener;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    TextView textViewDate;


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
        setContentView(R.layout.activity_sessions_date);

        timeSlotData = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_marking);
        setTimeSlot();

        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        textViewDate = (TextView) findViewById(R.id.date_selector);

        ButterKnife.bind(SessionsDate.this);

        setUpToolbar();
        setUpSetView();
        setUpColorButton();

        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_sessions);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intentHome = new Intent(SessionsDate.this, Home.class);
                        startActivity(intentHome);
                        break;

                    case  R.id.nav_myProfile:
                        Intent intentmyprofile = new Intent(SessionsDate.this, MyProfile.class);
                        startActivity(intentmyprofile);
                        break;

                    case  R.id.nav_sessions:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(SessionsDate.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case R.id.nav_points:
                        Intent intentPoints = new Intent(SessionsDate.this, Points.class);
                        startActivity(intentPoints);
                        break;

                    case R.id.nav_settings:
                        Intent intentsettings = new Intent(SessionsDate.this, Settings.class);
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

        //Date Picker
        final TextView text_date = (TextView) findViewById(R.id.date_selector);
        text_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                datePickerDialog = DatePickerDialog.newInstance(SessionsDate.this, Year, Month, Day);
                datePickerDialog.setThemeDark(true);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Agendar dia");

                Calendar min_date = Calendar.getInstance();
                datePickerDialog.setMinDate(min_date);

                Calendar max_date = Calendar.getInstance();
                max_date.set(Calendar.MONTH, Month+6);
                datePickerDialog.setMaxDate(max_date);

                //Desativar fins de semana
                for (Calendar loopdate = min_date; min_date.before(max_date); min_date.add(Calendar.DATE, 1), loopdate = min_date) {
                    int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                        Calendar[] disabledDays =  new Calendar[1];
                        disabledDays[0] = loopdate;
                        datePickerDialog.setDisabledDays(disabledDays);
                    }
                }

                //Desativar feriados
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String[] holidays = {"10-06-2022","16-06-2022","15-08-2022","05-10-2022","01-11-2022","01-12-2022","08-12-2022"};
                java.util.Date date2 = null;

                for (int i = 0;i < holidays.length; i++) {
                    try {
                        date2 = sdf.parse(holidays[i]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar = dateToCalendar(date2);

                    List<Calendar> dates = new ArrayList<>();
                    dates.add(calendar);
                    Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
                    datePickerDialog.setDisabledDays(disabledDays1);
                }

                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });


    }

    private void setAdapter() {
        setOnClickListner();
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(timeSlotData, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListner() {
        listener = new MyTimeSlotAdapter.RecyclerViewClickListener() {
            @Override
            public void onRecyclerClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), Sessions_step3.class);
                intent.putExtra("date", sessiondate);
                intent.putExtra("hour", timeSlotData.get(position).getHour());
                startActivity(intent);
            }
        };
    }

    private void setTimeSlot() {
        timeSlotData.add(new TimeSlotData("09:00 - 09:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("09:30 - 10:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("10:00 - 10:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("10:30 - 11:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("11:00 - 11:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("11:30 - 12:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("13:00 - 13:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("13:30 - 14:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("14:00 - 14:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("14:30 - 15:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("15:00 - 15:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("15:30 - 16:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("16:00 - 16:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("16:30 - 17:00", "Disponível", false));
        timeSlotData.add(new TimeSlotData("17:00 - 17:30", "Disponível", false));
        timeSlotData.add(new TimeSlotData("17:30 - 18:00", "Disponível", false));
    }

    public Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.sessionsDate_Layout);
        Toolbar toolbar = findViewById(R.id.toolbarsessionsDate);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void setUpSetView() {
        List<String> stepList = new ArrayList<>();
        stepList.add(getString(R.string.date));
        stepList.add(getString(R.string.confirm));
        stepView.setSteps(stepList);
        stepView.go(0,true);
    }

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

    public void setBtn_next3Clicked(View v){
        Intent intentNext = new Intent(SessionsDate.this, Sessions_step3.class);
        startActivity(intentNext);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        String date = Day+"/"+(Month+1)+"/"+Year;
        textViewDate.setText(date);
        sessiondate = date;
        setAdapter();
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