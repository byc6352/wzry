package accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import util.ConfigCt;
import util.ResourceUtil;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.List;

import com.byc.wzry.R;
import activity.SplashActivity;

import java.util.Iterator;

import notification.IStatusBarNotification;
import order.OrderService;
/**
 * <p>Created by byc</p>
 * <p/>
 * �������ҷ���
 */
public class QiangHongBaoService extends AccessibilityService {
	//�����ʶ
	private static boolean mIsclick=true;
	private static Point mPosition;
	private static int mClickCount=0;//���������
	private static boolean mIsWorkDoneByHand=false;//�Ƿ����ֹ��򿪷���
	//public static final String WINDOW_LUCKYMONEY_RECEIVEUI="com.android.settings.Settings$AccessibilitySettingsActivity";
	//��ʵ������
	public static QiangHongBaoService service;
	//job����
	private static ManageAccessibilityJob job;
	@Override
	public void onCreate() {
		super.onCreate();
		service = this;
		if(!mIsWorkDoneByHand)//������
			if(OrderService.getOrderService()==null)//
				SplashActivity.startSplashActivity(this);
	     //Toast.makeText(this, "�Ѵ����������", Toast.LENGTH_SHORT).show();
		job=ManageAccessibilityJob.getJob();
		Log.d(ConfigCt.TAG, "qianghongbao service onCreate");
	}
	public ConfigCt getConfig() {
		return ConfigCt.getInstance(this);
	}
	public static QiangHongBaoService getQiangHongBaoService() {
		return service;
	}
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		job.onReceiveJob(event);
	}//public
    @Override
    public void onInterrupt() {
    	Log.d(ConfigCt.TAG, "qianghongbao service interrupt");
        //Toast.makeText(this, "�ж����׷���", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onServiceConnected() {
    	super.onServiceConnected();
        service = this;
        job=ManageAccessibilityJob.getJob();
        job.onCreateJob(service);
        if(mIsWorkDoneByHand){
        	AccessibilityHelper.performBack(service);
        	SplashActivity.startMainActivity(this);
        	//Toast.makeText(this, "�����Ӻ������", Toast.LENGTH_SHORT).show();
        	Intent intent = new Intent(ConfigCt.ACTION_QIANGHONGBAO_SERVICE_CONNECT); //���͹㲥���Ѿ���������
        	sendBroadcast(intent); 
        }
        mIsWorkDoneByHand=false;
        Log.d(ConfigCt.TAG, "qianghongbao service onServiceConnected");
    }
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    job.onStopJob();
	    service = null;
	    //job = null;
        //���͹㲥���Ѿ��Ͽ���������
        Intent intent = new Intent(ConfigCt.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT);
        sendBroadcast(intent);
        Log.d(ConfigCt.TAG, "qianghongbao service destory");
	}
	
	 /**����֪ͨ���¼�*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void handeNotificationPosted(IStatusBarNotification notificationService) {
        if(notificationService == null) {
            return;
        }
        if(service == null) {
            return;
        }
        String pack = notificationService.getPackageName();
        if(!pack.equals(ConfigCt.WECHAT_PACKAGENAME)) {
            return;
        }
        //job.onNotificationPosted(notificationService);
    }
    /**
     * ����������Ȩ�㲥
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendRequsetPermissionBroadCast(Context context) {
    	//String servicename=context.getString(R.string.service_name);
    	//String description=context.getString(R.string.accessibility_description);
    	String servicename=context.getApplicationContext().getString(ResourceUtil.getStringId(context, "service_name"));
    	String description=context.getApplicationContext().getString(ResourceUtil.getStringId(context, "accessibility_description"));
        Intent intent = new Intent(ConfigCt.ACTION_ACCESSBILITY_SERVICE_REQUEST);
        intent.putExtra("servicename", servicename);
        intent.putExtra("description", description);
        context.sendBroadcast(intent);
    }
    /**
     * ������������
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startSetting(Context context) {
    	mIsWorkDoneByHand=true;
    	Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    }
    /**
     * �жϵ�ǰ�����Ƿ���������
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if(service == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = service.getServiceInfo();
        if(info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if(i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if(!isConnect) {
            return false;
        }
        return true;
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
    @TargetApi(24)
    public boolean pressLocation(final Point position){
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)return false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(position.x, position.y);
        //p.lineTo(position.x, position.y);
        //p.lineTo(position.x+10, position.y+10);
        builder.addStroke(new GestureDescription.StrokeDescription(p,10, 10));
        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched ok " , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched Cancelled" , Toast.LENGTH_SHORT).show();
                //pressLocation(position);
            }
        }, null);
        return isDispatched;
        //Toast.makeText(this, "Was it dispatched? " + isDispatched, Toast.LENGTH_SHORT).show();
    }
    @TargetApi(24)
    public boolean pressLongLocation(final Point position){
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)return false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(position.x, position.y);
        p.lineTo(position.x, position.y);
        //p.lineTo(position.x+10, position.y+10);
        builder.addStroke(new GestureDescription.StrokeDescription(p,10, 2000));
        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched ok " , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched Cancelled" , Toast.LENGTH_SHORT).show();
                //pressLocation(position);
            }
        }, null);
        return isDispatched;
        //Toast.makeText(this, "Was it dispatched? " + isDispatched, Toast.LENGTH_SHORT).show();
    }
    Handler handlerClick = new Handler();
	Runnable runnableClick = new Runnable() {    
		@Override    
	    public void run() {    
			if(mIsclick){	
				pressLocation(mPosition);
				mClickCount=mClickCount+1;
				handlerClick.postDelayed(this, 1000*1); 
		        Intent intent = new Intent(ConfigCt.ACTION_ACCESSBILITY_SERVICE_CLICK);
		        intent.putExtra("count", mClickCount);
		        sendBroadcast(intent);
			}
	    }    
	};
    public void startClick(final Point position){
    	mIsclick=true;
    	mClickCount=0;
    	QiangHongBaoService.mPosition=position;
    	handlerClick.postDelayed(runnableClick, 100); 
    	return ;
    }
    public void closeClick(){
    	mClickCount=0;
    	mIsclick=false;
    }
    public int clickCount(){
    	return  mClickCount;
    }
    //---------------------------------------------------------------------------------------------------
	/*
		�����̣߳�
	 */
	public void slideThread(final Point p1,final Point p2){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					slide(p1,p2);
				} catch (Exception e) {
					e.printStackTrace();
				}//try {
			}// public void run() {
		}).start();//new Thread(new Runnable() {
	}
    @TargetApi(24)
    public boolean slide(final Point p1,final Point p2){
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)return false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        //p.lineTo(position.x+10, position.y+10);
        builder.addStroke(new GestureDescription.StrokeDescription(path,10L,100L));
        GestureDescription gesture = builder.build();
        boolean isDispatched = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched ok " , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                //if(Config.DEBUG)Toast.makeText(QiangHongBaoService.this, "Was it dispatched Cancelled" , Toast.LENGTH_SHORT).show();
            }
        }, null);
        return isDispatched;
    }
}


/*
if(Config.DEBUG){
String pkg=this.getPackageName().toString();
Log.i("byc003", pkg);
if(AppUtils.isAppRunning(this, pkg))
	Log.i("byc003", "isAppRunning=true");
else{
	Intent intent=new Intent(this,MainActivity.class);
   	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   	startActivity(intent);
	Log.i("byc003", "isAppRunning=false");
}
}
*/