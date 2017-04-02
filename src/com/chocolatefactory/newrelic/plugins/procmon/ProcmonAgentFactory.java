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
		System.out.println("Entered ProcmonAgentFactory");
    	
    	if (properties.containsKey("OS")) {
    		this.osType  = (String) properties.get("OS");
    	} else {
    		this.osType = "auto";
    	}

    	if (this.osType.equals("auto")) {
    		this.osType = System.getProperty("os.name").toLowerCase();
    	}
      	
    	if (properties.containsKey("name")) {
    		this.name = (String) properties.get("name");
    	} else {
    		this.name = "auto";
    	}
    	
    	if (this.name.equals("auto")) {
			try {
				name = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	if (properties.containsKey("debug")) {
    		this.debug = (Boolean) properties.get("debug");
    	} else {
    		this.debug = false;
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
        
        return new ProcmonAgent(this.osType, this.name, this.location, this.file, this.debug);
    }
}
