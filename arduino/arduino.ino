#include <ArduinoBLE.h>
#include <Arduino_LSM6DS3.h>

BLEService ledService("4e8f776f-31f1-465b-bfe6-aaea92226c74");
BLEIntCharacteristic switchCharacteristic("97906bf1-ca71-4e30-9fde-a23753cc3d73", BLERead | BLENotify | BLEWrite);


void setup() {
  IMU.begin();
  Serial.begin(9600);
  pinMode(13, HIGH);
  BLE.begin();
  BLE.setConnectable(true);
  BLE.setDeviceName("YLTester");
  ledService.addCharacteristic(switchCharacteristic);
  BLE.addService(ledService);
  BLE.setAdvertisedService(ledService);
  BLE.advertise();
}

void loop() {
  float x, y, z;

  if (IMU.gyroscopeAvailable() && BLE.central()) {
    IMU.readGyroscope(x, y, z);
    if ((abs(x) + abs(y) + abs(z)) / 3 > 50) {
      digitalWrite(13, HIGH);
      switchCharacteristic.writeValue(0x01);
    }
    else {
      digitalWrite(13, LOW);
      switchCharacteristic.writeValue(0x00);
    }
}
  delay(100);
}