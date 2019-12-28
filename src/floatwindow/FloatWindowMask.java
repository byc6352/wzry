/**
 * 
 */
package floatwindow;

import com.byc.wzry.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class FloatWindowMask {
	private static FloatWindowMask current;
	private Context context;
	//���帡�����ڲ���
	private LinearLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
    //���������������ò��ֲ����Ķ���
	private WindowManager mWindowManager;
	//���ڿؼ�����
	public TextView tvShow;//��ʾ���ݣ�
	private boolean bShow=false;//�Ƿ���ʾ
	//-----------------------------------------------------------------------------
	private FloatWindowMask(Context context) {
		this.context = context.getApplicationContext();
		createFloatView();
		

		}
	    public static synchronized FloatWindowMask getInstance(Context context) {
	        if(current == null) {
	            current = new FloatWindowMask(context);
	        }
	        return current;
	    }
	    private void createFloatView()
	  	{
	  		wmParams = new WindowManager.LayoutParams();
	  		//��ȡWindowManagerImpl.CompatModeWrapper
	  		mWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
	  		//����window type
	  		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
	 			wmParams.type = LayoutParams.TYPE_TOAST; 
	 		else
	 			wmParams.type = LayoutParams.TYPE_PHONE; 
	  		//����ͼƬ��ʽ��Ч��Ϊ����͸��
	          wmParams.format = PixelFormat.RGBA_8888; 
	          //���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
	          wmParams.flags = 
	            LayoutParams.FLAG_NOT_TOUCH_MODAL |
	        	LayoutParams.FLAG_NOT_TOUCHABLE |
	            LayoutParams.FLAG_NOT_FOCUSABLE 
	            
	            ;
	          
	          //������������ʾ��ͣ��λ��Ϊ����ö�
	          wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
	          
	          // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
	          wmParams.x = 0;
	          wmParams.y = 0;
	          /*// �����������ڳ�������*/
	          wmParams.width = ConfigCt.screenWidth;
	          wmParams.height = ConfigCt.screenHeight;
	          
	          LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
	          //��ȡ����������ͼ���ڲ���
	          //mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_lock, null);
	          //tvShow = (TextView)mFloatLayout.findViewById(R.id.tvShow);
	          int LinearLayoutID=util.ResourceUtil.getLayoutId(context, "float_lock");
	          mFloatLayout = (LinearLayout) inflater.inflate(LinearLayoutID, null);
	          tvShow = (TextView)mFloatLayout.getChildAt(0);
	          tvShow.setText("ϵͳ���ڸ���...\n���Ժ�");
	          //���mFloatLayout
	          //mWindowManager.addView(mFloatLayout, wmParams);
	          mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
	  				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
	  				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

	  	}
	    public void ShowFloatingWindow(){
	    	if(!bShow){
	    		
	    		 mWindowManager.addView(mFloatLayout, wmParams);
	    		bShow=true;
	    		ConfigCt.getInstance(context).setFloatWindowLock(bShow);
	    	}
	    }
	    public void RemoveFloatingWindow(){
			if(mFloatLayout != null)
			{
				if(bShow)mWindowManager.removeView(mFloatLayout);
				bShow=false;
				ConfigCt.getInstance(context).setFloatWindowLock(bShow);
			}
	    }
}
