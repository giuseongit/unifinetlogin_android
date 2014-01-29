package it.unifiTools.unifiNetLogIn;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Gius on 28/01/14.
 */
public class Info extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(main.appTag,"Seconda Activity partita");
        setContentView(R.layout.activity_info);
    }

    @Override
    public void onBackPressed(){
        Log.i(main.appTag,"Seconda Activity fermata");
        finish();
    }

}
