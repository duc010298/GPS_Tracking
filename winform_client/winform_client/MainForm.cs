using GMap.NET.WindowsForms;
using System;
using System.Collections;
using System.Threading;
using System.Windows.Forms;
using WebSocketSharp;
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
            
            listView1.Items.Add(new ListViewItem(new string[] { "Bphone 2017", "356060070359140", "Online" }));
        }
    }
}
