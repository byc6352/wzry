/**
 * 
 */
package notification;



/**
 * @author ASUS
 *
 */
public interface NotificationJob {
	String[] getTargetPackageName();//获取要处理的包；
	boolean isTargetPackageName(String pkg);//是否是目标包；
	void setTargetPackageName(String[] pkgs);//设置要处理的包；
	boolean isEnable();//是否可用；
    void onCreateJob(QHBNotificationService service);//创建工作；
    void onStopJob();//停止工作；
    void onReceiveJob(IStatusBarNotification mysbn);//事件驱动；
    void EventStart();//开始事件处理；
    void closeEventWorking();//关闭事件处理；
}
