#include <SPI.h>
#include <Ethernet.h>

#define WRITE_OP 1
#define READ_OP  0

#define ANALOG_OP  0
#define DIGITAL_OP 1

#define ANALOG_NUM  6
#define DIGITAL_NUM 14

#define PUSH_ON  1
#define PUSH_OFF 0

// This masks refers to most significative part of the word (16bits)
#define PIN_MASK 0xF0
#define RW_MASK  0x08
#define AD_MASK  0x04

// Enter a MAC address and IP address for your controller below.
// The IP address will be dependent on your local network:
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192,168,0, 13);

EthernetServer server(9000);

char buffer[2];

void setup()
{
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip);
  server.begin();
  Serial.begin(9600);
}

void loop()
{
  // listen for incoming clients
  EthernetClient client = server.available();
  if (client) {
    Serial.println("connesso");
    int buffer_count = 0;
    while (client.connected()) {
      if (client.available()) {
        buffer[buffer_count++] = client.read();
        
        // if you've gotten to the end of the line (received a newline
        // character) and the line is blank, the http request has ended,
        // so you can send a reply
          if(buffer_count == 2) {
            buffer_count = 0;
            int pin = getPin(buffer);
            int rw = isWriteOperation(buffer);
            int ad = isDigitalOperation(buffer);
            int val = getOperationValue(buffer);
    
          // Digital Write
          if(rw == WRITE_OP && ad == DIGITAL_OP) {
            //int val = getOperationValue(buffer);
            if(val == 1) {
              digitalWrite(pin, HIGH);
            } else if(val == 0) {
              digitalWrite(pin, LOW);
            } else {
              Serial.println("Corrupted Digital Value in Write Operation!");
            }
          } 
          // Digital Read
          else if(rw == READ_OP && ad == DIGITAL_OP && val != 1 && val != 2) {
            client.print(getReadValue(digitalRead(pin))); 
          }
          // Analog Write
          else if(rw == WRITE_OP && ad == ANALOG_OP) {
            analogWrite(pin, getOperationValue(buffer));
          }
          // Analog Read
          else if(rw == READ_OP && ad == ANALOG_OP && val != 1 && val != 2) {
            client.print(getReadValue(analogRead(pin)));
            //Serial.println("Ho letto l'input analogico");
            //Serial.println(return_value, DEC);
          }
          // Error
          else {
            Serial.println("Error, Operation not found!");  
          }

          }
          if(buffer[buffer_count] == 'q') {
            break;
          }
      }
    }
    // give the web browser time to receive the data
    delay(1);
    // close the connection:
    client.stop();
  }
}

int getPin(char *buffer) {
  return  ((buffer[1] & PIN_MASK) >> 4) & 0x000F;
}

int isWriteOperation(char* buffer) {
    return ((buffer[1] & RW_MASK) >> 3) & 0x000F;
}

int isDigitalOperation(char *buffer) {
   return ((buffer[1] & AD_MASK) >> 2) & 0x000F; 
}

int getOperationValue(char *buffer) {
  return buffer[0] & 0x00FF;
}

// Return the value of Read
char *getReadValue(int value) {
  char val[2];
  val[0] = *((char*)&value);
  val[1] = *(((char*)&value) + 1);
  return val;
}
