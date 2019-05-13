namespace winform_client
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.Windows.Forms.ColumnHeader Name;
            System.Windows.Forms.ColumnHeader IMEI;
            System.Windows.Forms.ColumnHeader Status;
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.accountToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.logoutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.modeMarkerToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.currentToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.allToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.devicesToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.label1 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.button1 = new System.Windows.Forms.Button();
            this.listView1 = new System.Windows.Forms.ListView();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.txtCharging = new System.Windows.Forms.Label();
            this.txtBatteryLevel = new System.Windows.Forms.Label();
            this.txtNetworkType = new System.Windows.Forms.Label();
            this.txtNetworkName = new System.Windows.Forms.Label();
            this.txtLastUpdate = new System.Windows.Forms.Label();
            this.txtImei = new System.Windows.Forms.Label();
            this.txtDeviceName = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.button2 = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.gMap = new GMap.NET.WindowsForms.GMapControl();
            Name = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
            IMEI = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
            Status = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
            this.menuStrip1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // Name
            // 
            Name.Text = "Name";
            Name.Width = 140;
            // 
            // IMEI
            // 
            IMEI.Text = "IMEI";
            IMEI.Width = 120;
            // 
            // Status
            // 
            Status.Text = "Status";
            // 
            // menuStrip1
            // 
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.accountToolStripMenuItem,
            this.modeMarkerToolStripMenuItem,
            this.devicesToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(1107, 24);
            this.menuStrip1.TabIndex = 0;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // accountToolStripMenuItem
            // 
            this.accountToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.logoutToolStripMenuItem});
            this.accountToolStripMenuItem.Name = "accountToolStripMenuItem";
            this.accountToolStripMenuItem.Size = new System.Drawing.Size(64, 20);
            this.accountToolStripMenuItem.Text = "Account";
            // 
            // logoutToolStripMenuItem
            // 
            this.logoutToolStripMenuItem.Name = "logoutToolStripMenuItem";
            this.logoutToolStripMenuItem.Size = new System.Drawing.Size(112, 22);
            this.logoutToolStripMenuItem.Text = "Logout";
            this.logoutToolStripMenuItem.Click += new System.EventHandler(this.LogoutToolStripMenuItem_Click);
            // 
            // modeMarkerToolStripMenuItem
            // 
            this.modeMarkerToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.currentToolStripMenuItem,
            this.allToolStripMenuItem});
            this.modeMarkerToolStripMenuItem.Name = "modeMarkerToolStripMenuItem";
            this.modeMarkerToolStripMenuItem.Size = new System.Drawing.Size(90, 20);
            this.modeMarkerToolStripMenuItem.Text = "Mode marker";
            // 
            // currentToolStripMenuItem
            // 
            this.currentToolStripMenuItem.Checked = true;
            this.currentToolStripMenuItem.CheckState = System.Windows.Forms.CheckState.Checked;
            this.currentToolStripMenuItem.Name = "currentToolStripMenuItem";
            this.currentToolStripMenuItem.Size = new System.Drawing.Size(114, 22);
            this.currentToolStripMenuItem.Text = "Current";
            this.currentToolStripMenuItem.Click += new System.EventHandler(this.CurrentToolStripMenuItem_Click);
            // 
            // allToolStripMenuItem
            // 
            this.allToolStripMenuItem.Name = "allToolStripMenuItem";
            this.allToolStripMenuItem.Size = new System.Drawing.Size(114, 22);
            this.allToolStripMenuItem.Text = "All";
            this.allToolStripMenuItem.Click += new System.EventHandler(this.AllToolStripMenuItem_Click);
            // 
            // devicesToolStripMenuItem
            // 
            this.devicesToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem,
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem});
            this.devicesToolStripMenuItem.Name = "devicesToolStripMenuItem";
            this.devicesToolStripMenuItem.Size = new System.Drawing.Size(59, 20);
            this.devicesToolStripMenuItem.Text = "Devices";
            // 
            // turnOffServicesOnCurrentDeviceToolStripMenuItem
            // 
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem.Name = "turnOffServicesOnCurrentDeviceToolStripMenuItem";
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem.Size = new System.Drawing.Size(256, 22);
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem.Text = "Turn off services on current device";
            this.turnOffServicesOnCurrentDeviceToolStripMenuItem.Click += new System.EventHandler(this.TurnOffServicesOnCurrentDeviceToolStripMenuItem_Click);
            // 
            // turnOnServicesOnCurrentDeviceToolStripMenuItem
            // 
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem.Name = "turnOnServicesOnCurrentDeviceToolStripMenuItem";
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem.Size = new System.Drawing.Size(256, 22);
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem.Text = "Turn on services on current device";
            this.turnOnServicesOnCurrentDeviceToolStripMenuItem.Click += new System.EventHandler(this.TurnOnServicesOnCurrentDeviceToolStripMenuItem_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft YaHei", 18F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.ForeColor = System.Drawing.Color.Blue;
            this.label1.Location = new System.Drawing.Point(12, 24);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(289, 31);
            this.label1.TabIndex = 1;
            this.label1.Text = "GPS Tracking Manager";
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox1.Controls.Add(this.button1);
            this.groupBox1.Controls.Add(this.listView1);
            this.groupBox1.Location = new System.Drawing.Point(18, 68);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(338, 350);
            this.groupBox1.TabIndex = 2;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Devices";
            // 
            // button1
            // 
            this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button1.Image = global::winform_client.Properties.Resources.refresh1;
            this.button1.Location = new System.Drawing.Point(300, 19);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(32, 32);
            this.button1.TabIndex = 1;
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.Button1_Click);
            // 
            // listView1
            // 
            this.listView1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.listView1.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            Name,
            IMEI,
            Status});
            this.listView1.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.listView1.FullRowSelect = true;
            this.listView1.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.Nonclickable;
            this.listView1.Location = new System.Drawing.Point(6, 57);
            this.listView1.MultiSelect = false;
            this.listView1.Name = "listView1";
            this.listView1.Size = new System.Drawing.Size(326, 287);
            this.listView1.TabIndex = 0;
            this.listView1.UseCompatibleStateImageBehavior = false;
            this.listView1.View = System.Windows.Forms.View.Details;
            this.listView1.SelectedIndexChanged += new System.EventHandler(this.ListView1_SelectedIndexChanged);
            this.listView1.Click += new System.EventHandler(this.ListView1_Click);
            this.listView1.Leave += new System.EventHandler(this.ListView1_Leave);
            // 
            // groupBox2
            // 
            this.groupBox2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox2.Controls.Add(this.txtCharging);
            this.groupBox2.Controls.Add(this.txtBatteryLevel);
            this.groupBox2.Controls.Add(this.txtNetworkType);
            this.groupBox2.Controls.Add(this.txtNetworkName);
            this.groupBox2.Controls.Add(this.txtLastUpdate);
            this.groupBox2.Controls.Add(this.txtImei);
            this.groupBox2.Controls.Add(this.txtDeviceName);
            this.groupBox2.Controls.Add(this.label8);
            this.groupBox2.Controls.Add(this.label7);
            this.groupBox2.Controls.Add(this.label6);
            this.groupBox2.Controls.Add(this.label5);
            this.groupBox2.Controls.Add(this.label4);
            this.groupBox2.Controls.Add(this.label3);
            this.groupBox2.Controls.Add(this.label2);
            this.groupBox2.Controls.Add(this.button2);
            this.groupBox2.Location = new System.Drawing.Point(18, 435);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(338, 241);
            this.groupBox2.TabIndex = 3;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Device Information";
            // 
            // txtCharging
            // 
            this.txtCharging.AutoSize = true;
            this.txtCharging.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtCharging.Location = new System.Drawing.Point(88, 210);
            this.txtCharging.Name = "txtCharging";
            this.txtCharging.Size = new System.Drawing.Size(60, 13);
            this.txtCharging.TabIndex = 27;
            this.txtCharging.Text = "Unknown";
            // 
            // txtBatteryLevel
            // 
            this.txtBatteryLevel.AutoSize = true;
            this.txtBatteryLevel.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtBatteryLevel.Location = new System.Drawing.Point(104, 180);
            this.txtBatteryLevel.Name = "txtBatteryLevel";
            this.txtBatteryLevel.Size = new System.Drawing.Size(60, 13);
            this.txtBatteryLevel.TabIndex = 26;
            this.txtBatteryLevel.Text = "Unknown";
            // 
            // txtNetworkType
            // 
            this.txtNetworkType.AutoSize = true;
            this.txtNetworkType.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtNetworkType.Location = new System.Drawing.Point(109, 149);
            this.txtNetworkType.Name = "txtNetworkType";
            this.txtNetworkType.Size = new System.Drawing.Size(60, 13);
            this.txtNetworkType.TabIndex = 25;
            this.txtNetworkType.Text = "Unknown";
            // 
            // txtNetworkName
            // 
            this.txtNetworkName.AutoSize = true;
            this.txtNetworkName.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtNetworkName.Location = new System.Drawing.Point(115, 117);
            this.txtNetworkName.Name = "txtNetworkName";
            this.txtNetworkName.Size = new System.Drawing.Size(60, 13);
            this.txtNetworkName.TabIndex = 24;
            this.txtNetworkName.Text = "Unknown";
            // 
            // txtLastUpdate
            // 
            this.txtLastUpdate.AutoSize = true;
            this.txtLastUpdate.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtLastUpdate.Location = new System.Drawing.Point(104, 86);
            this.txtLastUpdate.Name = "txtLastUpdate";
            this.txtLastUpdate.Size = new System.Drawing.Size(60, 13);
            this.txtLastUpdate.TabIndex = 23;
            this.txtLastUpdate.Text = "Unknown";
            // 
            // txtImei
            // 
            this.txtImei.AutoSize = true;
            this.txtImei.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtImei.Location = new System.Drawing.Point(68, 59);
            this.txtImei.Name = "txtImei";
            this.txtImei.Size = new System.Drawing.Size(60, 13);
            this.txtImei.TabIndex = 22;
            this.txtImei.Text = "Unknown";
            // 
            // txtDeviceName
            // 
            this.txtDeviceName.AutoSize = true;
            this.txtDeviceName.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(163)));
            this.txtDeviceName.Location = new System.Drawing.Point(109, 29);
            this.txtDeviceName.Name = "txtDeviceName";
            this.txtDeviceName.Size = new System.Drawing.Size(60, 13);
            this.txtDeviceName.TabIndex = 21;
            this.txtDeviceName.Text = "Unknown";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(30, 210);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(52, 13);
            this.label8.TabIndex = 20;
            this.label8.Text = "Charging:";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(30, 180);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(68, 13);
            this.label7.TabIndex = 19;
            this.label7.Text = "Battery level:";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(30, 149);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(73, 13);
            this.label6.TabIndex = 18;
            this.label6.Text = "Network type:";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(30, 117);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(79, 13);
            this.label5.TabIndex = 17;
            this.label5.Text = "Network name:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(30, 86);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(68, 13);
            this.label4.TabIndex = 16;
            this.label4.Text = "Last Update:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(30, 59);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(32, 13);
            this.label3.TabIndex = 15;
            this.label3.Text = "IMEI:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(30, 29);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(73, 13);
            this.label2.TabIndex = 14;
            this.label2.Text = "Device name:";
            // 
            // button2
            // 
            this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button2.Image = global::winform_client.Properties.Resources.refresh1;
            this.button2.Location = new System.Drawing.Point(300, 19);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(32, 32);
            this.button2.TabIndex = 2;
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.Button2_Click);
            // 
            // panel1
            // 
            this.panel1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel1.Controls.Add(this.gMap);
            this.panel1.Location = new System.Drawing.Point(373, 68);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(722, 608);
            this.panel1.TabIndex = 4;
            // 
            // gMap
            // 
            this.gMap.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.gMap.Bearing = 0F;
            this.gMap.CanDragMap = true;
            this.gMap.EmptyTileColor = System.Drawing.Color.AliceBlue;
            this.gMap.GrayScaleMode = false;
            this.gMap.HelperLineOption = GMap.NET.WindowsForms.HelperLineOptions.DontShow;
            this.gMap.LevelsKeepInMemmory = 5;
            this.gMap.Location = new System.Drawing.Point(3, 3);
            this.gMap.MarkersEnabled = true;
            this.gMap.MaxZoom = 20;
            this.gMap.MinZoom = 2;
            this.gMap.MouseWheelZoomEnabled = true;
            this.gMap.MouseWheelZoomType = GMap.NET.MouseWheelZoomType.MousePositionAndCenter;
            this.gMap.Name = "gMap";
            this.gMap.NegativeMode = false;
            this.gMap.PolygonsEnabled = true;
            this.gMap.RetryLoadTile = 0;
            this.gMap.RoutesEnabled = true;
            this.gMap.ScaleMode = GMap.NET.WindowsForms.ScaleModes.Integer;
            this.gMap.SelectedAreaFillColor = System.Drawing.Color.FromArgb(((int)(((byte)(33)))), ((int)(((byte)(65)))), ((int)(((byte)(105)))), ((int)(((byte)(225)))));
            this.gMap.ShowTileGridLines = false;
            this.gMap.Size = new System.Drawing.Size(714, 600);
            this.gMap.TabIndex = 0;
            this.gMap.Zoom = 0D;
            this.gMap.OnMarkerClick += new GMap.NET.WindowsForms.MarkerClick(this.GMap_OnMarkerClick);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1107, 688);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.menuStrip1);
            this.MainMenuStrip = this.menuStrip1;
            this.MinimumSize = new System.Drawing.Size(900, 550);
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "GPS Tracking Manager";
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem accountToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem logoutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem modeMarkerToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem currentToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem allToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem devicesToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem turnOffServicesOnCurrentDeviceToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem turnOnServicesOnCurrentDeviceToolStripMenuItem;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.ListView listView1;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.Label txtCharging;
        private System.Windows.Forms.Label txtBatteryLevel;
        private System.Windows.Forms.Label txtNetworkType;
        private System.Windows.Forms.Label txtNetworkName;
        private System.Windows.Forms.Label txtLastUpdate;
        private System.Windows.Forms.Label txtImei;
        private System.Windows.Forms.Label txtDeviceName;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.Panel panel1;
        private GMap.NET.WindowsForms.GMapControl gMap;
    }
}