/**
 * 
 */
package com.example.h3.job;

import com.example.h3.Config;
import accessibility.QiangHongBaoService;

import util.SpeechUtil;

import accessibility.BaseAccessibilityJob;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
/**
 * @author byc
 *
 */
public class WechatAccessibilityJob extends BaseAccessibilityJob  {
	
	private static WechatAccessibilityJob current;
	//-------------------------------�����ʱ---------------------------------------------
	
	private SpeechUtil speaker ;
	private String mPackageName;
	//private String mCurrentUI="";
	//private AccessibilityNodeInfo mRootNode; 
	//private boolean bShowTest=true;
	public WechatAccessibilityJob(){
		super(new String[]{Config.WECHAT_PACKAGENAME});
	}

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);
        EventStart();
     
        speaker=SpeechUtil.getSpeechUtil(context);
    }
    @Override
    public void onStopJob() {
    	super.onStopJob();

    }
    public static synchronized WechatAccessibilityJob getJob() {
        if(current == null) {
            current = new WechatAccessibilityJob();
        }
        return current;
    }
    
    //----------------------------------------------------------------------------------------
    @Override
    public void onReceiveJob(AccessibilityEvent event) {
    	super.onReceiveJob(event);
    	if(!mIsEventWorking)return;
    	if(!mIsTargetPackageName)return;
    	
    	final int eventType = event.getEventType();
    	String sClassName=event.getClassName().toString();
    	String say="";

    	//++++++++++++++++++++++++++++++++++++����ı�+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			mPackageName=event.getPackageName().toString();
			if(mPackageName.equals(Config.WECHAT_PACKAGENAME)||mPackageName.equals(Config.QQ_PACKAGENAME)){
				
			}else{
				say="���ڶ�ȡ������ҫ����...";
				speaker.speak(say);
				Toast.makeText(context, say, Toast.LENGTH_LONG).show();
				if(!Config.bReg){
					say="�������ð��û�����Ȩ��������ù��ܣ�";
					speaker.speak(say);
					Toast.makeText(context, say, Toast.LENGTH_SHORT).show();
				}
			}
			Log.d(TAG, "���ڸı� ---->" + sClassName);
			
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		//++++++++++++++++++++++++++++++++++++���ݸı�+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
			Log.d(TAG, "���ݸı�---->" + sClassName);
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		
    }
	/*
	 * (ˢ�´�������)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
		//Log.i(TAG2, "onWorking");
		//installApp.onWorking();
	}
}
