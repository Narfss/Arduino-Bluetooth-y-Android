#include <SoftwareSerial.h>

int RX = 10;
int TX = 11;
SoftwareSerial bluetooth(RX, TX);
int LED_PIN = 13;
char serialData[2];

void setup() {
  bluetooth.begin(9600);
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  int bufferSize = bluetooth.available();
  if (bufferSize == 2) {
    bluetooth.readBytes(serialData, bufferSize);
    if (serialData == "ON") {
      digitalWrite(LED_PIN, HIGH);  
    } else if (serialData == "OF") {
      digitalWrite(LED_PIN, LOW);
    }
  }
  delay(20);
}
