package org.unbiquitous.auoslauncher;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.unbiquitous.driver.execution.executeAgent.ClassToolbox;
import org.unbiquitous.driver.execution.executeAgent.GatewayMap;
import org.unbiquitous.uImpala.dalvik.impl.core.Game;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uos.core.UOSLogging;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import dalvik.system.DexClassLoader;

public class LaunchActivity extends Activity {
	
	static final Logger logger = UOSLogging.getLogger();
	private View.OnClickListener startListener, stopListener;
	private StartStopMiddlewareTask middlewareTask;
	private Button startStopButton;
	private View spinner;
	private Runnable consoleUpdater;
	private TextView fakeConsole;
	private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        runMiddlewareDebugger();
        runGameDebugger();
    }

    private void runGameDebugger(){
    	Game.run(new GameSettings() {
			{ // TODO: Game Settings could have helper methods
				put("main_activity", LaunchActivity.this);
				put("first_scene", GameMenu.class);
				put("input_managers", Arrays.asList(MouseManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
			}
		});
    }
    
	private void runMiddlewareDebugger() {
		setContentView(R.layout.activity_launch);
        
        spinner = LaunchActivity.this.findViewById(R.id.middlewareStartSpinner);
        
        setName();
        setMiddlewareTask();
		setFakeConsole();
		setLogger();
	}

	private void setName() {
		name = new NameBuilder(this).getName();
        TextView title = (TextView) findViewById(R.id.title);
		title.setText(name);
	}
    
    

	private void setMiddlewareTask() {
		middlewareTask = new StartStopMiddlewareTask(this,name);
        startStopButton = (Button) this.findViewById(R.id.startStopMiddleware);
        startListener = new View.OnClickListener() {
			public void onClick(View v) {
				fakeConsole.setText("");
				spinner.setVisibility(View.VISIBLE);
				startStopButton.setVisibility(View.INVISIBLE);
				middlewareTask.run();
			}
		};
		stopListener = new View.OnClickListener() {
			public void onClick(View v) {
				spinner.setVisibility(View.VISIBLE);
				startStopButton.setVisibility(View.INVISIBLE);
				middlewareTask.run();
			}
		};
		startStopButton.setOnClickListener(startListener);
	}

	private void setFakeConsole() {
		fakeConsole = (TextView) LaunchActivity.this.findViewById(R.id.fake_console);
		fakeConsole.setMovementMethod(ScrollingMovementMethod.getInstance());
		fakeConsole.setScrollbarFadingEnabled(true);
		consoleUpdater = new Runnable() {
			
			int lineCount(){
				int count = 1;
				CharSequence text = fakeConsole.getText();
				for (int i =0 ; i < text.length(); i ++){
					if (text.charAt(i) == '\n') count ++;
				}
				return count;
			}
			
			public void run() {
				int totalSize = fakeConsole.getLineHeight()*lineCount();
				int scrollValue = totalSize - fakeConsole.getHeight();
				fakeConsole.scrollTo(0, scrollValue);
			}
		};
		fakeConsole.post(consoleUpdater);
	}
    
	private void setLogger() {
		Formatter logFormatter = new Formatter() {
			public String format(LogRecord record) {
				String baseMessage = String.format("%s (%s.%s) :\n %s",
						record.getLevel(), record.getSourceClassName(),
						record.getSourceMethodName(), record.getMessage());

				if (record.getThrown() != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					record.getThrown().printStackTrace(pw);
					baseMessage += sw.toString() + '\n';
				}
				return baseMessage;
			}
		};
		Handler logHandler = new Handler() {
			public void publish(final LogRecord record) {
				runOnUiThread(new Runnable() {
					public void run() {
						final TextView fakeConsole = (TextView) LaunchActivity.this.findViewById(R.id.fake_console);
						fakeConsole.setText(fakeConsole.getText()+"\n"
											+getFormatter().format(record));
						fakeConsole.post(consoleUpdater);
					}
				});
			}
			
			public void flush() {}
			public void close() {}
		};
		logHandler.setFormatter(logFormatter);
		logger.addHandler(logHandler);
	}
	
    public void middlewareStarted(){
    	runOnUiThread(new Runnable() {
			public void run() {
				startStopButton.setOnClickListener(stopListener);
				spinner.setVisibility(View.INVISIBLE);
				startStopButton.setText(R.string.stop_middleware);
				startStopButton.setVisibility(View.VISIBLE);
			}
		});
    }
    
    public void middlewareStopped(){
    	runOnUiThread(new Runnable() {
			public void run() {
				startStopButton.setOnClickListener(startListener);
				startStopButton.setText(R.string.start_middleware);
				spinner.setVisibility(View.INVISIBLE);
				startStopButton.setVisibility(View.VISIBLE);
			}
		});
    }
    
    void testLoadAClass(String apk_file, String class_name, Gateway gateway){
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
