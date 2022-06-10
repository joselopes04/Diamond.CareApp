package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.diamondcare.Model.User;
import com.example.diamondcare.Utility.NetworkChangeListener;
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

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyProfile extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,DatePickerDialog.OnDateSetListener {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    int Year, Month, Day;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    DatabaseReference databaseReference;
    EditText editTextName, editTextEmail, editTextPhone;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    TextView textViewDate;
    Button save;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setUpToolbar();

        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        save = findViewById(R.id.btn_save);
        editTextName = (EditText) findViewById(R.id.txtName);
        editTextEmail = (EditText) findViewById(R.id.txtMail);
        editTextPhone = (EditText) findViewById(R.id.txtPhone);
        textViewDate = (TextView) findViewById(R.id.text_date);

        //Menu de navegação
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setCheckedItem(R.id.nav_myprofile);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_home:
                        Intent intentHome = new Intent(MyProfile.this, Home.class);
                        startActivity(intentHome);

                    case  R.id.nav_myprofile:
                        drawerLayout.closeDrawer(navigationView);
                        break;

                    case  R.id.nav_sessions:
                        Intent intentSession = new Intent(MyProfile.this, SessionsDate.class);
                        startActivity(intentSession);
                        break;

                    case  R.id.nav_mySessions:
                        Intent intentMySession = new Intent(MyProfile.this, MySessions.class);
                        startActivity(intentMySession);
                        break;

                    case  R.id.nav_share:{

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody =  "Hey eu estou a adorar usar a app Diamond Care http://play.google.com/store/apps/detail?id=" + getPackageName();
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


        //Mostrar os dados do user
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        userID =  user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    String name = userProfile.name;
                    String email = userProfile.email;
                    String phone = userProfile.phone;
                    String birth = userProfile.birth;

                    editTextName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);

                    //Verificar se o user já têm data de nascimento
                    if(!birth.isEmpty()){
                        textViewDate.setText(birth);
                    }else{
                        textViewDate.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String toastcontent = "Algo correu mal ..., por favor tente novamente.";
                ToastError(toastcontent);

            }
        });

    }

    //Date Picker
    public void txtDataClicked(View v){
            datePickerDialog = DatePickerDialog.newInstance(MyProfile.this, Year, Month, Day);
            datePickerDialog.setThemeDark(true);
            datePickerDialog.showYearPickerFirst(false);
            datePickerDialog.setTitle("Data de nascimento");

            Calendar min_date = Calendar.getInstance();
            min_date.set(Calendar.YEAR, Year-100);
            datePickerDialog.setMinDate(min_date);

            Calendar max_date = Calendar.getInstance();
            max_date.set(Calendar.YEAR, Year-10);
            datePickerDialog.setMaxDate(max_date);

            datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
        }

    //Butão para abrir o Menu de navegação
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.myprofileLayout);
        Toolbar toolbar = findViewById(R.id.toolbarmyprofile);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    //Menu com os 3 pontos
    public void dotsMenuClicked(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.myprofile_menu);
        popupMenu.show();
    }

    //Opções do menu
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.menu_edit_profile:
                editData();
                return true;
            case R.id.menu_edit_password:
                resetPassword();
                return true;
            case R.id.menu_delete_account:
                deleteUser();
                return true;
            default:
                return false;
        }
    }

    //Habilitar a possibilidade de alterar os dados
    public void editData() {
        save.setVisibility(View.VISIBLE);
        editTextName.setEnabled(true);
        editTextEmail.setEnabled(true);
        editTextPhone.setEnabled(true);
        textViewDate.setEnabled(true);

    }

    //Mostrar data que o user escolheu
    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        String date = Day+"/"+(Month+1)+"/"+Year;
        textViewDate.setText(date);
        save.setVisibility(View.VISIBLE);
    }

    //Salvar dados que o utilizador introduziu
    public void btnsavedataClicked(View v){
        String username = editTextName.getText().toString().trim();
        String useremail = editTextEmail.getText().toString().trim();
        String userphone = editTextPhone.getText().toString().trim();
        String userbirth = textViewDate.getText().toString().trim();

        if (username.isEmpty() || username.length()<2) {
            editTextName.setError("Por favor insira o seu nome");
            editTextName.requestFocus();
            return;
        }

        if (useremail.isEmpty()) {
            editTextEmail.setError("Por favor insira o seu E-Mail");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            editTextEmail.setError("Por favor insira um endereço de email válido");
            editTextEmail.requestFocus();
            return;
        }

        if (userphone.isEmpty()) {
            editTextPhone.setError("Por favor insira o número seu telemóvel");
            editTextPhone.requestFocus();
            return;
        }

        if(!validateMobile(userphone)) {
            editTextPhone.setError("Por favor insira um número de telemóvel válido");
            editTextPhone.requestFocus();
            return;
        }

        new AlertDialog.Builder(MyProfile.this)
                .setTitle("Confirmar alterações")
                .setMessage("Tens mesmo a certeza que desejas alterar os dados da tua conta?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        updateData(username,useremail,userphone, userbirth);

                        save.setVisibility(View.GONE);
                        editTextName.setEnabled(false);
                        editTextEmail.setEnabled(false);
                        editTextPhone.setEnabled(false);
                        textViewDate.setEnabled(false);

                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    //Verificar nº de telefone
    boolean validateMobile(String input){
        String regex = "(9[1236][0-9]) ?([0-9]{3}) ?([0-9]{3})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    //Salvar os dados que o utilizador introduziu na DB
    public void updateData(String username, String useremail, String userphone, String date){
        HashMap User = new HashMap();
        User.put("name",username);
        User.put("email", useremail);
        User.put("phone",userphone);
        User.put("birth",date);
        userID =  user.getUid();
        databaseReference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        databaseReference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()){

                    //atualizar o email de no mAuth
                    user.updateEmail(useremail);

                    String toastcontent = "Alterações realizadas com sucesso";
                    ToastSuccess(toastcontent);

                }else{
                    String toastcontent = "Algo correu mal...";
                    ToastError(toastcontent);

                }
            }
        });
    }

    //Mandar email para repor a pass
    public void resetPassword(){
        new AlertDialog.Builder(MyProfile.this)
                .setTitle("Repor Palavra-Passe")
                .setMessage("Tens mesmo a certeza que desejas repor a tua palavra-passe?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String useremail = editTextEmail.getText().toString().trim();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendPasswordResetEmail(useremail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            String toastcontent = "Verifique o seu E-mail";
                                            ToastSuccess(toastcontent);
                                        }
                                        else {
                                            String toastcontent = "Falha ao resetar a palavra-passe. Tente novamente...";
                                            ToastError(toastcontent);
                                        }
                                    }
                                });
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    //apagar a conta do user
    public void deleteUser(){
        new AlertDialog.Builder(MyProfile.this)
                .setTitle("Apagar a conta")
                .setMessage("Tens mesmo a certeza que queres apagar a tua conta, esta ação e irreversível! Iremos sentir muito a sua falta :(")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //apagar user na realtimedatabase
                                            databaseReference = FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(userID);
                                            databaseReference.removeValue();

                                            SharedPreferences preferences = getSharedPreferences("Checkbox", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            String keep = preferences.getString("remember", "");
                                            editor.putString("remember", "OFF");
                                            editor.apply();

                                            String toastcontent = "Conta apagada com sucesso";
                                            ToastSuccess(toastcontent);

                                            Intent intentHome = new Intent(MyProfile.this, MainActivity.class);
                                            startActivity(intentHome);

                                        }else{
                                            String toastcontent = "Falha ao apagar a conta. Tente novamente... ";
                                            ToastError(toastcontent);
                                        }
                                    }
                                });

                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    //Sair da conta
    public void btnlogoutnClicked(View v){
        new AlertDialog.Builder(MyProfile.this)
                .setTitle("Terminar sessão")
                .setMessage("Tens mesmo a certeza que desejas terminar sessão?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences preferences = getSharedPreferences("Checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        String keep = preferences.getString("remember", "");
                        editor.putString("remember", "OFF");
                        editor.apply();

                        String toastcontent = "Saiu da conta com sucesso";
                        ToastSuccess(toastcontent);

                        Intent intentHome = new Intent(MyProfile.this, MainActivity.class);
                        startActivity(intentHome);
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    //Butão back
    @Override
    public void onBackPressed() {
        if(save.getVisibility() == View.VISIBLE){
            new AlertDialog.Builder(MyProfile.this)
                    .setTitle("Voltar")
                    .setMessage("Queres voltar atrás ? " +
                            "Todos os dados não guardados serão perdidos!")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentHome = new Intent(MyProfile.this, Home.class);
                            startActivity(intentHome);
                        }
                    }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }else{
            Intent intentHome = new Intent(MyProfile.this, Home.class);
            startActivity(intentHome);
        }
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

    //Verificar ligação á internet do user
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