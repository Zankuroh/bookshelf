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
using Google.Apis.Books.v1.Data;
using Windows.UI.Core;
using Windows.Data.Json;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class BookFile : Page
    {
        clBook book = null;
        public BookFile()
        {
            this.InitializeComponent();
            var currentView = SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;
        }

        public BookFile(clBook bk)
        {
            this.InitializeComponent();
            var currentView = SystemNavigationManager.GetForCurrentView();
            currentView.AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
            currentView.BackRequested += OnBackRequested;

            try
            {
                this.book = bk;
                txblTitle.Text = book.BookData.VolumeInfo.Title;
                txblAuthor.Text = book.BookData.VolumeInfo.Authors.FirstOrDefault();
                txblGenre.Text = book.BookData.VolumeInfo.Categories.FirstOrDefault();
                txblSynopsis.Text = book.BookData.VolumeInfo.Description;
                txblPublisher.Text = book.BookData.VolumeInfo.Publisher;
                txblLanguage.Text = book.BookData.VolumeInfo.Language;
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("public BookFile(clBook bk)", ex);
            }
        }

        private void OnBackRequested(object sender, BackRequestedEventArgs e)
        {
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            var frame = Window.Current.Content as Frame;
            frame.Navigate(typeof(MainFrame), new Library());
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            try
            {
                if (e.Parameter is clBook)
                {
                    this.book = e.Parameter as clBook;

                    clRequestAPI Req = new clRequestAPI("/api/review" + "?isbn=" + this.book.BookISBN);
                    Req.addAuthorization("Bearer", App.Token);
                    string res = await Req.GetRequest();
                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    JsonValueType j = jsonRes["data"].ValueType;
                    JsonObject k = jsonRes["data"].GetObject();
                    j = k["reviews"].ValueType;
                    JsonArray l = k["reviews"].GetArray();
                    ucReviewEditor revEdit = null;
                    foreach (IJsonValue i in l)
                    {
                        //Exemple de Json Review
                        //{{"id":2,"isbn":"9780441007462","rate":"5","content":"assez cool","created_at":"2017-06-15 15:07:46","user_name":"diobrando","can_edit":"false"}}
                        JsonObject t = i.GetObject();
                        if (t["can_edit"].GetString() == "false")
                        {
                            stpnlRev.Children.Add(new ucReviews(t));
                        }
                        else
                        {
                            revEdit = new ucReviewEditor(t);
                            grdUsrReview.Children.Add(revEdit);
                        }
                    }
                    if (revEdit == null)
                    {
                        revEdit = new ucReviewEditor(this.book.BookISBN);
                        grdUsrReview.Children.Add(revEdit);
                    }

                    Grid.SetRow(revEdit, 0);
                    if (book.BookData.VolumeInfo.Title != null)
                        txblTitle.Text = book.BookData.VolumeInfo.Title;
                    if (book.BookData.VolumeInfo.Authors.FirstOrDefault() != null)
                        txblAuthor.Text = book.BookData.VolumeInfo.Authors.FirstOrDefault();
                    if (book.BookData.VolumeInfo.Categories.FirstOrDefault() != null)
                        txblGenre.Text = book.BookData.VolumeInfo.Categories.FirstOrDefault();
                    if (book.BookData.VolumeInfo.Description != null)
                        txblSynopsis.Text = book.BookData.VolumeInfo.Description;
                    if (book.BookData.VolumeInfo.Publisher != null)
                        txblPublisher.Text = book.BookData.VolumeInfo.Publisher;
                    if (book.BookData.VolumeInfo.Language != null)
                        txblLanguage.Text = book.BookData.VolumeInfo.Language;
                    Windows.UI.Xaml.Media.Imaging.BitmapImage bitmapImage = new Windows.UI.Xaml.Media.Imaging.BitmapImage();
                    Uri uri = new Uri(book.BookData.VolumeInfo.ImageLinks.Thumbnail);
                    bitmapImage.UriSource = uri;
                    imgBook.Source = bitmapImage;
                    //book.BookData.VolumeInfo.ImageLinks.SmallThumbnail);
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("BookFile.onNavigatedTo()", ex);
            }
        }

        private async void btAddBook_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/book");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            res = await Req.PostRequest("isbn=" + book.BookISBN, "application/x-www-form-urlencoded");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            //if (!jsonRes.ContainsKey("errors"))
            if (jsonRes["errors"].ToString() != "null")
            {
                //placer une MsgBox ici
                string msg = null;
                msg += jsonRes["errors"].ToString() + " ";
                if (jsonRes.ContainsKey("title"))
                    msg += jsonRes["title"].ToString();
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
            }
            else
            {
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Succés : le livre est ajouté à la bibliothèque");
                await dial.ShowAsync();
            }
        }

        private async void btAddtoWhishlst(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/wish/book");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            res = await Req.PostRequest("isbn=" + book.BookISBN, "application/x-www-form-urlencoded");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            if (jsonRes["errors"].ToString() != "null")
            {
                //placer une MsgBox ici
                string msg = null;
                msg += jsonRes["errors"].ToString() + " ";
                if (jsonRes.ContainsKey("title"))
                    msg += jsonRes["title"].ToString();
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
            }
            else
            {
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Succés : le livre est ajouté à la bibliothèque");
                await dial.ShowAsync();
            }
        }

        private void AppBarButton_Click(object sender, RoutedEventArgs e)
        {

        }

        private async void btDeleteLib_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/book");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            res = await Req.DeletetRequest("?isbn=" + book.BookISBN + "&deleted=yes");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            //if (!jsonRes.ContainsKey("errors"))
            if (jsonRes["errors"].ToString() != "null")
            {
                //placer une MsgBox ici
                string msg = null;
                msg += jsonRes["errors"].ToString() + " ";
                if (jsonRes.ContainsKey("title"))
                    msg += jsonRes["title"].ToString();
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
            }
            else
            {
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Succés : le livre est retiré de la bibliothèque");
                await dial.ShowAsync();
            }
        }

        private async void btDeleteWish_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/wish/book");
            string res = null;

            Req.addHeader("application/x-www-form-urlencoded");
            Req.addAuthorization("Bearer", App.Token);

            res = await Req.DeletetRequest("?isbn=" + book.BookISBN + "&deleted=yes");
            JsonObject jsonRes;
            JsonObject.TryParse(res, out jsonRes);
            //if (!jsonRes.ContainsKey("errors"))
            if (jsonRes["errors"].ToString() != "null")
            {
                //placer une MsgBox ici
                string msg = null;
                msg += jsonRes["errors"].ToString() + " ";
                if (jsonRes.ContainsKey("title"))
                    msg += jsonRes["title"].ToString();
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                await dial.ShowAsync();
            }
            else
            {
                Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog("Succés : le livre est retiré de la liste de souhait");
                await dial.ShowAsync();
            }
        }
    }
}
