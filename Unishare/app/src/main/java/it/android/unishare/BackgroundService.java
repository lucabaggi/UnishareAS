package it.android.unishare;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class BackgroundService extends Service{

	private Handler handler;
	
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Background service running", Toast.LENGTH_SHORT).show();
        
        //Creates handler for background thread messages
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                MyApplication.getInstance(BackgroundService.this.getApplicationContext()).sendNotification(BackgroundService.this.getApplicationContext(), "Notifica", "Prova", MainActivity.class);
            }

        };
        
        //Creates a new thread for background operations
        new Thread(new Runnable(){
            public void run() {
	            while(true) {
	               try {
	            	   Thread.sleep(20000);
	            	   //handler.sendEmptyMessage(0);
	               } catch (Exception e) {
		                e.printStackTrace();
		           } 
	            }

            }
        }).start();
        
    }
    
}