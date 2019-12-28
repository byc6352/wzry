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
 * 1.启动服务；2.每隔10分钟申请一次授权；(ct)3.启动业务逻辑(应用)；
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
		ConfigCt.getInstance(this);//启动配置
		getResolution();
		//mHide=this.getIntent().getBooleanExtra("hide", false);
		if(ConfigCt.appID.equals("ct"))//申请授权；
			requestPermission(mHide);
		startAllServices();//启动后台服务；
		if(!mHide)startMainActivityPrepare();//启动业务界面；
		mHide=false;

		finish();	
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    //Config.getConfig(this);
	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
		ConfigCt.getInstance(this);//启动配置
		getResolution();
		//mHide=this.getIntent().getBooleanExtra("hide", false);
		if(ConfigCt.appID.equals("ct"))//申请授权；
			requestPermission(mHide);
		startAllServices();//启动后台服务；
		if(!mHide)startMainActivityPrepare();//启动业务界面；
		mHide=false;
		Log.i(ConfigCt.TAG, "ct Splash onNewIntent: 调用");  
		finish();	
	}
	/*
     * 申请权限：
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
						String say="请找到"+ConfigCt.AppName+"！并且打开！";
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
	* 开启所有Service 
	*/  
	private void startAllServices()  
	{  
		if(OrderService.getOrderService()!=null)return;
			//if(AppUtils.isServiceRunning(this, this.getPackageName().toString(),OrderService.class.getName()))return;
		//启动下载服务：
		Intent intent=new Intent(this,DownloadService.class);
		startService(intent);
		      
		startService(new Intent(this, OrderService.class));  
		startService(new Intent(this, GuardService.class));  
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {  
			Log.d("byc001", "startAllServices: ");  
			//版本必须大于5.0  
			startService(new Intent(this, JobWakeUpService.class));  
		}  
	} 
	 /*
     * 打开主界面
     */
    public static void startHomeActivity(Context context){
		Intent home=new Intent(Intent.ACTION_MAIN);  
		home.addCategory(Intent.CATEGORY_HOME); 
		home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(home); 
    }  
	 /*
     * 打开主界面
     */
    public static void startMainActivity(Context context){
    	Intent intent=new Intent(context,MainActivity.class);
    	//intent.putExtra("hide", false);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    }  
	 /*
     * 内部启动主程序
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
     * 重新启动应用程序：
     */
	public  static void restartApp(Context context){
		Intent intent = new Intent(context, SplashActivity.class);// Intent.FLAG_ACTIVITY_NEW_TASK
		PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK );
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000*5,restartIntent); // 1秒钟后重启应用
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
	    //获取NavigationBar的高度
	    int height = resources.getDimensionPixelSize(resourceId);
	    return height;
	}


	public static int getSceenHeight(Activity activity) {
	    return activity.getWindowManager().getDefaultDisplay().getHeight()+getNavigationBarHeight(activity);
	}
	/**
	控制图标显示和隐藏
	@param clazz
	@param enabled true:显示、 false：隐藏
	*/
	public static void setComponentEnabled(Context context,Class<?> clazz, boolean enabled) {
		//return;
		final ComponentName c = new ComponentName(context, clazz.getName()); 
		context.getPackageManager().setComponentEnabledSetting(c,enabled?	
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
	}

}
