#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#include <OneWire.h>
#include <DallasTemperature.h>
#include <SoftwareSerial.h>

#define ONE_WIRE_BUS 2
#define REPORTING_PERIOD_MS     1000
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
PulseOximeter pox;
uint32_t tsLastReport = 0;

void onBeatDetected()
{
  Serial.println("Beat!");
}

void setup() {
  Serial.begin(9600);   //初始化串口并设置波特率为9600
  //  pox.setOnBeatDetectedCallback(onBeatDetected);
  pox.begin();

}

void loop() {
  char Buf_Tostr[10];
  char send_str[255] = "";
  pox.update();
  if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
    /* 温度数据获取 */
    sensors.requestTemperatures();
    dtostrf(sensors.getTempCByIndex(0), 2, 2, Buf_Tostr); //将温度数据转化为字符（保留两位小数）
    strcat(send_str, ""); //添加温度标识
    strcat(send_str, Buf_Tostr); //添加温度数据到发送缓冲区 "a+T27.5"
    /* 心率数据获取 */
    strcat(send_str, "+"); //添加心率标识
    dtostrf(pox.getHeartRate(), 3, 2, Buf_Tostr); //将心率数据转化为字符（保留两位小数）bpm
    strcat(send_str, Buf_Tostr); //添加心率数据到发送缓冲区 "a+T27.5+H83.52"
    strcat(send_str, "+"); //
    dtostrf(pox.getSpO2(), 2, 2, Buf_Tostr); //将SpO2转化为字符（保留两位小数）
    strcat(send_str, Buf_Tostr); //添加SpO2数据到发送缓冲区 "a+T27.5+H83.52+S95"
    //    strcat(send_str, "%");
    strcat(send_str, "+");
    dtostrf(pox.getTemperature(), 2, 2, Buf_Tostr);//将temp转化为字符（保留两位小数）C
    strcat(send_str, Buf_Tostr); //添加SpO2数据到发送缓冲区 "a+T27.5+H83.52+S95+t40.32"
    strcat(send_str, "#");
    Serial.print(send_str);
    delay(500);
    tsLastReport = millis();
  }
}

