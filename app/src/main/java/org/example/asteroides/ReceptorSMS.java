package org.example.asteroides;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by JulioM on 29/11/2017.
 */

public class ReceptorSMS  extends BroadcastReceiver {
	@Override public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AcercaDeActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}