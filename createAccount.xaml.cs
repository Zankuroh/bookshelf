using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Data.Json;
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
    public sealed partial class createAccount : Page
    {
        public createAccount()
        {
            this.InitializeComponent();
            var currentView = Windows.UI.Core.SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = Windows.UI.Core.AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;
        }

        private void OnBackRequested(object sender, Windows.UI.Core.BackRequestedEventArgs e)
        {
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = Windows.UI.Core.AppViewBackButtonVisibility.Collapsed;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainPage));
        }

        //Registering a new valid user
        private async void btcreatAcc_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string res = null;
                if (pwdbxUsr.Password == pwdbxUsrValid.Password)
                {
                    clRequestAPI Req = new clRequestAPI("/api/register");
                    string requestContent = "email=" + txbxUsrEmail.Text + "&password=" + pwdbxUsr.Password + "&name=" + txbxUsrName.Text;
                    res = await Req.PostRequest(requestContent, "application/x-www-form-urlencoded");
                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    if (jsonRes["errors"].ToString() == "null")
                    {
                        //placer une MsgBox ici
                        string msg = null;
                        msg += "Le profil a été crée avec succés";
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                        SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
                        var frame = Window.Current.Content as Frame;
                        frame.Navigate(typeof(MainPage));
                    }
                    else
                    {
                        string msg = null;
                        msg += "création de profil a echoué\n" + jsonRes["errors"].ToString() + " ";
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                    }
                }
                else
                {
                    string msg = null;
                    msg += "création de profil a echoué\nle mot de passe doit être entré deux fois";
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btcreatAcc_Click(object sender, RoutedEventArgs e)", ex);
            }
        }


        private async void btFBcreate_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string res = null;
                clFacebook fb = new clFacebook();
                winsdkfb.FBSession sess = await fb.FBgetSession();
                clRequestAPI Req = new clRequestAPI("/api/register");
                string requestContent = "email=" + sess.User.Email + "&password=" + "samourai" + "&name=" + sess.User.FirstName;
                res = await Req.PostRequest(requestContent, "application/x-www-form-urlencoded");
                JsonObject jsonRes;
                JsonObject.TryParse(res, out jsonRes);
                if (jsonRes["errors"].ToString() == "null")
                {
                    //placer une MsgBox ici
                    string msg = null;
                    msg += "Le profil a été crée avec succés";
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                    SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
                    var frame = Window.Current.Content as Frame;
                    frame.Navigate(typeof(MainPage));
                }
                else
                {
                    string msg = null;
                    msg += "création de profil a echoué\n" + jsonRes["errors"].ToString() + " ";
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btFBcreate_Click(object sender, RoutedEventArgs e)", ex);
            }
        }
    }
}
