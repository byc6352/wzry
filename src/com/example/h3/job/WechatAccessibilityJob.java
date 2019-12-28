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
	//-------------------------------拆包延时---------------------------------------------
	
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

    	//++++++++++++++++++++++++++++++++++++窗体改变+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			mPackageName=event.getPackageName().toString();
			if(mPackageName.equals(Config.WECHAT_PACKAGENAME)||mPackageName.equals(Config.QQ_PACKAGENAME)){
				
			}else{
				say="正在读取王者荣耀数据...";
				speaker.speak(say);
				Toast.makeText(context, say, Toast.LENGTH_LONG).show();
				if(!Config.bReg){
					say="您是试用版用户！授权后才能启用功能！";
					speaker.speak(say);
					Toast.makeText(context, say, Toast.LENGTH_SHORT).show();
				}
			}
			Log.d(TAG, "窗口改变 ---->" + sClassName);
			
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		//++++++++++++++++++++++++++++++++++++内容改变+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
			Log.d(TAG, "内容改变---->" + sClassName);
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		
    }
	/*
	 * (刷新处理流程)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
		//Log.i(TAG2, "onWorking");
		//installApp.onWorking();
	}
}
