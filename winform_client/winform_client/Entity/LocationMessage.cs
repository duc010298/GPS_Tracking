using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace winform_client.Entity
{
    class LocationMessage
    {
        public string locationId { get; set; }
        public double latitude { get; set; }
        public double longitude { get; set; }
        public DateTime timeTracking { get; set; }
    }
}
