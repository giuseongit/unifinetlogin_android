package it.unifiTools.unifiNetLogin;

import java.io.BufferedReader;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class UnifiNetLogInActivity extends Activity {
    /** Called when the activity is first created. */
	Button b;
	Button c;
	PrintWriter out;
	BufferedReader in;
	ImageView g;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
        this.setRequestedOrientation(
        		ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        b=(Button) findViewById (R.id.button1);
    	final TextView matr =(TextView) findViewById (R.id.editText1);
    	final TextView pass = (TextView) findViewById (R.id.editText2);
    	
    	
    	SharedPreferences settings = getSharedPreferences("settings", 0);
		matr.setText(settings.getString("matricola", ""));
		pass.setText(settings.getString("password", ""));
    	
        b.setOnClickListener(new View.OnClickListener() {
        	
        	public boolean chk(String s){
        		if(s == null){
        			return false;
        		}else if(s.length() > 6 && s.substring(0, 5).equals("Unifi")){
        			return true;
        		}else{
        			return false;
        		}
        	}
        	
        	public boolean signed(){
        		String data1 = matr.getText().toString();
                String data2 = pass.getText().toString();
                if(data1.equals("") || data2.equals("")){
                	return false;
                }else{
                	return true;
                }
                	
        	}
        	
			public void onClick(View v) {
				if(b.getText().equals("avvia")){
					ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo net = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
					WifiInfo info = manager.getConnectionInfo();
					if(net.isAvailable() && chk(info.getSSID()) && signed()){
						b.setText("arresta");
						
						
						/*try{
			                out = new PrintWriter(new FileWriter("/sdcard/unificfg.imp"));
			                String data1 = matr.getText().toString();
			                String data2 = pass.getText().toString();
			                String data = data1+"#"+data2;
			                out.write(data);
			                out.flush();
			                out.close();
			            }catch(Exception e){mostraToast("ERRORE SCRITTURA");}*/
						
						SharedPreferences settings = getSharedPreferences("settings", 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString("matricola", matr.getText().toString());
					    editor.putString("password", pass.getText().toString());
					    editor.commit();
						
						
						avviaServizio();
					}else{
						if(matr.getText().toString().equals("") || pass.getText().toString().equals("")){
							mostraToast("Inserisci tutti i dati!");
						}else if(!net.isAvailable()){
							mostraToast("Connessione wi-fi non trovata!");
						}else if(!chk(info.getSSID())){
							mostraToast("Bisogna essere connessi ad una rete dell'ateneo fiorentino!");
						}
					}
				}else{
					b.setText("avvia");
					arrestaServizio();
				}
				
			}
		});
    }
    
    private void mostraToast(String s){
    	
    	Toast toast=Toast.makeText(this, s, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 500);
    	toast.show();
    }
   
    @Override
    public void onBackPressed() {
    	if(b.getText().equals("arresta")){
	       Log.d("CDA", "onBackPressed Called");
	       Intent setIntent = new Intent(Intent.ACTION_MAIN);
	       setIntent.addCategory(Intent.CATEGORY_HOME);
	       setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	       startActivity(setIntent);
    	}else{
    		finish();
    		super.onBackPressed();
    	}
	}
    @Override
	protected void onDestroy(){
    	super.onDestroy();
    	Log.i("attivit�", "distrutta");
    }
    private void avviaServizio() {
		startService(new Intent(this, ServizioUni.class));
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.icon_48x48;        // icon from resources
        CharSequence tickerText = "Avvio";              // ticker-text
        long when = System.currentTimeMillis();         // notification time
        Context context = getApplicationContext();// application Context
        CharSequence contentTitle = "UnifiNetLogIn - Running";  // message title
        CharSequence contentText = "Wi-Fi Unlocked";      // message text
        Intent notificationIntent = new Intent(this, UnifiNetLogInActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags= Notification.FLAG_ONGOING_EVENT;
        notification.defaults= Notification.DEFAULT_VIBRATE;
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(1, notification);
        
	}


	private void arrestaServizio() {
		stopService(new Intent(this, ServizioUni.class));
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.icon_48x48;        // icon from resources
        CharSequence tickerText = "Avvio";              // ticker-text
        long when = System.currentTimeMillis();         // notification time
        Context context = getApplicationContext();// application Context
        CharSequence contentTitle = "UnifiNetLogIn - Running";  // message title
        CharSequence contentText = "Wi-Fi Unlocked";      // message text
        Intent notificationIntent = new Intent(this, UnifiNetLogInActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // the next two lines initialize the Notification, using the configurations above
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(1, notification);
        mNotificationManager.cancel(1);
	}
}