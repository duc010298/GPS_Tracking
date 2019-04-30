using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.entity
{
    class JsonDevice
    {
        public string imei { get; set; }
        public string deviceName { get; set; }
        public bool isOnline { get; set; }
        public DateTime lastOnline { get; set; }
    }
}
