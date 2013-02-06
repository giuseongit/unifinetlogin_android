package it.unifiTools.unifiNetLogin;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class ServizioUni extends Service{

	BufferedReader in;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	private Timer timer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		TimerTask task = new TimerTask() {
			final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		                return true;
		        }
			};
			
			private void trustAllHosts() {
		        // Create a trust manager that does not validate certificate chains
		        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                        return new java.security.cert.X509Certificate[] {};
		                }

		                public void checkClientTrusted(X509Certificate[] chain,
		                                String authType) throws CertificateException {
		                }

		                public void checkServerTrusted(X509Certificate[] chain,
		                                String authType) throws CertificateException {
		                }
		        } };

		        // Install the all-trusting trust manager
		        try {
		                SSLContext sc = SSLContext.getInstance("TLS");
		                sc.init(null, trustAllCerts, new java.security.SecureRandom());
		                HttpsURLConnection
		                                .setDefaultSSLSocketFactory(sc.getSocketFactory());
		        } catch (Exception e){}
			}
			
			@Override
			public void run() {
				String matr, pass;
								
				SharedPreferences settings = getSharedPreferences("settings", 0);
				matr = settings.getString("matricola", "");
				pass = settings.getString("password", "");
				
				String[] keys = { "buttonClicked", "redirect_url", "err_flag","info_flag","info_msg","username","password" };
			    String[] vals = { "4", "null", "0","0","0", matr, pass };
			    String data = "";
			      
			    try{
			    	for(int i = 0; i<keys.length; i++){
			    		data += URLEncoder.encode(keys[i], "UTF-8")+"="+URLEncoder.encode(vals[i], "UTF-8");
			            if(i != keys.length -1){
			            	data += "&";
			            }
			        }
			        URL url = new URL("https://1.1.1.1/login.html");
			        HttpURLConnection conn = null;

			        if (url.getProtocol().toLowerCase().equals("https")) {
			            trustAllHosts();
			            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			            https.setHostnameVerifier(DO_NOT_VERIFY);
			            conn = https;
			        } else {
			            conn = (HttpURLConnection) url.openConnection();
			        }
			              
			        conn.setDoOutput(true);
			        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			        out.write(data);
			        out.flush();
			        BufferedReader inc = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        String dat = "";
			        String lol = inc.readLine();
			        while(lol != null){
			        	dat += lol;
			        	lol = inc.readLine();
			        }
			        inc = null;
			        dat = null;
			        lol = null;
			        conn = null;
			        out = null;
			    }catch(Exception e){}
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, 5000);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		timer = null;
	}
}
