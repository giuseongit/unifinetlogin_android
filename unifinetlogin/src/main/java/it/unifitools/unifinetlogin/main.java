package it.unifitools.unifinetlogin;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.util.Log;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.Notification;
import android.graphics.BitmapFactory;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

public class main extends Activity {
    public static String appTag = "unl";
    public static String prefs = "unlPrefs";
    public static String pref_m = "matricola";
    public static String pref_p = "password";
    private String matr,pwd;
    private SharedPreferences settings;
    private Worker work;
    private Button btn;
    private EditText m,p;
    private String sta, sto;
    private NotificationManager nman;
    private Notification note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(appTag,"Applicazione partita");
        settings = this.getSharedPreferences(prefs,MODE_PRIVATE);
        btn = (Button) findViewById(R.id.btn);
        sta = getResources().getString(R.string.start);
        sto = getResources().getString(R.string.stop);

        setContentView(R.layout.activity_main);

        nman = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
            .setContentTitle("unifinetlogin")
            .setContentText("servizio avviato")
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        Intent tIntent = new Intent(this, main.class);
        tIntent.setAction(Intent.ACTION_MAIN);
        tIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,tIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);

        note = nBuilder.build();
        note.flags = Notification.FLAG_ONGOING_EVENT;

        m = (EditText) findViewById(R.id.matri);
        p = (EditText) findViewById(R.id.passi);

        matr = settings.getString(pref_m, "");
        pwd = settings.getString(pref_p,"");

        m.setText(matr);
        p.setText(pwd);
    }

    public void addNote(){
        nman.notify(0,note);
    }

    public void removeNote(){
        nman.cancel(0);
    }

    public boolean checkConn(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        try{
            String ssid = info.getSSID();
            String temp = ssid.substring(1,6);
            if(temp.equalsIgnoreCase("unifi")){
                return true;
            }
        }catch(Exception e){
            Log.i(appTag, "ERRORE: "+e);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void help(MenuItem m){
        Intent intent = new Intent(this, Info.class);
        startActivity(intent);

    }

    public void toggle(View v){
        Log.i(appTag,"Bottone premuto");
        btn = (Button) findViewById(R.id.btn);
        if(btn.getText().equals(sta)){
            if(!check()){
                return;
            }
            save();
            if(!checkConn()){
                return;
            }

            work = new Worker(matr, pwd);
            work.start();
            addNote();
            btn.setText(sto);
        }else{
            work.stopThread();
            work = null;
            removeNote();
            btn.setText(sta);
        }
    }

    public void toast(CharSequence toShow){
        Toast toast = Toast.makeText(this, toShow, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void save(){
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString(pref_m, matr);
        editor.putString(pref_p,pwd);
        editor.commit();
    }

    @Override
    public void onBackPressed(){
        try{
            if(work.isAlive()){
                work.stopThread();
                removeNote();
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
