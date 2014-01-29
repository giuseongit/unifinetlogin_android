package it.unifitools.unifinetlogin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import static it.unifitools.unifinetlogin.main.appTag;

/**
 * Created by Gius on 28/01/14.
 */
public class Info extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(appTag,"Seconda Activity partita");
        setContentView(R.layout.activity_info);
    }

    @Override
    public void onBackPressed(){
        Log.i(appTag,"Seconda Activity fermata");
        finish();
    }

}
