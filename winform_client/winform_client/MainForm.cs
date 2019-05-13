using GMap.NET;
using GMap.NET.WindowsForms;
using GMap.NET.WindowsForms.Markers;
using Newtonsoft.Json;
using System;
using System.Collections;
using System.Configuration;
using System.Drawing;
using System.Globalization;
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
                        case "LOCATION_UPDATED":
                            LocationUpdated(appMessage);
                            break;
                        case "UPDATE_INFO":
                            UpdateInfo(appMessage);
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
        private void LocationUpdated(AppMessage appMessage)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            string imei = item.SubItems[1].Text;
            if (imei.Equals(appMessage.imei))
            {
                CleanGmap();
                txtLastUpdate.Text = DateTime.Now.ToString("dd/MM/yy HH:mm:ss");
                locationHistories.Clear();
                ArrayList arrayList = JsonConvert.DeserializeObject<ArrayList>(appMessage.content.ToString());
                foreach (var i in arrayList)
                {
                    LocationMessage locationMessage = JsonConvert.DeserializeObject<LocationMessage>(i.ToString());
                    locationHistories.Add(locationMessage);
                }
                if (showOnlyCurrentLocation)
                {
                    MarkedLastLocation();
                }
                else
                {
                    MarkedAllLocation();
                }
            }
        }
        private void UpdateInfo(AppMessage appMessage)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            string imei = item.SubItems[1].Text;
            if (imei.Equals(appMessage.imei))
            {
                PhoneInfoMessage phoneInfoMessage = JsonConvert.DeserializeObject<PhoneInfoMessage>(appMessage.content.ToString());
                txtNetworkName.Text = phoneInfoMessage.networkName;
                txtNetworkType.Text = phoneInfoMessage.networkType;
                txtBatteryLevel.Text = phoneInfoMessage.batteryLevel + "%";
                txtCharging.Text = phoneInfoMessage.isCharging.ToString();
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

            txtDeviceName.Text = item.SubItems[0].Text;
            txtImei.Text = imei;
            txtLastUpdate.Text = "Unknown";
            txtNetworkName.Text = "Unknown";
            txtNetworkType.Text = "Unknown";
            txtBatteryLevel.Text = "Unknown";
            txtCharging.Text = "Unknown";

            //Update info
            UpdateInfo(imei);
            //Update location
            UpdateLocation(imei);
        }

        private void UpdateInfo(string imei)
        {
            AppMessage appMessage = new AppMessage();
            appMessage.command = "UPDATE_INFO";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void UpdateLocation(string imei)
        {
            AppMessage appMessage = new AppMessage();
            appMessage.command = "UPDATE_LOCATION";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));
        }

        private void CleanGmap()
        {
            gMap.Zoom = 5;
            gMapOverlay.Markers.Clear();
            gMap.Overlays.Add(gMapOverlay);
        }

        private void InitGmap()
        {
            gMap.MapProvider = GMap.NET.MapProviders.GoogleMapProvider.Instance;
            GMaps.Instance.Mode = AccessMode.ServerOnly;
            gMap.ShowCenter = false;
            gMap.Zoom = 16;
            gMap.DragButton = MouseButtons.Left;
        }

        private void MarkedLastLocation()
        {
            if (locationHistories.Count == 0) return;
            InitGmap();
            LocationMessage locationMessage = (LocationMessage)locationHistories[0];
            gMap.Position = new GMap.NET.PointLatLng(locationMessage.latitude, locationMessage.longitude);
            GMarkerGoogle marker = new GMarkerGoogle(new PointLatLng(locationMessage.latitude, locationMessage.longitude), GMarkerGoogleType.red)
            {
                ToolTipText = locationMessage.timeTracking.ToString("dd/MM/yy HH:mm:ss")
            };
            gMapOverlay.Markers.Add(marker);
            gMap.Overlays.Add(gMapOverlay);
        }

        private void MarkedAllLocation()
        {
            if (locationHistories.Count == 0) return;
            InitGmap();
            bool isFrist = true;
            foreach (LocationMessage l in locationHistories)
            {
                var styleMarker = isFrist ? GMarkerGoogleType.red : GMarkerGoogleType.red_small;
                GMarkerGoogle marker = new GMarkerGoogle(new PointLatLng(l.latitude, l.longitude), styleMarker)
                {
                    ToolTipText = l.timeTracking.ToString("dd/MM/yy HH:mm:ss")
                };
                gMapOverlay.Markers.Add(marker);
                isFrist = false;
            }

            //TODO fix here
            //for (int i = 0; i < locationHistories.Count - 1; i++)
            //{
            //    JsonLocationHistory s = (JsonLocationHistory)locationHistories[0];
            //    JsonLocationHistory e = (JsonLocationHistory)locationHistories[0];
            //    PointLatLng start = new PointLatLng(s.latitude, s.longitude);
            //    PointLatLng end = new PointLatLng(e.latitude, e.longitude);
            //    MapRoute route = GMap.NET.MapProviders.GoogleMapProvider.Instance.GetRoute(start, end, false, false, 15);
            //    GMapRoute r = new GMapRoute(route.Points, "a");
            //    gMapOverlay.Routes.Add(r);
            //}
            gMap.Overlays.Add(gMapOverlay);
            LocationMessage locationHistory = (LocationMessage)locationHistories[0];
            gMap.Position = new GMap.NET.PointLatLng(locationHistory.latitude, locationHistory.longitude);
        }

        private void CurrentToolStripMenuItem_Click(object sender, EventArgs e)
        {
            currentToolStripMenuItem.Checked = true;
            allToolStripMenuItem.Checked = false;
            showOnlyCurrentLocation = true;
            CleanGmap();
            MarkedLastLocation();
        }

        private void AllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            currentToolStripMenuItem.Checked = false;
            allToolStripMenuItem.Checked = true;
            showOnlyCurrentLocation = false;
            CleanGmap();
            MarkedAllLocation();
        }

        private static bool flagClickMarker = true;
        Thread t = null;
        public static void ChangeFlag()
        {
            Thread.Sleep(3000);
            flagClickMarker = true;
        }
        private void GMap_OnMarkerClick(GMapMarker item, MouseEventArgs e)
        {
            if (flagClickMarker)
            {
                double latitude = item.Position.Lat;
                double longitude = item.Position.Lng;
                System.Diagnostics.Process.Start("https://www.google.com/maps/search/?api=1&query=" +
                    latitude.ToString("G", CultureInfo.InvariantCulture) + "," + longitude.ToString("G", CultureInfo.InvariantCulture));
                flagClickMarker = false;
                if (t != null)
                {
                    if (t.IsAlive)
                    {
                        t.Interrupt();
                    }
                }
                t = new Thread(ChangeFlag);
                t.Start();
            }
        }

        private void Button2_Click(object sender, EventArgs e)
        {
            ListView1_Click(sender, e);
        }

        private void TurnOffServicesOnCurrentDeviceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            string imei = item.SubItems[1].Text;

            AppMessage appMessage = new AppMessage();
            appMessage.command = "TURN_OFF_SERVICES";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));

            MessageBox.Show("Turn off all services on current device", "Successfully", MessageBoxButtons.OK);
        }

        private void TurnOnServicesOnCurrentDeviceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (listView1.SelectedItems.Count == 0) return;
            ListViewItem item = listView1.SelectedItems[0];
            string imei = item.SubItems[1].Text;

            AppMessage appMessage = new AppMessage();
            appMessage.command = "TURN_ON_SERVICES";
            appMessage.imei = imei;

            string json = JsonConvert.SerializeObject(appMessage);

            var broad = new StompMessage("SEND", json);
            broad["content-type"] = "application/json";
            broad["destination"] = "/app/manager";
            ws.Send(serializer.Serialize(broad));

            MessageBox.Show("Turn on all services on current device", "Successfully", MessageBoxButtons.OK);
        }
    }
}
