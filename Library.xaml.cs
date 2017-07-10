using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Data.Json;
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
    public sealed partial class Library : Page
    {
        public Library()
        {
            this.InitializeComponent();
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/book");
            string res = null;

            try
            {
                Req.addAuthorization("Bearer", App.Token);

                res = await Req.GetRequest();
                //JsonObject jsonRes;
                //JsonObject.TryParse(res, out jsonRes);
                //JsonArray j =  jsonRes["data"].GetArray();
                List<string> lst = Req.findResponseS("isbn");
                foreach (string str in lst)
                {
                    clBook bk = await clISBNsearch.SearchISBNclBook(str);
                    ucBook child = new ucBook(bk);
                    wgrdLibrary.Children.Add(new ucBook(bk));
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("Grid_Loaded(object sender, RoutedEventArgs e)", ex);
            }
        }
    }
}
