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
using Windows.Data.Json;
using Windows.Security.Authentication.Web;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace BookShelf
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
        }
        private async void btFacebook_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                clFacebook fb = new clFacebook();
                string token = await fb.FBLogin();
                clRequestAPI Req = new clRequestAPI("/api/oauth");
                string requestContent = "token=" + token + "&provider=" + "facebook";
                string result = await Req.PostRequest(requestContent, "application/x-www-form-urlencoded");
                JsonObject jsonRes;
                JsonObject.TryParse(result, out jsonRes);
                if (jsonRes.ContainsKey("errors") && jsonRes["errors"].ToString() == "null")
                {
                    JsonObject test = jsonRes["data"].GetObject();
                    string tk = test["token"].ToString();
                    if (tk == "false")
                    {
                        string msg = "Echec de l'authentification Facebook";
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                    }
                    else
                    {
                        BookShelf.App.socialAuthLogged = true;
                        App.Token = tk;
                        //App.Token = Req.findResponse("token");
                        Frame.Navigate(typeof(MainFrame));
                    }
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btFacebook_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        //public async Task ParseAuthenticationResult(WebAuthenticationResult result)
        //{
        //    switch (result.ResponseStatus)
        //    {
        //        case WebAuthenticationStatus.ErrorHttp:
        //            //Debug.WriteLine("Error");
        //            break;
        //        case WebAuthenticationStatus.Success:
        //            var pattern = string.Format("{0}#access_token={1}&expires_in={2}", WebAuthenticationBroker.GetCurrentApplicationCallbackUri(), "(?<access_token>.+)", "(?<expires_in>.+)");
        //            var match = System.Text.RegularExpressions.Regex.Match(result.ResponseData, pattern);

        //            var access_token = match.Groups["access_token"];
        //            var expires_in = match.Groups["expires_in"];

        //            //AccessToken = access_token.Value;
        //            //TokenExpiry = DateTime.Now.AddSeconds(double.Parse(expires_in.Value));

        //            break;
        //        case WebAuthenticationStatus.UserCancel:
        //            //Debug.WriteLine("Operation aborted");
        //            break;
        //        default:
        //            break;
        //    }
        //}

        private async void btConnection_Click(object sender, RoutedEventArgs e)
        {
            clRequestAPI Req = new clRequestAPI("/api/auth");
            string requestContent = "email=" + txbxUsrEmail.Text + "&password=" + pwdbxUsr.Password;
            try
            {
                string result = await Req.PostRequest(requestContent, "application/x-www-form-urlencoded");
                JsonObject jsonRes;
                JsonObject.TryParse(result, out jsonRes);
                //if (!jsonRes.ContainsKey("errors"))
                if (jsonRes.ContainsKey("errors") && jsonRes["errors"].ToString() == "null")
                {
                    JsonObject test = jsonRes["data"].GetObject();
                    string tk = test["token"].ToString();
                    if (tk == "false")
                    {
                        string msg = "email ou mot de passe incorrect";
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                        await dial.ShowAsync();
                    }
                    else
                    {
                        App.Token = tk;
                        //App.Token = Req.findResponse("token");
                        Frame.Navigate(typeof(MainFrame));
                    }
                }
                else
                {
                    //placer une MsgBox ici
                    string msg = "Echec : la connexion a echoué\n";
                    msg += jsonRes["errors"].ToString() + " ";
                    if (jsonRes.ContainsKey("title"))
                        msg += jsonRes["title"].ToString();
                    Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg);
                    await dial.ShowAsync();
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btConnection_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        private void btCreateAccount_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(createAccount));
        }

        //-------------------------- FB connection Test---------------------------------
        //private void OutputToken(String TokenUri)
        //{
        //    FacebookReturnedToken.Text = TokenUri;
        //}

        //private async void Launch_Click(object sender, RoutedEventArgs e)
        //{
        //    if (FacebookClientID.Text == "")
        //    {
        //        //rootPage.NotifyUser("Please enter an Client ID.", NotifyType.StatusMessage);
        //    }
        //    else if (FacebookCallbackUrl.Text == "")
        //    {
        //        //rootPage.NotifyUser("Please enter an Callback URL.", NotifyType.StatusMessage);
        //    }

        //    try
        //    {
        //        string fbID= "223894524688935";
        //        string SID = WebAuthenticationBroker.GetCurrentApplicationCallbackUri().AbsoluteUri;
        //        String FacebookURL = "https://www.facebook.com/dialog/oauth?client_id=" + Uri.EscapeDataString(FacebookClientID.Text) + "&redirect_uri=" + Uri.EscapeDataString(FacebookCallbackUrl.Text) + "&scope=read_stream&display=popup&response_type=token";

        //        System.Uri StartUri = new Uri(FacebookURL);
        //        System.Uri EndUri = new Uri(FacebookCallbackUrl.Text);

        //        //rootPage.NotifyUser("Navigating to: " + FacebookURL, NotifyType.StatusMessage);

        //        WebAuthenticationResult WebAuthenticationResult = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.None, StartUri, EndUri);
        //        if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.Success)
        //        {
        //            OutputToken(WebAuthenticationResult.ResponseData.ToString());
        //            await GetFacebookUserNameAsync(WebAuthenticationResult.ResponseData.ToString());
        //        }
        //        else if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.ErrorHttp)
        //        {
        //            OutputToken("HTTP Error returned by AuthenticateAsync() : " + WebAuthenticationResult.ResponseErrorDetail.ToString());
        //        }
        //        else
        //        {
        //            OutputToken("Error returned by AuthenticateAsync() : " + WebAuthenticationResult.ResponseStatus.ToString());
        //        }

        //    }
        //    catch (Exception Error)
        //    {
        //        //rootPage.NotifyUser(Error.Message, NotifyType.ErrorMessage);
        //    }
        //}

        ///// <summary>
        ///// This function extracts access_token from the response returned from web authentication broker
        ///// and uses that token to get user information using facebook graph api. 
        ///// </summary>
        ///// <param name="webAuthResultResponseData">responseData returned from AuthenticateAsync result.</param>
        //private async Task GetFacebookUserNameAsync(string webAuthResultResponseData)
        //{
        //    //Get Access Token first
        //    string responseData = webAuthResultResponseData.Substring(webAuthResultResponseData.IndexOf("access_token"));
        //    String[] keyValPairs = responseData.Split('&');
        //    string access_token = null;
        //    string expires_in = null;
        //    for (int i = 0; i < keyValPairs.Length; i++)
        //    {
        //        String[] splits = keyValPairs[i].Split('=');
        //        switch (splits[0])
        //        {
        //            case "access_token":
        //                access_token = splits[1]; //you may want to store access_token for further use. Look at Scenario 5 (Account Management).
        //                break;
        //            case "expires_in":
        //                expires_in = splits[1];
        //                break;
        //        }
        //    }

        //    //rootPage.NotifyUser("access_token = " + access_token, NotifyType.StatusMessage);
        //    //Request User info.
        //    HttpClient httpClient = new HttpClient();
        //    string response = await httpClient.GetStringAsync(new Uri("https://graph.facebook.com/me?access_token=" + access_token));
        //    Windows.Data.Json.JsonObject value = JsonValue.Parse(response).GetObject();
        //    string facebookUserName = value.GetNamedString("name");

        //    //rootPage.NotifyUser(facebookUserName + " is connected!", NotifyType.StatusMessage);
        //}

        private async void btForgotPwd_Click(object sender, RoutedEventArgs e)
        {
            //A supprimer pour la release
            //Frame.Navigate(typeof(Search));
            try
            {
                cdEnterEmail dBox = new cdEnterEmail();
                ContentDialogResult result = await dBox.ShowAsync();
                if (result == ContentDialogResult.Primary)
                {
                    string requestContent = "email=" + dBox.Email;
                    clRequestAPI Req = new clRequestAPI("/api/resetpassword");
                    string res = null;

                    Req.addHeader("application/x-www-form-urlencoded");
                    Req.addAuthorization("Bearer", App.Token);

                    res = await Req.PostRequest(requestContent, "application/x-www-form-urlencoded");
                    JsonObject jsonRes;
                    JsonObject.TryParse(res, out jsonRes);
                    //if (!jsonRes.ContainsKey("errors"))
                    if (jsonRes["errors"].ToString() == "null")
                    {
                        cdEnterPsswd pssWord = new cdEnterPsswd();
                        ContentDialogResult result2 = await pssWord.ShowAsync();
                        if (result2 == ContentDialogResult.Primary)
                        {
                            clRequestAPI Req2 = new clRequestAPI("/api/resetpassword/" + pssWord.Passwd);
                            string res2 = null;

                            Req2.addAuthorization("Bearer", App.Token);
                            res2 = await Req2.GetRequest();
                        }
                    }
                    else
                    {
                        JsonObject msg = jsonRes["errors"].GetObject();
                        ValueType s =  msg["email"].ValueType;
                        Windows.UI.Popups.MessageDialog dial = new Windows.UI.Popups.MessageDialog(msg["email"].GetArray().GetStringAt(0));
                        await dial.ShowAsync();
                    }
                }

            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btForgotPwd_Click(object sender, RoutedEventArgs e)", ex);
            }
        }

        private async void btGoogle_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string OutputToken = null;
                string id = "758818420378-05bhrq3ks8sbk49pbs3blf8h85sd7uuf.apps.googleusercontent.com";
                string callbackurl = WebAuthenticationBroker.GetCurrentApplicationCallbackUri().ToString();
                String GoogleURL = "https://accounts.google.com/o/oauth2/auth?client_id=" + Uri.EscapeDataString(id) + "&redirect_uri=" + Uri.EscapeDataString(callbackurl) + "&response_type=code&scope=" + Uri.EscapeDataString("http://picasaweb.google.com/data");

                Uri StartUri = new Uri(GoogleURL);
                // When using the desktop flow, the success code is displayed in the html title of this end uri
                Uri EndUri = new Uri("https://accounts.google.com/o/oauth2/approval?");

                WebAuthenticationResult WebAuthenticationResult = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.UseTitle, StartUri, EndUri);
                if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.Success)
                {
                    OutputToken = (WebAuthenticationResult.ResponseData.ToString());
                }
                else if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.ErrorHttp)
                {
                    OutputToken = ("HTTP Error returned by AuthenticateAsync() : " + WebAuthenticationResult.ResponseErrorDetail.ToString());
                }
                else
                {
                    OutputToken = ("Error returned by AuthenticateAsync() : " + WebAuthenticationResult.ResponseStatus.ToString());
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("btGoogle_Click(object sender, RoutedEventArgs e)", ex);
            }
        }
    }
}
