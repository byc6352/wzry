/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.example.h3.permission.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HuaweiUtils {
    private static final String TAG = "HuaweiUtils";

    /**
     * ��� Huawei ������Ȩ��
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    /**
     * ȥ��ΪȨ������ҳ��
     */
    public static void applyPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//��ΪȨ�޹���
//   ComponentName comp = new ComponentName("com.huawei.systemmanager",
//      "com.huawei.permissionmanager.ui.SingleAppActivity");//��ΪȨ�޹�����ת��ָ��app��Ȩ�޹���λ����Ҫ��Ϊ�ӿ�Ȩ�ޣ�δ���
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//����������ҳ��
            intent.setComponent(comp);
            if (RomUtils.getEmuiVersion() == 3.1) {
                //emui 3.1 ������
                context.startActivity(intent);
            } else {
                //emui 3.0 ������
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");//����������ҳ��
                intent.setComponent(comp);
                context.startActivity(intent);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//��ΪȨ�޹���
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//��ΪȨ�޹�����ת����app��Ȩ�޹���ҳ��,�����Ҫ��Ϊ�ӿ�Ȩ�ޣ�δ���
//      ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//����������ҳ��
            intent.setComponent(comp);
            context.startActivity(intent);
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ActivityNotFoundException e) {
            /**
             * �ֻ��ܼҰ汾�ϵ� HUAWEI SC-UL10
             */
//   Toast.makeText(MainActivity.this, "act�Ҳ���", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");//Ȩ�޹���ҳ�� android4.4
//   ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.single_app_activity");//�˴�����ת��ָ��app��Ӧ��Ȩ�޹���ҳ�棬������Ҫ���Ȩ�ޣ�δ���
            intent.setComponent(comp);
            context.startActivity(intent);
            e.printStackTrace();
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            //�׳��쳣ʱ��ʾ��Ϣ
            Toast.makeText(context, "��������ҳ��ʧ�ܣ����ֶ�����", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                Object o=method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
                int i = Integer.parseInt(String.valueOf(o));
                return AppOpsManager.MODE_ALLOWED == i;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }
}


