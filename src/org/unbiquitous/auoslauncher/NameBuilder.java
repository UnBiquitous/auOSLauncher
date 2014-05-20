package org.unbiquitous.auoslauncher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;

public class NameBuilder {
	private Activity main;
	
	public NameBuilder(Activity main) {
		this.main = main;
	}

	public String getName(){
		String name = getBTName();
		if (name == null){
			name = getWifiName();
			if (name == null){
				name = Secure.getString(main.getContentResolver(), Secure.ANDROID_ID);
			}
		}
		return name;
	}

	private String getWifiName() {
		String macAddress = macAddress();
		if (macAddress != null){
			return namePrefix() + macAddress.replace(":", "");
		}
		return null;
	}

	private String namePrefix() {
		return noBlank(Build.MANUFACTURER) +'-'+ noBlank(Build.MODEL);
	}

	private String macAddress() {
		WifiManager manager = (WifiManager) main.getSystemService(Context.WIFI_SERVICE);
		String mac = null;
		if (manager != null){
			WifiInfo info = manager.getConnectionInfo();
			mac = info.getMacAddress();
		}
		return mac;
	}

	private String getBTName() {
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		String name = null;
		if (bt != null){
			name = bt.getName();
		}
		return name;
	}
	
	private String noBlank(String original){
		return original.replace(" ", "");
	}
}
