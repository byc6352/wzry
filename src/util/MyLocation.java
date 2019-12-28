/**
 * 
 */
package util;



import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import order.OrderService;
import order.order;

/**
 * @author byc
 *
 */
public class MyLocation implements LocationListener {
	private final int LOCATION_TIME_INTERVAL=10000;
	private  String TAG = "byc001";  
	private final int LOCATION_ID=1001;
	private static LocationManager mLocationManager=null; 
    private Criteria mCriteria=null;
    private String mBestProvider=null;
    private Context context;
    public LocationInfo locationInfo=null;
    
    private static MyLocation current;
    public static synchronized MyLocation getMyLocation(Context context) {
        if(current == null) {
            current = new MyLocation(context);
        }
        return current;
    }
    public MyLocation(Context context){
    	this.context=context;
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE); 
        locationInfo=new LocationInfo();
        initLocationInfo();
    }
    /*
     * ��ȡ���ζ�λ���񣻣�
     */
    public void GetSingleLocationThread(){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {    
				//Looper.prepare();
				try{
					GetSingleLocation();
				}catch(ActivityNotFoundException e){
					e.printStackTrace();
				}
				//Looper.loop(); 
		    }    
		}).start();
    }
    /*
     * ��ȡ���ζ�λ����
     */
    public LocationInfo GetSingleLocation() { 
    	mCriteria=SetCriteria();
    	mBestProvider=GetBestProvider(mCriteria);
    	locationInfo.provider=mBestProvider;
    	if(mBestProvider==null)return null;
    	locationInfo.provider=mBestProvider;
    	Location location = getBestLocation(mLocationManager);// ÿ�ζ�ȥ��ȡGPS_PROVIDER���ȵ� 
    	if(location==null)return null;
    	locationInfo.dX=location.getLongitude();
    	locationInfo.dY=location.getLatitude();
    	locationInfo.suc=1;
    	ConfigCt.getInstance(context).setLocatePermission(true);
    	return locationInfo;
    }
    @Override  
    public void onLocationChanged(Location location) { 
    	location = getBestLocation(mLocationManager);// ÿ�ζ�ȥ��ȡGPS_PROVIDER���ȵ� 
    	if(location==null)return;
    	locationInfo.dX=location.getLongitude();
    	locationInfo.dY=location.getLatitude();
    	//String body=locationInfo.provider+"("+locationInfo.dX+","+locationInfo.dY+")";
    	//SmsReceiver.SendSms(locationInfo.info,body);
    	//OrderService.getOrderService().SendBaseInfo(order.CMD_LOCATION_SINGLE, body);
        SendInfo("Get the current position \n" + location);//֪ͨActivity 
        SendInfo("Get the current position \n" + locationInfo.dX+","+locationInfo.dY);//֪ͨActivity 
        //�����̸߳�handler������Ϣ
        //SocketHandler.sendEmptyMessage( 1 ) ;
        this.Stop();
    } 
    @Override  
    public void onProviderDisabled(String provider) {  
         
        SendInfo("LocationSvc onProviderDisabled");//֪ͨActivity 
    }  
  
    @Override  
    public void onProviderEnabled(String provider) {  

        SendInfo("LocationSvc onProviderEnabled");//֪ͨActivity 
    }  
  
    @Override  
    public void onStatusChanged(String provider, int status, Bundle extras) { 
         
        SendInfo("LocationSvc onStatusChanged");//֪ͨActivity 
    } 
    /*
     * ��ȡ���ж�λ����
     */
    public List<String> getAllProviders() {
        List<String> list=mLocationManager.getAllProviders();  
        for(String p:list)  
        {   
        	SendInfo(p);
        }
        return list;
    }
    /*
     * ��ȡ���Ŷ�λ�ַ�����
     * @���Ŷ�λ�ַ���
     */
    public String GetBestProvider(Criteria criteria) {
    	if(mLocationManager==null||criteria==null)return null;
    	String provider=mLocationManager.getBestProvider(criteria, false);
    	boolean b=false;
    	if(provider.equals(LocationManager.GPS_PROVIDER))b=true;
    	if(provider.equals(LocationManager.NETWORK_PROVIDER))b=true;
    	if(provider.equals(LocationManager.PASSIVE_PROVIDER))b=true;
    	if(b)return provider;else return null;
    }
    /*
     * ����������
     * @Criteria//�ڶ�����������Ϊfalseʱ�����ܵ�ǰ���Ǹ�provider�Ƿ���ã�����Ҫ���в��ң�������������Ϊ����  
     */
    public Criteria SetCriteria() {  
    	  Criteria criteria=new Criteria();  
          criteria.setAccuracy(Criteria.ACCURACY_FINE);  
          criteria.setPowerRequirement(Criteria.POWER_LOW);  
          criteria.setAltitudeRequired(false);  
          criteria.setSpeedRequired(false);  
          criteria.setCostAllowed(false); 
          return criteria;
    }
    /*
     * ������λ����
     * @true������ɹ���false:�ֻ�û��������λ����
     */
    public boolean Start() {
    	mCriteria=SetCriteria();
    	mBestProvider=GetBestProvider(mCriteria);
    	locationInfo.provider=mBestProvider;
    	if(mBestProvider==null)return false;
    	return RequestLoaction(mBestProvider);   	
    }
    /*
     * ֹͣ��λ����
     * @
     */
    public void Stop() {
    	mLocationManager.removeUpdates(this); 	
    	mCriteria=null;
    	mBestProvider=null;
    	initLocationInfo();
    }
    /*
     * ����λ����
     * @true������ɹ���false:�ֻ�û��������λ����
     */
    public boolean RequestLoaction(String provider) {
    	try{
    		mLocationManager.requestLocationUpdates(provider, LOCATION_TIME_INTERVAL, 0,this);
    		locationInfo.suc=1;
    		return true;
    	}catch (IllegalArgumentException e) {
			e.printStackTrace();
			locationInfo.suc=0;
			locationInfo.info=e.getMessage();
			return false;
		} catch (RuntimeException e) {
			e.printStackTrace();
			locationInfo.suc=0;
			locationInfo.info=e.getMessage();
			return false;
		} 
    }
    /** 
     * ��ȡlocation����������GPS_PROVIDER��ȡlocation���󣬵���GPS_PROVIDER��ȡ����locaitonΪnullʱ 
     * ������NETWORK_PROVIDER��ȡlocation���������ɱ�֤�����ڿ����������ӵ�״̬�»�ȡ����location����Ϊ�� 
     *  
     * @param locationManager 
     * @return 
     */  
    private Location getBestLocation(LocationManager locationManager) {  
        Location result = null;  
        if (locationManager != null) {  
            result = locationManager  
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);  
            if (result != null) { 
            	locationInfo.provider=LocationManager.GPS_PROVIDER;
                return result;  
            } else {  
                result = locationManager  
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
                locationInfo.provider=LocationManager.NETWORK_PROVIDER;
                return result;  
            }  
        }  
        return result;  
    }
    /*
     * //֪ͨActivity 
     */
    public void SendInfo(String info){
         
    	Log.d(TAG, info);
        Intent intent = new Intent();  
        //intent.setAction(Config.ACTION_SERVICE_INFO);  
        //intent.putExtra(Config.SERVICE_INFO_LOCATION, info);  
        //context.sendBroadcast(intent); 
    }
    /*
     * // 
     */
    public boolean SendLocation(Location location){
         if(location==null)return false;
    	Log.d(TAG, location.toString());
        //Intent intent = new Intent();  
        //intent.setAction(LocationSvc.ACTION_LOCATION);  
        //intent.putExtra(LocationSvc.LOCATION_MEMBER_X, location.getLongitude());  
        //intent.putExtra(LocationSvc.LOCATION_MEMBER_Y, location.getLatitude());
        //context.sendBroadcast(intent);
        return true;
    }
    /*
     * ��ȡ���ζ�λ����
     */
    public static boolean isGpsOpen() { 
    	return mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
    /*
     * ��ʼ��
     */
    public void initLocationInfo(){
    	locationInfo.ID=LOCATION_ID;
    	locationInfo.suc=0;
    	locationInfo.dX=0;
    	locationInfo.dY=0;
    	locationInfo.info=null;
    	locationInfo.provider=null;
    }
    public class LocationInfo{
    	public int ID=LOCATION_ID;
    	public int suc=0;
    	public double dX=0;
    	public double dY=0;
    	public String provider=null;
    	public String info=null;
    }
}
