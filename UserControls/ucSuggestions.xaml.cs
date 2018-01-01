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

// The User Control item template is documented at http://go.microsoft.com/fwlink/?LinkId=234236

namespace BookShelf
{
    public sealed partial class ucSuggestions : UserControl
    {
        string strISBN = null;
        string strName = null;
        string imgUrl = null;
        public ucSuggestions()
        {
            this.InitializeComponent();
        }
        public ucSuggestions(string ISBN, string name, string img)
        {
            this.InitializeComponent();
            try
            {
                strISBN = ISBN;
                strName = name;
                imgUrl = img;
                txblNameBook.Text = strName;
                Windows.UI.Xaml.Media.Imaging.BitmapImage bitmapImage = new Windows.UI.Xaml.Media.Imaging.BitmapImage();
                Uri uri = new Uri(imgUrl);
                bitmapImage.UriSource = uri;
                imgThumb.Source = bitmapImage;
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("ucSuggestions", ex);
            }
        }

        private async void ucSuggestion_Click(object sender, RoutedEventArgs e)
        {
            // The URI to launch
            var uriBing = new Uri(@"http://api.duckduckgo.com/?q=\" + strISBN + "!amazon&t=BookShelf&format=json");
            var success = await Windows.System.Launcher.LaunchUriAsync(uriBing);

            if (success)
            {
                // URI launched
            }
            else
            {
                // URI launch failed
            }
        }
    }
}
