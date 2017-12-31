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
    public sealed partial class ucAuthorListItem : UserControl
    {
        public clAuthor Auth;
        public ucAuthorListItem()
        {
            this.InitializeComponent();
        }
        public ucAuthorListItem(clAuthor auth)
        {
            this.InitializeComponent();
            this.Auth = auth;
            this.chbxName.Content = this.Auth.authorSName + " " + this.Auth.authorLName;
        }
        public bool? isChecked()
        {
            return chbxName.IsChecked;
        }
    }
}
