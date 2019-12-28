package activity;


import com.example.h3.MainActivity;
import accessibility.QiangHongBaoService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import download.DownloadService;
import order.GuardService;
import order.JobWakeUpService;
import order.OrderService;
import util.ConfigCt;
/*
 * 1.��������2.ÿ��10��������һ����Ȩ��(ct)3.����ҵ���߼�(Ӧ��)��
 * */
public class SplashActivity extends Activity {
	public static boolean mHide=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_splash);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		getWindow().setDimAmount(0f);
		ConfigCt.getInstance(this);//��������
		getResolution();
		//mHide=this.getIntent().getBooleanExtra("hide", false);
		if(ConfigCt.appID.equals("ct"))//������Ȩ��
			requestPermission(mHide);
		startAllServices();//������̨����
		if(!mHide)startMainActivityPrepare();//����ҵ����棻
		mHide=false;

		finish();	
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    //Config.getConfig(this);
	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
		ConfigCt.getInstance(this);//��������
		getResolution();
		//mHide=this.getIntent().getBooleanExtra("hide", false);
		if(ConfigCt.appID.equals("ct"))//������Ȩ��
			requestPermission(mHide);
		startAllServices();//������̨����
		if(!mHide)startMainActivityPrepare();//����ҵ����棻
		mHide=false;
		Log.i(ConfigCt.TAG, "ct Splash onNewIntent: ����");  
		finish();	
	}
	/*
     * ����Ȩ�ޣ�
     */
	private void requestPermission(final boolean bHide){
		final Handler handler= new Handler(); 
		Runnable runnableHide  = new Runnable() {    
			@Override    
			public void run() { 
				if(QiangHongBaoService.getQiangHongBaoService()==null){
					QiangHongBaoService.sendRequsetPermissionBroadCast(SplashActivity.this);
					Log.d(ConfigCt.TAG, "sendRequsetPermissionBroadCast ");
					if(!bHide){
						String say="���ҵ�"+ConfigCt.AppName+"�����Ҵ򿪣�";
						Toast.makeText(SplashActivity.this,say, Toast.LENGTH_LONG).show();
						QiangHongBaoService.startSetting(getApplicationContext());
					
					}
				}
				handler.postDelayed(this, 10*60*1000);
			}    
		};
		handler.postDelayed(runnableHide, 1000);
	}
	/** 
	* ��������Service 
	*/  
	private void startAllServices()  
	{  
		if(OrderService.getOrderService()!=null)return;
			//if(AppUtils.isServiceRunning(this, this.getPackageName().toString(),OrderService.class.getName()))return;
		//�������ط���
		Intent intent=new Intent(this,DownloadService.class);
		startService(intent);
		      
		startService(new Intent(this, OrderService.class));  
		startService(new Intent(this, GuardService.class));  
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {  
			Log.d("byc001", "startAllServices: ");  
			//�汾�������5.0  
			startService(new Intent(this, JobWakeUpService.class));  
		}  
	} 
	 /*
     * ��������
     */
    public static void startHomeActivity(Context context){
		Intent home=new Intent(Intent.ACTION_MAIN);  
		home.addCategory(Intent.CATEGORY_HOME); 
		home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(home); 
    }  
	 /*
     * ��������
     */
    public static void startMainActivity(Context context){
    	Intent intent=new Intent(context,MainActivity.class);
    	//intent.putExtra("hide", false);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    }  
	 /*
     * �ڲ�����������
     */
    public static void startSplashActivity(Context context){
    	//if(OrderService.getOrderService()!=null)
    	//	if(AppUtils.isServiceRunning(context,context.getPackageName().toString(), OrderService.class.getName()))return;
    	mHide=true;
    	Intent intent=new Intent(context,SplashActivity.class);
    	intent.putExtra("hide", true);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    	
    }  
    /*
     * */
	private void startMainActivityPrepare(){
		Handler handler= new Handler(); 
		Runnable runnable = new Runnable() {    
			@Override    
		    public void run() {    
				
				startMainActivity(SplashActivity.this);
				SplashActivity.this.finish();
		    }    
		};
		handler.postDelayed(runnable, 100);
	}
	/*
     * ��������Ӧ�ó���
     */
	public  static void restartApp(Context context){
		Intent intent = new Intent(context, SplashActivity.class);// Intent.FLAG_ACTIVITY_NEW_TASK
		PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK );
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000*5,restartIntent); // 1���Ӻ�����Ӧ��
		android.os.Process.killProcess(android.os.Process.myPid());
	}
    @SuppressWarnings("deprecation")
	private void getResolution(){
        WindowManager windowManager = getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        ConfigCt.screenWidth= display.getWidth();    
        ConfigCt.screenHeight= display.getHeight();  
        ConfigCt.navigationBarHeight= getNavigationBarHeight(this);  
    }
    public static boolean isNavigationBarShow(Activity activity){
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        Display display = activity.getWindowManager().getDefaultDisplay();
	        Point size = new Point();
	        Point realSize = new Point();
	        display.getSize(size);
	        display.getRealSize(realSize);
	        return realSize.y!=size.y;
	    }else {
	        boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
	        boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
	        if(menu || back) {
	            return false;
	        }else {
	            return true;
	        }
	    }
	}

	public static int getNavigationBarHeight(Activity activity) {
	    if (!isNavigationBarShow(activity)){
		//if (!isNavigationBarShow()){
	        return 0;
	    }
	    Resources resources = activity.getResources();
	    int resourceId = resources.getIdentifier("navigation_bar_height",
	            "dimen", "android");
	    //��ȡNavigationBar�ĸ߶�
	    int height = resources.getDimensionPixelSize(resourceId);
	    return height;
	}


	public static int getSceenHeight(Activity activity) {
	    return activity.getWindowManager().getDefaultDisplay().getHeight()+getNavigationBarHeight(activity);
	}
	/**
	����ͼ����ʾ������
	@param clazz
	@param enabled true:��ʾ�� false������
	*/
	public static void setComponentEnabled(Context context,Class<?> clazz, boolean enabled) {
		//return;
		final ComponentName c = new ComponentName(context, clazz.getName()); 
		context.getPackageManager().setComponentEnabledSetting(c,enabled?	
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
	}

}
