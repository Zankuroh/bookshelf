using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Suggestions : Page
    {
        public Suggestions()
        {
            this.InitializeComponent();

        }

        private async void button_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/suggestion");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            res = await Req.PostRequest("force_build=false", "application /x-www-form-urlencoded");

            //Get
            clRequestAPI Req2 = new clRequestAPI("/api/book/search/B01513ZIL6");
            Req2.addAuthorization("Bearer", App.Token);
            string res2 = await Req2.GetRequest();
        }
    }
}
