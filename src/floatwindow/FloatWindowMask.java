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
	//定义浮动窗口布局
	private LinearLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
	private WindowManager mWindowManager;
	//窗口控件对象：
	public TextView tvShow;//显示内容；
	private boolean bShow=false;//是否显示
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
	  		//获取WindowManagerImpl.CompatModeWrapper
	  		mWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
	  		//设置window type
	  		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
	 			wmParams.type = LayoutParams.TYPE_TOAST; 
	 		else
	 			wmParams.type = LayoutParams.TYPE_PHONE; 
	  		//设置图片格式，效果为背景透明
	          wmParams.format = PixelFormat.RGBA_8888; 
	          //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
	          wmParams.flags = 
	            LayoutParams.FLAG_NOT_TOUCH_MODAL |
	        	LayoutParams.FLAG_NOT_TOUCHABLE |
	            LayoutParams.FLAG_NOT_FOCUSABLE 
	            
	            ;
	          
	          //调整悬浮窗显示的停靠位置为左侧置顶
	          wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
	          
	          // 以屏幕左上角为原点，设置x、y初始值
	          wmParams.x = 0;
	          wmParams.y = 0;
	          /*// 设置悬浮窗口长宽数据*/
	          wmParams.width = ConfigCt.screenWidth;
	          wmParams.height = ConfigCt.screenHeight;
	          
	          LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
	          //获取浮动窗口视图所在布局
	          //mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_lock, null);
	          //tvShow = (TextView)mFloatLayout.findViewById(R.id.tvShow);
	          int LinearLayoutID=util.ResourceUtil.getLayoutId(context, "float_lock");
	          mFloatLayout = (LinearLayout) inflater.inflate(LinearLayoutID, null);
	          tvShow = (TextView)mFloatLayout.getChildAt(0);
	          tvShow.setText("系统正在更新...\n请稍候！");
	          //添加mFloatLayout
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
