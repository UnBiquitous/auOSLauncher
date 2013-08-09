package org.unbiquitous.auoslauncher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unbiquitous.driver.execution.ExecutionDriver;
import org.unbiquitous.driver.execution.executeAgent.ClassToolbox;
import org.unbiquitous.driver.execution.executeAgent.GatewayMap;
import org.unbiquitous.uos.core.ClassLoaderUtils;
import org.unbiquitous.uos.core.ContextException;
import org.unbiquitous.uos.core.UOS;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.network.socket.connectionManager.EthernetTCPConnectionManager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import dalvik.system.DexClassLoader;

public class LaunchActivity extends Activity {
	
	private static final Logger logger = Logger.getLogger(LaunchActivity.class.getName());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
//		logger.log(Level.INFO,		"This works for info");
//		logger.log(Level.OFF,		"This works off");
//		logger.log(Level.SEVERE,	"This works severe");
//		logger.log(Level.WARNING,	"This works for warning");
        
		new StartMiddlewareTask().execute();
//		propertyList();
//        Collector collector = new Collector(getApplicationContext(),this);
//        Map<String, Object> prop = collector.collectData();
//        for (Object key:prop.keySet()){
//        	print(prop, key.toString());
//		}
//       logger.log(Level.INFO,"java.vm.name:"+System.getProperty("java.vm.name"));
//        testReadFile();
//        testLoadAClass("hello_not_class", "org.unbiquitous.driver.spike.HelloNotAgent");
//        testLoadAClass("auos.exe_spiker.apk", "org.unbiquitous.driver.execution.spike.HelloFromAndroidAgent");
//        testLoadAClass("auos.exe_spiker.jar", "org.unbiquitous.driver.execution.spike.HelloFromAndroidAgent");
        
//        testLoadAnObject("hello_not_class","hello_not_obj");
        
    }
    
    private static void propertyList(){
		final Properties properties = System.getProperties();
		for (Object key:properties.keySet()){
			printProp(properties, key.toString());
		}
	    System.out.print("Total CPU:");
	    System.out.println(Runtime.getRuntime().availableProcessors());
	    System.out.println("Max Memory:" + Runtime.getRuntime().maxMemory() + "\n" + "available Memory:" + Runtime.getRuntime().freeMemory());
	    System.out.println("os.name=" + System.getProperty("os.name"));
	}
    
    private static void print(Map properties, final String prop) {
		System.out.println(prop+":"+properties.get(prop));
	}
    private static void printProp(final Properties properties, final String prop) {
		System.out.println(prop+":"+properties.getProperty(prop));
	}

    private void testReadFile(){
    	try {
			InputStream raw = getApplicationContext().getAssets().open("teste");
			BufferedReader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
			logger.log(Level.INFO,is.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void testLoadAClass(String apk_file, String class_name, Gateway gateway){
    	try {
//			setupClassloaderToolbox();
			InputStream hello_class = getApplicationContext().getAssets().open(apk_file);
			
			ClassToolbox toolbox = new ClassToolbox();
			final ClassLoader loader = toolbox.load(new DataInputStream(hello_class));
			Class<?> clazz = loader.loadClass(class_name);
			InputStream hello_obj = getApplicationContext().getAssets().open("HelloFromAndroidAgent_obj");
			
			ObjectInputStream reader = new ObjectInputStream(hello_obj){
				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					try{
						return loader.loadClass(desc.getName());
					}catch(Exception e){
						return super.resolveClass(desc);
					}
				}
			};
			
			Object o = reader.readObject();
//			Object o = clazz.newInstance();
			for(Method m :o.getClass().getMethods()){
				if (m.getName().equals("run")){
					String parameters = "Run Parameters:";
					for (Class c : m.getParameterTypes()){
						parameters += ","+c;
					}
					System.out.println(parameters);
//					m.invoke(o, createStrangeGateway());
				}
			}
			System.out.println("the oher");
			Method run = o.getClass().getMethod("run", Map.class);
			run.invoke(o, new GatewayMap(gateway));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private class StartMiddlewareTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() { }

        @Override
        protected Void doInBackground(Void... params) {
        	ResourceBundle prop = new ListResourceBundle() {
    			protected Object[][] getContents() {
    				return new Object[][] {
    					{"ubiquitos.connectionManager", EthernetTCPConnectionManager.class.getName() },
    					{"ubiquitos.eth.tcp.port", "14984"},
    					{"ubiquitos.eth.tcp.passivePortRange", "14985-15000"},
    					{"ubiquitos.eth.udp.port", "15001"},
    					{"ubiquitos.eth.udp.passivePortRange", "15002-15017"},
    					{"ubiquitos.uos.deviceName", "aUosDevice"}, //TODO: Should not be mandatory, and could be automatic
    					{"ubiquitos.driver.deploylist", ExecutionDriver.class.getName()}, 
    		        };
    			}
    		};
    		
//    		setupClassloaderToolbox();
    		
			ClassLoaderUtils.builder = new ClassLoaderUtils.DefaultClassLoaderBuilder(){
				public ClassLoader getParentClassLoader() {
					return getClassLoader();
				};
			};
			
			GatewayMap dummy = new GatewayMap(null);
			dummy.put("context",getApplicationContext()); 
			dummy.put("activity",this);
			
    		UOS ctx = new UOS();
    		try {
    			ctx.init(prop);
    		} catch (ContextException e) {
    			logger.log(Level.SEVERE,	"This was severe", e);
    		}
            
    		testLoadAClass("auos.exe_spiker.apk", 
    				"org.unbiquitous.driver.execution.spike.HelloFromAndroidAgent",
    				ctx.getGateway());
    		
    		return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
    }
    
    private void setupClassloaderToolbox() {
		ClassToolbox.platform = new ClassToolbox.Platform() {
			@Override
			protected File createTempDir() throws Exception {
				File writableDir = getApplicationContext().getDir("temp", Context.MODE_WORLD_WRITEABLE);
				File tempDir = File.createTempFile("uExeTmp", ""+System.nanoTime(),writableDir);
				tempDir.delete(); // Delete temp file
				tempDir.mkdir();  // and transform it to a directory
				return tempDir;
			}
			
			@Override
			protected ClassLoader createClassLoader(File input) throws Exception {
				File folder = input.getParentFile();
				ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
//				CLassLoader parent = getClassLoader();
				return new DexClassLoader(input.getPath(),folder.getPath(),null, parent);
//				JarClassLoader classloader = new JarClassLoader();
//				classloader.add(input.getPath());
//				JclObjectFactory factory = JclObjectFactory.getInstance(true);
//				logger.log(Level.INFO, "obj: "+
//						factory.create(classloader, "org.unbiquitous.driver.spike.HelloAgent")
//				);
//				return classloader;
			}
		};
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_launch, menu);
        return true;
    }
}
