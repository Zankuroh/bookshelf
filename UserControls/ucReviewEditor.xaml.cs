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

// The User Control item template is documented at http://go.microsoft.com/fwlink/?LinkId=234236

namespace BookShelf
{
    public sealed partial class ucReviewEditor : UserControl
    {
        JsonObject Review = null;
        string isbn = null;

        public ucReviewEditor()
        {
            this.InitializeComponent();
        }

        public ucReviewEditor(string isbn)
        {
            this.InitializeComponent();
            this.isbn = isbn;
        }

        public ucReviewEditor(JsonObject r)
        {
            this.InitializeComponent();
            btReviewEdit.Content = "MODIFIER";
            btReviewDel.Visibility = Visibility.Visible;
            try
            {
                this.Review = r;
                this.isbn = r["isbn"].GetString();
                sldGrade.Value = double.Parse(this.Review["rate"].GetString());
                txbxReview.Text = this.Review["content"].GetString();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("ucReviewEditor(JsonObject r)", ex);
            }

        }

        private async void btReviewDel_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string v = "";
                if (this.Review["id"].ValueType == JsonValueType.String)
                    v = this.Review["id"].GetString();
                else if (this.Review["id"].ValueType == JsonValueType.Number)
                    v = this.Review["id"].GetNumber().ToString();

                clRequestAPI Req = new clRequestAPI("/api/review/" + v);
                string res = null;

                Req.addHeader("application/x-www-form-urlencoded");
                Req.addAuthorization("Bearer", App.Token);

                res = await Req.DeletetRequest("?validation=true");

                JsonObject jsonRes;
                JsonObject.TryParse(res, out jsonRes);
                if (jsonRes["errors"].ToString() != "null")
                {
                    //placer une MsgBox ici
                    string msg = null;
                    msg += "La suppression de la critique a echoué\n" + jsonRes["errors"].ToString() + " ";
                    if (jsonRes.ContainsKey("title"))
                        msg += jsonRes["title"].ToString();
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                }
                else
                {
                    btReviewEdit.Content = "ENVOYER";
                    btReviewDel.Visibility = Visibility.Collapsed;
                    txbxReview.Text = "";
                    sldGrade.Value = 0;
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btReviewDel_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        private async void btReviewEdit_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Button b = sender as Button;
                if (b.Content.ToString() == "MODIFIER")
                {
                    string v = this.Review["id"].GetString();
                    string rev = this.Review["content"].GetString();
                    string rate = this.Review["rate"].GetString();
                    clRequestAPI Req = new clRequestAPI("/api/review/" + v);
                    string res = null;

                    Req.addHeader("application/x-www-form-urlencoded");
                    Req.addAuthorization("Bearer", App.Token);

                    res = await Req.PutRequest("content=" + txbxReview.Text+ "&rate=" + sldGrade.Value.ToString(), "application/x-www-form-urlencoded");

                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    if (jsonRes["errors"].ToString() != "null")
                    {
                        //placer une MsgBox ici
                        string msg = null;
                        msg += "La modification a echoué\n" + jsonRes["errors"].ToString() + " ";
                        if (jsonRes.ContainsKey("title"))
                            msg += jsonRes["title"].ToString();
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                        txbxReview.Text = rev;
                        sldGrade.Value = double.Parse(rate);
                    }
                }
                else if (b.Content.ToString() == "ENVOYER")
                {
                    clRequestAPI post = new clRequestAPI("/api/review");
                    post.addHeader("application/x-www-form-urlencoded");
                    post.addAuthorization("Bearer", App.Token);
                    string res = await post.PostRequest("isbn=" + this.isbn + "&content=" + txbxReview.Text + "&rate=" + sldGrade.Value.ToString(), "application/x-www-form-urlencoded");

                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    if (jsonRes["errors"].ToString() != "null")
                    {
                        //placer une MsgBox ici
                        string msg = null;
                        msg += "La publication de la critique a echoué\n" + jsonRes["errors"].ToString() + " ";
                        if (jsonRes.ContainsKey("title"))
                            msg += jsonRes["title"].ToString();
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                        txbxReview.Text = "";
                        sldGrade.Value = 0;
                    }
                    else
                    {
                        this.Review = jsonRes["data"].GetObject()["review"].GetObject();
                        btReviewEdit.Content = "MODIFIER";
                        btReviewDel.Visibility = Visibility.Visible;
                    }
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btReviewEdit_Click(object sender, RoutedEventArgs e)", ex);
            }
        }
    }
}
