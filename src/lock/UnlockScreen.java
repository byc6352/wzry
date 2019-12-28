/**
 * 
 */
package lock;

import accessibility.QiangHongBaoService;
import accessibility.app.ExeClick;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import lock.LockService.LockState;
import order.order;
import order.screen.ScreenRecordActivity;
import permission.GivePermission;
import util.ConfigCt;
import util.RootShellCmd;

/**
 * @author ASUS
 *
 */
public class UnlockScreen {
	private static UnlockScreen current;//ʵ����
	private static final int SLIDE_DOWN_TO_UP=1;//���µ��ϻ�����
	private static final int SLIDE_UP_TO_DOWN=2;//���ϵ��»�����
	private static final int SLIDE_LEFT_TO_RIGHT=3;//�����һ�����
	private static final int SLIDE_RIGHT_TO_LEFT=4;//���ҵ��󻬶���
	private int mOrientation=SLIDE_DOWN_TO_UP;//��������
	private Context context;
	private boolean bScreenOn=false;//�Ƿ�������
	private boolean bScreenLocked=true;//�Ƿ�������
	private IntentFilter filter;
	private BroadcastReceiver mBatInfoReceiver;
	 
	private UnlockScreen(Context context) {
		this.context=context;
	}
	public static synchronized UnlockScreen getInstance(Context context) {
		if(current == null) {
			current = new UnlockScreen(context);
		}else{
			current.context=context;;
	    }
		return current;
	}
    /**
     * ������Ļ
     */
    public void execUnlockScreen() {
    	if(mBatInfoReceiver!=null){
    		destoryUnlockScreen();
    		return;
    	}
    	bScreenOn=isScreenOn(context);
    	bScreenLocked=isScreenLocked(context);
    	if(bScreenOn==false||bScreenLocked==true){//��������������
    		
    		ScreenReceiver(context);//1.ע������¼���
    		wakeUpAndUnlock(context);//2.ִ�н�����
    		endUnlockScreen();//3.5���Ӻ��Զ���ֹ������
    	}
    }
    /**
     * ����������Ļ
     */
    public void destoryUnlockScreen() {
    	if(mBatInfoReceiver!=null){
        	context.unregisterReceiver(mBatInfoReceiver);
        	mBatInfoReceiver=null;
        	Log.d(ConfigCt.TAG, "unregisterReceiver(mBatInfoReceiver)");
    	}
    }
    /**
     * ����������Ļ
     * orientation��������SLIDE_DOWN_TO_UP,
     */
    private void seriesSlideScreen() {
    	 new Thread(new Runnable() {  
	          @Override  
	          public void run() {  
	        	  try {  
	        		  for(int i=0;i<10;i++){
		        		  SlideScreen(mOrientation,i);
		        		  Thread.sleep(1000);
		        		  
		        	  }
	        		  if(mOrientation==SLIDE_DOWN_TO_UP)
	        			  mOrientation=SLIDE_LEFT_TO_RIGHT;
	        		  else 
	        			  mOrientation=SLIDE_DOWN_TO_UP;
	              } catch (Exception e) {  
	                  // TODO: handle exception  
	                  e.printStackTrace();  
	              }  
	          }
    	 }).start();
	        	 
    }
    /**
     * ������Ļ
     * orientation��������SLIDE_DOWN_TO_UP,
     */
    public void SlideScreen(int orientation,int count) {
    	Points p=getPoints(orientation,count);
    	//Log.d(ConfigCt.TAG, "���ڻ��������="+p.start+"���յ�="+p.end);
    	if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.N){//
  			if(QiangHongBaoService.service!=null){
  				QiangHongBaoService.service.slideThread(p.start,p.end);
  			}
  		}else{
  			if(ConfigCt.bRoot){
  				RootShellCmd.processSwipe(p.start,p.end);
  			}else{
  				if(QiangHongBaoService.service!=null){
  					ExeClick.getInstance(order.CMD_SLIDE).slide(p.start,p.end);
  				}
  			}
  		}
    }
    /**
     * ���������յ�����
     * orientation��������SLIDE_DOWN_TO_UP,
     */
    private Points getPoints(int orientation,int count) {
    	Points p=new Points();
    	p.start=new Point();
    	p.end=new Point();
    	int h=ConfigCt.screenHeight;
    	int w=ConfigCt.screenWidth;
    	switch(orientation){
    	case SLIDE_DOWN_TO_UP:
    		p.start.x=w/2+count*10;
    		p.start.y=h-200;
    		p.end.x=w/2+count*10;
    		p.end.y=h-500;
    		break;
    	case SLIDE_UP_TO_DOWN:
    		p.start.x=w/2;
    		p.start.y=100;
    		p.end.x=w/2;
    		p.end.y=h-100;
    		break;
    	case SLIDE_LEFT_TO_RIGHT:
    		p.start.x=100;
    		p.start.y=h/2+count*10;
    		p.end.x=400;
    		p.end.y=h/2+count*10;
    		break;
    	case SLIDE_RIGHT_TO_LEFT:
    		p.start.x=w-100;
    		p.start.y=h/2;
    		p.end.x=100;
    		p.end.y=h/2;
    		break;
    	}
    	return p;
    }
    /**
     * ������Ļ�¼�
     */
    private void ScreenReceiver(Context context){
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
                	bScreenLocked=isScreenLocked(context);
                	if(bScreenLocked){
                		seriesSlideScreen();//���µ��ϻ���10��
                	}
                    Log.d(ConfigCt.TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                	wakeUpAndUnlock(context);//2.ִ�н�����
                    Log.d(ConfigCt.TAG, "screen off");
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                	destoryUnlockScreen();
                    Log.d(ConfigCt.TAG, "screen unlock");
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(ConfigCt.TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                }
            }
        };
        Log.d(ConfigCt.TAG, "mBatInfoReceiver  registerReceiver");
        context.registerReceiver(mBatInfoReceiver, filter);
    }
    //------------------------------------------------------------------------------------------
    /*
     * �������������յ�����
     * */
    public class Points {
    	Point start;
    	Point end;
    }
	//-----------------------------------------��������------------------------------------------
	/**   
     * �ж��Ƿ����   
     * @param c   
     * @return   
     */    
    public final static boolean isScreenOn(Context context) {    
     
             //android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(c.KEYGUARD_SERVICE);    
             //return !mKeyguardManager.inKeyguardRestrictedInputMode();    
    		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
    		boolean isScreenOn = pm.isScreenOn();//���Ϊtrue�����ʾ��Ļ�������ˣ�������Ļ�������ˡ�  
    		return isScreenOn;
    }    
    /**   
     * �ж��Ƿ�����   
     * @param c   
     * @return   
     */    
    public final static boolean isScreenLocked(Context context) {    
    	android.app.KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);    
    	return mKeyguardManager.inKeyguardRestrictedInputMode();    

     
    } 
    /**
     * �����ֻ���Ļ������
     */
    public static void wakeUpAndUnlock(Context context) {
        // ��ȡ��Դ����������
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // ��ȡPowerManager.WakeLock����,����Ĳ���|��ʾͬʱ��������ֵ,������LogCat���õ�Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // ������Ļ
            wl.release(); // �ͷ�
        }
        // ��Ļ����
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // ��Ļ����
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // ����
    }
    /*
     * ��ʱ����5���Ӻ��Զ���ֹ������
     */
	private void endUnlockScreen(){
		final Handler handler= new Handler(); 
		Runnable runnable  = new Runnable() {    
			@Override    
		    public void run() {   
				destoryUnlockScreen();
				//handler.postDelayed(this, 1000*5);    
		    }    
		};
		handler.postDelayed(runnable, 1000*60*5);  
	}
}
