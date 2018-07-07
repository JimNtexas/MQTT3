package com.example.jim.myapplication


//  https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/#dependencies

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence


class MainActivity : AppCompatActivity(), MqttCallback {
    private val theMsg: TextView? = null
    internal var client: MqttClient? = null
    internal var clientId = MqttClient.generateClientId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);


        val chkButton = findViewById<Button>(R.id.check_connection)
        chkButton.setOnClickListener { Log.d(TAG, "client connected: " + client!!.isConnected) }

        val txtMsg = findViewById<TextInputEditText>(R.id.textMessage)


        val sendMsg = findViewById<Button>(R.id.send_msg)
        sendMsg.setOnClickListener {
            if (client!!.isConnected) {

                val msgText = txtMsg.text.toString()
                val message = MqttMessage(msgText.toByteArray())
                message.qos = 2
                message.isRetained = false

                try {
                    Log.d(TAG, "sending message on topic " + topic)
                    client!!.publish(topic, message)
                    Log.i(TAG, "Message published")

                } catch (e: MqttPersistenceException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()

                } catch (e: MqttException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

            } else {
                Log.d(TAG, "couldn't send, client disconnected")
            }
        }


        val theMsg = findViewById<TextView>(R.id.textMessage)

        //tcp://10.0.61.122:1883

        try {
            client = MqttClient("tcp://10.211.1.127:1883", clientId, MemoryPersistence())

            client!!.setCallback(this)
            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true
            client!!.connect(options)

            client!!.subscribe(topic)
        } catch (e1: MqttException) {
            Log.d(TAG, "connect exception: " + e1.toString())
            e1.printStackTrace()
        }

    }


    override fun connectionLost(cause: Throwable) {
        Log.d(TAG, "connection lost")
    }

    @Throws(Exception::class)
    override fun messageArrived(topic: String, message: MqttMessage) {


        //  Toast.makeText(MainActivity.this, "Topic: "+topic+"\nMessage: "+message, Toast.LENGTH_LONG).show();
        Log.d(TAG, "arrived: " + message.toString())
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
        //     Toast.makeText(MainActivity.this,"delivery complete", Toast.LENGTH_LONG).show();

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    companion object {

        private val TAG = "MQTT MAIN"
        private val topic = "car_command"
    }


}
