package it.android.unishare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, BackgroundService.class);
        context.startService(service);
    }
}
