/**
 * 
 */
package permission;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import accessibility.AccessibilityHelper;

/**
 * @author ASUS
 *
 */
public class GivePermission  extends BaseAccessibilityJob  {
	   private static GivePermission current;
	   private static final String BUTTON_CLASS_NAME="android.widget.Button";
	   private static final String CHECK_CLASS_NAME="android.widget.CheckBox";
	   public static final String WINDOW_NOTIFICATION_ACCESS_UI="com.android.settings.Settings$NotificationAccessSettingsActivity";
	   private String[] mKeyWords;
	    
	    private GivePermission() {
	    	super(null);
	    }
	    public static synchronized GivePermission getGivePermission() {
	        if(current == null) {
	            current = new GivePermission();
	        }
	        return current;
	    }
	    @Override
		public void onCreateJob(QiangHongBaoService service) {
			super.onCreateJob(service);
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
	    	int eventType = event.getEventType();
	    	if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
	    		AccessibilityNodeInfo nodeInfo = event.getSource();
	    		if(nodeInfo==null)return;
	    		nodeInfo=AccessibilityHelper.getRootNode(nodeInfo);
	    		recycleClick(nodeInfo);
	    	}
	    }
		/*
		 * (ˢ�´�������)
		 * @see accessbility.AccessbilityJob#onWorking()
		 */
		@Override
		public void onWorking(){
			if(service==null)return;
	    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
	    	if(rootNode==null)return;
	    	//recycleGiveNotification(event);
	    	recycleClick(rootNode);
		}
		private void recycleClick(AccessibilityNodeInfo rootNode){
    		recycleGiveCheck(rootNode);
    		recycleGiveButton(rootNode);
	    	//closeTimeWorking();}
		}
	    /*
	     * (non-Javadoc)
	     * @see accessbility.AccessbilityJob#EventStart()
	     */
	    @Override
	    public void EventStart(){
	    	super.EventStart();
	    	Handler handler= new Handler(); 
	    	Runnable runnableEvent = new Runnable() {    
	    		@Override    
	    		public void run() {    
	    			closeEventWorking();
	    			mKeyWords=null;
	    		}    
	    	};
	    	handler.postDelayed(runnableEvent, TIME_WORKING_CONTINUE);
	    }
		//-----------------------------------------------------------------------------------------------

	  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	    public void recycleGiveCheck(AccessibilityNodeInfo info) {
	    	if (info.getChildCount() == 0) {
	    		//ȡ��Ϣ
	    		if(info.getClassName()==null)return;
	    		String className=info.getClassName().toString();
	    		if(className.equals(CHECK_CLASS_NAME)&&info.isCheckable()){
	    			if(!info.isChecked())if(info.isClickable())info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    		}
	    		
	    	} else {
	    		for (int i = 0; i < info.getChildCount(); i++) {
	    			if(info.getChild(i)!=null){
	    				recycleGiveCheck(info.getChild(i));
	    			}
	    		}
	    	}
	    }
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void recycleGiveButton(AccessibilityNodeInfo info) {
	    	if (info.getChildCount() == 0) {
	    		//ȡ��Ϣ
	    		if(info.getClassName()==null)return;
	    		String className=info.getClassName().toString();
	    		if(className.equals(AccessibilityHelper.WIDGET_TEXT)&&info.isClickable()){
	    			if(info.getText()==null)return;
	    			String txtTxt=info.getText().toString();
	    			if(txtTxt.equals("���µ�¼"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			return;
	    		}
	    		if(className.equals(BUTTON_CLASS_NAME)&&info.isClickable()){
	    			if(info.getText()==null)return;
	    			String btnTxt=info.getText().toString();	
	    			if(btnTxt.contains("��Ȩ"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);//��Ȩ��superU����������������
	    			if(btnTxt.contains("����"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			if(btnTxt.contains("��װ"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			//if(btnTxt.contains("����"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);//oppo 
	    			if(btnTxt.indexOf("����")!=-1)info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			if(btnTxt.indexOf("������ʼ")!=-1)info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			if(btnTxt.indexOf("��Ȼ֧��")!=-1)info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			if(btnTxt.contains("����һ��"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
	    			clickKeyWords(btnTxt,info);
	    		}
	    	} else {
	    		for (int i = 0; i < info.getChildCount(); i++) {
	    			if(info.getChild(i)!=null){
	    				recycleGiveButton(info.getChild(i));
	    			}
	    		}
	    	}
	}
	/*
	 * ���ùؼ���
	 * */
	public void setKeyWords(String[] keyWords){
		this.mKeyWords=keyWords;
	}
	/*
	 * ����ؼ���
	 * */
	private void clickKeyWords(String btnTxt,AccessibilityNodeInfo info){
		if(mKeyWords==null||mKeyWords.length==0)return;
		for(String key:mKeyWords){
			if(btnTxt.contains(key))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);//��������
		}
	}
	
}
