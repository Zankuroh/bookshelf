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
    public sealed partial class MainFrame : Page
    {
        

        public MainFrame()
        {
            this.InitializeComponent();
            //pansement à changer (rechercher comment en appeller directement au ctor avec argument de la page)
            if (this.ShellSplitView.Content == null)
            {
                this.ShellSplitView.Content = new Frame();
                ((Frame)ShellSplitView.Content).Navigate(typeof(Library));
            }
        }
        public MainFrame(Frame frame)
        {
            this.InitializeComponent();
            this.ShellSplitView.Content = frame;
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (e.Parameter is Page)
            {
                //this.ShellSplitView.Content = e.Parameter as Page;
                Type t = e.Parameter.GetType();
                ((Frame)ShellSplitView.Content).Navigate(t);
            }
        }

        private void OnMenuButtonClicked(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = !ShellSplitView.IsPaneOpen;
            ((RadioButton)sender).IsChecked = false;
        }

        private void LibraryButton_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
                ((Frame)ShellSplitView.Content).Navigate(typeof(Library));
        }

        private void SearchButton_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
            {
                //ShellSplitView.Content = new Frame();
                ((Frame)ShellSplitView.Content).Navigate(typeof(Search));
            }
        }

        private void AuthorsButton_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
                ((Frame)ShellSplitView.Content).Navigate(typeof(FavAuthors));
        }

        private void WishLstButton_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
                ((Frame)ShellSplitView.Content).Navigate(typeof(WishList));
        }

        private void ProfileButton_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
                ((Frame)ShellSplitView.Content).Navigate(typeof(Profile));
        }

        private void Suugestions_Click(object sender, RoutedEventArgs e)
        {
            ShellSplitView.IsPaneOpen = false;
            if (ShellSplitView.Content != null)
                ((Frame)ShellSplitView.Content).Navigate(typeof(Suggestions));
        }

        private async void Deconnection_Click(object sender, RoutedEventArgs e)
        {
            App.Token = null;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainPage));
            if (App.fb != null)
            {
                await App.fb.LogoutAsync();
                App.fb = null;
            }
        }
        //private void OnSettingsButtonChecked(object sender, RoutedEventArgs e)
        //{
        //    ShellSplitView.IsPaneOpen = false;
        //    if (ShellSplitView.Content != null)
        //        ((Frame)ShellSplitView.Content).Navigate(typeof(SettingsPage));
        //}

        //private void OnAboutButtonChecked(object sender, RoutedEventArgs e)
        //{
        //    ShellSplitView.IsPaneOpen = false;
        //    if (ShellSplitView.Content != null)
        //        ((Frame)ShellSplitView.Content).Navigate(typeof(AboutPage));
        //}
    }
}
