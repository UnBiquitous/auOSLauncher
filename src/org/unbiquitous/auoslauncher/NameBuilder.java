package org.unbiquitous.auoslauncher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class NameBuilder {
	private Activity main;
	
	public NameBuilder(Activity main) {
		this.main = main;
	}

	public String getName(){
		String name = getBTName();
		if (name != null){
			return name;
		}else{
			return getWifiName();
		}
	}

	private String getWifiName() {
		return namePrefix() + macAddress().replace(":", "");
	}

	private String namePrefix() {
		return noBlank(Build.MANUFACTURER) +'-'+ noBlank(Build.MODEL);
	}

	private String macAddress() {
		WifiManager manager = (WifiManager) main.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String mac = info.getMacAddress();
		return mac;
	}

	private String getBTName() {
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		String name = bt.getName();
		return name;
	}
	
	private String noBlank(String original){
		return original.replace(" ", "");
	}
}
