using Newtonsoft.Json;
using System;
using System.Collections;
using System.Configuration;
using System.Linq;
using System.Threading;
using System.Windows.Forms;
using WebSocketSharp;
using winform_client.entity;
using winform_client.Stomp;

namespace winform_client
{
    public partial class MainForm : Form
    {
        private readonly WebSocket ws;
        private readonly StompMessageSerializer serializer;
        private readonly string clientId;
        private ArrayList locationHistories;

        public MainForm()
        {
            InitializeComponent();

            ws = new WebSocket(ConfigurationManager.AppSettings["socket-url"]);
            serializer = new StompMessageSerializer();

            ws.OnMessage += ws_OnMessage;
            ws.OnClose += ws_OnClose;
            ws.OnOpen += ws_OnOpen;
            ws.OnError += ws_OnError;
            ws.Connect();

            clientId = RandomString(5);

            var connect = new StompMessage("CONNECT");
            connect["accept-version"] = "1.1";
            connect["heart-beat"] = "10000,10000";
            connect["Authorization"] = StaticToken.token;
            ws.Send(serializer.Serialize(connect));

            var sub = new StompMessage("SUBSCRIBE");
            sub["id"] = clientId;
            sub["destination"] = "/user/topic/manager";
            ws.Send(serializer.Serialize(sub));
        }

        public string RandomString(int length)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            var random = new Random();
            return new string(Enumerable.Repeat(chars, length)
              .Select(s => s[random.Next(s.Length)]).ToArray());
        }

        private void LogoutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            StaticToken.token = null;
            OpenLoginForm();
        }

        private void OpenLoginForm()
        {
            Thread t = new Thread(OpenForm);
            t.SetApartmentState(ApartmentState.STA);
            t.Start();
        }

        private void OpenForm()
        {
            Action close = () => this.Close();
            if (InvokeRequired)
            {
                Invoke(close);
            }
            else
            {
                close();
            }
            Application.Run(new LoginForm());
        }

        private void Button1_Click(object sender, EventArgs e)
        {
            txtDeviceName.Text = "Unknown";
            txtImei.Text = "Unknown";
            txtLastUpdate.Text = "Unknown";
            txtNetworkName.Text = "Unknown";
            txtNetworkType.Text = "Unknown";
            txtBatteryLevel.Text = "Unknown";
            txtCharging.Text = "Unknown";

            CustomAppMessage custom = new CustomAppMessage();
            custom.command = "GET_DEVICE_LIST";

            string json = JsonConvert.SerializeObject(custom);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            Button1_Click(sender, e);
        }

        void ws_OnOpen(object sender, EventArgs e)
        {

        }

        void ws_OnMessage(object sender, MessageEventArgs e)
        {
            this.BeginInvoke((Action)delegate ()
            {
                StompMessage msg = serializer.Deserialize(e.Data);
                if (msg.Command == StompFrame.MESSAGE)
                {
                    CustomAppMessage customAppMessage = JsonConvert.DeserializeObject<CustomAppMessage>(msg.Body);
                    string command = customAppMessage.command;
                    switch(command)
                    {
                        case "GET_DEVICE_LIST":
                            GetDeviceList(customAppMessage);
                            break;
                        case "UPDATE_INFO":
                            UpdateInfo(customAppMessage);
                            break;
                        case "LOCATION_UPDATED":
                            LocationUpdated(customAppMessage);
                            break;
                        case "GET_LOCATION":
                            GetLocation(customAppMessage);
                            break;
                    }
                }
            });
        }

        private void GetDeviceList(CustomAppMessage customAppMessage)
        {
            ArrayList jsonDevices = new ArrayList();
            ArrayList arrayList = JsonConvert.DeserializeObject<ArrayList>(customAppMessage.content.ToString());
            foreach (var item in arrayList)
            {
                JsonDevice jsonDevice = JsonConvert.DeserializeObject<JsonDevice>(item.ToString());
                jsonDevices.Add(jsonDevice);
            }
            processAddListDevice(jsonDevices);
        }

        private void processAddListDevice(ArrayList jsonDevices)
        {
            listBox1.Items.Clear();
            foreach (JsonDevice jsonDevice in jsonDevices)
            {
                string str = jsonDevice.deviceName + " | " + jsonDevice.imei + " | " +
                    jsonDevice.lastUpdate.ToString("dd/MM/yy HH:mm:ss");
                listBox1.Items.Add(str);
            }
        }

        private void UpdateInfo(CustomAppMessage customAppMessage)
        {
            string imei = customAppMessage.imei;
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (String.IsNullOrEmpty(data)) return;
            string[] arrayValue = data.Split(new[] { " | " }, StringSplitOptions.None);
            if (imei.Equals(arrayValue[1]))
            {
                PhoneInfoUpdate phoneInfoUpdate = JsonConvert.DeserializeObject<PhoneInfoUpdate>(customAppMessage.content.ToString());
                txtNetworkName.Text = phoneInfoUpdate.networkName;
                txtNetworkType.Text = phoneInfoUpdate.networkType;
                txtBatteryLevel.Text = phoneInfoUpdate.batteryLevel + "%";
                txtCharging.Text = phoneInfoUpdate.isCharging.ToString();
            }
        }

        private void LocationUpdated(CustomAppMessage customAppMessage)
        {
            string imei = customAppMessage.imei;
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (String.IsNullOrEmpty(data)) return;
            string[] arrayValue = data.Split(new[] { " | " }, StringSplitOptions.None);
            if (imei.Equals(arrayValue[1]))
            {
                txtLastUpdate.Text = DateTime.Now.ToString("dd/MM/yy HH:mm:ss");
                CustomAppMessage appMessage = new CustomAppMessage();
                appMessage.command = "UPDATE_INFO";
                appMessage.imei = arrayValue[1];

                string json = JsonConvert.SerializeObject(appMessage);

                var broad = new StompMessage("SEND", json);
                broad["content-type"] = "application/json";
                broad["destination"] = "/app/manager";
                ws.Send(serializer.Serialize(broad));
            }
        }

        private void GetLocation(CustomAppMessage customAppMessage)
        {
            locationHistories = new ArrayList();
            string imei = customAppMessage.imei;
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (String.IsNullOrEmpty(data)) return;
            string[] arrayValue = data.Split(new[] { " | " }, StringSplitOptions.None);
            if (imei.Equals(arrayValue[1]))
            {
                ArrayList arrayList = JsonConvert.DeserializeObject<ArrayList>(customAppMessage.content.ToString());
                foreach (var item in arrayList)
                {
                    JsonLocationHistory jsonLocationHistory = JsonConvert.DeserializeObject<JsonLocationHistory>(item.ToString());
                    locationHistories.Add(jsonLocationHistory);
                }
            }
            Console.WriteLine(locationHistories.Count);
            //TODO draw map
        }

        void ws_OnClose(object sender, CloseEventArgs e)
        {

        }
        void ws_OnError(object sender, ErrorEventArgs e)
        {
            
        }

        private void ListBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (String.IsNullOrEmpty(data)) return;
            string[] arrayValue = data.Split(new[] { " | " }, StringSplitOptions.None);

            txtDeviceName.Text = arrayValue[0];
            txtImei.Text = arrayValue[1];
            txtLastUpdate.Text = arrayValue[2];
            txtNetworkName.Text = "Unknown";
            txtNetworkType.Text = "Unknown";
            txtBatteryLevel.Text = "Unknown";
            txtCharging.Text = "Unknown";

            //Update info
            UpdateInfo(arrayValue[1]);

            //Update location to server
            CallUpdateLocationToServer(arrayValue[1]);

            //Get location
            GetLocation(arrayValue[1]);
        }

        private void UpdateInfo(string imei)
        {
            CustomAppMessage appMessage = new CustomAppMessage();
            appMessage.command = "UPDATE_INFO";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void CallUpdateLocationToServer(string imei)
        {
            CustomAppMessage appMessage = new CustomAppMessage();
            appMessage.command = "UPDATE_LOCATION";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void GetLocation(string imei)
        {
            CustomAppMessage appMessage = new CustomAppMessage();
            appMessage.command = "GET_LOCATION";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void Button2_Click(object sender, EventArgs e)
        {
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (!String.IsNullOrEmpty(data))
            {
                ListBox1_SelectedIndexChanged(sender, e);
            }
        }
    }
}
