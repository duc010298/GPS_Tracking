using GMap.NET;
using GMap.NET.WindowsForms;
using GMap.NET.WindowsForms.Markers;
using Newtonsoft.Json;
using System;
using System.Collections;
using System.Configuration;
using System.Globalization;
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
        private readonly GMapOverlay gMapOverlay;
        private bool showOnlyCurrentLocation;

        public MainForm()
        {
            InitializeComponent();
            locationHistories = new ArrayList();
            gMapOverlay = new GMapOverlay("marker");
            showOnlyCurrentLocation = true;

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
                string str = jsonDevice.deviceName + " | " + jsonDevice.imei;
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

                UpdateInfo(imei);
                GetLocation(imei);
            }
        }

        private void GetLocation(CustomAppMessage customAppMessage)
        {
            locationHistories.Clear();
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
            if(showOnlyCurrentLocation)
            {
                MarkedLastLocation();
            } else
            {
                RoutesAllMarker();
            }
        }

        void ws_OnClose(object sender, CloseEventArgs e)
        {

        }
        void ws_OnError(object sender, ErrorEventArgs e)
        {
            
        }

        private void ListBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            CleanGmap();
            string data = listBox1.GetItemText(listBox1.SelectedItem);
            if (String.IsNullOrEmpty(data)) return;
            string[] arrayValue = data.Split(new[] { " | " }, StringSplitOptions.None);

            txtDeviceName.Text = arrayValue[0];
            txtImei.Text = arrayValue[1];
            txtLastUpdate.Text = "Unknown";
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

        private void CleanGmap()
        {
            gMap.Zoom = 0;
            gMapOverlay.Markers.Clear();
        }

        private void MarkedLastLocation()
        {
            if (locationHistories.Count == 0) return;
            InitGmap();
            JsonLocationHistory locationHistory = (JsonLocationHistory) locationHistories[0];
            gMap.Position = new GMap.NET.PointLatLng(locationHistory.latitude, locationHistory.longitude);
            GMarkerGoogle marker = new GMarkerGoogle(new PointLatLng(locationHistory.latitude, locationHistory.longitude), GMarkerGoogleType.red)
            {
                ToolTipText = locationHistory.timeTracking.ToString("dd/MM/yy HH:mm:ss")
            };
            gMapOverlay.Markers.Add(marker);
            gMap.Overlays.Add(gMapOverlay);
        }

        private void RoutesAllMarker()
        {
            if (locationHistories.Count == 0) return;
            InitGmap();
            bool isFrist = true;
            foreach(JsonLocationHistory l in locationHistories)
            {
                var styleMarker = isFrist ? GMarkerGoogleType.red : GMarkerGoogleType.red_small;
                GMarkerGoogle marker = new GMarkerGoogle(new PointLatLng(l.latitude, l.longitude), styleMarker)
                {
                    ToolTipText = l.timeTracking.ToString("dd/MM/yy HH:mm:ss")
                };
                gMapOverlay.Markers.Add(marker);
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
            JsonLocationHistory locationHistory = (JsonLocationHistory)locationHistories[0];
            gMap.Position = new GMap.NET.PointLatLng(locationHistory.latitude, locationHistory.longitude);
        }

        private void InitGmap()
        {
            gMap.MapProvider = GMap.NET.MapProviders.GoogleMapProvider.Instance;
            GMaps.Instance.Mode = AccessMode.ServerOnly;
            gMap.ShowCenter = false;
            gMap.Zoom = 15;
            gMap.DragButton = MouseButtons.Left;
        }

        private void GMap_OnMarkerClick(GMapMarker item, MouseEventArgs e)
        {
            double latitude = item.Position.Lat;
            double longitude = item.Position.Lng;
            System.Diagnostics.Process.Start("https://www.google.com/maps/search/?api=1&query=" + 
                latitude.ToString("G", CultureInfo.InvariantCulture) + "," + longitude.ToString("G", CultureInfo.InvariantCulture));
        }

        private void CurrentLocationToolStripMenuItem_Click(object sender, EventArgs e)
        {
            currentLocationToolStripMenuItem.Checked = true;
            routesToolStripMenuItem.Checked = false;
            showOnlyCurrentLocation = true;
            CleanGmap();
            MarkedLastLocation();
        }

        private void RoutesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            currentLocationToolStripMenuItem.Checked = false;
            routesToolStripMenuItem.Checked = true;
            showOnlyCurrentLocation = false;
            CleanGmap();
            RoutesAllMarker();
        }
    }
}
