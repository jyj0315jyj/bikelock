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
    private fun every() {
        bindService(Intent(this, MyServices::class.java), object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serv: MyServices = (service as MyServices.Mynder).getServices()
                serv.init()
                val kord: String = serv.findDevice()
                if (kord != "") {
                    MainScope().launch {
                        val inte = Intent(this@MainActivity, NextActivity::class.java).apply {
                            putExtra("add", kord)
                        }
                        serv.connect(kord)
                        startActivity(inte)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }, BIND_AUTO_CREATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        every()
    }

    override fun onResume() {
        super.onResume()
        every()
    }
}