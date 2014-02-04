package org.unbiquitous.auoslauncher;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.unbiquitous.uos.core.ClassLoaderUtils;
import org.unbiquitous.uos.core.ContextException;
import org.unbiquitous.uos.core.UOS;
import org.unbiquitous.uos.network.socket.connectionManager.TCPConnectionManager;
import org.unbiquitous.uos.network.socket.radar.MulticastRadar;

import android.os.AsyncTask;

class StartStopMiddlewareTask  {

		private final LaunchActivity mainActivity;
		private UOS middleware;
		private ResourceBundle properties;
		private AsyncTask<Void, Void, Void> task = new StartTask();

		StartStopMiddlewareTask(LaunchActivity mainActivity, final String name) {
			this.mainActivity = mainActivity;
			
			properties = new ListResourceBundle() {
    			protected Object[][] getContents() {
    				return new Object[][] {
    					{"ubiquitos.connectionManager", TCPConnectionManager.class.getName() },
    					{"ubiquitos.radar",MulticastRadar.class.getName()},
    					{"ubiquitos.eth.tcp.port", "14984"},
    					{"ubiquitos.eth.tcp.passivePortRange", "14985-15000"},
    					{"ubiquitos.eth.udp.port", "15001"},
    					{"ubiquitos.eth.udp.passivePortRange", "15002-15017"},
    					{"ubiquitos.uos.deviceName", name}, 
    					{"ubiquitos.driver.deploylist", 
//    						ExecutionDriver.class.getName()+";"+
    						PrintDriver.class.getName()}, 
    		        };
    			}
    		};
    		
    		
			ClassLoaderUtils.builder = new ClassLoaderUtils.DefaultClassLoaderBuilder(){
				public ClassLoader getParentClassLoader() {
					return StartStopMiddlewareTask.this.mainActivity.getClassLoader();
				};
			};
			
		}

		protected void onPreExecute() { }

        protected void run() {
        	task.execute();
        }

        class StartTask extends AsyncTask<Void, Void, Void>{
        	protected Void doInBackground(Void... params) {
        		try {
    				middleware = new UOS();
    				middleware.init(properties);
    				mainActivity.middlewareStarted();
    				task = new StopTask();
    			} catch (ContextException e) {
    				LaunchActivity.logger.log(Level.SEVERE,	"This was severe", e);
    			}
        		return null;
        	}
        }
        
        class StopTask extends AsyncTask<Void, Void, Void>{
        	protected Void doInBackground(Void... params) {
        		try {
    				middleware.tearDown();
    				middleware = null;
    				mainActivity.middlewareStopped();
    				task = new StartTask();
    			} catch (ContextException e) {
    				LaunchActivity.logger.log(Level.SEVERE,	"This was severe", e);
    			}
        		return null;
        	}
        }
    }