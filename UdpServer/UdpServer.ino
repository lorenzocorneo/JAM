#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

#define WRITE_OP 1
#define READ_OP  0

#define ANALOG_OP  0
#define DIGITAL_OP 1

#define ANALOG_NUM  6
#define DIGITAL_NUM 14

// MAC Address of Arduino Ethernet Shield
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// IP Address of Arduino Ethernet Shield
IPAddress ip(192, 168, 0, 13);
// Listening UDP Port
unsigned int localPort = 8888;
// Listening UDP Port of the elaborator's socket
unsigned int pushPort = 8889;
IPAddress pushIp;
int push_device = 0;
// Buffer where are contained incoming data
char packetBuffer[UDP_TX_PACKET_MAX_SIZE];

typedef struct push_device {
  //int pin;
  int isPush;
  int prev_value;
} push_array;

push_array push_analog[ANALOG_NUM];
push_array push_digital[DIGITAL_NUM];

EthernetUDP Udp;

// PIN Mask
char pin_mask = 0xF0;

// Read/Write Mask
char rw_mask = 0x08;

// Analog/Digital Mask
char ad_mask = 0x04;

void setup() {
  Ethernet.begin(mac, ip);
  Udp.begin(localPort);
  Serial.begin(9600);
  pinMode(9, OUTPUT);
}

void loop() {
  int packetSize = Udp.parsePacket();
  
  // PULL
  if(packetSize)
  {
    /*Serial.print("Received packet of size ");
    Serial.println(packetSize);
    Serial.print("From ");
    
    for (int i =0; i < 4; i++)
    {
      Serial.print(remote[i], DEC);
      if (i < 3)
      {
        Serial.print(".");
      }
    }
    Serial.print(", port ");
    Serial.println(Udp.remotePort());*/

    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    /*Serial.println("Contents:");
    Serial.println(packetBuffer);
    Serial.println(packetBuffer[1], DEC);*/
    int pin = getPin(packetBuffer);
    int rw = isWriteOperation(packetBuffer);
    int ad = isDigitalOperation(packetBuffer);
    int val = getOperationValue(packetBuffer);
    /*Serial.print(pin, DEC);
    Serial.print(rw, DEC);
    Serial.print(ad, DEC);
    Serial.print(val, DEC);*/
    
    // verify PUSH registration
    if(rw == READ_OP && val == 1) {
      if(ad == ANALOG_OP) {
        push_analog[pin].isPush = 1;
        // initialization
        push_analog[pin].prev_value = 0;
        Serial.println("Richiesta push intercettata!");
      } else {
        push_digital[pin].isPush = 1;
        push_digital[pin].prev_value = 0;
      }
      push_device++;
      pushIp = Udp.remoteIP();
      // pass next cycle's step
      return;
    }
    
    // verify PUSH deregistration
    if(rw == READ_OP && val == 2) {
      // manca controllo per deregistrazione anche se non sei registrato
      if(ad == ANALOG_OP) {
        push_analog[pin].isPush = 0;
      } else {
        push_digital[pin].isPush = 0;
      } 
      push_device--;
      // pass next cycle's step
      return;
    }
    
    int return_value = 0;
    
    // Digital Write
    if(rw == WRITE_OP && ad == DIGITAL_OP) {
      int val = getOperationValue(packetBuffer);
      if(val == 1) {
        digitalWrite(pin, HIGH);
        return_value = 1;
      } else if(val == 0) {
        digitalWrite(pin, LOW);
        return_value = 1; 
      } else {
        Serial.println("Corrupted Digital Value in Write Operation!");
      }
    } 
    // Digital Read
    else if(rw == READ_OP && ad == DIGITAL_OP && val != 1 && val != 2) {
      return_value = digitalRead(pin); 
    }
    // Analog Write
    else if(rw == WRITE_OP && ad == ANALOG_OP) {
      analogWrite(pin, getOperationValue(packetBuffer));
      return_value = 1;
    }
    // Analog Read
    else if(rw == READ_OP && ad == ANALOG_OP && val != 1 && val != 2) {
      return_value = analogRead(pin);
      //Serial.println("Ho letto l'input analogico");
      //Serial.println(return_value, DEC);
    }
    // Error
    else {
      Serial.println("Error, Operation not found!");  
    }

    sendPacket(getReadValue(return_value), Udp.remoteIP(), Udp.remotePort());
  }
  
  // PUSH

  if(push_device != 0) {
    // analog
    for(int i = 0; i < ANALOG_NUM; i++){
       int read_value = analogRead(i);
       if(push_analog[i].isPush == 1 && read_value != push_analog[i].prev_value) {
         char val[2];
         val[0] = *((char*)&read_value);
         val[1] = *(((char*)&read_value) + 1);
         val[1] |= (i << 4);
         sendPacket(val, pushIp, pushPort);
         push_analog[i].prev_value = read_value;
         Serial.print("Inviato valore ");
         Serial.print(read_value, DEC);
         Serial.print(" del pin ");
         Serial.println(i, DEC);
       }
    }
    
    //digital
    for(int i = 0; i < DIGITAL_NUM; i++) {
      int read_value = digitalRead(i);
      if(push_digital[i].isPush == 1 && read_value != push_digital[i].prev_value) { 
        char val[2];
        if(read_value == HIGH) {
          val[0] = 1;
        } else {
          val[0] = 0;
        }
        val[1] = (i << 4) | (DIGITAL_OP << 2);
        sendPacket(val, pushIp, pushPort);
        push_digital[i].prev_value = read_value;
      }
    }
  }
  delay(100);
}

void sendPacket(char *buffer, IPAddress target_ip, int target_port) {
  Udp.beginPacket(target_ip, target_port);
  Udp.write(buffer);
  Udp.endPacket();
  Serial.print("Inviato paccetto alla porta");
  Serial.println(target_port, DEC);
}

// Return Pin number inside packet created with my protocol
int getPin(char *buffer) {
  return  ((buffer[1] & pin_mask) >> 4) & 0x000F;
}

// Return One if it is Write Operation, Zero if Read Operation
int isWriteOperation(char* buffer) {
    return ((buffer[1] & rw_mask) >> 3) & 0x000F;
}

// Return One if it is Digital Operation, Zero if Analog Operation
int isDigitalOperation(char *buffer) {
   return ((buffer[1] & ad_mask) >> 2) & 0x000F; 
}

// Return the value for Write Operation
int getOperationValue(char *buffer) {
  return buffer[0] & 0x00FF;
}

// Return the value of Read
char *getReadValue(int pin) {
  char val[2];
  val[0] = *((char*)&pin);
  val[1] = *(((char*)&pin) + 1);
  return val;
}
