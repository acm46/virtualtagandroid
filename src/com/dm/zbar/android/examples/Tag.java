package com.dm.zbar.android.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.hardware.*;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Tag extends Activity implements SensorEventListener {

	private SensorManager sm = null;
	private static float direction = 0;

	private TextView xCoor;
	private TextView yCoor;
	private TextView zCoor;
	private TextView compassOutput;
	private EditText textbox;
	private Button button;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.tag);
        
        xCoor = (TextView) findViewById(R.id.xCoor);
        yCoor = (TextView) findViewById(R.id.yCoor);
        zCoor = (TextView) findViewById(R.id.zCoor);
        textbox = (EditText) findViewById(R.id.edit_message);
        
        compassOutput = (TextView) findViewById(R.id.textview1);
        button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(buttonhandler);
    }
    
    View.OnClickListener buttonhandler = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			HttpClient client = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://agile-taiga-5205.herokuapp.com/task");
			
			try {
		        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		        params.add(new BasicNameValuePair("qr_id", "12345"));
		        params.add(new BasicNameValuePair("direction", Float.toString(direction)));
		        params.add(new BasicNameValuePair("message", textbox.getText().toString()));
		        params.add(new BasicNameValuePair("location_x", "1"));
		        params.add(new BasicNameValuePair("location_y", "1"));
		        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		        
				HttpResponse response = client.execute(httppost);
				Toast.makeText(getApplicationContext(), "POST request sent.", Toast.LENGTH_SHORT).show();
				
			} 
			catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "ClientProtocolException.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} 
			catch (IOException e) {
				Toast.makeText(getApplicationContext(), "IOException.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			finally{
				client.getConnectionManager().shutdown();
			}
		}
	};

    @Override
    protected void onResume(){
    	super.onResume();
    	sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sm.SENSOR_DELAY_FASTEST);
    	sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), sm.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    	sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized(this){
			switch (event.sensor.getType()){
				case Sensor.TYPE_ACCELEROMETER: 
					xCoor.setText("x: " + event.values[0]);
					yCoor.setText("y: " + event.values[1]);
					zCoor.setText("z: " + event.values[2]);
					break;
				case Sensor.TYPE_ORIENTATION:
					direction = event.values[0];		  
					compassOutput.setText("Compass:"+Float.toString(direction));	  
					break;
			}
		}	
	}

}
