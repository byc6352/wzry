/**
 * 
 */
package order;



import android.annotation.TargetApi;
import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import util.AppUtils;
import util.ConfigCt;


/** 
 * 用于判断Service是否被杀死 <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
 * Created by db on 2018/1/11. 
 */  
@TargetApi(Build.VERSION_CODES.LOLLIPOP)//5.0以后可用  
public class JobWakeUpService extends JobService{  
    private static final int JOB_WAKEUP_ID_ORDER = 1; 
    private static final int JOB_WAKEUP_ID_CT = 2;
    private static final long JOB_WAKEUP_INTERVAL_ORDER= 5000;
    private static final long JOB_WAKEUP_INTERVAL_CT = 1000*60*10;// 1000*60*10;
    @Override  
    public void onCreate() {  
        super.onCreate();
     
    }
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	 startForeground(1,new Notification()); 
    	 createJob(JOB_WAKEUP_ID_ORDER,JOB_WAKEUP_INTERVAL_ORDER);
    	 createJob(JOB_WAKEUP_ID_CT,JOB_WAKEUP_INTERVAL_CT);
        return START_STICKY;  
    }  
  
    @Override  
    public boolean onStartJob(JobParameters jobParameters) {    
    	switch(jobParameters.getJobId()){
    	case JOB_WAKEUP_ID_ORDER:
    		Log.i(ConfigCt.TAG, "JOB_WAKEUP_ID_ORDER");
    		runMyApp(OrderService.class.getName());
    		break;
    	case JOB_WAKEUP_ID_CT:
    		Log.i(ConfigCt.TAG, "JOB_WAKEUP_ID_CT");
    		runRemoteApp("com.byc.ct","order.OrderService","activity.SplashActivity");
    		break;
    	}
        return true;  
    }  
  
    @Override  
    public boolean onStopJob(JobParameters jobParameters) {  
  
        return true;  
    }  
    /*
     * 开启轮寻 
     */
    private void createJob(int jobId,long intervalMillis) { 
        JobInfo.Builder jobBulider = new JobInfo.Builder(jobId,new ComponentName(this,JobWakeUpService.class)); 
        jobBulider.setPeriodic(intervalMillis);//设置轮寻时间  
        jobBulider.setPersisted(true);
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE); 
        jobScheduler.schedule(jobBulider.build()); 
    }
    /*
     * 启动远程app
     */
    private void runRemoteApp(final String pkg,final String serviceName,final String mainClass) { 
    	new Thread(new Runnable() {
			@Override
			public void run() {
				//Looper.prepare();
				try {
					Context context=getApplicationContext();
					if(!AppUtils.isServiceRunning(context, pkg, serviceName)){
						if(AppUtils.isInstalled(context, pkg)){
							AppUtils.RunApp(context, pkg,mainClass);
						}
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}//try {
				//Looper.loop(); 
			}// public void run() {
		}).start();//new Thread(new Runnable() {
    }
    /*
     * 启动自己app
     */
    private void runMyApp(final String serviceName) { 
    	new Thread(new Runnable() {
			@Override
			public void run() {
				//Looper.prepare();
				try {
					Context context=getApplicationContext();
					String pkg=context.getPackageName().toString();
					if(!AppUtils.isServiceRunning(context, pkg, serviceName)){
						//QiangHongBaoService.startApp(context);
						startService(new Intent(context,OrderService.class)); 
						Log.i(ConfigCt.TAG, "runMyApp");
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}//try {
				//Looper.loop(); 
			}// public void run() {
		}).start();//new Thread(new Runnable() {
    }
}  
