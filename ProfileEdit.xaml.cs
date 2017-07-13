using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Data.Json;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Popups;
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
    public sealed partial class ProfileEdit : Page
    {
        public ProfileEdit()
        {
            this.InitializeComponent();
            var currentView = SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (e.Parameter is clProfile)
            {
                txbxPseudo.PlaceholderText = ((clProfile)e.Parameter).Pseudo;
            }
        }

        private void OnBackRequested(object sender, BackRequestedEventArgs e)
        {
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainFrame), new Profile());
        }

        private void btCancel_Click(object sender, RoutedEventArgs e)
        {
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainFrame), new Profile());
        }

        private async void btValidate_Click(object sender, RoutedEventArgs e)
        {
            cdEnterPsswd dialog = new cdEnterPsswd();
            string pwd = null;
            ContentDialogResult dialres = await dialog.ShowAsync();
            bool success = true;
            if (dialres == ContentDialogResult.Primary)
            {
                pwd = dialog.Passwd;
                if (txbxPseudo.Text != "")
                {
                    clRequestAPI Req = new clRequestAPI("/api/profile/name");
                    string res = null;
                    Req.addHeader("application/x-www-form-urlencoded");
                    Req.addAuthorization("Bearer", App.Token);
                    res = await Req.PostRequest("password=" + pwd + "&name=" + txbxPseudo.Text, "application/x-www-form-urlencoded");

                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    //if (!jsonRes.ContainsKey("errors"))
                    if (jsonRes["errors"].ToString() != "null")
                    {
                        success = false;
                        //placer une MsgBox ici
                        string msg = null;
                        msg += "Erreur : le nom d'utilisateur na pas été modifié\n" + jsonRes["errors"].ToString() + " ";
                        if (jsonRes.ContainsKey("title"))
                            msg += jsonRes["title"].ToString();
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                    }
                }
                if (txbxEmail.Text != "")
                {
                    clRequestAPI Req = new clRequestAPI("/api/profile/email");
                    string res = null;
                    Req.addHeader("application/x-www-form-urlencoded");
                    Req.addAuthorization("Bearer", App.Token);
                    res = await Req.PostRequest("password=" + pwd + "&email=" + txbxEmail.Text, "application/x-www-form-urlencoded");

                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    if (jsonRes["errors"].ToString() != "null")
                    {
                        success = false;
                        //placer une MsgBox ici
                        string msg = null;
                        msg += "Erreur : l'email utilisateur na pas été modifié\n" + jsonRes["errors"].ToString() + " ";
                        if (jsonRes.ContainsKey("title"))
                            msg += jsonRes["title"].ToString();
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                    }
                }
                if (pwdbxUsr.Password != "")
                {
                    if (pwdbxUsr.Password == pwdbxUsrconf.Password)
                    {
                        clRequestAPI Req = new clRequestAPI("/api/profile/password");
                        string res = null;
                        Req.addHeader("application/x-www-form-urlencoded");
                        Req.addAuthorization("Bearer", App.Token);
                        res = await Req.PostRequest("password=" + pwd + "&new_password=" + pwdbxUsr.Password, "application/x-www-form-urlencoded");


                        JsonObject jsonRes;
                        JsonObject.TryParse(res, out jsonRes);
                        if (jsonRes["errors"].ToString() != "null")
                        {
                            success = false;
                            string msg = null;
                            msg += "Erreur : le mot de passe utilisateur na pas été modifié\n" + jsonRes["errors"].ToString() + " ";
                            if (jsonRes.ContainsKey("title"))
                                msg += jsonRes["title"].ToString();
                            Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                            await dial.ShowAsync();
                        }
                    }
                    else
                    {
                        //password dissemblables
                        MessageDialog msg = new MessageDialog("Vous devez entrer deux fois le même passeport");
                        await msg.ShowAsync();
                    }
                }
                if (success == true)
                {
                    MessageDialog msg = new MessageDialog("Le profil a été modifié avec succés");
                    await msg.ShowAsync();
                }
            }
        }

        private async void btDelete_Click(object sender, RoutedEventArgs e)
        {
            {
                cdEnterPsswd dialog = new cdEnterPsswd();
                string pwd = null;
                ContentDialogResult dialres = await dialog.ShowAsync();
                if (dialres == ContentDialogResult.Primary)
                {
                    pwd = dialog.Passwd;
                    string reqParam = "?password=" + pwd + "&delete=yes";
                    clRequestAPI Req = new clRequestAPI("/api/profile");
                    Req.addAuthorization("Bearer", App.Token);
                    Req.addHeader("application/x-www-form-urlencoded");

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
                        SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
                        var frame = Window.Current.Content as Frame;
                        frame.Navigate(typeof(MainPage));
                    }
                }
            }
        }
    }
}
