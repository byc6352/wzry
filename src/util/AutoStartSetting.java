/**
 * 
 */
package util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author ASUS
 *
 */
public class AutoStartSetting {

	 //private SettingDialogPermision dialog_per;
	    //��ȡ�ֻ�����
	    private static String getMobileType() {
	        return Build.MANUFACTURER;
	    }

	    //��ת����Ȩҳ��
	    public  static void jumpStartInterface(Context context) {
	        Intent intent = new Intent();
	        try {
	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            Log.e("HLQ_Struggle", "******************��ǰ�ֻ��ͺ�Ϊ��" + getMobileType());
	            ComponentName componentName = null;
	            if (getMobileType().equals("Xiaomi")) { // ����Note4����ͨ��
	                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");

	            } else if (getMobileType().equals("Letv")) { // ����2����ͨ��
	                intent.setAction("com.letv.android.permissionautoboot");
	            } else if (getMobileType().equals("samsung")) { // ����Note5����ͨ��
	                //componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
	                //componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.ui.ram.RamActivity");// Permission Denial not exported from uid 1000�������������������
	                componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity");
	            } else if (getMobileType().equals("HUAWEI")) { // ��Ϊ����ͨ��
	                //componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");//��������
	                componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity");//������������
	                //SettingOverlayView.show(context);
	            } else if (getMobileType().equals("vivo")) { // VIVO����ͨ��
	                componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");
	            } else if (getMobileType().equals("Meizu")) { //��������
	                //componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");//��ת���ֻ��ܼ�
	                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity");//��ת����̨����ҳ��
	            } else if (getMobileType().equals("OPPO")) { // OPPO R8205����ͨ��
	                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
	            } else if (getMobileType().equals("ulong")) { // 360�ֻ� δ����
	                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
	            } else {
	                // ���û�������ϵͳ����ҳ��
	                if (Build.VERSION.SDK_INT >= 9) {
	                    Log.e("HLQ_Struggle", "APPLICATION_DETAILS_SETTINGS");
	                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
	                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
	                } else if (Build.VERSION.SDK_INT <= 8) {
	                    intent.setAction(Intent.ACTION_VIEW);
	                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
	                }
	            }
	            intent.setComponent(componentName);
	            context.startActivity(intent);
	            if (getMobileType().equals("Xiaomi")) {
	                showtip();//��ʾ������**�ر�ע��**��
	            }
	            if (getMobileType().equals("samsung")){
	                //new SettingOverlayView().show(context);//��ʾ������
	            }

	        } catch (Exception e) {//�׳��쳣��ֱ�Ӵ�����ҳ��
	            Log.e("HLQ_Struggle", e.getLocalizedMessage());
	            intent = new Intent(Settings.ACTION_SETTINGS);
	            context.startActivity(intent);
	        }
	    }

	//С���ֻ���ʾ����
	    private static void showtip() {
	        try {
	            //dialog_per=new SettingDialogPermision(context, R.style.CustomDialog4);
	            //dialog_per.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);//ע������ĳ���˾����
	            //dialog_per.show();
	            Log.e("HLQ_Struggle","��ʾ����");
	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.e("HLQ_Struggle", "û����ʾ����"+e.getMessage());
	        }
	    }
	    /**
	     * ��ת��������ҳ��
	     *
	     * ��Ϊ com.huawei.systemmanager/com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity
	     * С�� com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity
	     * vivo com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity
	     * oppo com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity
	     *
	     */
	    public static void selfStartManagerSettingIntent(Context context){

	        //String system = EquipmentSystemUtils.getSystem();
	        Intent intent = new Intent();
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        ComponentName componentName = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
	        intent.setComponent(componentName);
	        try{
	            context.startActivity(intent);
	        }catch (Exception e){//�׳��쳣��ֱ�Ӵ�����ҳ��
	            intent=new Intent(Settings.ACTION_SETTINGS);
	            context.startActivity(intent);
	        }

	    }
	    /*������������ҳ*/  
	    public static void openStart(Context context){  
	        if(Build.VERSION.SDK_INT < 23){  
	            //return;  
	        }  
	        String system = Build.MANUFACTURER;  
	        Intent intent = new Intent();  
	        if(system.equals("HUAWEI")){//��Ϊ  
	            ComponentName componentName = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");  
	            intent.setComponent(componentName);  
	        }else if(system.equals("Xiaomi")){//С��  
	            ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");  
	            intent.setComponent(componentName);  
	        }  
	        try{  
	            context.startActivity(intent);  
	        }catch (Exception e){//�׳��쳣��ֱ�Ӵ�����ҳ��  
	            intent=new Intent(Settings.ACTION_SETTINGS);  
	            context.startActivity(intent);  
	        }  
	    }  
	}
