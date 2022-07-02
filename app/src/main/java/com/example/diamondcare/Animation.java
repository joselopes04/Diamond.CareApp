package com.example.diamondcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

public class Animation extends AppCompatActivity {

    public static final String SAVEDLANGUAGE = "Language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        //Fundo animado
        ConstraintLayout constraintLayout = findViewById(R.id.animationLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Verificar o idioma que o user têm selecionado
        String savedLanguage;
        SharedPreferences sharedPreferences = getSharedPreferences(SAVEDLANGUAGE, MODE_PRIVATE);
        savedLanguage = sharedPreferences.getString(SAVEDLANGUAGE,"");
        if(savedLanguage.equals("en")){
            setLocal(Animation.this, "en");
        }else{
            setLocal(Animation.this,"pt");
        }

        //Delay para mostrar a animação
        Thread thread = new Thread() {

            public void run(){
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent( Animation.this, Home.class);
                    startActivity(intent);
                }
            }
        };
        thread.start();
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

    //Desativar o butão back
    @Override
    public void onBackPressed() {}

}