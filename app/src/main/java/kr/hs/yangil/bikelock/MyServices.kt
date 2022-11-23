package kr.hs.yangil.bikelock

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

class MyServices: Service() {
    val binder: Mynder = Mynder()
    lateinit var bluetoothManager: BluetoothManager
    val bback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if ((BluetoothGatt.GATT_SUCCESS == status) && (newState == BluetoothGatt.STATE_CONNECTED) &&
                checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                gatt?.discoverServices()
            }
        }
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            for (devk: BluetoothGattService in gatt!!.services) {
                if (devk.uuid.toString() == "4e8f776f-31f1-465b-bfe6-aaea92226c74") {
                    for (cha: BluetoothGattCharacteristic in devk.characteristics) {
                        if (cha.uuid.toString() == "97906bf1-ca71-4e30-9fde-a23753cc3d73" &&
                            checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            gatt.setCharacteristicNotification(cha, true)
                            val disc: BluetoothGattDescriptor = cha.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            disc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(disc)
                            break
                        }
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic?.uuid.toString() == "97906bf1-ca71-4e30-9fde-a23753cc3d73" &&
                checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                sendBroadcast(Intent().apply {
                    action = "kr.hs.yangil.bikelock.VALUE_CHANGED"
                    putExtra("VALUE", characteristic!!.value[0].toInt() == 1)
                })
            }
        }
    }
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    fun init() {
        bluetoothManager = getSystemService<BluetoothManager>(BluetoothManager::class.java)
    }
    fun connect(addr: String?)  {
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (addr != null) {
                Log.d(null, addr)
            }
            val dev: BluetoothGatt =
                bluetoothManager.adapter.getRemoteDevice(addr).connectGatt(this, false, bback)
            dev.discoverServices()
        }
    }
    fun findDevice(): String? {
        val bdev = if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) bluetoothManager.adapter.bondedDevices else setOf()
        var kord: String = ""
        for (devk: BluetoothDevice in bdev) {
            if (devk.name == "YLTester") {
                kord = devk.address
            }
        }
        return kord
    }
    inner class Mynder: Binder() {
        fun getServices(): MyServices = this@MyServices
    }
}