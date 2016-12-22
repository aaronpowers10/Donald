package donald.doe2_runner;

/**
 * 
 * @author Aaron Powers
 * 
 */

import java.io.IOException;
import java.util.ArrayList;

import booker.building_data.UpdateListener;

public class DOE2Run {

	private String inpFileName;
	private String location;
	private ArrayList<UpdateListener> updateListeners;
	private BDLErrorListener errorListener;

	public DOE2Run(String inpFileName, String location){
		this.inpFileName = inpFileName;
		this.location = location;
		updateListeners = new ArrayList<UpdateListener>();
		errorListener = new BDLErrorListener();
		updateListeners.add(errorListener);
		
	}

	public void addListener(UpdateListener updateListener){
		updateListeners.add(updateListener);
	}

	public void run() {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process doeProcess = runtime.exec("c:\\doe22\\doe22.bat exe48r " + "\""+inpFileName+"\"" + " " + "\""+location+"\"");
			StreamInterceptor streamInterceptor = new StreamInterceptor(doeProcess.getInputStream(),new DOE2SimProgressUpdater(updateListeners));
			streamInterceptor.start();
			doeProcess.waitFor();
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
