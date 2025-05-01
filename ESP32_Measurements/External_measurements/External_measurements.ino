#include <WiFi.h>
#include <esp_wifi.h>
#include <WebServer.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>
#include <BH1750.h>
#include <HTTPClient.h>
#include <SPI.h>

#define BME_SCK 18
#define BME_MISO 19
#define BME_MOSI 23
#define BME_CS 5
#define SEALEVELPRESSURE_HPA (1013.25)

const char* ssid = "ssid";
const char* password = "password";
const char* serverUrl = "";

Adafruit_BME280 bme(BME_CS, BME_MOSI, BME_MISO, BME_SCK);
BH1750 bh;
WebServer server(80);

void setup(){
  Serial.begin(115200);
  Wire.begin();
  WiFi.begin(ssid, password);

  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.println("Trying to connect...");
  }
  Serial.println("\nWiFi connected");
  Serial.println(WiFi.localIP());

  WiFi.setSleep(true);
  esp_wifi_set_ps(WIFI_PS_MIN_MODEM);

  if (!bme.begin(0x76)) {
    Serial.println("BME280 not found");
    while (1);
  }  

  if (!bh.begin(BH1750::CONTINUOUS_HIGH_RES_MODE)) {
    Serial.println("BH1750 not found");
    while (1);
  }

  server.on("/measure", HTTP_GET, handleMeasure);
  server.begin();
  Serial.println("HTTP server started");
}

void loop(){
  server.handleClient();
  delay(50);
}

String performMeasurements(){
  float temperature = bme.readTemperature();
  float humidity = bme.readHumidity();
  float pressure = bme.readPressure() / 100.0F;
  float light = bh.readLightLevel();

  String payload = "{";
  payload += "\"temperature\":" + String(temperature, 1) + ",";
  payload += "\"humidity\":" + String(humidity, 1) + ",";
  payload += "\"pressure\":" + String(pressure, 1) + ",";
  payload += "\"light\":" + String(light, 1);
  payload += "}";

  Serial.println("[MEASUREMENTS] " + payload);
  return payload;
}

void sendDataToServer(const String& jsonPayload){
  HTTPClient http;
  http.begin(serverUrl);
  http.addHeader("Content-Type", "application/json");
  int responseCode = http.POST(jsonPayload);
  Serial.print("[POST] Response code: ");
  Serial.println(responseCode);
  http.end();
}

void handleMeasure(){
  Serial.println("[REQUEST] /measure received");
  esp_wifi_set_ps(WIFI_PS_NONE);
  String jsonData = performMeasurements();
  sendDataToServer(jsonData);
  server.send(200, "text/plain", "OK");
  esp_wifi_set_ps(WIFI_PS_MIN_MODEM);
}





