/*
 *
 *  Copyright (C) 2017 Aaron Powers
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package donald.doe2_runner;

import java.io.IOException;
import java.util.ArrayList;

import otis.lexical.UpdateListener;

public class DOE2Run implements Runnable {

	private String inpFileName;
	private String location;
	private ArrayList<UpdateListener> updateListeners;
	private ArrayList<DOE2SimCompleteListener> simulationCompleteListeners;
	private BDLErrorListener errorListener;
	private boolean debugMode;

	public DOE2Run(String inpFileName, String location, boolean debugMode){
		this.inpFileName = inpFileName;
		this.location = location;
		updateListeners = new ArrayList<UpdateListener>();
		simulationCompleteListeners = new ArrayList<DOE2SimCompleteListener>();
		errorListener = new BDLErrorListener();
		updateListeners.add(errorListener);
		this.debugMode = debugMode;
		
	}

	public void addUpdateListener(UpdateListener updateListener){
		updateListeners.add(updateListener);
	}
	
	public void addSimulationCompleteListener(DOE2SimCompleteListener simulationCompleteListener){
		simulationCompleteListeners.add(simulationCompleteListener);
	}

	@Override
	public void run() {
		
		try {
			Runtime runtime = Runtime.getRuntime();
			//Process doeProcess = runtime.exec("c:\\doe22\\doe22.bat exe48y " + "\""+inpFileName+"\"" + " " + location);
			for(UpdateListener updateListener:updateListeners){
				updateListener.update("Running Simulation: " + inpFileName.substring(inpFileName.lastIndexOf("\\") + 1, inpFileName.length()));
			}
			Process doeProcess = runtime.exec("c:\\doe22\\doe22.bat exe48y " + "\""+ inpFileName + "\""+ " " + "\""+ location + "\"");
			StreamInterceptor streamInterceptor = new StreamInterceptor(doeProcess.getInputStream(),new DOE2SimProgressUpdater(updateListeners,debugMode));
			streamInterceptor.start();
			doeProcess.waitFor();
			for(DOE2SimCompleteListener simulationCompleteListener: simulationCompleteListeners){
				simulationCompleteListener.simulationComplete(isError());
			}
			//System.out.println("FINISHED");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isError(){
		return errorListener.isError();
	}
}
