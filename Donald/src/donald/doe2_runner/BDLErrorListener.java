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

import booker.building_data.UpdateListener;

public class BDLErrorListener implements UpdateListener {

	private boolean isError;

	public BDLErrorListener() {
		isError = false;
	}

	public boolean isError() {
		return isError;
	}

	@Override
	public void update(String message) {
		if (message.startsWith(" *** DUE TO THESE ERRORS NO SIMULATION WILL BE PERFORMED")) {
			isError = true;
		}

	}

}
