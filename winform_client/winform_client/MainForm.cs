using System;
using System.Threading;
using System.Windows.Forms;

namespace winform_client
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
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
    }
}
