using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.Entity
{
    class DeviceMessage
    {
        public string imei { get; set; }
        public string deviceName { get; set; }
        public DateTime lastUpdate { get; set; }
    }
}
