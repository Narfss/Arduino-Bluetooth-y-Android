#include <SoftwareSerial.h>

int RX = 10;
int TX = 11;
int KEY = 9;
SoftwareSerial bluetooth(RX, TX);

void setup() {
  pinMode(KEY, OUTPUT);
  digitalWrite(KEY, HIGH);
  Serial.begin(9600);
  Serial.println("Enter AT commands:");
  bluetooth.begin(38400);
}

void loop() {
  if (bluetooth.available())
    Serial.write(bluetooth.read());
  if (Serial.available())
    bluetooth.write(Serial.read());
}
