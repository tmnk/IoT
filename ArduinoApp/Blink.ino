 #include <SpacebrewYun.h>

#define LIGHT_ON_1 1
#define LIGHT_OFF_1 2
#define LIGHT_ON_2 3
#define LIGHT_OFF_2 4
#define LIGHT_ON_3 5
#define LIGHT_OFF_3 6

int myled1 = 12;
int myled2 = 8;
int myled3 = 7;

int input_code = 0;

void setup() {
  Serial.begin(9600);
  pinMode(myled1, OUTPUT);
  pinMode(myled2, OUTPUT);
  pinMode(myled3, OUTPUT);
  digitalWrite(myled1, HIGH);
  digitalWrite(myled2, HIGH);
  digitalWrite(myled3, HIGH);
}

void loop() {
  if (Serial.available() > 0) {
    input_code = Serial.read();
    Serial.write(input_code);
    input_code &= 7;
    switch(input_code){
      case LIGHT_ON_1:
        digitalWrite(myled1, HIGH); break;
      case LIGHT_ON_2:
        digitalWrite(myled2, HIGH); break;
      case LIGHT_OFF_1:
        digitalWrite(myled1, LOW); break;
      case LIGHT_OFF_2:
        digitalWrite(myled2, LOW); break;
      case LIGHT_ON_3:
        digitalWrite(myled3, HIGH); break;
      case LIGHT_OFF_3:
        digitalWrite(myled3, LOW); break;
      default:
        digitalWrite(myled1, LOW); digitalWrite(myled2, LOW);digitalWrite(myled3, LOW); break;
    }
    delay(2000);
  }
}
