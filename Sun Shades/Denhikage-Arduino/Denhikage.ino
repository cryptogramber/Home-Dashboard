#include <SPI.h>
#include <Ethernet.h>

byte mac[] = { 0x, 0x, 0x, 0x, 0x, 0x };                    // mac address is on sticker on shield
IPAddress ip(x,x,x,x);                                      // remove this if using dhcp, change to ipif not
EthernetServer server(80);                                  // create a server at port 80

#define LED_CH1    A0
#define LED_CH2    A1
#define LED_CH3    A2
#define LED_CH4    A3
#define CTRL_UP    9
#define CTRL_STOP  8
#define CTRL_DOWN  7
#define CTRL_SEL   5
int incomingCmd = 0;

void setup() {  
    pinMode(CTRL_UP, OUTPUT);          
    pinMode(CTRL_STOP, OUTPUT);
    pinMode(CTRL_DOWN, OUTPUT);
    pinMode(CTRL_SEL, OUTPUT);
    Ethernet.begin(mac, ip);          // initialize Ethernet device
    server.begin();                   // start to listen for clients
}

int digital_button(int button) {
    digitalWrite(button, HIGH);
    delay(150);
    digitalWrite(button, LOW);
}

int which_chan() {
    int currentCh = 0;
    int statCh1 = 1;
    int statCh2 = 1;
    int statCh3 = 1;
    int statCh4 = 1;
    int statChans;
    
    for (int i = 0 ; i < 10 ; i++){
        statCh1 &= digitalRead(LED_CH1);
        statCh2 &= digitalRead(LED_CH2);
        statCh3 &= digitalRead(LED_CH3);
        statCh4 &= digitalRead(LED_CH4);
        delay(10);
    }
  
    statChans = statCh4<<3 | statCh3<<2 | statCh2<<1 | statCh1;
    switch(statChans) {
        case 14: currentCh=1; break; // LED1, channel 1 selected 
        case 13: currentCh=2; break; // LED2, channel 2 selected 
        case 11: currentCh=3; break; // LED3, channel 3 selected 
        case 7:  currentCh=4; break; // LED4, channel 4 selected 
        case 0:  currentCh=5; break; // ALL,  channel 5 selected 
        default:  currentCh=0;       // no LEDs are blinking 
    }
    return currentCh;
}

bool select_chan(int targetCh) {
    int currCh;            // current channel
    int prevCh=-1;         // previous channel 
    int t = 0;             // counter

    while ((currCh = which_chan()) != targetCh && t < 10) {
        t++;
        if (prevCh < 0 || (currCh != prevCh && currCh > 0)) {
            digital_button(CTRL_SEL);
            prevCh = currCh;
            t = 0;
        }
    }
    return t < 10 || currCh == targetCh;
}

int get_url_command(EthernetClient client, char *s, int n) {
    int len;
    s[0] = 0;
    if (client.find("GET /")) {
        len=client.readBytesUntil(' ',s,n);
        s[len] = 0; 
    }
}

int process_command(char* command) {
    int channelNum = 0;
    if (strlen(command) == 3) {
        switch (command[0]) {
            case 'c':
                switch(command[1]) {    // dirty hack because arduino casting is borked
                    case '1': channelNum = 1; break;
                    case '2': channelNum = 2; break;
                    case '3': channelNum = 3; break;
                    case '4': channelNum = 4; break;
                    case '5': channelNum = 5; break;
                    default: channelNum = 0; break;
                }
                if (channelNum >= 1 && channelNum <= 5) {
                      if (select_chan(channelNum)) {
                           switch(command[2]) {
                               case 'u':  digital_button(CTRL_UP); break;
                               case 'd':  digital_button(CTRL_DOWN); break;
                               case 's':  digital_button(CTRL_STOP); break;
                               default: break; 
                           }
                      } 
                      break;
                }
            default: break;   
        } 
    }
}

int return_page(EthernetClient client, char *s) {
    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: text/html");
    client.println("Connection: close");
    client.println();
    client.println("<!DOCTYPE html>");
    client.println("<html>");
    client.println("<head>");
    client.println("<title>Denhikage Shade Controller</title>");
    client.println("<p>");
    client.println(s);
    client.println("</p>");
    client.println("</body>");
    client.println("</html>"); 
}

void loop() {
    char command[5];
    EthernetClient client = server.available();          // is there an available client
    if (client) {                                        // if yes, then proceed
        while (client.connected()) {
            if (client.available()) {                    
                get_url_command(client, command, 3);
                process_command(command);
                return_page(client, command); 
                break;
            } 
        } 
        delay(1);                                        // give the system time to process
        client.stop();                                   // close the connection
    } 
}
