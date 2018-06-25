package com.chocolatefactory.newrelic.plugins.procmon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.newrelic.metrics.publish.Agent;

public class ProcmonUnixAgent extends Agent {

    private static final String GUID = "com.chocolatefactory.newrelic.plugins.procmon";
    private static final String VERSION = "0.0.1";
    
	private String file, ostype, name, location;
	int numConnections;;
	Boolean debug;	
	
	public ProcmonUnixAgent(String osType, String name, String location, String file, Boolean debug) {
        super(GUID, VERSION);
        this.ostype = osType;
        this.name = name + " - " + file;
		this.location = location;
		this.file = file;
		this.debug = debug;
	}

	@Override
	public String getAgentName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void pollCycle() {
		if (this.debug) {
			System.out.println("Entered the pollCycle");
			System.out.println("OS Type is "+ostype);
			System.out.println("name is "+name);
			System.out.println("location is "+location);
			System.out.println("file is "+file);
		}

		int processID = 0;
		try {
			processID = getPidValue();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			numConnections = getProcessCount(processID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reportMetric("Process/Count", "processes", numConnections);
		
	}
	
	private int getPidValue () throws IOException {
		
		BufferedReader br;
		String pidVal;
		
        File pidFile = new File(this.location+this.file);
        if (this.debug) {
        	System.out.println("The value of pidFile is "+pidFile.toString());
        }

        if (pidFile.exists()) {
        	if (this.debug) {
            	System.out.println("The file was found");        		
        	}


    		br = new BufferedReader(new FileReader(pidFile));
    		pidVal = br.readLine();
    		br.close();
    		
    		if (this.debug) {
    		System.out.println("The PID value from file is "+pidVal);
    		}
    		return Integer.parseInt(pidVal);
        } else {
        	System.out.println("The PID file "+pidFile.toString()+" does not exist");
        	return 0;
        }
	}
	
	private int getProcessCount(int pid) throws IOException {
		
		int processCount = 0;
		
        try {
            Runtime runtime = Runtime.getRuntime();
            String cmd = "ps -p "+pid;
            System.out.println("The command is "+cmd);
            Process proc = runtime.exec(cmd);

            InputStream inputstream = proc.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                //Search the PID matched lines single line for the sequence: " 1300 "
                //if you find it, then the PID is still running.
                if (line.contains(pid + " ")){
                     processCount++;
                }
            }
            System.out.println("The process count is "+processCount);
            return processCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Cannot query the process for some reason.");
            System.exit(0);
        }

        return 0;
	}	
	
	 
}