using Google.Apis.Books.v1.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BookShelf
{
    public class clBook
    {
        public enum Status { AchetéLu = 0, EmpruntéLu, NonLu };
        Volume bookData = null;
        string bookISBN = "";
        Status bookStatus;

        public Volume BookData
        {
            get
            {
                return bookData;
            }

            set
            {
                bookData = value;
            }
        }

        public string BookISBN
        {
            get
            {
                return bookISBN;
            }

            set
            {
                bookISBN = value;
            }
        }

        public clBook(string isbn, Volume data)
        {
            BookISBN = isbn;
            bookData = data;
        }

        public Status BookStatus
        {
            get
            {
                return bookStatus;
            }

            set
            {
                bookStatus = value;
            }
        }
    }
}
