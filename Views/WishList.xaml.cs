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
    public sealed partial class WishList : Page
    {
        List<ucBook> lib = new List<ucBook>();
        public WishList()
        {
            this.InitializeComponent();
                //foreach (clBook thing in App.lstBook)
                //{
                //    ucBook b = new ucBook(thing);
                //    System.Diagnostics.Debug.WriteLine(b.Book.BookTitle);
                //    wgrdWishlst.Children.Add(b);
                //}
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            try
            {
                clRequestAPI Req = new clRequestAPI("/api/wish/book");
                string res = null;

                Req.addAuthorization("Bearer", App.Token);

                res = await Req.GetRequest();
                List<string> lst = Req.findResponseS("isbn");
                foreach (string str in lst)
                {
                    clBook bk = await clISBNsearch.SearchISBNclBook(str);
                    ucBook child = new ucBook(bk);
                    lib.Add(child);
                    wgrdWishlst.Children.Add(new ucBook(bk));
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("Grid_Loaded(object sender, RoutedEventArgs e) in WishList.xaml.cs", ex);
            }
   
        }

        private void txbxSearch_TextChanged(object sender, TextChangedEventArgs e)
        {
            Func<ucBook, bool> mySearch = null;
            try
            {
                switch (cbbxSearch.SelectedIndex)
                {
                    case 0:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxSearch.Text);
                        break;
                    case 1:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Authors.FirstOrDefault().Contains(txbxSearch.Text);
                        break;
                    case 2:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Categories.FirstOrDefault().Contains(txbxSearch.Text);
                        break;
                    default:
                        mySearch = (x) => x.Book.BookData.VolumeInfo.Title.Contains(txbxSearch.Text);
                        break;
                }
                //var filt = lib.Where(c => c.Book.BookData.VolumeInfo.Title.Contains(txbxAllSearch.Text));
                var filt = lib.Where(mySearch);
                wgrdWishlst.Children.Clear();
                foreach (ucBook u in filt)
                {
                    wgrdWishlst.Children.Add(u);
                }
                this.InitializeComponent();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("txbxSearch_TextChanged(object sender, TextChangedEventArgs e)", ex);
            }
        }
    }
}
