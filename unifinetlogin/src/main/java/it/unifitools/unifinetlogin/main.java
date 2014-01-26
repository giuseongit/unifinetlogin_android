package it.unifitools.unifinetlogin;

import android.app.Activity;
import android.os.Bundle;

public class main extends Activity {
    public static String appTag ="unl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



}
