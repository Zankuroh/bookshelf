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
    public sealed partial class ucBook : UserControl
    {
        clBook book = null;
        public ucBook()
        {
            this.InitializeComponent();
        }

        internal clBook Book
        {
            get
            {
                return book;
            }

            set
            {
                book = value;
            }
        }
        public ucBook(clBook newBook)
        {
            InitializeComponent();
            try
            {
                book = newBook;
                if (newBook.BookData.VolumeInfo.Title != null)
                    txblNameBook.Text = newBook.BookData.VolumeInfo.Title;
                Windows.UI.Xaml.Media.Imaging.BitmapImage bitmapImage = new Windows.UI.Xaml.Media.Imaging.BitmapImage();
                Uri uri = new Uri(book.BookData.VolumeInfo.ImageLinks.SmallThumbnail);
                bitmapImage.UriSource = uri;
                imgThumb.Source = bitmapImage;
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("ucBook(clBook newBook)", ex);
            }
        }

        private void ucBook_Click(object sender, RoutedEventArgs e)
        {
            //a Garder pour plus tard
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(BookFile), book);
            //Type t = this.Parent.GetType();
            //BookFile bk = new BookFile(book);
            //var frame = Window.Current.Content as Frame;
            //frame.Navigate(typeof(MainFrame), bk);
        }

        public ucBook Clone()
        {
            ucBook copy = new ucBook(this.book);
            return copy;
        }
    }
}
