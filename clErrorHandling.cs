using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BookShelf
{
    class clErrorHandling
    {
        static public string ErrorMessage(string whereIam, Exception ex)
        {
            string Message = DateTime.Today.ToString() + "=> An Rrror occured in \"" + whereIam + "\"\n" + ex.Source.ToString() + "\n" + ex.Message;
            System.Diagnostics.Debug.WriteLine(Message);
            return (Message);
        }

        static public string ErrorMessage(string whereIam, Exception ex, string suppMesssage)
        {
            string Message = DateTime.Today.ToString() + "=> An Rrror occured in \"" + whereIam + "\"\n" + ex.Source.ToString() + "\n" + ex.Message + "\n" + suppMesssage;
            System.Diagnostics.Debug.WriteLine(Message);
            return (Message);
        }
    }
}
