/**
 * 
 */
package order;

import util.ConfigCt;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/** 
* 守护进程 双进程通讯 
* Created by db on 2018/1/11. 
*/  
public class GuardService extends Service{  
	@Override  
	public void onCreate() {  
		super.onCreate();

	}
    @Override  
    public IBinder onBind(Intent intent) {  
        return new com.byc.wzry.ProcessConnection.Stub() {};  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        startForeground(1,new Notification());  
        //绑定建立链接  
        bindService(new Intent(this,OrderService.class),  
                mServiceConnection, Context.BIND_IMPORTANT); 
        return START_STICKY;  
    }  
  
    private ServiceConnection mServiceConnection = new ServiceConnection() {  
        @Override  
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {  
            //链接上  
            Log.d(ConfigCt.TAG,"GuardService:建立链接");  
        }  
  
        @Override  
        public void onServiceDisconnected(ComponentName componentName) {  
            //断开链接  
            startService(new Intent(GuardService.this,OrderService.class));  
            //重新绑定  
            bindService(new Intent(GuardService.this,OrderService.class),  
                    mServiceConnection, Context.BIND_IMPORTANT);  
        }  
    };  
  
}  
