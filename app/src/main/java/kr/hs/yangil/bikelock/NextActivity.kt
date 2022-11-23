package kr.hs.yangil.bikelock

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
        if (intent.extras?.getString("add") == null) {
            Log.d(null, "NULL")
        }
        Log.d(null, intent.extras?.getString("add").toString())
        val texiview: TextView = findViewById(R.id.textView)
        ContextCompat.registerReceiver(this, object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                MainScope().launch {
                    texiview.text = if (intent?.extras?.getBoolean("VALUE") == true) "Alert" else "Everything is fine"
                }
            }
        }, IntentFilter("kr.hs.yangil.bikelock.VALUE_CHANGED"), ContextCompat.RECEIVER_NOT_EXPORTED)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
        bindService(Intent(this, MyServices::class.java),
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val serv: MyServices = (service as MyServices.Mynder).getServices()
                    serv.connect(intent.extras?.getString("add"))
                }

                override fun onServiceDisconnected(name: ComponentName?) {

                }
            }, BIND_AUTO_CREATE)
    }
}