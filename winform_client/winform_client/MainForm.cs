﻿using Newtonsoft.Json;
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

        public string RandomString(int length)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            var random = new Random();
            return new string(Enumerable.Repeat(chars, length)
              .Select(s => s[random.Next(s.Length)]).ToArray());
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
                    if (command.Equals("GET_DEVICE_LIST"))
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
                }
            });
        }

        void ws_OnClose(object sender, CloseEventArgs e)
        {

        }
        void ws_OnError(object sender, ErrorEventArgs e)
        {
            
        }

        private void processAddListDevice(ArrayList jsonDevices)
        {
            listBox1.Items.Clear();
            foreach (JsonDevice jsonDevice in jsonDevices) {
                string str = jsonDevice.deviceName + " | " + jsonDevice.imei + " | ";
                if (jsonDevice.isOnline)
                {
                    str += "Online";
                } else
                {
                    str += jsonDevice.lastOnline.ToString("dd/MM/yy HH:mm:ss");
                }
                listBox1.Items.Add(str);
            }
        }
    }
}
