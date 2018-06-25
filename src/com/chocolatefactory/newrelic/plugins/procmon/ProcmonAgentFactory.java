package com.chocolatefactory.newrelic.plugins.procmon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class ProcmonAgentFactory extends AgentFactory {

	private String osType, name, file, location = null;
	private Boolean debug;

	@Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
    	
    	if (properties.containsKey("debug")) {
    		this.debug = (Boolean) properties.get("debug");
    	} else {
    		this.debug = false;
    	}
		
    	if (this.debug) {
			System.out.println("Entered ProcmonAgentFactor.\n");
		}
    	
    	if (properties.containsKey("OS")) {
    		this.osType  = (String) properties.get("OS");
    	} else {
    		this.osType = "auto";
    	}

    	if (this.debug) {
    		System.out.print("About to get the OS Type.\n");
    	}
    	
    	if (this.osType.equals("auto")) {
    		this.osType = System.getProperty("os.name").toLowerCase();
    		System.out.println("The OS Type value is "+osType);
    	}
      	
    	if (properties.containsKey("name")) {
    		this.name = (String) properties.get("name");
    	} else {
    		this.name = "auto";
    	}
    	
    	if (this.debug) {
    		System.out.print("About to get the System name.\n");
    	}
    	
    	if (this.name.equals("auto")) {
			try {
				this.name = InetAddress.getLocalHost().getHostName();
				if (this.debug) {
					System.out.println("The system name value returned is "+this.name);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

		if (properties.containsKey("PID_Location")) {
			this.location = (String) properties.get("PID_Location");
		} else {
            throw new ConfigurationException("Property 'PID_Location' must be specified in plugin.json and cannot be null.");
		}
		
		if (properties.containsKey("PID_File")) {
			this.file = (String) properties.get("PID_File");
		} else {
            throw new ConfigurationException("Property 'PID_File' must be specified in plugin.json and cannot be null.");
		}


		// Determine which OS to monitor
		if(this.osType.contains("mac os") || this.osType.contains("linux") || osType.contains("mac os")){
			System.out.println("We are running in a Unix system");
    		return new ProcmonUnixAgent(this.osType, this.name, this.location, this.file, this.debug);
		} else {
			if (this.osType.toLowerCase().contains("win")) {
					System.out.println("We are running in a Windows system");
					return new ProcmonWindowsAgent(this.osType, this.name, this.location, this.file, this.debug);
			} else {
				System.out.println("Unsupported OS");
				return null;
			}
		}
	}
}
