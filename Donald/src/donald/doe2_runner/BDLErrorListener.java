package donald.doe2_runner;

/**
 * 
 * @author Aaron Powers
 * 
 */

import booker.building_data.UpdateListener;

public class BDLErrorListener implements UpdateListener{
	
	private boolean isError;
	
	public BDLErrorListener(){
		isError = false;
	}
	
	public boolean isError(){
		return isError;
	}

	@Override
	public void update(String message) {
		if(message.startsWith(" *** DUE TO THESE ERRORS NO SIMULATION WILL BE PERFORMED")){
			isError = true;
		}
		
	}

}
