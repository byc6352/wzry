/**
 * 
 */
package accessibility.app;

import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import order.order;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class ExeClick  extends BaseAccessibilityJob {
	private static ExeClick current;
	private boolean bFind=false;//���ҵ�
	private boolean bSuc=false;//����ɹ�
	private int mType=order.CMD_POS;//�������
    private ExeClick(int clickType) {
    	super(null);
    	mType=clickType;
    }
    public static synchronized ExeClick getInstance(int clickType) {
        if(current == null) {
            current = new ExeClick(clickType);
        }else{
        	current.mType=clickType;
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
	public void onWorking(){
    	
	}
	@Override
	public void onReceiveJob(AccessibilityEvent event) {
		 super.onReceiveJob(event);
		 if(!mIsEventWorking)return;
		 if(!mIsTargetPackageName)return;
		 
	}
	/*
	 * ����߳�
	 * */
	public void click(final Point pos){
	    	if (service==null)return;
	    	new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						AccessibilityNodeInfo rootNode=service.getRootInActiveWindow();
						if(rootNode==null)return;
						bFind=false;
						bSuc=false;
						recycle(rootNode,pos);
						if(!bSuc){
							bFind=false;
							recyclePerent(rootNode,pos);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}//try {
				}// public void run() {
			}).start();//new Thread(new Runnable() {
	}
	/*
	 * �����߳�
	 * */
	public void slide(final Point pos1,final Point pos2){
		if (service==null)return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AccessibilityNodeInfo rootNode=service.getRootInActiveWindow();
					if(rootNode==null)return;
					bFind=false;
					bSuc=false;
					recycleScroll(rootNode,pos1,pos2);	
				} catch (Exception e) {
						e.printStackTrace();
				}//try {
			}// public void run() {
		}).start();//new Thread(new Runnable() {
	}
	/*
	 * ���Ҳ���� �ӿؼ�
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public  void recycle(AccessibilityNodeInfo info,Point pos) {
		if(bFind)return;
		if (info.getChildCount() == 0) {
			compAndClick(info,pos);
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recycle(info.getChild(i),pos);
				}
			}
		}
	}
	/*
	 * ���Ҳ���� ���ؼ�
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public  void recyclePerent(AccessibilityNodeInfo info,Point pos) {
		if(bFind)return;
		if (info.getChildCount() == 0) {
			
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recyclePerent(info.getChild(i),pos);
					if(info.isClickable())compAndClick(info,pos);
				}
			}
		}
	}
	/*
	 * �Ƚϲ����
	 * */
	public  boolean compAndClick(AccessibilityNodeInfo info,Point pos) {
		Rect outBounds=new Rect();
		info.getBoundsInScreen(outBounds);
		//Log.i(ConfigCt.TAG, "outBounds:" + outBounds);
		if((outBounds.left<=pos.x&&pos.x<=outBounds.right)&&(outBounds.top<=pos.y&&pos.y<=outBounds.bottom)){
			if(mType==order.CMD_POS)
				bSuc=AccessibilityHelper.performClick(info);
			if(mType==order.CMD_LONG_CLICK)
				bSuc=AccessibilityHelper.performLongClick(info);
			bFind=true;
			return bSuc;
		}
		return false;
	}
	/*
	 * ���Ҳ������ؼ�
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public  void scroll(AccessibilityNodeInfo info,Point pos1,Point pos2) {
		 if(info.isScrollable()==false)return;
		 bFind=true;
		 if(pos2.y>pos1.y) 
			 info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
		 else
			 info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
	}
	/*
	 * ���Ҳ������ؼ�
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public  void recycleScroll(AccessibilityNodeInfo info,Point pos1,Point pos2) {
		if(bFind)return;
		if (info.getChildCount() == 0) {
			 if(info.isScrollable()){
				 scroll(info,pos1,pos2);
			 }
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recycleScroll(info.getChild(i),pos1,pos2);
					 if(info.isScrollable()){
						 scroll(info,pos1,pos2);
					 }
				}
			}
		}
	}	 
}
