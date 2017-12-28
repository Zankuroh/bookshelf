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

// The Content Dialog item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    public sealed partial class cdSearchFriend : ContentDialog
    {
        public cdSearchFriend()
        {
            this.InitializeComponent();
        }

        private async void ContentDialog_OkClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {
            //Search id
            clRequestAPI R = new clRequestAPI("/api/profile/search");
            string res = null;

            R.addHeader("application/x-www-form-urlencoded");
            R.addAuthorization("Bearer", App.Token);
            res = await R.PostRequest("keywords_search=" + txbxSearch.Text, "application/x-www-form-urlencoded");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            int id = 0;
            if (jsonRes["errors"].ToString() == "null")
            {
                JsonArray j = jsonRes["data"].GetArray();
                if (j.Count > 0)
                {
                    JsonObject it = j.GetObjectAt(0);
                    id = (int)it["id"].GetNumber();
                }
                else
                {
                    string msg = txbxSearch.Text + "is not an existing user";
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                }
                //Add
                clRequestAPI Req = new clRequestAPI("/api/friend");
                string res2 = null;

                Req.addAuthorization("Bearer", App.Token);
                Req.addHeader("application/x-www-form-urlencoded");

                res2 = await Req.PostRequest("friend_id=" + id.ToString(), "application/x-www-form-urlencoded");
            }
            else
            {
                txblError.Text = jsonRes["errors"].GetString();
            }
        }

        private void ContentDialog_CancelClick(ContentDialog sender, ContentDialogButtonClickEventArgs args)
        {

        }
    }
}
