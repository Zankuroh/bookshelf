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
        List<ucBook> lib = new List<ucBook>();
        List<ucBook> Fav = new List<ucBook>();
        List<ucBook> Read = new List<ucBook>();
        List<ucBook> Unread = new List<ucBook>();

        public Library()
        {
            this.InitializeComponent();
        }

        private async void Pivot_Loaded(object sender, RoutedEventArgs e)
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
                    bk.BookStatus = int.Parse(it["status_id"].GetString());
                    ucBook child = new ucBook(bk);
                    lib.Add(child);
                    wgrdAll.Children.Add(child);
                    if (child.Book.BookStatus == 3)
                    {
                        ucBook b = child.Clone();
                        Fav.Add(b);
                        wgrdFav.Children.Add(b);
                    }
                    if (child.Book.BookStatus == 0)
                    {
                        ucBook b = child.Clone();
                        Read.Add(b);
                        wgrdRead.Children.Add(b);
                    }
                    if (child.Book.BookStatus == 1)
                    {
                        ucBook b = child.Clone();
                        Unread.Add(b);
                        wgrdUnRead.Children.Add(b);
                    }

                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("Grid_Loaded(object sender, RoutedEventArgs e)", ex);
            }
        }

        private void txbxSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            Func<ucBook, bool> mySearch = null;
            try
            {
                switch (cbbxAllSearch.SelectedIndex)
                {
                    case 0:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxAllSearch.Text);
                        break;
                    case 1:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Authors.FirstOrDefault().Contains(txbxAllSearch.Text);
                        break;
                    case 2:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Categories.FirstOrDefault().Contains(txbxAllSearch.Text);
                        break;
                    default:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxAllSearch.Text);
                        break;
                }
                //var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxAllSearch.Text));
                var filt = lib.Where(mySearch);
                wgrdAll.Children.Clear();
                foreach (ucBook u in filt)
                {
                    wgrdAll.Children.Add(u);
                }
                this.InitializeComponent();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("txbxSearch_TextChanged(object sender, TextChangedEventArgs e)", ex);
            }
        }

        private void txbxFavSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            Func<ucBook, bool> mySearch = null;
            try
            {
                switch (cbbxFavSearch.SelectedIndex)
                {
                    case 0:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxFavSearch.Text);
                        break;
                    case 1:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Authors.FirstOrDefault().Contains(txbxFavSearch.Text);
                        break;
                    case 2:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Categories.FirstOrDefault().Contains(txbxFavSearch.Text);
                        break;
                    default:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxFavSearch.Text);
                        break;
                }
                //var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxFavSearch.Text));
                var filt = Fav.Where(mySearch);
                wgrdFav.Children.Clear();
                foreach (ucBook u in filt)
                {
                    wgrdFav.Children.Add(u);
                }
                this.InitializeComponent();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("txbxSearch_TextChanged(object sender, TextChangedEventArgs e)", ex);
            }
        }

        private void txbxReadSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            Func<ucBook, bool> mySearch = null;
            try
            {
                switch (cbbxReadSearch.SelectedIndex)
                {
                    case 0:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxReadSearch.Text);
                        break;
                    case 1:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Authors.FirstOrDefault().Contains(txbxReadSearch.Text);
                        break;
                    case 2:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Categories.FirstOrDefault().Contains(txbxReadSearch.Text);
                        break;
                    default:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxReadSearch.Text);
                        break;
                }
                //var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxReadSearch.Text));
                var filt = Read.Where(mySearch);
                wgrdRead.Children.Clear();
                foreach (ucBook u in filt)
                {
                    wgrdRead.Children.Add(u);
                }
                this.InitializeComponent();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("txbxReadSearch_TextChanged(object sender, TextChangedEventArgs e)", ex);
            }
        }

        private void txbxUnreadSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            Func<ucBook, bool> mySearch = null;
            try
            {
                switch (cbbxUnreadSearch.SelectedIndex)
                {
                    case 0:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxUnReadSearch.Text);
                        break;
                    case 1:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Authors.FirstOrDefault().Contains(txbxUnReadSearch.Text);
                        break;
                    case 2:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Categories.FirstOrDefault().Contains(txbxUnReadSearch.Text);
                        break;
                    default:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxUnReadSearch.Text);
                        break;
                }
                //var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxUnReadSearch.Text));
                var filt = Unread.Where(mySearch);
                wgrdUnRead.Children.Clear();
                foreach (ucBook u in filt)
                {
                    wgrdUnRead.Children.Add(u);
                }
                this.InitializeComponent();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("txbxUnReadSearch_TextChanged(object sender, TextChangedEventArgs e)", ex);
            }
        }
    }
}
