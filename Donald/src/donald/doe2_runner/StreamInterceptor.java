package donald.doe2_runner;

/**
 * 
 * @author Aaron Powers
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import booker.building_data.UpdateListener;

public class StreamInterceptor extends Thread{

	InputStream inputStream;
	UpdateListener listener;

    StreamInterceptor(InputStream inputStream, UpdateListener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    public void run(){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line=null;
            while ((line = bufferedReader.readLine()) != null)
                listener.update(line);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
