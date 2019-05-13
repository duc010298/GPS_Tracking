using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading;
using System.Windows.Forms;

namespace winform_client
{
    public partial class LoginForm : Form
    {
        public LoginForm()
        {
            InitializeComponent();
        }

        private void ButtonLogin_Click(object sender, EventArgs e)
        {
            string username = textBox1.Text.Trim().ToLower();
            string password = textBox2.Text.Trim().ToLower();

            if (String.IsNullOrEmpty(username) || String.IsNullOrEmpty(password))
            {
                MessageBox.Show("Username and password cannot be empty", "Warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            var formContent = new FormUrlEncodedContent(new[] {
                new KeyValuePair<string, string>("username", username),
                new KeyValuePair<string, string>("password", password)
            });

            string loginUrl = ConfigurationManager.AppSettings["api-url"];
            HttpClient client = new HttpClient();
            client.BaseAddress = new Uri(loginUrl);
            client.DefaultRequestHeaders.Accept.Clear();
            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

            try
            {
                var response = client.PostAsync("login", formContent).Result;
                if (response.IsSuccessStatusCode)
                {
                    StaticToken.token = response.Headers.GetValues("Authorization").FirstOrDefault();
                    OpenMainForm();
                }
                else
                {
                    MessageBox.Show("Incorrect username or password.", "Info", MessageBoxButtons.OK, MessageBoxIcon.Information);
                    return;
                }
            }
            catch (Exception ex)
            {
                if (ex.Message.Equals("The given header was not found."))
                {
                    MessageBox.Show("Incorrect username or password.", "Info", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
                else
                {
                    MessageBox.Show("Failed to send request, check your connection and try again", "Take an error!", MessageBoxButtons.OK, MessageBoxIcon.Hand);
                }
                return;
            }
        }

        private void OpenMainForm()
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
            Application.Run(new MainForm());
        }

        private void TextBox1_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyData == Keys.Enter)
            {
                if (String.IsNullOrEmpty(textBox2.Text.Trim()))
                {
                    textBox2.Focus();
                }
                else
                {
                    ButtonLogin_Click(sender, e);
                }
            }
        }

        private void TextBox2_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyData == Keys.Enter)
            {
                ButtonLogin_Click(sender, e);
            }
        }
    }
}
