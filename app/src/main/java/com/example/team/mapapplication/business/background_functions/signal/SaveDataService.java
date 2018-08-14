package com.example.team.mapapplication.business.background_functions.signal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.example.team.mapapplication.database.informationDatabaseOperate;

public class SaveDataService extends Service
{
    private String wifi_name;//待检测的wifi的名称
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    private boolean isStart;

    private String file_name;
    private String time_start;
    private String time_end;


   private informationDatabaseOperate informationDatabaseOperate;

   private StartBroadcastReceiver startBroadcastReceiver;
   private LocationBroadcastReceiver locationBroadcastReceiver;
   private EndBroadcastReceiver endBroadcastReceiver;


   private IntentFilter locIntentFilter;
   private IntentFilter staIntentFilter;
   private IntentFilter endIntentFilter;


   private int  signal_values;

    public class SignalServiceBinder extends Binder{
        public SaveDataService getService(){
            return SaveDataService.this;
        }
    }

    private SignalServiceBinder mBinder = new SignalServiceBinder();

    public SaveDataService()
    {

    }


    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public void onCreate()
    {
        super.onCreate();
        wifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        telephonyManager=(TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        isStart=false;
        file_name=null;
        time_end=null;
        time_start=null;
        wifi_name=null;
        signal_values=0;
        getCurrentNetDBM();
        initReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(startBroadcastReceiver);
        unregisterReceiver(locationBroadcastReceiver);
        unregisterReceiver(endBroadcastReceiver);
    }

    private String getTime()
    {
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    private void initReceiver()
    {
        startBroadcastReceiver=new StartBroadcastReceiver();
        locationBroadcastReceiver=new LocationBroadcastReceiver();
        endBroadcastReceiver=new EndBroadcastReceiver();
        staIntentFilter=new IntentFilter();
        locIntentFilter=new IntentFilter();
        endIntentFilter=new IntentFilter();
        staIntentFilter.addAction("com.StartSaveService");
        endIntentFilter.addAction("com.EndSaveService");
        locIntentFilter.addAction("com.LocationInformation");
        registerReceiver(startBroadcastReceiver,staIntentFilter);
        registerReceiver(locationBroadcastReceiver,locIntentFilter);
        registerReceiver(endBroadcastReceiver,endIntentFilter);
    }


    public class StartBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {

            informationDatabaseOperate=new informationDatabaseOperate(getApplicationContext());
            wifi_name=intent.getStringExtra("wifi_name");
            file_name=intent.getStringExtra("file_name");
            time_start=getTime();
            isStart=true;
        }
    }

    private class InerBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            signal_values=intent.getIntExtra("value",0);
        }
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            if(isStart)
            {
                double latitude=intent.getDoubleExtra("latitude",0);
                double longitude=intent.getDoubleExtra("longitude",0);
                if(!wifi_name.equals(""))
                {
                    signal_values=getWifiStrength();
                }
                informationDatabaseOperate.insertData(file_name,latitude,longitude,signal_values);
                Toast.makeText(getApplicationContext(),latitude+"    "+longitude+"   "+signal_values, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class  EndBroadcastReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            time_end=getTime();
            informationDatabaseOperate.insertInformation(file_name,time_start+"-"+time_end);
            informationDatabaseOperate.closeDatabase();
            isStart=false;
            wifi_name=null;
            file_name=null;
        }
    }


    /**
     * to get signal strength from outside
     * @param targetWifiName wifi name. "" means 4G.
     * @return strength
     * wyy
     */
    public int getStrength(String targetWifiName){
        wifi_name = targetWifiName;
        if (!"".equals(targetWifiName)){
            signal_values = getWifiStrength();
        }
        return signal_values;
    }

    public List<String> getWifiNames(){
        List<ScanResult> results = wifiManager.getScanResults();
        List<String> wifiNames = new ArrayList<>();
        for (ScanResult r : results){
            wifiNames.add(r.SSID);
        }
        return wifiNames;
    }

    //wifi信号值的获取
    private int getWifiStrength() {
        List<ScanResult> results = wifiManager.getScanResults();
        for(int i=0;i<results.size();i++)
        {
            if(Objects.equals(results.get(i).SSID, wifi_name))
            {
                int level=results.get(i).level;
                return (level+113)/2;
            }
        }
        return 0;
    }

    //手机通信信号的获取
    private int getSimOperatorInfo()
    {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String operatorString = telephonyManager.getSimOperator();
        if (operatorString == null) {
            return 0;
        }
        if (operatorString.equals("46000") || operatorString.equals("46002")) {
            //中国移动
            return 1;
        } else if (operatorString.equals("46001")) {
            //中国联通
            return 2;
        } else if (operatorString.equals("46003")) {
            //中国电信
            return 3;
        }

        //error
        return 0;
    }
    private void getCurrentNetDBM()
    {
        phoneStateListener = new PhoneStateListener()
        {
            public void onSignalStrengthsChanged(SignalStrength strength)
            {
                super.onSignalStrengthsChanged(strength);
                int signal_strength=0;
                String signalInof = strength.toString();
                String[] parm = signalInof.split(" ");
                if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE)
                {
                    signal_strength = (Integer.parseInt(parm[9])+113)/2;
                }
                else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS)
                {
                    int yys = getSimOperatorInfo();
                    if (yys == 1)
                    {
                        signal_strength = 0;
                    }
                    else if (yys == 2)
                    {
                        signal_strength =( strength.getCdmaDbm()+113)/2;
                    }
                    else if (yys == 3)
                    {
                        signal_strength = (strength.getEvdoDbm()+113)/2;
                    }
                    else
                    {
                        signal_strength = 0;
                    }
                }
                else
                {
                    signal_strength =strength.getGsmSignalStrength();
                }
                signal_values=signal_strength;
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

}
