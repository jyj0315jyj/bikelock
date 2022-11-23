package kr.hs.yangil.bikelock

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
        val texiview: TextView = findViewById(R.id.textView)
        val button: Button = findViewById(R.id.button)
        val switch: SwitchCompat = findViewById(R.id.switch1)
        button.setOnClickListener {
            texiview.text = getString(R.string.fineok)
            it.isEnabled = false
        }
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {button.isEnabled = false}
        }
        ContextCompat.registerReceiver(this, object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                MainScope().launch {
                    if (!button.isEnabled && intent?.extras?.getBoolean("VALUE") == true) {
                        texiview.text = getString(R.string.alert)
                        if (switch.isChecked) {button.isEnabled = true}
                    }
                    else if (!switch.isChecked && intent?.extras?.getBoolean("VALUE") == false) {
                        texiview.text = getString(R.string.fineok)
                    }
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