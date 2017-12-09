using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;

namespace BookShelf
{
    public class clProfile
    {
        public string Pseudo;
        public string Email;
        public string Birthday;
        public string LikedGenre;
        public string FavBook;
        public DateTime ProfileCreationDate;
        public DateTime LastConnection;
        //{
        //    get
        //    {
        //        return FirstName;
        //    }

        //    set
        //    {
        //        FirstName = value;
        //    }
        //}

        //public string LastName
        //{
        //    get
        //    {
        //        return LastName;
        //    }

        //    set
        //    {
        //        LastName = value;
        //    }
        //}

        //public int Age
        //{
        //    get
        //    {
        //        return Age;
        //    }

        //    set
        //    {
        //        Age = value;
        //    }
        //}

        //public string Gender
        //{
        //    get
        //    {
        //        return Gender;
        //    }

        //    set
        //    {
        //        Gender = value;
        //    }
        //}

        //public Image Pic
        //{
        //    get
        //    {
        //        return Pic;
        //    }

        //    set
        //    {
        //        Pic = value;
        //    }
        //}

        public static implicit operator clProfile(NavigationEventArgs v)
        {
            throw new NotImplementedException();
        }
    }
}
