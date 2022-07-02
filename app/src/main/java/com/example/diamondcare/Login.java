package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    TextView textViewCriarConta,textViewEsqPassword ;
    EditText editTextEmail, editTextPassword;
    CheckBox remenber;
    LottieAnimationView progressBar;
    FirebaseAuth mAuth;
    Button btn_entrar;
    TextInputLayout emailLayout, passLayout;

    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //Objeto para fzr um delay
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_entrar = (Button) findViewById(R.id.btn_entrar);
        editTextEmail = (EditText) findViewById(R.id.user_email);
        editTextPassword = (EditText) findViewById(R.id.user_password);
        textViewEsqPassword = (TextView) findViewById(R.id.textView2);
        textViewCriarConta = (TextView) findViewById(R.id.txt_regist);
        remenber = (CheckBox) findViewById(R.id.checkBoxRemenber);
        progressBar = (LottieAnimationView) findViewById(R.id.progressBarLogin);
        emailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutmail);
        passLayout = (TextInputLayout) findViewById(R.id.textInputLayoutPass);

        mAuth = FirebaseAuth.getInstance();

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.loginLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        emailLayout.setEndIconVisible(false);
    }

    //Text view esqueceu se da palavra passe
    public void txtViewEsqPassword(View v){
        Intent intent = new Intent(Login.this,ForgotPassword.class);
        startActivity(intent);
    }

    //Text view criar conta
    public void txtViewCriarConta(View v){
        Intent intent = new Intent(Login.this,Register.class);
        startActivity(intent);
    }

    //Butão Login
    public void btnloginClicked(View v){

        String userMail = editTextEmail.getText().toString().trim();
        String userPass = editTextPassword.getText().toString().trim();

        emailLayout.setEndIconVisible(true);

        //Verificar se os dados são válidos
        if (!Patterns.EMAIL_ADDRESS.matcher(userMail).matches()){
            emailLayout.setError(getString(R.string.invalidEmail));
            emailLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else{
            emailLayout.setEndIconDrawable(R.drawable.ic_check_circle);
            emailLayout.setError(null);
        }

        if (editTextPassword.length() == 0){
            passLayout.setError(getString(R.string.insertPassword));
            return;
        }
        else{
            passLayout.setError(null);
        }

        //Desativar tudo enquanto a app verifica os dados
        btn_entrar.setEnabled(false);
        remenber.setEnabled(false);
        textViewEsqPassword.setEnabled(false);
        textViewCriarConta.setEnabled(false);
        editTextEmail.setEnabled(false);
        editTextPassword.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);



        //Verificação do user na DB
        mAuth.signInWithEmailAndPassword(userMail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    //Delay
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(Login.this,Animation.class);
                            startActivity(intent);
                        }
                    }, 2500);

                }
                else {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_error, (ViewGroup) findViewById(R.id.toast_error_layout));
                    TextView toastText = layout.findViewById(R.id.toast_error_txt);
                    toastText.setText(getString(R.string.wrongPassword));
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    btn_entrar.setEnabled(true);
                    remenber.setEnabled(true);
                    textViewEsqPassword.setEnabled(true);
                    textViewCriarConta.setEnabled(true);
                    editTextEmail.setEnabled(true);
                    editTextPassword.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        //Manter iniciada a conta do utilizador
                if (remenber.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("Checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "ON");
                    editor.apply();

                }else if(!remenber.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("Checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "OFF");
                    editor.apply();

                }
    }

    //Desativar butão back durante a verificação dos dados
    @Override
    public void onBackPressed() {
        if(!remenber.isEnabled()){

        }else{
           super.onBackPressed();
        }
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