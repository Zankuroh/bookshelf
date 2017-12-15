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
        List<ucBook> lib;

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
                JsonObject jsonRes;
                JsonObject.TryParse(res, out jsonRes);
                JsonArray j = jsonRes["data"].GetArray();
                foreach (IJsonValue obj in j)
                {
                    JsonObject it = obj.GetObject();
                    clBook bk = await clISBNsearch.SearchISBNclBook(it["isbn"].GetString());
                    ucBook child = new ucBook(bk);
                    wgrdLibrary.Children.Add(new ucBook(bk));
                }
                var b = wgrdLibrary.Children.ToList();
                lib = b.Cast<ucBook>().ToList();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("Grid_Loaded(object sender, RoutedEventArgs e)", ex);
            }
        }

        private void txbxSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            //var d = lib.Where<ucBook>(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxSearch.Text));
            //foreach (UIElement u in wgrdLibrary.Children)
            //{
            //    ucBook b = (ucBook)u;
            //    if (!b.Book.BookData.VolumeInfo.Title.Contains(txbxSearch.Text) && txbxSearch.Text != null)
            //    {
            //        u.Visibility = Visibility.Collapsed;
            //    }
            //    else
            //    {
            //        u.Visibility = Visibility.Visible;
            //    }
            //}
            //wgrdLibrary.UpdateLayout();
            var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxSearch.Text));
            wgrdLibrary.Children.Clear();
            foreach (ucBook u in filt)
            {
                wgrdLibrary.Children.Add(u);
            }
            this.InitializeComponent();
        }
    }
}
