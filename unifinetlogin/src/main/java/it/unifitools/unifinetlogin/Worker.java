package it.unifitools.unifinetlogin;

import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import android.util.Log;

import static it.unifitools.unifinetlogin.main.*;

/**
 * @author giuse
 *
 * La classe Worker estende la classe Thread e costituisce il processo che
 * fisicamente manda la richiesta di autenticazione al gateway (1.1.1.1)
 */
public class Worker extends Thread{
    private String matr, pass;
    private volatile boolean active;


    public Worker(String matr, String pass){
        Log.i(appTag,"I parametri passati al thread sono "+matr+" e "+pass);
        this.matr = matr;
        this.pass = pass;
        active = true;
    }

    public void stopThread(){
        active = false;
    }

    public void run(){
        Log.i(appTag,"Thread avviato.");
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {Log.i(appTag,"Errore durante l'installazione dei certificati SSL.");}

        while(active){
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

               /* URL host = new URL("https://1.1.1.1/login.html");
                URLConnection conn = host.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(data);
                out.flush();*/
                Log.i(appTag,"HTTP Req sent");

               /* BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                @SuppressWarnings("unused")
                String _page = "";
                String line;
                while ((line = in.readLine()) != null) {
                    _page += line;
                }
                out.close();
                in.close();*/
            }catch(Exception e){
                Log.i(appTag,"Errore di connessione:\n"+e);
            }

            try{
                sleep(5000);
            }catch(InterruptedException e){
                Log.i(appTag,"Errore nello sleep. Applicazione probabilmente interrotta. Info:\n"+e);
            }
        }
        Log.i(appTag,"Thread terminato");
    }
}