package com.example.sendcount

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


lateinit var iAddress:EditText
lateinit var iPort:EditText
lateinit var iTerm:EditText

lateinit var btAction:Button
lateinit var oCount:TextView
var sendCnt:Int = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iAddress = findViewById(R.id.iAddress)
        iPort = findViewById(R.id.iPort)
        iTerm = findViewById(R.id.iTerm)

        btAction = findViewById(R.id.btAction)
        oCount = findViewById(R.id.oCount)

        loadSettings()

        val thread = SendSomethingThread()
        thread.start()
    }

    private fun loadSettings() {
        val pref = this.getPreferences(0)
        val address = pref.getString("Address", "None")
        val port = pref.getString("Port", "None")
        val terms = pref.getString("Terms", "None")

        if (address != "None" && port != "None" && terms != "None") {
            iAddress.setText(address)
            iPort.setText(port)
            iTerm.setText(terms)
        }
    }

    fun Run(view: View) {
        saveSettings(iAddress.text.toString(), iPort.text.toString(), iTerm.text.toString())

        if (btAction.text =="start"){
            btAction.text = "stop"
        } else {
            btAction.text = "start"
        }
    }

    private fun saveSettings(address: String, port: String, terms:String) {
        val pref = this.getPreferences(0)
        val editor = pref.edit()

        editor.putString("Address", address).putString("Port", port).putString("Terms", terms).apply()
    }
}

fun send(host: String, port: Int, sendData: ByteArray) {
    var socket: DatagramSocket?=null
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    try {
        socket= DatagramSocket()
        socket.broadcast = true

        val packet= DatagramPacket(sendData, sendData.size, InetAddress.getByName(host), port)
        socket.send(packet)
    }catch (e: Exception){
        println(e)
    }finally {
        socket?.close()
    }
}
 class SendSomethingThread: Thread(){
     override fun run():Unit {
         super.run()
         sleep(2000)
         while (true){
             try {
                 val address = iAddress.text.toString()
                 val port = iPort.text.toString().toInt()
                 val terms = iTerm.text.toString().toInt()

                 if (btAction.text == "stop") {
                     sendCnt++
                     oCount.text = sendCnt.toString()
                     send(address, port, sendCnt.toString().toByteArray())
                     sleep((terms * 1000).toLong())
                 }
             }
             catch (e: Exception){
                 println("Error in thread : $e")
             }
         }
     }
 }