package com.example.jim.myapplication


//  https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/#dependencies

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence



class MainActivity : AppCompatActivity(), MqttCallback, IMqttActionListener {

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

        //todo - move to function, recall if connect button pressed and connection isn't there
        async(UI){
            // val theMsg = findViewById<TextView>(R.id.textMessage)

            //tcp://10.0.61.122:1883

            try {
                client = MqttClient("tcp://10.211.1.127:1883", clientId, MemoryPersistence())
                val options = MqttConnectOptions()
                options.isAutomaticReconnect = true
                options.setConnectionTimeout(5)
                val token = client!!.connectWithResult(options) as IMqttToken
                token.waitForCompletion(5000)
                Log.d(TAG, "connection complete")
                setConnectionStatusIconState(true)

                client!!.subscribe(topic)
            } catch (e1: MqttException) {
                Log.d(TAG, "connect exception: " + e1.toString())
                setConnectionStatusIconState(false)
                e1.printStackTrace()
            }

        }
    }


    override fun connectionLost(cause: Throwable) {
        Log.d(TAG, "connection lost")
        setConnectionStatusIconState(false)
    }

    @Throws(Exception::class)
    override fun messageArrived(topic: String, message: MqttMessage) {


        //  Toast.makeText(MainActivity.this, "Topic: "+topic+"\nMessage: "+message, Toast.LENGTH_LONG).show();
        Log.d(TAG, "arrived: " + message.toString())
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
       Log.d(TAG, "delivery complete")

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

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        Log.d(TAG, "connection success")
        setConnectionStatusIconState(true)
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        Log.d(TAG, "connection failure")
        setConnectionStatusIconState(false)
    }


    fun setConnectionStatusIconState(connected:Boolean) {
        val statusIcon = findViewById<ImageButton>(R.id.connected_icon)
        val green = getResources().getColor(R.color.holo_green_light)
        val red = getResources().getColor(R.color.holo_red_light)
        statusIcon.visibility = View.VISIBLE
        if(connected) {
            statusIcon.setImageResource(R.drawable.ic_truck_solid)
            statusIcon.setColorFilter(green)
        } else {
            statusIcon.setImageResource(R.drawable.ic_baseline_error_24px)
            statusIcon.setColorFilter(red)
        }
    }

    companion object {

        private val TAG = "MQTT MAIN"
        private val topic = "car_command"
        private val activity = this
    }


}
