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
    //Exemple de Json Review
    //{{"id":2,"isbn":"9780441007462","rate":"5","content":"assez cool","created_at":"2017-06-15 15:07:46","user_name":"diobrando","can_edit":"false"}}
    public sealed partial class ucReviews : UserControl
    {
        JsonObject Review = null;
        public ucReviews()
        {
            this.InitializeComponent();
        }
        public ucReviews(JsonObject r)
        {
            this.InitializeComponent();
            this.Review = r;
            txblUsrName.Text = this.Review["user_name"].GetString();
            txbxReviewDate.Text = this.Review["created_at"].GetString();
            txblReview.Text = this.Review["content"].GetString();
        }
    }
}
