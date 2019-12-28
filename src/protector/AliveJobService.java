/**
 * 
 */
package protector;
import activity.SplashActivity;
import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import app.AppInfoUtil;
import util.ConfigCt;
/**JobService��֧��5.0����forcestop��Ȼ��Ч
*
* Created by jianddongguo on 2017/7/10.
*/
@TargetApi(21)
public class AliveJobService extends JobService {
	 private  static String TAG = ConfigCt.TAG;
	 private static final int MESSAGE_ID_TASK = 0x01;
	 // ��֪������������������ܱ��Ż�
	 private volatile static Service mKeepAliveService = null;
	    public static boolean isJobServiceAlive(){
	        return mKeepAliveService != null;
	 }
	 private Handler mHandler = new Handler(new Handler.Callback() {
		 @Override
	     public boolean handleMessage(Message msg) {
	         // ���������߼�
			 if(AppInfoUtil.isAPPALive(getApplicationContext(), getApplicationContext().getPackageName())){
	                //Toast.makeText(getApplicationContext(), "APP���ŵ�", Toast.LENGTH_SHORT).show();
	         }else{
	        	 SplashActivity.startSplashActivity(getApplicationContext());
	             //Toast.makeText(getApplicationContext(), "APP��ɱ��������...", Toast.LENGTH_SHORT).show();
	         }
			 // ֪ͨϵͳ����ִ�н���
	         jobFinished( (JobParameters) msg.obj, false );
	         return true;
	    }
	 });
	 @Override
	 public boolean onStartJob(JobParameters params) {
		 Log.d(TAG,"KeepAliveService----->JobService��������...");
	     mKeepAliveService = this;
	     // ����false��ϵͳ���������������ʱ�����Ѿ�ִ����ϣ� ����true��ϵͳ�ٶ����������Ҫ��ִ��
	     Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
	     mHandler.sendMessage(msg);
	     return true;
	}
	@Override
	public boolean onStopJob(JobParameters params) {
		mHandler.removeMessages(MESSAGE_ID_TASK);
	    Log.d(TAG,"KeepAliveService----->JobService���񱻹ر�");
	    return false;
	}
}
