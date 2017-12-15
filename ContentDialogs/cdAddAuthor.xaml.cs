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

// The Content Dialog item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    public sealed partial class cdAddAuthor : ContentDialog
    {
        public string AuthorFName { get; set; }
        public string AuthorLName { get; set; }

        public cdAddAuthor()
        {
            this.InitializeComponent();
        }

        private void ContentDialog_OkClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {
            AuthorFName = txbxAuthorFName.Text;
            AuthorLName = txbxAuthorLName.Text;

        }

        private void ContentDialog_CancelClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {
        }
    }
}
