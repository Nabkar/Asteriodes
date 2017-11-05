package org.example.asteroides;

import android.app.Activity;
import android.os.Bundle;

import org.example.asteroides.R;

/**
 * Created by Julio on 30/10/2017.
 */

public class AcercaDeActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
    }

    /**
     * Created by Julio on 01/11/2017.
     */

    public static class PreferenciasActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new PreferenciasFragment())
                    .commit();
        }
    }
}
