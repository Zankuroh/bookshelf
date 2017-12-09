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
    public sealed partial class Profile : Page
    {
        private clProfile User = new clProfile();
        public Profile()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            //if (e.Parameter is clProfile)
            //{
            //    App.p = e.Parameter as clProfile;
            //    if (App.p.Pseudo != null)
            //    {
            //        txblUsrPseudo.Text = App.p.Pseudo;
            //    }
            //    if (App.p.Email != null)
            //    {
            //        txblUsrEmail.Text = App.p.Email;
            //    }
            //    if (App.p.Birthday != null)
            //    {
            //        txblUsrBirthday.Text = App.p.Birthday;
            //    }
            //    if (App.p.LikedGenre != null)
            //    {
            //        txblUsrLikedGenre.Text = App.p.LikedGenre;
            //    }
            //    if (App.p.FavBook != null)
            //    {
            //        txblUsrFavBook.Text = App.p.FavBook;
            //    }
            //}
            //else
            //{
            //    if (App.p.Pseudo != null)
            //    {
            //        txblUsrPseudo.Text = App.p.Pseudo;
            //    }
            //    if (App.p.Email != null)
            //    {
            //        txblUsrEmail.Text = App.p.Email;
            //    }
            //    if (App.p.Birthday != null)
            //    {
            //        txblUsrBirthday.Text = App.p.Birthday;
            //    }
            //    if (App.p.LikedGenre != null)
            //    {
            //        txblUsrLikedGenre.Text = App.p.LikedGenre;
            //    }
            //    if (App.p.FavBook != null)
            //    {
            //        txblUsrFavBook.Text = App.p.FavBook;
            //    }
            //}
            //base.OnNavigatedTo(e);
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/profile");
            string res = null;

            Req.addAuthorization("Bearer", App.Token);

            res = await Req.GetRequest();
            string str = Req.findResponse("name");
            string mail = Req.findResponse("email");
            User.Pseudo = str;
            User.Email = mail;
            txblPseudo.Text += str;
            txblEmail.Text += mail;
        }

        private void btModifProf_Click(object sender, RoutedEventArgs e)
        {
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(ProfileEdit));
        }

        private async void btDeleteProf_Click(object sender, RoutedEventArgs e)
        {
            string pwd = "";

            if (!BookShelf.App.socialAuthLogged)
            {
                cdEnterPsswd dialog = new cdEnterPsswd();
                ContentDialogResult dialres = await dialog.ShowAsync();
                if (dialres == ContentDialogResult.Primary)
                {
                    pwd = dialog.Passwd;
                }   
            }

            string reqParam = "?password=" + pwd + "&delete=yes";
            clRequestAPI Req = new clRequestAPI("/api/profile");
            Req.addAuthorization("Bearer", App.Token);
            Req.addHeader("application/x-www-form-urlencoded");
            
            System.Diagnostics.Debug.WriteLine("Profile Delete : URI = " + reqParam);
            string res = await Req.DeletetRequest(reqParam);

            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            if (jsonRes["errors"].ToString() != "null")
            {
                //placer une MsgBox ici
                string msg = null;
                msg += "Le profil n'a pas été éffacé\n" + jsonRes["errors"].ToString() + " ";
                if (jsonRes.ContainsKey("title"))
                    msg += jsonRes["title"].ToString();
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
            }
            else
            {
                string msg = null;
                msg += "Le profil a bien été éffacé\n" + jsonRes["errors"].ToString() + " ";
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
                App.Token = null;
                var frame = Window.Current.Content as Frame;
                frame.Navigate(typeof(MainPage));
            }
        }
    }
}
