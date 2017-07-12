using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.ServiceBus.Messaging;
using System.Threading;
using System.Timers;
using System.IO.Ports;

namespace ConsoleApp2
{
    class Program
    {
        static string connectionString = "HostName=UNOr3.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=1TNcogW41lhNVvMgK0stQE6UomuM1Fe9+xngSMOZuRk=";
        static string iotHubD2cEndpoint = "messages/events";
        static EventHubClient eventHubClient;
        //static SerialPort currentPort = new SerialPort("COM4", 9600);
        
        private static async Task ReceiveMessagesFromDeviceAsync(string partition, CancellationToken ct)
        {
            var eventHubReceiver = eventHubClient.GetDefaultConsumerGroup().CreateReceiver(partition, DateTime.UtcNow);
            while (true)
            {
                if (ct.IsCancellationRequested) break;
                EventData eventData = await eventHubReceiver.ReceiveAsync();
                if (eventData == null) continue;

                var data = Encoding.UTF8.GetString(eventData.GetBytes());
                Console.WriteLine(data);

                //currentPort.Write(data.ToString());
            }
        }
        static void Main(string[] args)
        {
            //currentPort.Open();
            Console.WriteLine("Press ctrl+C to exit\n");
            eventHubClient = EventHubClient.CreateFromConnectionString(connectionString, iotHubD2cEndpoint);

            var d2cPartitions = eventHubClient.GetRuntimeInformation().PartitionIds;

            CancellationTokenSource cts = new CancellationTokenSource();

            System.Console.CancelKeyPress += (s, e) =>
            {
                e.Cancel = true;
                cts.Cancel();
                Console.WriteLine("Bye...");
            };

            var tasks = new List<Task>();
            foreach (string partition in d2cPartitions)
            {
                tasks.Add(ReceiveMessagesFromDeviceAsync(partition, cts.Token));
            }
            Task.WaitAll(tasks.ToArray());

            //currentPort.Close();
        }
    }
}
