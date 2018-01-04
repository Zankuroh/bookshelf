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
    public sealed partial class ucFriendListItem : UserControl
    {
        public clFriend Friend;
        public ucFriendListItem()
        {
            this.InitializeComponent();
        }
        public ucFriendListItem(clFriend auth)
        {
            this.InitializeComponent();
            this.Friend = auth;
            this.chbxName.Content = this.Friend.friendName;
        }
        public bool? isChecked()
        {
            return chbxName.IsChecked;
        }

        private void btFriendItem_Click(object sender, RoutedEventArgs e)
        {
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(FriendWish), Friend);
        }
    }
}
