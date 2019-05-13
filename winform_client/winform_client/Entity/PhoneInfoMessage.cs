using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.Entity
{
    class PhoneInfoMessage
    {
        public string networkName { get; set; }
        public string networkType { get; set; }
        public int batteryLevel { get; set; }
        public bool isCharging { get; set; }
    }
}
