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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import otis.lexical.UpdateListener;

public class DOE2Run implements Runnable {

	private String inpFileName;
	private String location;
	private ArrayList<UpdateListener> updateListeners;
	private ArrayList<DOE2SimCompleteListener> simulationCompleteListeners;
	private BDLErrorListener errorListener;
	private boolean debugMode;
	private DOE2Version version;

	public DOE2Run(String inpFileName, String location,DOE2Version version, boolean debugMode) {
		this.inpFileName = inpFileName;
		this.location = location;
		this.version = version;
		updateListeners = new ArrayList<UpdateListener>();
		simulationCompleteListeners = new ArrayList<DOE2SimCompleteListener>();
		errorListener = new BDLErrorListener();
		updateListeners.add(errorListener);
		this.debugMode = debugMode;
		// this.debugMode = true;
	}

	public void addUpdateListener(UpdateListener updateListener) {
		updateListeners.add(updateListener);
	}

	public void addSimulationCompleteListener(DOE2SimCompleteListener simulationCompleteListener) {
		simulationCompleteListeners.add(simulationCompleteListener);
	}

	@Override
	public void run() {

		try {
			deleteDOE2Files();
			copyFile("DOEBDL.EXE");
			copyFile("DOESIM.EXE");
			copyFile("BDLKEY.BIN");
			copyFile("HDRFIL.BIN");
			copyFile("BDLLIB.DAT");
			copyFile("BDLDFT.DAT");
			File inpTemp = new File("INPUT2.TMP");
			File inp = new File(inpFileName + ".inp");
			File loc = new File(location + ".bin");
			File locTemp = new File("WEATHER.BIN");
			Files.copy(inp.toPath(), inpTemp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(loc.toPath(), locTemp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Runtime runtime = Runtime.getRuntime();
			for (UpdateListener updateListener : updateListeners) {
				updateListener.update("Running Simulation: "
						+ inpFileName.substring(inpFileName.lastIndexOf("\\") + 1, inpFileName.length()));
			}
//			Process doeProcess = runtime.exec(System.getProperty("user.dir") + "\\src\\res\\DOEBDL", null,
//					new File(System.getProperty("user.dir") + "\\src\\res"));
			Process doeProcess = runtime.exec("DOEBDL");
			StreamInterceptor streamInterceptor = new StreamInterceptor(doeProcess.getInputStream(),
					new DOE2SimProgressUpdater(updateListeners, debugMode));
			streamInterceptor.start();
			doeProcess.waitFor();
//			doeProcess = runtime.exec(System.getProperty("user.dir") + "\\src\\res\\DOESIM", null,
//					new File(System.getProperty("user.dir") + "\\src\\res"));
			doeProcess = runtime.exec("DOESIM");
			streamInterceptor = new StreamInterceptor(doeProcess.getInputStream(),
					new DOE2SimProgressUpdater(updateListeners, debugMode));
			streamInterceptor.start();
			doeProcess.waitFor();
			
			
			
			File bdlFile = new File("DOEBDL.OUT");
			bdlFile.renameTo(new File(inpFileName + ".BDL"));
			
			File simFile = new File("DOESIM.OUT");
			simFile.renameTo(new File(inpFileName + ".SIM"));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (DOE2SimCompleteListener simulationCompleteListener : simulationCompleteListeners) {
			simulationCompleteListener.simulationComplete(isError());
		}
		
		deleteDOE2Files();

		
//		try {
//			Runtime runtime = Runtime.getRuntime();
//			//Process doeProcess = runtime.exec("c:\\doe22\\doe22.bat exe48y " + "\""+inpFileName+"\"" + " " + location);
//			for(UpdateListener updateListener:updateListeners){
//				updateListener.update("Running Simulation: " + inpFileName.substring(inpFileName.lastIndexOf("\\") + 1, inpFileName.length()));
//			}
//			Process doeProcess = runtime.exec("c:\\doe23\\doe23_ap.bat " + "\""+ inpFileName + "\""+ " " + "\""+ location + "\"");
//			StreamInterceptor streamInterceptor = new StreamInterceptor(doeProcess.getInputStream(),new DOE2SimProgressUpdater(updateListeners,debugMode));
//			streamInterceptor.start();
//			doeProcess.waitFor();
//			for(DOE2SimCompleteListener simulationCompleteListener: simulationCompleteListeners){
//				simulationCompleteListener.simulationComplete(isError());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	private void deleteDOE2Files() {
		deleteFile("DOEBDL.EXE");
		deleteFile("DOESIM.EXE");
		deleteFile("BDLKEY.BIN");
		deleteFile("HDRFIL.BIN");
		deleteFile("BDLLIB.DAT");
		deleteFile("BDLDFT.DAT");
		deleteFile("CTRL.TMP");
		deleteFile("DOEBDL.LOG");
		deleteFile("DOESIM.LOG");
		deleteFile("DOEHRREP.LIN");
		deleteFile("DOEHRREP.SIN");
		deleteFile("DOEREP.ERP");
		deleteFile("DOEREP.SRP");
		deleteFile("DOEREP.LRP");
		deleteFile("PLTOUT.TMP");
		deleteFile("STDFIL.TMP");
		deleteFile("DSNFIL.TMP");
		deleteFile("WEATHER.BIN");
		deleteFile("INPUT2.TMP");
		deleteFile("DOESIM.TXT");
		deleteFile("plt.wth");
		deleteFile("USRLIB.DAT");
		deleteFile("DOEBDL.OUT");
		deleteFile("DOESIM.OUT");
	}

	private void deleteFile(String fileName) {
		File f = new File(fileName);
		f.delete();
	}

	private void copyFile(String fileName) throws IOException {
		InputStream input = null;
		FileOutputStream output = null;
//		try {
		String resSubfolder = "";
		if(version == DOE2Version.DOE2_2) {
			resSubfolder = "22/";
		} else if (version == DOE2Version.DOE2_3) {
			resSubfolder = "23/";
		}
			URL u = this.getClass().getClassLoader().getResource("resources/" + resSubfolder +fileName);
			input = u.openStream();
			output = new FileOutputStream(fileName);

			byte[] buffer = new byte[4096];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
			
			input.close();
			output.close();
			//System.out.println("FINISHED COPYING " + fileName);
//		} finally {
//			//System.out.println("CLOSING " + fileName);
//			input.close();
//			output.close();
//			System.out.println("CLOSING " + fileName);
//
//		}
	}

	public boolean isError() {
		return errorListener.isError();
	}
}
