/*
 * Copyright Francisco M Sirvent 2015 (@narfss)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Francisco M Sirvent <narfss@gmail.com>
 */
 
 #include <SoftwareSerial.h>

int RX = 10;
int TX = 11;
SoftwareSerial bluetooth(RX, TX);
int LED_PIN = 13;
int BUTTON_PIN = 2;
char serialData[3];
char ON[] = "ON"; 
char OF[] = "OF";
bool pressed = false;
bool updateButton= false;

void setup() {
  bluetooth.begin(9600);
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
}

void loop() {
  int buttonState = digitalRead(BUTTON_PIN);
  if (buttonState == 1 && !updateButton) {
    pressed = !pressed;
    updateButton = true;
  } else if (buttonState == 0 && updateButton) {
    updateButton = false;
    if (pressed) {
      bluetooth.write("HI!"); 
    } else {
      bluetooth.write("Bye bye!");
    }
  }
  int bufferSize = bluetooth.available();
  if (bufferSize == 2) {
    bluetooth.readBytes(serialData, bufferSize);
    if (strcmp(serialData, ON) == 0) {
      digitalWrite(LED_PIN, HIGH);  
    } else if (strcmp(serialData, OF) == 0) {
      digitalWrite(LED_PIN, LOW);
    }
  }
  delay(20);
}
