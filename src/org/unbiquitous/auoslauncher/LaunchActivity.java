package org.unbiquitous.auoslauncher;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import br.unb.unbiquitous.ubiquitos.uos.context.ContextException;
import br.unb.unbiquitous.ubiquitos.uos.context.UOSApplicationContext;

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
    }

    private class StartMiddlewareTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() { }

        @Override
        protected Void doInBackground(Void... params) {
        	ResourceBundle prop = new ListResourceBundle() {
    			protected Object[][] getContents() {
    				return new Object[][] {
//    					{"ubiquitos.message.response.timeout", "100"}, //Optional
//    					{"ubiquitos.message.response.retry", "30"},//Optional
    					{"ubiquitos.connectionManager", "br.unb.unbiquitous.ubiquitos.network.ethernet.connectionManager.EthernetTCPConnectionManager"},
    					{"ubiquitos.eth.tcp.port", "14984"},
    					{"ubiquitos.eth.tcp.passivePortRange", "14985-15000"},
    					{"ubiquitos.eth.udp.port", "15001"},
    					{"ubiquitos.eth.udp.passivePortRange", "15002-15017"},
    					{"ubiquitos.uos.deviceName", "aUosDevice"}, //TODO: Should not be mandatory, and could be automatic
//    					{"ubiquitos.persistence.hsqldb.database", "db_"+deviceName}, 
    					{"ubiquitos.driver.deploylist", 
    						"br.unb.unbiquitous.ubiquitos.uos.driver.DeviceDriverImpl," +
    						"org.unbiquitous.driver.execution.ExecutionDriver"}, 
//    					{"ubiquitos.ontology.path","resources/owl/uoscontext.owl"}, //Can't have ontology, otherwise everything goes caputz
    		        };
    			}
    		};
    		
    		UOSApplicationContext ctx = new UOSApplicationContext();
    		try {
    			ctx.init(prop);
    		} catch (ContextException e) {
    			logger.log(Level.SEVERE,	"This was severe", e);
    		}
                
                return null;
        }

        @Override
        protected void onPostExecute(Void result) {}
}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_launch, menu);
        return true;
    }
}
