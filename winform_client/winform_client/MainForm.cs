using GMap.NET.WindowsForms;
using Newtonsoft.Json;
using System;
using System.Collections;
using System.Configuration;
using System.Drawing;
using System.Threading;
using System.Windows.Forms;
using WebSocketSharp;
using winform_client.Entity;
using winform_client.Stomp;

namespace winform_client
{
    public partial class MainForm : Form
    {
        private WebSocket ws;
        private readonly StompMessageSerializer serializer;
        private readonly ArrayList locationHistories;
        private readonly GMapOverlay gMapOverlay;
        private bool showOnlyCurrentLocation;
        private bool isLogout;
        public MainForm()
        {
            InitializeComponent();
            isLogout = false;
            locationHistories = new ArrayList();
            gMapOverlay = new GMapOverlay("marker");
            showOnlyCurrentLocation = true;
            serializer = new StompMessageSerializer();

            ConnectToServer();
        }

        private void ConnectToServer()
        {
            ws = new WebSocket(ConfigurationManager.AppSettings["socket-url"]);
            ws.OnOpen += Ws_OnOpen;
            ws.OnMessage += Ws_OnMessage;
            ws.OnClose += Ws_OnClose;
            ws.OnError += Ws_OnError;
            ws.Connect();
        }

        void Ws_OnOpen(object sender, EventArgs e)
        {
            var connect = new StompMessage("CONNECT");
            connect["accept-version"] = "1.1";
            connect["heart-beat"] = "10000,10000";
            connect["Authorization"] = StaticToken.token;
            ws.Send(serializer.Serialize(connect));

            var sub = new StompMessage("SUBSCRIBE");
            //This is only random id
            sub["id"] = "987654";
            sub["destination"] = "/user/topic/manager";
            ws.Send(serializer.Serialize(sub));
        }
        void Ws_OnMessage(object sender, MessageEventArgs e)
        {
            this.BeginInvoke((Action)delegate ()
            {
                StompMessage msg = serializer.Deserialize(e.Data);
                if (msg.Command == "MESSAGE")
                {
                    AppMessage appMessage = JsonConvert.DeserializeObject<AppMessage>(msg.Body);
                    string command = appMessage.command;
                    switch (command)
                    {
                        case "GET_DEVICE_LIST":
                            GetDeviceList(appMessage);
                            break;
                        case "DEVICE_ONLINE":
                            SetDeviceOnline(appMessage);
                            break;
                    }
                }
            });
        }
        private void GetDeviceList(AppMessage appMessage)
        {
            ArrayList deviceMessages = new ArrayList();
            ArrayList arrayList = JsonConvert.DeserializeObject<ArrayList>(appMessage.content.ToString());
            foreach (var item in arrayList)
            {
                DeviceMessage deviceMessage = JsonConvert.DeserializeObject<DeviceMessage>(item.ToString());
                deviceMessages.Add(deviceMessage);
            }
            listView1.Items.Clear();
            listView1.Refresh();
            foreach (DeviceMessage deviceMessage in deviceMessages)
            {
                string[] row = { deviceMessage.deviceName, deviceMessage.imei, "Offline" };
                listView1.Items.Add(new ListViewItem(row));
            }
            appMessage = new AppMessage();
            appMessage.command = "CHECK_ONLINE";

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }
        private void SetDeviceOnline(AppMessage appMessage)
        {
            string imei = appMessage.imei;
            foreach (ListViewItem i in listView1.Items)
            {
                if(i.SubItems[1].Text.Equals(imei))
                {
                    i.SubItems[2].Text = "Online";
                }
            }
        }
        void Ws_OnClose(object sender, CloseEventArgs e)
        {
            if (!isLogout)
            {
                Thread.Sleep(3000);
                ConnectToServer();
            }
        }
        void Ws_OnError(object sender, ErrorEventArgs e)
        {
            Thread.Sleep(3000);
            if (ws.IsAlive)
            {
                ws.Close();
            }
            ConnectToServer();
        }

        private void LogoutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            isLogout = true;
            ws.Close();
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

        private void MainForm_Load(object sender, EventArgs e)
        {
            Button1_Click(sender, e);
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

            AppMessage appMessage = new AppMessage();
            appMessage.command = "GET_DEVICE_LIST";

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void ListView1_Leave(object sender, EventArgs e)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            item.BackColor = Color.FromArgb(0, 120, 215);
            item.ForeColor = Color.White;
        }

        private void ListView1_SelectedIndexChanged(object sender, EventArgs e)
        {
            foreach(ListViewItem i in listView1.Items)
            {
                i.BackColor = Color.White;
                i.ForeColor = Color.Black;
            }
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            item.BackColor = Color.FromArgb(0, 120, 215);
            item.ForeColor = Color.White;
        }

        private void ListView1_Click(object sender, EventArgs e)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            string imei = item.SubItems[1].Text;
            
        }
    }
}
