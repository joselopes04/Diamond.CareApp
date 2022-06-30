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
    Button btn_register;
    TextInputLayout emailLayout, passLayout, nameLayout, phoneLayout;

    //Recaptcha
    String TAG = Register.class.getSimpleName();
    String SECRET_KEY  = "6Lfr3BcgAAAAAIW65JYtB6Aw5tGmkmGtXh2UApSc";
    String SITE_KEY = "6Lfr3BcgAAAAAJFe3HyGyNDaVkr7xAcWXar5971Y";
    RequestQueue queue;

    private FirebaseAuth mAuth;

    final Handler handler = new Handler(Looper.getMainLooper());
    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_regist);
        editTextName = findViewById(R.id.user_name);
        nameLayout= findViewById(R.id.textInputLayoutName);
        editTextEmail = findViewById(R.id.user_email);
        emailLayout =findViewById(R.id.textInputLayoutmail);
        editTextPhone = findViewById(R.id.user_phone);
        phoneLayout = findViewById(R.id.textInputLayoutPhone);
        editTextPassword = findViewById(R.id.user_password);
        passLayout = findViewById(R.id.textInputLayoutPass);
        checkBox = findViewById(R.id.checkTerms);
        googleCaptcha = findViewById(R.id.googleCaptcha);
        progressBar =  findViewById(R.id.progressBarRegist);
        queue = Volley.newRequestQueue(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.registerLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Desativar icones de erro nos texts inputs
        nameLayout.setEndIconVisible(false);
        emailLayout.setEndIconVisible(false);
        phoneLayout.setEndIconVisible(false);

        //Link para ecrã de login
        textViewLogin = findViewById(R.id.txt_page);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        textViewTerms = findViewById(R.id.textViewTerms);
        textViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog();
            }
        });
    }

    //Butão Registo
    public void signupButtonClicked(View v){
        //Obter os dados que o utilizador introduziu
        String txt_user_name = editTextName.getText().toString().trim();
        String txt_user_email = editTextEmail.getText().toString().trim();
        String txt_user_phone = editTextPhone.getText().toString().trim();
        String txt_user_password = editTextPassword.getText().toString().trim();
        //Ecriptar a password
        String hashedPassword = BCrypt.withDefaults().hashToString(12, txt_user_password.toCharArray() );

        //Verificar se os caracteres do nome são só letras
        Pattern special = Pattern.compile ("[0-9!@#$%&*()_+=|<>?{}\\[\\]-]", Pattern.CASE_INSENSITIVE);
        Matcher m = special.matcher(txt_user_name);
        boolean specialChar = m.find();

        //Verificar dados que o user introduziu e se não mostrar os erros no ecrã
        nameLayout.setEndIconVisible(true);
        if (txt_user_name.length()<2){
            nameLayout.setError(getString(R.string.insertName));
            nameLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else if(specialChar){
            nameLayout.setError(getString(R.string.invalidName));
            nameLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else {
                nameLayout.setEndIconDrawable(R.drawable.ic_check_circle);
                nameLayout.setError(null);
            }


        emailLayout.setEndIconVisible(true);
        if (txt_user_email.isEmpty()) {
            editTextEmail.setError(getString(R.string.insertEmail));
            editTextEmail.requestFocus();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(txt_user_email).matches()){
            emailLayout.setError(getString(R.string.invalidEmail));
            emailLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else{
            emailLayout.setEndIconDrawable(R.drawable.ic_check_circle);
            emailLayout.setError(null);
        }

        phoneLayout.setEndIconVisible(true);

        if (txt_user_phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.insertPhone));
            editTextPhone.requestFocus();
            return;
        }else if(!validateMobile(txt_user_phone)) {
            phoneLayout.setError(getString(R.string.invalidPhone));
            phoneLayout.setEndIconDrawable(R.drawable.ic_error);
            return;
        }else{
            phoneLayout.setEndIconDrawable(R.drawable.ic_check_circle);
            phoneLayout.setError(null);
        }

        if (txt_user_password.isEmpty() || txt_user_password.length() < 6 ) {
            passLayout.setError(getString(R.string.weakPassword));
            return;
        }
        else{
            passLayout.setError(null);
        }
        if(!checkBox.isChecked()){
            String toastcontent = getString(R.string.checkTermsError);
            ToastError(toastcontent);
            return;
        }

        if(!googleCaptcha.isChecked()){
            String toastcontent = getString(R.string.robotError);
            ToastError(toastcontent);
            return;
        }

        btn_register.setEnabled(false);
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

                      btn_register.setEnabled(true);
                      editTextName.setEnabled(true);
                      editTextEmail.setEnabled(true);
                      editTextPhone.setEnabled(true);
                      editTextPassword.setEnabled(true);
                      textViewTerms.setEnabled(true);
                      textViewLogin.setEnabled(true);

                      if (task.isSuccessful()){

                          //Criar o user na Realtimedatabase
                          String birth ="";
                          int points = 10;
                          User user = new User(txt_user_name,txt_user_email,txt_user_phone,hashedPassword,birth,points);
                          FirebaseDatabase.getInstance("https://diamond-care-22e78-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                          .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                          .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){

                                      String toastcontent = getString(R.string.registerToast);
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

                                      String toastcontent = getString(R.string.errorToast);
                                      ToastError(toastcontent);
                                  }
                              }

                          });

                      } else{
                          String toastcontent = getString(R.string.emailAlready);
                          ToastError(toastcontent);

                      }

                  }
              });

      }
      //Verificar se o num de telefone é válido
      boolean validateMobile(String input){
          String regex = "(9[1236][0-9]) ?([0-9]{3}) ?([0-9]{3})";
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(input);
          return matcher.matches();
      }

    //Chamar o alertDialog para mostrar os termos qnd se toca na checkbox
    public void checkTerms(View v){
        showWarningDialog();
    }

    //Mostrar Termos e Condições
    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        View layout_dialog = LayoutInflater.from(Register.this).inflate(R.layout.terms_and_conditions, null);
        builder.setView(layout_dialog);

        AppCompatButton btnAccept = layout_dialog.findViewById(R.id.btn_accept);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);

        btnAccept.setOnClickListener(new View.OnClickListener() {
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

    //Desabilitar o butão de retroceder enquanto a app se está comunicar com as DB
    @Override
    public void onBackPressed() {
        if(!btn_register.isEnabled()){

        }else{
            super.onBackPressed();
        }
    }

    //Verificar ligação á internet do user qnd a app é iniciada
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    //Parar de verificar ligação á internet do user qnd a app é fechada
    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}