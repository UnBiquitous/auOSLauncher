package org.unbiquitous.auoslauncher;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.adaptabitilyEngine.NotifyException;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.driverManager.UosEventDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpService.ParameterType;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Notify;
import org.unbiquitous.uos.core.messageEngine.messages.Response;
import org.unbiquitous.uos.core.network.model.NetworkDevice;


public class PrintDriver implements UosEventDriver{

	private Set<UpDevice> devices = new HashSet<UpDevice>();
	
	public UpDriver getDriver() {
		UpDriver driver = new UpDriver("example.Print");
		driver.addService("print")
				.addParameter("text", ParameterType.MANDATORY);
		return driver;
	}

	public List<UpDriver> getParent() {
		return null;
	}

	public void init(final Gateway gateway, InitialProperties properties, String instanceId) {
		new Thread(new Runnable() {
			public void run() {
				while(true){
					if (!devices.isEmpty()){
						sendEvents(gateway);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}
	
	private void sendEvents(Gateway gateway){
		System.out.println("Event sender Mode ON");
		Scanner scanner = new Scanner(System.in);
		String text = "Dummy";
		Notify event = new Notify("echo");
		event.setDriver("example.Print");
		while(text != null && !text.isEmpty()){
			System.out.println("Send text ?");
			text = scanner.nextLine();
			event.addParameter("text", text);
			try {
				for(UpDevice device : devices){
					gateway.notify(event,device);
				}
			} catch (NotifyException e) {
				throw new RuntimeException(e);
			}
		}
		System.out.println("Event sender Mode OFF");
	}

	public void destroy() {}

	public void print(Call call, Response response, CallContext ctx) {
		String message = call.getParameterString("text");
		if (ctx.getCallerDevice() != null){
			message = String.format("%s: %s", ctx.getCallerDevice().getName(), 
									message);
		}
		System.out.println(message);
	}

	public void registerListener(Call serviceCall,
			Response serviceResponse, CallContext messageContext) {
		NetworkDevice callerDevice = messageContext.getCallerNetworkDevice();
		UpDevice d = new UpDevice("DummyGuy")
			.addNetworkInterface(
					callerDevice.getNetworkDeviceName(), 
					callerDevice.getNetworkDeviceType()
				);
		devices.add(d);
	}

	public void unregisterListener(Call serviceCall,
			Response serviceResponse, CallContext messageContext) {
		// TODO Auto-generated method stub
		
	}

}
