package kr.hs.yangil.bikelock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serv: MyServices = (service as MyServices.Mynder).getServices()
                serv.init()
                val kord: String? = serv.findDevice()
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