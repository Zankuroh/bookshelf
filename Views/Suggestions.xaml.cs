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
    public sealed partial class Suggestions : Page
    {
        public Suggestions()
        {
            this.InitializeComponent();

        }

        private async void button_Click(object sender, RoutedEventArgs e)
        {
            //Get
            clRequestAPI Req2 = new clRequestAPI("/api/book/search/B01513ZIL6");
            Req2.addAuthorization("Bearer", App.Token);
            string res2 = await Req2.GetRequest();
        }

        private async void Grid_Loaded(object sender, RoutedEventArgs e)
        {
            try
            {
                clRequestAPI Req = new clRequestAPI("/api/suggestion");
                string res = null;

                Req.addHeader("application/x-www-form-urlencoded");
                Req.addAuthorization("Bearer", App.Token);

                res = await Req.PostRequest("force_build=false", "application /x-www-form-urlencoded");
                JsonObject jsonRes;
                JsonObject.TryParse(res, out jsonRes);
                JsonObject j = jsonRes["data"].GetObject();
                JsonArray t = j["latest_suggestions"].GetArray();
                foreach (IJsonValue obj in t)
                {
                    string it = obj.GetString();
                    clRequestAPI Req2 = new clRequestAPI("/api/book/search/" + it);
                    Req2.addAuthorization("Bearer", App.Token);
                    string res2 = await Req2.GetRequest();
                    JsonObject bk;
                    JsonObject.TryParse(res2, out bk);
                    JsonArray a = bk["data"].GetArray();
                    foreach (IJsonValue o in a)
                    {
                        JsonObject suggest = o.GetObject();
                        string title = suggest["book_title"].GetString();
                        string img = suggest["book_picture_url"].GetString();
                        ucSuggestions child = new ucSuggestions(it, title, img);
                        wgrdAll.Children.Add(child);
                    }
                }
                
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("Grid_Loaded(object sender, RoutedEventArgs e) in suggestion.xaml.cs", ex);
            }

        }
    }
}
