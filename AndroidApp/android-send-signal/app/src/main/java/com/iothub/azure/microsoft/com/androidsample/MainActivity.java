package com.iothub.azure.microsoft.com.androidsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.net.IotHubMessageUri;

import java.io.IOException;
import java.net.URISyntaxException;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

    Switch simpleSwitch1, simpleSwitch2, simpleSwitch3;
    Button submit;

    String connString = "HostName=UNOr3.azure-devices.net;DeviceId=MyFirstPythonDevice;SharedAccessKey=PDv410RzEg8qr0D7ZfEaeGHWl6istLbEb9x7rEvERwc=";
    String deviceId = "MyFirstPythonDevice";
    String temperature;
    double humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSwitch1 = (Switch) findViewById(R.id.simpleSwitch1);
        simpleSwitch2 = (Switch) findViewById(R.id.simpleSwitch2);
        simpleSwitch3 = (Switch) findViewById(R.id.simpleSwitch3);
        submit = (Button) findViewById(R.id.submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1, statusSwitch2, statusSwitch3;
                String act1, act2, act3;
                if (simpleSwitch1.isChecked()) {
                    statusSwitch1 = simpleSwitch1.getTextOn().toString();
                    act1 = "1";
                }
                else {
                    statusSwitch1 = simpleSwitch1.getTextOff().toString();
                    act1 = "2";
                }
                if (simpleSwitch2.isChecked()) {
                    statusSwitch2 = simpleSwitch2.getTextOn().toString();
                    act2 = "3";
                }
                else {
                    statusSwitch2 = simpleSwitch2.getTextOff().toString();
                    act2 = "4";
                }
                if (simpleSwitch3.isChecked()) {
                    statusSwitch3 = simpleSwitch3.getTextOn().toString();
                    act3 = "5";
                }
                else {
                    statusSwitch3 = simpleSwitch3.getTextOff().toString();
                    act3 = "6";
                }

                try {
                    SendMessage(act1);
                    SendMessage(act2);
                    SendMessage(act3);
                }
                catch(IOException e1)
                {
                    System.out.println("Exception while opening IoTHub connection: " + e1.toString());
                }
                catch(Exception e2)
                {
                    System.out.println("Exception while opening IoTHub connection: " + e2.toString());
                }

                Toast.makeText(getApplicationContext(), "First :" + statusSwitch1 + "\n" + "Second :" + statusSwitch2 + "\n" + "Third :" + statusSwitch3, Toast.LENGTH_LONG).show(); // display the current state for switch's
            }
        });
    }

    public void SendMessage(String act) throws URISyntaxException, IOException
    {
        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
        DeviceClient client = new DeviceClient(connString, protocol);

        try {
            client.open();
        }
        catch(IOException e1)
        {
            System.out.println("Exception while opening IoTHub connection: " + e1.toString());
        }
        catch(Exception e2)
        {
            System.out.println("Exception while opening IoTHub connection: " + e2.toString());
        }

        String msgStr = act;
            try
            {
                Message msg = new Message(msgStr);
                System.out.println(msgStr);
                EventCallback eventCallback = new EventCallback();
                client.sendEventAsync(msg, eventCallback, 0);
            }
            catch (Exception e)
            {
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        client.close();
    }

    protected static class MessageCallbackMqtt implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }
    }

    protected static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context){
            Integer i = (Integer) context;
            System.out.println("IoT Hub responded to message "+i.toString()
                    + " with status " + status.name());
        }
    }

    protected static class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult execute(Message msg, Object context)
        {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            int switchVal = counter.get() % 3;
            IotHubMessageResult res;
            switch (switchVal)
            {
                case 0:
                    res = IotHubMessageResult.COMPLETE;
                    break;
                case 1:
                    res = IotHubMessageResult.ABANDON;
                    break;
                case 2:
                    res = IotHubMessageResult.REJECT;
                    break;
                default:
                    // should never happen.
                    throw new IllegalStateException("Invalid message result specified.");
            }

            System.out.println("Responding to message " + counter.toString() + " with " + res.name());

            counter.increment();

            return res;
        }
    }

    protected static class Counter
    {
        protected int num;

        public Counter(int num)
        {
            this.num = num;
        }

        public int get()
        {
            return this.num;
        }

        public void increment()
        {
            this.num++;
        }

        @Override
        public String toString()
        {
            return Integer.toString(this.num);
        }
    }

}
