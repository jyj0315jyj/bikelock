#include <Arduino_LSM6DS3.h>

void setup() {
  // put your setup code here, to run once:
  IMU.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  if(IMU.gyroscopeAvailable()) {
    float x, y, z;
    Serial.println("---------------")
    Serial.print("X: ");
    Serial.println(x);
    Serial.print("Y: ");
    Serial.println(y);
    Serial.print("Z: ");
    Serial.println(z);
  }
}
