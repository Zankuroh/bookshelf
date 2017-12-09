using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Media;
using Windows.UI.Core;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class FavAuthors : Page
    {
        public FavAuthors()
        {
            this.InitializeComponent();
        }

        private async void scviMain_Loaded(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/author");
            string res = null;

            Req.addAuthorization("Bearer", App.Token);

            res = await Req.GetRequest();
            List<string> lst = Req.findResponseS("first_name");
            List<string> lst2 = Req.findResponseS("last_name");
            int i = 0;
            foreach (string s in lst)
            {
                TextBlock t = new TextBlock();
                t.Text = s + " " + lst2.ElementAt(i);
                stpnlAuth.Children.Add(t);
                i++;
            }
        }

        private async void btAddAuthors_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/author");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            string fname = "David";
            string lname = "Gemell";
            res = await Req.PostRequest("first_name=" + fname + "&last_name=" + lname, "application/x-www-form-urlencoded");
        }

        private void AppBarButton_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}
