package com.example.jim.myapplication;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


//  https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/#dependencies

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private static String TAG = "MQTT MAIN";
    private TextView theMsg;
    MqttClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        String clientId = MqttClient.generateClientId();


        final Button chkButton = findViewById(R.id.check_connection);
        chkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG,"client connected: " + client.isConnected());
            }
        });

        final TextView theMsg = findViewById(R.id.the_msg);


        try {
            client = new MqttClient("tcp://10.0.61.122:1883", clientId, new MemoryPersistence());

            client.setCallback(this);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            client.connect(options);

            String topic = "car_command";
            client.subscribe(topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG,"connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {


      //  Toast.makeText(MainActivity.this, "Topic: "+topic+"\nMessage: "+message, Toast.LENGTH_LONG).show();
        Log.d(TAG, "arrived: " + message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
   //     Toast.makeText(MainActivity.this,"delivery complete", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
