package com.example.diamondcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diamondcare.Utility.NetworkChangeListener;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String SAVEDLANGUAGE = "Language";
    private long backPressedTime;
    public Button button;
    TextView textView;
    String savedLanguage;

    //Objeto que verifica a ligação á internet
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verificar se a app já verificou o idioma
        // a app quando faz o primeiro if guarda um valor em como já verificou o idioma
        //Sem este if a app ia estar num loop infinito de verificação do idioma
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            //Verificar o idioma que o user têm selecionado
            SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
            savedLanguage = sharedPreferences.getString(SAVEDLANGUAGE,"");
            if(savedLanguage.equals("en")){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("refreshed","yes");
                setLocal(MainActivity.this, "en");
                finish();
                startActivity(intent);

            }else{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("refreshed","yes");
                setLocal(MainActivity.this,"pt");
                finish();
                startActivity(intent);
            }
        }

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Logotipo
        ImageView imageView = (ImageView) findViewById(R.id.img_logo);
        imageView.setImageResource(R.drawable.logo_sem_fundo);

        //Se a opção lembrar me foi assinalada abrir a app pela activity animation
        SharedPreferences preferences = getSharedPreferences("Checkbox", MODE_PRIVATE);
        String keep = preferences.getString("remember", "");
        if (keep.equals("ON")){
            Intent intent = new Intent(MainActivity.this, Animation.class);
            startActivity(intent);
        }

        //Butão Login
        button = (Button) findViewById(R.id.btn_main);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);

            }
        });

        //Texto Criar Conta
        textView = (TextView)findViewById(R.id.txt_regist);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });

    }

    //Mostrar o dialog para o user selecionar o idioma
    public void changeLanguageDialog(View v){
        String savedLanguage;
        int selectLang;
        SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString(SAVEDLANGUAGE,"");

        //Selecionar a opção que o user tinha escolhida anteriormente
        if(savedLanguage.equals("en")){
            selectLang = 1;
        }else{
            selectLang = 0;
        }

        String[] languages = {"Portugês", "English"};
        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.languageDialog))
                .setSingleChoiceItems(languages, selectLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = languages[i];
                        if(selected.equals("Portugês")){
                            setLocal(MainActivity.this,"pt");
                            finish();
                            startActivity(getIntent());

                        }else if (selected.equals("English")){
                            setLocal(MainActivity.this, "en");
                            finish();
                            startActivity(getIntent());
                        }
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    //Selecionar o idioma
    public void setLocal(Activity activity, String lanCode){
        Locale locale = new Locale(lanCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String language = Locale.getDefault().getLanguage();
        editor.putString(SAVEDLANGUAGE, language);
        editor.commit();
    }


    //Confirmar se o user deseja mesmo sair da app
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
            backToast.cancel();
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