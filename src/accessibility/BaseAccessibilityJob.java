/**
 * 
 */
package accessibility;

import android.content.Context;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import util.ConfigCt;
/**
 * @author byc
 *
 */
public abstract class BaseAccessibilityJob implements AccessibilityJob {
	private static final int TIME_WORKING_INTERVAL=200;//ʱ��������ˢ��Ƶ�ʣ�
	protected static final int TIME_WORKING_CONTINUE=1000*60*1;//ʱ�������ĳ���ʱ�䣻
	//�����ʶ
	protected String TAG ;	
	protected String TAG2 ;
	protected QiangHongBaoService service;
	public Context context;
	protected String mCurrentUI="";
	protected int eventType;//�¼�����;
	protected String[] mPkgs;//������İ�;
	protected boolean mIsTimeWorking=false;//�Ƿ�ʼˢ�´���
	protected boolean mIsEventWorking=false;//�Ƿ�ʼ�¼�����
	protected boolean mIsTargetPackageName=false;//�Ƿ��Ǳ����������
	

    public  BaseAccessibilityJob(String[] pkgs) {
        TAG=ConfigCt.TAG;
        TAG2=ConfigCt.TAG2;
        mPkgs=pkgs;
    }
    /*
     * (������������)
     * @see accessbility.AccessbilityJob#onCreateJob(accessbility.QiangHongBaoService)
     */
    @Override
    public void onCreateJob(QiangHongBaoService service) {
        this.service = service;
        TAG=ConfigCt.TAG;
        TAG2=ConfigCt.TAG2;
        context=service.getApplicationContext();
    }
    /*
     * (non-Javadoc)
     * @see accessbility.AccessbilityJob#onStopJob()
     */
    @Override
    public void onStopJob() {
    	service=null;
    	context=null;
    }
    protected Context getContext() {
        return service.getApplicationContext();
    }

    protected ConfigCt getConfig() {
        return service.getConfig();
    }

    protected QiangHongBaoService getService() {
        return service;
    }
    @Override
    public boolean isEnable(){
    	if(service==null)
    		return false;
    	else
    		return true;
    }
    public void  setPkgs(String[] pkgs) {
    	this.mPkgs=pkgs;
    }
    //----------------------------------pkg����------------------------------
    /*
     * (�Ƿ���������İ�?)
     * @see accessbility.AccessbilityJob#isTargetPackageName(java.lang.String)
     */
    @Override
    public boolean isTargetPackageName(String pkg){
    	if(mPkgs==null)return true;
    	if(mPkgs.length==0)return true;
    	if(pkg==null||pkg.equals(""))return false;
    	for(int i=0;i<mPkgs.length;i++){
    		if(mPkgs[i].equals(pkg))return true;
    	}
    	return false;
    }
    /*
     * (����������İ�)
     * @see accessbility.AccessbilityJob#getTargetPackageName()
     */
    @Override
    public String[] getTargetPackageName(){
    	return mPkgs;
    }
    //----------------------------------�¼�����----------------------------------------
    /*
     * (�¼���������)
     * @see accessbility.AccessbilityJob#onReceiveJob(android.view.accessibility.AccessibilityEvent)
     */
    @Override
    public void onReceiveJob(AccessibilityEvent event) {
    	
    	if(!mIsEventWorking)return;
    	if(event.getPackageName()==null)return;
    	if(!isTargetPackageName(event.getPackageName().toString())){
    		mIsTargetPackageName=false;
    		return;
    	}
    	mIsTargetPackageName=true;
    	eventType = event.getEventType();
   		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
   		{
   	       	if(event.getClassName()==null)return;
   	       	mCurrentUI=event.getClassName().toString();
   		}
    }
  //---------------------------�¼�����-----------------------------------------------
    /*
     * (non-Javadoc)
     * @see accessbility.AccessbilityJob#EventStart()
     */
    @Override
    public void EventStart(){
    	mIsEventWorking=true;
    }
    /*
     * (non-Javadoc)
     * @see accessbility.AccessbilityJob#closeEventWorking()
     */
    @Override
    public void closeEventWorking(){
    	mIsEventWorking=false;
    }
    /*
     * (����1���Ӻ��Զ��ر��¼�����)
     * @see accessbility.AccessbilityJob#EventTimeStart()
     */
    @Override
    public void EventTimeStart(){
    	mIsEventWorking=true;
    	Runnable runnableStop = new Runnable() {    
    		@Override    
    		public void run() {    
    			mIsEventWorking=false;	   
    		}    
    	}; 
    	handler.postDelayed(runnableStop, TIME_WORKING_CONTINUE);
    }
    //---------------------------ʱ������-----------------------------------------------
    /*
     * (��ʼˢ�´���1���Ӻ��Զ��ر�)
     * @see accessbility.AccessbilityJob#onStart()
     */
    @Override
    public void TimeStart(){
    	mIsTimeWorking=true;
    	Runnable runnableStop = new Runnable() {    
    		@Override    
    		public void run() {    
    			mIsTimeWorking=false;	   
    		}    
    	}; 
    	Runnable runnableTime = new Runnable() {    
    		@Override    
    		public void run() {    
    			if(!mIsTimeWorking)return;
    			onWorking();
    			handler.postDelayed(this, TIME_WORKING_INTERVAL);			   
    		}    
    	};
    	handler.postDelayed(runnableStop, TIME_WORKING_CONTINUE); 
    	handler.postDelayed(runnableTime, 10); 
    }
    private Handler handler = new Handler();
	
	/*
	 * (non-Javadoc)
	 * @see accessbility.AccessbilityJob#closeTimeWorking()
	 */
    @Override
    public void closeTimeWorking(){
    	mIsTimeWorking=false;
    }
	
}
