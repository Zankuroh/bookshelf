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
using System.Threading.Tasks;
using Google.Apis.Books.v1;
using Google.Apis.Books.v1.Data;
using Google.Apis.Services;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Search : Page
    {
        public Search()
        {
            this.InitializeComponent();
        }
        private async void btSearch_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string isbn = txbxISBNNum.Text;
                Volume result = await clISBNsearch.SearchISBNVol(isbn);
                if (result != null)
                {
                    clBook book = new clBook(isbn, result);
                    var frame = Window.Current.Content as Frame;
                    frame.Navigate(typeof(BookFile), book);
                }
                else
                {
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Echec : Aucun resultat correspondant à cet ISBN");
                    await dial.ShowAsync();
                }
            }
            catch (Google.GoogleApiException gex)
            {
                clErrorHandling.ErrorMessage("btSearch_Click(object sender, RoutedEventArgs e)", gex);
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("BookShelf was unable to retrieve Book Data\nAre your sure you entered/scanned a Valid ISBN?");
                await dial.ShowAsync();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btSearch_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        private async void btScan_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string str = await clBarCode.readBarcode();
                if ((str != "") && (str != null))
                {
                    str = str.Replace(" ", string.Empty);
                    Volume result = await clISBNsearch.SearchISBNVol(str);
                    if (result != null)
                    {
                        clBook book = new clBook(str, result);
                        var frame = Window.Current.Content as Frame;
                        frame.Navigate(typeof(BookFile), book);
                    }
                }
                else
                {
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Echec : Aucun resultat correspondant à cet ISBN");
                    await dial.ShowAsync();
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btScan_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        private async void btPicker(object sender, RoutedEventArgs e)
        {
            try
            {
                string str = await clBarCode.testPicker();
                if ((str != "") && (str != null))
                {
                    str = str.Replace(" ", string.Empty);
                    Volume result = await clISBNsearch.SearchISBNVol(str);
                    clBook book = new clBook(str, result);
                    var frame = Window.Current.Content as Frame;
                    frame.Navigate(typeof(BookFile), book);
                }
                else
                {
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Echec : Aucun resultat correspondant à cet ISBN");
                    await dial.ShowAsync();
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btPicker(object sender, RoutedEventArgs e)", ex);
            }
        }
    }
}
