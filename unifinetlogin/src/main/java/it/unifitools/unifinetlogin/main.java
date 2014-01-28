package it.unifitools.unifinetlogin;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.util.Log;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.content.Intent;


public class main extends Activity {
    public static String appTag = "unl";
    public static String prefs = "unlPrefs";
    private String matr,pwd;
    private SharedPreferences settings;
    private Worker work;
    private Button btn;
    private EditText m,p;
    private String sta, sto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(appTag,"Applicazione partita");
        btn = (Button) findViewById(R.id.btn);
        sta = getResources().getString(R.string.start);
        sto = getResources().getString(R.string.stop);
        try{
            if(work.isAlive()){
                btn.setText(sta);
            }
            Log.i(appTag,"secondo Avvio");
        }catch(NullPointerException e){
            Log.i(appTag,"Primo Avvio");
        }


        setContentView(R.layout.activity_main);

        m = (EditText) findViewById(R.id.matri);
        p = (EditText) findViewById(R.id.passi);

        settings = getSharedPreferences(prefs, 0);
        matr = settings.getString("matricola","");
        pwd = settings.getString("password","");

    }

    public boolean checkConn(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        try{
            String ssid = info.getSSID();
            if(ssid.substring(0,6).equals("unifi")){
                return true;
            }
        }catch(Exception e){}

        toast("Bisogna essere connessi ad una rete dell'ateneo fiorentino!");
        return false;
    }

    public boolean check(){

        if(!m.getText().equals("") && !p.getText().equals("")){
            matr = m.getText().toString();
            pwd = p.getText().toString();
            return true;
        }

        toast("inserire matricola e password...");
        return false;
    }

    public void toggle(View v){
        Log.i(appTag,"Bottone premuto");
        btn = (Button) findViewById(R.id.btn);
        if(btn.getText().equals(sta)){
            if(!check()){
                return;
            }
            save();
           /* if(!checkConn()){
                return;
            }*/

            work = new Worker(matr, pwd);
            work.start();
            btn.setText(sto);
        }else{
            work.stopThread();
            work = null;
            btn.setText(sta);
        }
    }

    public void toast(CharSequence toShow){
        Toast toast = Toast.makeText(this, toShow, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void save(){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("matricola",matr);
        editor.putString("password",pwd);
        editor.commit();
        editor.apply();
    }

    @Override
    public void onBackPressed(){
        try{
            if(work.isAlive()){
                work.stopThread();
            }
        }catch(NullPointerException e){}
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(appTag,"Applicazione terminata.");
    }

}
