using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.Entity
{
    class AppMessage
    {
        public string command { get; set; }
        public string imei { get; set; }
        public Object content { get; set; }
    }
}
