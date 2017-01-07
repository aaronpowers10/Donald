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

import java.util.ArrayList;
import booker.building_data.UpdateListener;

public class DOE2SimProgressUpdater implements UpdateListener {

	private ArrayList<UpdateListener> updateListeners;
	private String updateHeader;

	public DOE2SimProgressUpdater(ArrayList<UpdateListener> updateListeners) {
		this.updateListeners = updateListeners;
		updateHeader = "";
	}

	@Override
	public void update(String message) {
		if (message.startsWith(" *** DUE TO THESE ERRORS NO SIMULATION WILL BE PERFORMED")) {
			updateListeners(message);
		} else if (message.startsWith(" Start DOEBDL")) {
			updateListeners("Running DOEBDL");
		} else if (message.startsWith("Starting LOADS")) {
			updateHeader = "Running LOADS: ";
		} else if (message.startsWith("Starting HVAC")) {
			updateHeader = "Running HVAC: ";
		} else if (message.startsWith("Starting ECONOMIC")) {
			updateHeader = "Running ECONOMIC: ";
		}

		if (isTimeStamp(message)) {
			updateListeners(updateHeader + message);
		}

	}

	private void updateListeners(String message) {
		for (int i = 0; i < updateListeners.size(); i++) {
			updateListeners.get(i).update(message);
		}
	}

	private boolean isTimeStamp(String message) {
		if (message.startsWith("Hol")) {
			return true;
		} else if (message.startsWith("Sat")) {
			return true;
		} else if (message.startsWith("Sun")) {
			return true;
		} else if (message.startsWith("Mon")) {
			return true;
		} else if (message.startsWith("Tue")) {
			return true;
		} else if (message.startsWith("Wed")) {
			return true;
		} else if (message.startsWith("Thu")) {
			return true;
		} else if (message.startsWith("Fri")) {
			return true;
		} else if (message.startsWith("HDD")) {
			return true;
		} else if (message.startsWith("CDD")) {
			return true;
		}
		return false;
	}

}
