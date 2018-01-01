using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Media;
using Windows.UI.Core;
using Windows.Data.Json;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class FavAuthors : Page
    {
        public FavAuthors()
        {
            this.InitializeComponent();
        }

        private async void scviMain_Loaded(object sender, RoutedEventArgs e)
        {
            //clRequestAPI Req = new clRequestAPI("/api/author");
            //string res = null;

            //Req.addAuthorization("Bearer", App.Token);
            clRequestAPI Req = new clRequestAPI("/api/author/subscription");
            string res = null;

            Req.addAuthorization("Bearer", App.Token);

            res = await Req.GetRequest();
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            JsonArray j = jsonRes["data"].GetArray();
            foreach (IJsonValue obj in j)
            {
                JsonObject it = obj.GetObject();
                var auth = new clAuthor();
                auth.authorSName = it["first_name"].ToString() != "null" ? it["first_name"].GetString() : "";
                auth.authorLName = it["last_name"].ToString() != "null" ? it["last_name"].GetString() : "";
                auth.authorId = it["id"].ToString();
                var child = new ucAuthorListItem(auth);
                stpnlAuth.Children.Add(child);
            }
        }

        private async void btAddAuthors_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/author");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            cdAddAuthor dialog = new cdAddAuthor();
            ContentDialogResult dialres = await dialog.ShowAsync();
            res = await Req.PostRequest("first_name=" + dialog.AuthorFName + "&last_name=" + dialog.AuthorLName, "application/x-www-form-urlencoded");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            string err = jsonRes["errors"].ToString();
            if (err == "null")
            {
                JsonObject it = jsonRes["data"].GetObject();
                var auth = new clAuthor();
                auth.authorSName = it["first_name"].ToString() != "null" ? it["first_name"].GetString() : "";
                auth.authorLName = it["last_name"].ToString() != "null" ? it["last_name"].GetString() : "";
                auth.authorId = it["id"].ToString();

                clRequestAPI Req2 = new clRequestAPI("/api/author/subscription");
                string res2 = null;

                Req2.addHeader("application/x-www-form-urlencoded");
                Req2.addAuthorization("Bearer", App.Token);
                res2 = await Req2.PostRequest("author_id=" + auth.authorId, "application/x-www-form-urlencoded");


                var child = new ucAuthorListItem(auth);
                stpnlAuth.Children.Add(child);
            }
        }

        private async void btDeleteAuthor_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/author/subscription");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);
            foreach (ucAuthorListItem a in stpnlAuth.Children)
            {
                if (a.isChecked() == true)
                {
                    //res = await Req.PostRequest("id=" + a.Auth.authorId, "application/x-www-form-urlencoded");
                    res = await Req.DeletetRequest("?author_id=" + a.Auth.authorId);
                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    string j = jsonRes["errors"].ToString();
                    if (j == "null")
                        stpnlAuth.Children.Remove(a);
                }

            }
        }
    }
}
