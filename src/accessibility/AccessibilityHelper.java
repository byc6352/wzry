/**
 * 
 */
package accessibility;

import java.util.ArrayList;
import java.util.List;


import util.ConfigCt;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * @author byc
 *
 */
public class AccessibilityHelper {
	public static final String WIDGET_BUTTON="android.widget.Button";
	public static final String WIDGET_EDIT="android.widget.EditText";
	public static final String WIDGET_TEXT="android.widget.TextView";
	public static final String PACKAG_ENAME_SETTING="com.android.settings";
	 
	public static List<AccessibilityNodeInfo> classNames= new ArrayList<AccessibilityNodeInfo>();
	private static final String DIGITAL="0123456789";//
	private static boolean bFind=false;
	private static boolean bRecycle=true;
	private static AccessibilityNodeInfo mNodeInfo;
    /** ͨ���ı�����*/
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text,int i) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        if(i==-1)
        	return list.get(list.size()-1);
        else
        	return list.get(i);
    }
    /** ͨ���ı�����*/
    public static AccessibilityNodeInfo findNodeInfosByTextAndClassName(AccessibilityNodeInfo rootNode, String text,String className) {
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        for(AccessibilityNodeInfo node:list){
        	String cName=node.getClassName().toString();
        	if(cName.equals(className))return node;
        }
        return null;
    }
    /** ͨ���ı�����(��ȫƥ��)*/
    public static AccessibilityNodeInfo findNodeInfosByTextAllMatched(AccessibilityNodeInfo rootNode, String text) {
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        for(AccessibilityNodeInfo node:list){
        	if(node.getText()==null)continue;
        	String txt=node.getText().toString();
        	if(txt.equals(text))return node;
        }
        return null;
    }
    
    /** ͨ���ı�����*/
    public static AccessibilityNodeInfo findNodeInfosByTextNext(AccessibilityNodeInfo rootNode, String text) {
    	bFind=false;
    	bRecycle=true;
    	mNodeInfo=null;
    	findNodeInfosByTextNextRecycle(rootNode,text);
    	return mNodeInfo;
    }
    /** ͨ���ı�����*/
    private static void findNodeInfosByTextNextRecycle(AccessibilityNodeInfo rootNode, String text) {
    	if (rootNode.getChildCount() == 0) {
    		if(!bRecycle)return;
    		if(bFind){
    			bRecycle=false;
    			mNodeInfo=rootNode;
    		}
    		if(rootNode.getText()!=null){
    			String txt=rootNode.getText().toString();
    			if(txt.contains(text)){
    				bFind=true;
    			}
    		}
    		
    	} else {
    		for (int i = 0; i < rootNode.getChildCount(); i++) {
    			if(!bRecycle)return;
    			if(rootNode.getChild(i)!=null){
    				findNodeInfosByTextNextRecycle(rootNode.getChild(i),text);
    			}
    		}
    	}
    }
    
    /** ͨ��id����*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public  static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId,int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
            	if(i==-1)return list.get(list.size()-1);
                return list.get(i);
            }
        }
        return null;
    }
    /** ͨ���ؼ�����*/
    public static AccessibilityNodeInfo findNodeInfosByClassName(AccessibilityNodeInfo rootNode, String className,int i,boolean bClear) {
    	
    	if(bClear){
    		classNames.clear();
    		recycleClassName(rootNode,className);
    	}   	
        if(classNames == null || classNames.isEmpty()) return null;
        if(i==-1)
        	return classNames.get(classNames.size()-1);
        else{
        	if(classNames.size()>i)
        		return classNames.get(i);
        	else
        		return null;
        }
    }
    public static void recycleClassName(AccessibilityNodeInfo info,String className) {
		if(className.equals(info.getClassName()))
  			classNames.add(info);
  		if (info.getChildCount() == 0) {

  			//Log.i(Config.TAG, "child widget----------------------------" + info.getClassName());
  		} else {
  			for (int i = 0; i < info.getChildCount(); i++) {
  				if(info.getChild(i)!=null){
  					recycleClassName(info.getChild(i),className);
  				}
  			}
  		}
  	}
    //�����ʱ��
    public  static void Sleep(int MilliSecond) {
    	
	    try{
	    	  Thread.sleep(MilliSecond);
	    }catch(Exception e){
	    } 
    }
    /** �õ�rootNode*/
    public  static AccessibilityNodeInfo getRootNode(AccessibilityNodeInfo node){
    	if(node==null)return null;
    	AccessibilityNodeInfo parent=node.getParent();
    	AccessibilityNodeInfo tmp=node;
    	while(parent!=null){
    		tmp=parent;
    		parent=parent.getParent();
    	}
    	return tmp;
    }
    /** �����¼�*/
    public static void performBack(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    /** ������Ļ�¼�*/
    public static boolean performScrollForward(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        	return false;
        	
        }
        if(nodeInfo.isScrollable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        } else {
            return performScrollForward(nodeInfo.getParent());
        }
        
    }
    /** ����¼�*/
    public static boolean performClick(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        	return false;
        }
        if(nodeInfo.isClickable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            return performClick(nodeInfo.getParent());
        }
        
    }
    /** ����¼�*/
    public static boolean performLongClick(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        	return false;
        }
        if(nodeInfo.isClickable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        } else {
            return performLongClick(nodeInfo.getParent());
        }
        
    }
    /*�����ı�*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static  boolean nodeInput(AccessibilityNodeInfo edtNode,String txt){
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//android 5.0
    		Bundle arguments = new Bundle();
        	arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,txt);
        	edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        	return true;
    	}
    	/*
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){//android 4.3
    		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
    		ClipData clip = ClipData.newPlainText("text",txt);  
    		clipboard.setPrimaryClip(clip);  

    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);  
    		////ճ����������  
    		edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);  
    		return true;
    	}
    	
    	if(Config.currentapiVersion>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){//android 4.0
    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        	String sOrder="input text "+txt;
        	AccessibilityHelper.Sleep(5000);
        	if(RootShellCmd.getRootShellCmd().execShellCmd(sOrder)){
        		AccessibilityHelper.Sleep(5000);
        		return true;
        	}
        	return false;
    	}
    	*/
    	return false;
    } 
    //��ӡ�ӿؼ���
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static void recycle2(AccessibilityNodeInfo info) {
  		if (info.getChildCount() == 0) {
  			//ȡ��Ϣ
  			//mRedInfo[mIntInfo]=info.getText().toString();
  			Log.i(ConfigCt.TAG, "child widget----------------------------" + info.getClassName());
  			//Log.i(TAG, "showDialog:" + info.canOpenPopup());
  			Log.i(ConfigCt.TAG, "Text��" + info.getText());
  			Log.i(ConfigCt.TAG, "windowId:" + info.getWindowId());
  			Log.i(ConfigCt.TAG, "ResouceId:" + info.getViewIdResourceName());
  			Log.i(ConfigCt.TAG, "isClickable:" + info.isClickable());
  			Rect outBounds=new Rect();
  			info.getBoundsInScreen(outBounds);
  			Log.i(ConfigCt.TAG, "outBounds:" + outBounds);

  		} else {
  			//Log.i(Config.TAG, "child widget----------------------------" + info.getClassName());
  			//Log.i(TAG, "showDialog:" + info.canOpenPopup());
  			//Log.i(Config.TAG, "Text��" + info.getText());
  			//Log.i(Config.TAG, "windowId:" + info.getWindowId());
  			//Log.i(Config.TAG, "ResouceId:" + info.getViewIdResourceName());
  			//Log.i(Config.TAG, "isClickable:" + info.isClickable());
  			//Rect outBounds=new Rect();
  			//info.getBoundsInScreen(outBounds);
  			//Log.i(Config.TAG, "outBounds:" + outBounds);
  			for (int i = 0; i < info.getChildCount(); i++) {
  				if(info.getChild(i)!=null){
  					recycle2(info.getChild(i));
  				}
  			}
  		}
  	}
    //��ӡ���ӿؼ���
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static void recycle(AccessibilityNodeInfo info) {
  		if (info.getChildCount() == 0) {
  			//ȡ��Ϣ
  			//mRedInfo[mIntInfo]=info.getText().toString();
  			Log.i(ConfigCt.TAG, "child widget----------------------------" + info.getClassName());
  			//Log.i(TAG, "showDialog:" + info.canOpenPopup());
  			Log.i(ConfigCt.TAG, "Text��" + info.getText());
  			//if(info.getText()!=null)
  			//	if(info.getText().toString().contains("����ͷ"))
  			//		Toast.makeText(Config.context, "����ͷ----------------------------", Toast.LENGTH_LONG).show();
  			Log.i(ConfigCt.TAG, "windowId:" + info.getWindowId());
  			Log.i(ConfigCt.TAG, "ResouceId:" + info.getViewIdResourceName());
  			Log.i(ConfigCt.TAG, "isClickable:" + info.isClickable());
  			Log.i(ConfigCt.TAG, "isCheckable:" + info.isCheckable());
  			Log.i(ConfigCt.TAG, "isChecked:" + info.isChecked());
  			Rect outBounds=new Rect();
  			info.getBoundsInScreen(outBounds);
  			Log.i(ConfigCt.TAG, "outBounds:" + outBounds);
  			Log.i(ConfigCt.TAG, "isFocusable:" + info.isFocusable());
  			Log.i(ConfigCt.TAG, "isAccessibilityFocused:" + info.isAccessibilityFocused());
  			Log.i(ConfigCt.TAG, "isFocused:" + info.isFocused());
  			Log.i(ConfigCt.TAG, "getContentDescription��" + info.getContentDescription());
          	//Bundle arguments = new Bundle();
          	//String sText="11";
          	//arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,sText);
          	//info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
  			
  			//if(info.isClickable())info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
  		} else {
  			Log.i(ConfigCt.TAG, "parent widget----------------------------" + info.getClassName());
  			//Log.i(TAG, "showDialog:" + info.canOpenPopup());
  			Log.i(ConfigCt.TAG, "Text��" + info.getText());
  			Log.i(ConfigCt.TAG, "getContentDescription��" + info.getContentDescription());
  			Log.i(ConfigCt.TAG, "windowId:" + info.getWindowId());
  			Log.i(ConfigCt.TAG, "ResouceId:" + info.getViewIdResourceName());
  			Log.i(ConfigCt.TAG, "isClickable:" + info.isClickable());
  			Rect outBounds=new Rect();
  			info.getBoundsInScreen(outBounds);
  			Log.i(ConfigCt.TAG, "outBounds:" + outBounds);
  			for (int i = 0; i < info.getChildCount(); i++) {
  				if(info.getChild(i)!=null){
  					recycle(info.getChild(i));
  				}
  			}
  		}
  	}
    //�Ƿ������֣�
    public static boolean isDigital(String s){
    	if(DIGITAL.indexOf(s)==-1)return false;else return true;
    }
}