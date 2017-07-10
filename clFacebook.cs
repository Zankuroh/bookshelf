using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Authentication.Web;
using winsdkfb;

namespace BookShelf
{
    class clFacebook
    {
        public async Task<string> FBLogin()
        {
            FBSession sess = FBSession.ActiveSession;
            sess.FBAppId = "223894524688935";
            string SID = WebAuthenticationBroker.GetCurrentApplicationCallbackUri().ToString();
            sess.WinAppId = "s-1-15-2-2310251261-1648190076-1477656121-1768191773-3255972441-953421133-1881343534";
            List<String> permissionList = new List<String>();
            permissionList.Add("public_profile");
            permissionList.Add("email");
            permissionList.Add("user_friends");
            FBPermissions permissions = new FBPermissions(permissionList);
            //test relatif au token
            //FBAccessTokenData test = new FBAccessTokenData("wtf", )

            // Login to Facebook
            FBResult result = await sess.LoginAsync(permissions);
            if (result.Succeeded)
            {
                //FBUser user = sess.User;
                //ProfilePic.UserId = sess.User.Id;
                System.Diagnostics.Debug.WriteLine("Fb Login Success");
                string token = sess.AccessTokenData.AccessToken;
                return token;
            }
            else
            {
                //Login failed
                System.Diagnostics.Debug.WriteLine("Fb Login Failure");
                var msg = new Windows.UI.Popups.MessageDialog("Login Failure");
                await msg.ShowAsync();
                return null;
            }
        }

        public async Task<FBSession> FBgetSession()
        {
            FBSession sess = FBSession.ActiveSession;
            sess.FBAppId = "223894524688935";
            string SID = WebAuthenticationBroker.GetCurrentApplicationCallbackUri().ToString();
            sess.WinAppId = "s-1-15-2-2310251261-1648190076-1477656121-1768191773-3255972441-953421133-1881343534";
            List<String> permissionList = new List<String>();
            permissionList.Add("public_profile");
            permissionList.Add("email");
            permissionList.Add("user_friends");
            FBPermissions permissions = new FBPermissions(permissionList);
            //test relatif au token
            //FBAccessTokenData test = new FBAccessTokenData("wtf", )

            // Login to Facebook
            FBResult result = await sess.LoginAsync(permissions);
            if (result.Succeeded)
            {
                //FBUser user = sess.User;
                //ProfilePic.UserId = sess.User.Id;
                System.Diagnostics.Debug.WriteLine("Fb Login Success");
                string email = sess.User.Email;
                return sess;
            }
            else
            {
                //Login failed
                System.Diagnostics.Debug.WriteLine("Fb Login Failure");
                return null;
            }
        }
    }
}
