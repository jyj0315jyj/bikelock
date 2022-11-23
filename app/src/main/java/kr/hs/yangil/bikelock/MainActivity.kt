package kr.hs.yangil.bikelock

import android.app.PendingIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Debug.waitForDebugger()
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serv: MyServices = (service as MyServices.Mynder).getServices()
                serv.init()
                val kord: String? = serv.findDevice()
                Log.d(null, kord.toString())
                if (kord != null) {
                    MainScope().launch {
                        val inte = Intent(this@MainActivity, NextActivity::class.java).apply {
                            putExtra("add", kord)
                        }
                        startActivity(inte)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }
        bindService(Intent(this, MyServices::class.java), conn, BIND_AUTO_CREATE)
    }
}