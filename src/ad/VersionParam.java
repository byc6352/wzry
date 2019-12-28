/**
 * 
 */
package ad;

/**
 * @author byc
 *
 */
/**
 * @author byc
 *
 */
public class VersionParam {
	
  	public static String WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
  	public static String WINDOW_LUCKYMONEY_DETAILUI="com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
  	public static String WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
    public static String WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hm";//群标题控件ID
    public static String WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/afn";//红包语控件ID

    public static void init(int version) {
    	if(version >= 1020&&version <= 1180){
    		WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.En_";
    	}else{
    		WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    	}
    
        if(version <= 680) {
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/ew";
        } else if(version <= 700) {
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/cbo";
        }else if (version == 920) {//6.3.30
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/g1";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a5k";
        }else if (version == 940) {//6.3.31
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/g1";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a5k";
        }else if (version == 960) {//6.3.32
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gc";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a4z";
        }else if (version == 980) {//6.5.3
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gd";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a55";
        }else if (version == 1000) {//6.5.4
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gh";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a5t";
        }else if (version == 1020) {//6.5.6
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gh";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a6_";
        }else if (version == 1041) {//6.5.7
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gh";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a6_";
        }else if (version == 1060) {//6.5.8
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gp";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a78";
        }else if (version == 1080) {//6.5.10
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gs";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a8g";
        }else if (version == 1100) {//6.5.13
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/gz";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a9p";
        }else if (version == 1120) {//6.5.16
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/h2";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/a_a";
        }else if (version == 1140) {//6.5.19
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/h2";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ac0";
        }else if (version == 1160) {//6.5.22
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/h5";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/adc";
        }else if (version == 1180) {//6.5.23
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/h5";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/adc";
        }else if (version == 1200) {//6.6.0
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/ha";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/adu";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1220) {//6.6.1
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/ha";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/aea";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1240) {//6.6.2
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hj";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ae9";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1260) {//6.6.3 
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hj";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ae9";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1280) {//6.6.5
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hj";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ae9";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1300) {//6.6.6
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hn";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ae9";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1320) {//6.6.7
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/hm";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/afn";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1340) {//6.7.2
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/j1";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/ai4";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1360) {//6.7.3
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/j6";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/alv";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
        }else if (version == 1380) {//7.0.0
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/jw";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/apd";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
        }else if (version == 1400) {//7.0.3
        	WIDGET_ID_GROUP_TITLE = "com.tencent.mm:id/k3";
        	WIDGET_ID_LUCKYMONEY_SAY = "com.tencent.mm:id/aq5";
        	WINDOW_LUCKYMONEY_RECEIVEUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
        }
        
        
    }
}