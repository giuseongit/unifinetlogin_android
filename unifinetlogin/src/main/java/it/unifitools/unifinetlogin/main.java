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


public class main extends Activity {
    public static String appTag = "unl";
    public static String prefs = "unlPrefs";
    private String matr,pwd;
    private static SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(appTag,"Applicazione partita");

        setContentView(R.layout.activity_main);

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
        EditText m = (EditText) findViewById(R.id.matri);
        EditText p = (EditText) findViewById(R.id.passi);
        if(!m.getText().equals("") && !p.getText().equals("")){
            return true;
        }

        toast("inserire matricola e password...");
        return false;
    }

    public void toggle(View v){
        Log.i(appTag,"Bottone premuto");
        Button btn = (Button) findViewById(R.id.btn);
        String sta = getResources().getString(R.string.start);
        String sto = getResources().getString(R.string.stop);
        if(btn.getText().equals(sta)){
            if(!check()){
                return;
            }
            save();
            if(!checkConn()){
                return;
            }
            btn.setText(sto);
            save();
        }else{
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
    }

    @Override
    protected void onStop(){
        super.onStop();

        Log.i(appTag,"Applicazione terminata.");


        finish();
    }

}
