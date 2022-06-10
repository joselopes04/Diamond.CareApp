package com.example.diamondcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.*;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.diamondcare.Model.User;
import com.example.diamondcare.Utility.NetworkChangeListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class Register extends AppCompatActivity {

    TextView textViewLogin, textViewTerms;
    EditText editTextName, editTextEmail, editTextPhone, editTextPassword;
    LottieAnimationView progressBar;
    CheckBox checkBox, googleCaptcha;
    Button btn_regist;
    TextInputLayout emailLayout, passLayout, nameLayout;


    String TAG = Register.class.getSimpleName();
    String SECRET_KEY  = "6Lfr3BcgAAAAAIW65JYtB6Aw5tGmkmGtXh2UApSc";
    String SITE_KEY = "6Lfr3BcgAAAAAJFe3HyGyNDaVkr7xAcWXar5971Y";
    RequestQueue queue;

    ArrayList<String> appointments;
    private FirebaseAuth mAuth;

    final Handler handler = new Handler(Looper.getMainLooper());
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_regist = (Button) findViewById(R.id.btn_regist);
        editTextName = (EditText) findViewById(R.id.user_name);
        nameLayout= (TextInputLayout) findViewById(R.id.textInputLayoutName);
        editTextEmail = (EditText) findViewById(R.id.user_email);
        emailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutmail);
        editTextPhone = (EditText) findViewById(R.id.user_phone);
        editTextPassword = (EditText) findViewById(R.id.user_password);
        passLayout = (TextInputLayout) findViewById(R.id.textInputLayoutPass);
        checkBox = findViewById(R.id.checkTerms);
        googleCaptcha = findViewById(R.id.googleCaptcha);
        progressBar = (LottieAnimationView) findViewById(R.id.progressBarRegist);
        queue = Volley.newRequestQueue(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.registerLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        nameLayout.setEndIconVisible(false);
        emailLayout.setEndIconVisible(false);

        //Link para ecrã de login
        textViewLogin = (TextView)findViewById(R.id.txt_page);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        textViewTerms = (TextView)findViewById(R.id.textViewTerms);
        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog();
            }
        });
    }



    //Butão Registo
    public void signupButtonClicked(View v){
        String txt_user_name = editTextName.getText().toString().trim();
        String txt_user_email = editTextEmail.getText().toString().trim();
        String txt_user_phone = editTextPhone.getText().toString().trim();
        String txt_user_password = editTextPassword.getText().toString().trim();
        String hashedPassword = BCrypt.withDefaults().hashToString(12, txt_user_password.toCharArray() );

        //Verificar informações
        nameLayout.setEndIconVisible(true);
        if (txt_user_name.isEmpty() || txt_user_name.length()<2){
            nameLayout.setError("Por favor insira o seu nome");
            nameLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else{
            nameLayout.setEndIconDrawable(R.drawable.ic_check_circle);
            nameLayout.setError(null);
        }

        emailLayout.setEndIconVisible(true);
        if (!Patterns.EMAIL_ADDRESS.matcher(txt_user_email).matches()){
            emailLayout.setError("Por favor insira um endereço de email válido");
            emailLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else{
            emailLayout.setEndIconDrawable(R.drawable.ic_check_circle);
            emailLayout.setError(null);
        }

        if (txt_user_phone.isEmpty()) {
            editTextPhone.setError("Por favor insira o número seu telemóvel");
            editTextPhone.requestFocus();
            return;
        }

        if(!validateMobile(txt_user_phone)) {
            editTextPhone.setError("Por favor insira um número de telemóvel válido");
            editTextPhone.requestFocus();
            return;
        }

        if (txt_user_password.isEmpty() || txt_user_password.length() < 6 ) {
            passLayout.setError("Por favor insira uma palavra passe com pelo menos 6 caracters");
            return;
        }
        else{
            passLayout.setError(null);
        }
        if(!checkBox.isChecked()){
            String toastcontent = "Por favor aceite os termos e condições";
            ToastError(toastcontent);
            return;
        }

        if(!googleCaptcha.isChecked()){
            String toastcontent = "Têm que confirmar que não é um robot";
            ToastError(toastcontent);
            return;
        }

        btn_regist.setEnabled(false);
        editTextName.setEnabled(false);
        editTextEmail.setEnabled(false);
        editTextPhone.setEnabled(false);
        editTextPassword.setEnabled(false);
        textViewTerms.setEnabled(false);
        textViewLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

      //Criar User na DB
      mAuth.createUserWithEmailAndPassword(txt_user_email, txt_user_password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      progressBar.setVisibility(View.GONE);

                      btn_regist.setEnabled(true);
                      editTextName.setEnabled(true);
                      editTextEmail.setEnabled(true);
                      editTextPhone.setEnabled(true);
                      editTextPassword.setEnabled(true);
                      textViewTerms.setEnabled(true);
                      textViewLogin.setEnabled(true);

                      if (task.isSuccessful()){

                          String birth ="";
                          User user = new User(txt_user_name,txt_user_email,txt_user_phone,hashedPassword,birth,appointments);

                          FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                          .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                          .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){

                                      String toastcontent = "Foi registado com sucesso";
                                      ToastSuccess(toastcontent);

                                      //Delay
                                      handler.postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              progressBar.setVisibility(View.GONE);
                                              Intent intent = new Intent(Register.this,Login.class);
                                              startActivity(intent);
                                          }
                                      }, 2500);
                                  }
                                  else{

                                      String toastcontent = "Algo correu mal ..., por favor tente novamente.";
                                      ToastError(toastcontent);
                                  }
                              }

                          });

                      } else{
                          String toastcontent = "Já existe uma conta associada a esse E-mail";
                          ToastError(toastcontent);

                      }

                  }
              });

      }
      //Verificar num de telefone
      boolean validateMobile(String input){
          String regex = "(9[1236][0-9]) ?([0-9]{3}) ?([0-9]{3})";
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(input);
          return matcher.matches();
      }

      //Chamar a função para mostrar os termos qnd se toca na checkbox
    public void checkTerms(View v){
        showWarningDialog();
    }

    //Mostrar Termos e Condições
    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        View layout_dialog = LayoutInflater.from(Register.this).inflate(R.layout.terms_and_conditions, null);
        builder.setView(layout_dialog);

        AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btn_acept);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(true);
                checkBox.setEnabled(false);
                dialog.dismiss();
            }
        });

    }

    //Google Recaptcha
    public void Captcha(View view) {
        googleCaptcha.setChecked(false);
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            handleSiteVerify(response.getTokenResult());
                            googleCaptcha.setChecked(true);
                            googleCaptcha.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        googleCaptcha.setChecked(false);
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d(TAG, "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d(TAG, "Unknown type of error: " + e.getMessage());
                        }
                    }
                });

    }
    protected  void handleSiteVerify(final String responseToken){
        //it is google recaptcha siteverify server
        //you can place your server url
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                //code logic when captcha returns true Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getBoolean("success")),Toast.LENGTH_LONG).show();
                            }
                            else{
                                googleCaptcha.setChecked(false);
                                Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getString("error-codes")),Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "JSON exception: " + ex.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        googleCaptcha.setChecked(false);
                        Log.d(TAG, "Error message: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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

    //Desabilitar o butão retroceder durante o registo
    @Override
    public void onBackPressed() {
        if(!btn_regist.isEnabled()){

        }else{
            super.onBackPressed();
        }
    }

    //Verificar a ligação à internet do user
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