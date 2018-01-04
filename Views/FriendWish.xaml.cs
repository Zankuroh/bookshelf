using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
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
    public sealed partial class FriendWish : Page
    {
        List<ucBook> lib = new List<ucBook>();
        clFriend friendId = null;
        public FriendWish()
        {
            this.InitializeComponent();
            var currentView = SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;
        }

        public FriendWish(clFriend id)
        {
            this.InitializeComponent();
            var currentView = SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;
            friendId = id;
        }

        private void OnBackRequested(object sender, BackRequestedEventArgs e)
        {
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainFrame), new Profile());
        }

        protected async override void OnNavigatedTo(NavigationEventArgs e)
        {
            try
            {
                if (e.Parameter is clFriend)
                this.friendId = e.Parameter as clFriend;

                clRequestAPI Req = new clRequestAPI("/api/wish/book/" + friendId.Id);
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
                clErrorHandling.ErrorMessage("OnNavigatedTo(NavigationEventArgs e) in FriendWish.xaml.cs", ex);
            }
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            //try
            //{
            //    clRequestAPI Req = new clRequestAPI("/api/wish/book/" + friendId.friendId);
            //    string res = null;

            //    Req.addAuthorization("Bearer", App.Token);

            //    res = await Req.GetRequest();
            //    List<string> lst = Req.findResponseS("isbn");
            //    foreach (string str in lst)
            //    {
            //        clBook bk = await clISBNsearch.SearchISBNclBook(str);
            //        ucBook child = new ucBook(bk);
            //        lib.Add(child);
            //        wgrdWishlst.Children.Add(new ucBook(bk));
            //    }
            //}
            //catch (Exception ex)
            //{
            //    clErrorHandling.ErrorMessage("OnBackRequested(object sender, BackRequestedEventArgs e) in FriendWish.xaml.cs", ex);
            //}
        }
    }
}
