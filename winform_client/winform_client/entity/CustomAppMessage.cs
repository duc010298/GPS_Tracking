using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.entity
{
    class CustomAppMessage
    {
        public string command { get; set; }
        public string sendToImei { get; set; }
        public Object content { get; set; }
    }
}
