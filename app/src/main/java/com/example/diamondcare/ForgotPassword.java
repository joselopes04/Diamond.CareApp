package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText edit_text_email_rec;
    LottieAnimationView progressBar;
    FirebaseAuth mAuth;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edit_text_email_rec = (EditText) findViewById(R.id.user_email_recover);
        progressBar = (LottieAnimationView) findViewById(R.id.progressBarForgot_password);
        mAuth = FirebaseAuth.getInstance();

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.forgotpasswordLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }

    public void btn_recoverPressed(View v){
        resetPassword();
    }

    private void resetPassword(){
        new AlertDialog.Builder(ForgotPassword.this)
                .setTitle("Repor Palavra-Passe")
                .setMessage("Tens mesmo a certeza que desejas repor a tua palavra-passe?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String txt_email_rec = edit_text_email_rec.getText().toString().trim();

                        if (!Patterns.EMAIL_ADDRESS.matcher(txt_email_rec).matches()){
                            edit_text_email_rec.setError("Por favor insira um endereço de email válido");
                            edit_text_email_rec.requestFocus();
                            return;
                        }

                        mAuth.sendPasswordResetEmail(txt_email_rec).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    String toastcontent = "Verifique o seu E-mail";
                                    ToastSuccess(toastcontent);

                                    //Delay
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent intent = new Intent(ForgotPassword.this, Login.class);
                                            startActivity(intent);
                                        }
                                    }, 2500);

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