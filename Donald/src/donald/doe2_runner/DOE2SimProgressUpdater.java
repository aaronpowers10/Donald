package donald.doe2_runner;

/**
 * 
 * @author Aaron Powers
 * 
 */

import java.util.ArrayList;
import booker.building_data.UpdateListener;

public class DOE2SimProgressUpdater implements UpdateListener{

	private ArrayList<UpdateListener> updateListeners;
	private String updateHeader;

	public DOE2SimProgressUpdater(ArrayList<UpdateListener> updateListeners){
		this.updateListeners = updateListeners;
		updateHeader = "";
	}

	@Override
	public void update(String message){
		if(message.startsWith(" *** DUE TO THESE ERRORS NO SIMULATION WILL BE PERFORMED")){
			updateListeners(message);
		} else if(message.startsWith(" Start DOEBDL")){
			updateListeners("Running DOEBDL");
		} else if (message.startsWith("Starting LOADS")){
			updateHeader = "Running LOADS: ";
		} else if (message.startsWith("Starting HVAC")){
			updateHeader = "Running HVAC: ";
		} else if (message.startsWith("Starting ECONOMIC")){
			updateHeader = "Running ECONOMIC: ";
		}

		if(isTimeStamp(message)){
			updateListeners(updateHeader + message);
		}

	}

	private void updateListeners(String message){
		for(int i=0;i<updateListeners.size();i++){
			updateListeners.get(i).update(message);
		}
	}

	private boolean isTimeStamp(String message){
		if(message.startsWith("Hol")){
			return true;
		} else if (message.startsWith("Sat")){
			return true;
		} else if (message.startsWith("Sun")){
			return true;
		} else if (message.startsWith("Mon")){
			return true;
		} else if (message.startsWith("Tue")){
			return true;
		} else if (message.startsWith("Wed")){
			return true;
		} else if (message.startsWith("Thu")){
			return true;
		} else if (message.startsWith("Fri")){
			return true;
		} else if (message.startsWith("HDD")){
			return true;
		} else if (message.startsWith("CDD")){
			return true;
		}
		return false;
	}

}
