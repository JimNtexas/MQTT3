package com.example.jim.myapplication


//  https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/#dependencies

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence



class MainActivity : AppCompatActivity(), MqttCallback, IMqttActionListener, OnClickListener {

    private val theMsg: TextView? = null
    internal var client: MqttClient? = null
    internal var clientId = MqttClient.generateClientId()

    companion  object {
        private val TAG = "MQTT MAIN"
        private val topic = "car_command"
        private val activity = this }

    fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy(LazyThreadSafetyMode.NONE){ findViewById(res) as T }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        var carCommand = CarCommand()
        var json = carCommand.goForward()
        Log.d(TAG, "json: " + json)


        val chkButton: Button by bind(R.id.check_connection)
        val btnForward: Button by bind(R.id.btnForward); btnForward.setOnClickListener(this)
        val btnBackwards: Button by bind(R.id.btnBackwards); btnBackwards.setOnClickListener(this)
        val btnLeft: Button by bind(R.id.btnLeft); btnLeft.setOnClickListener(this)
        val btnRight: Button by bind(R.id.btnRight); btnRight.setOnClickListener(this)
        val btnStop: Button by bind(R.id.btnStop); btnStop.setOnClickListener(this)

        //todo:  reconnect if not connected
        chkButton.setOnClickListener { Log.d(TAG, "client connected: " + client!!.isConnected) }

        //todo - move to function, recall if connect button pressed and connection isn't there
        async(UI){
            // office - tcp://10.0.61.122:1883
            // robocar - tcp://192.168.1.201:1883
            // home pi -"tcp://10.211.1.127:1883"
            // netgear - "tcp://192.168.1.5"
            try {
                client = MqttClient("tcp://192.168.1.202:1883", clientId, MemoryPersistence())
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

    fun sendMsg(json: String?) {
        if(client == null) { Log.d(TAG, "client was null in send msg!"); return; }
        if (client!!.isConnected) {
            if(json == null) {Log.d(TAG, "null passed to sendMsg"); return}
                Log.d(TAG, "sending json: " + json)
                val message = MqttMessage(json.toByteArray())
                message.qos = 2
                message.isRetained = false

                try {
                    Log.d(ContentValues.TAG, "sending message on topic " + topic)
                    client!!.publish(topic, message)
                    Log.i(ContentValues.TAG, "Message published")

                } catch (e: MqttPersistenceException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()

                } catch (e: MqttException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

            } else {
                Log.d(ContentValues.TAG, "couldn't send, client disconnected")
                setConnectionStatusIconState(false)
            }

        }

    override fun onClick(v: View?) {
        val cmd : CarCommand = CarCommand()
        val id : Int = v?.id ?: 0
        when (id){
            R.id.btnForward ->  {sendMsg(cmd.goForward()); Log.d(TAG, "goforward")}
            R.id.btnBackwards -> {sendMsg(cmd.goBackward()); Log.d(TAG, "gobackward")}
            R.id.btnLeft -> {sendMsg(cmd.turnLeft()); Log.d(TAG, "turn left")}
            R.id.btnRight -> {sendMsg(cmd.turnRight()); Log.d(TAG, "turn right")}
            R.id.btnStop -> {sendMsg(cmd.stop()); Log.d(TAG, "stop!")}
            else -> Log.d(TAG, "Unknown id clicked:  " + id)
        }
    }



}
