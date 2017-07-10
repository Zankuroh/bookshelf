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
        //string bookTitle = "";
        //string bookSubTitle = "";
        //string bookAuthor = "";
        //string bookGenre = "";
        //string bookPublisher = "";
        //string bookLanguage = "";
        //string bookDescription = "";
        Volume bookData = null;
        string bookISBN = "";

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
        //public string BookTitle
        //{
        //    get
        //    {
        //        return bookTitle;
        //    }

        //    set
        //    {
        //        bookTitle = value;
        //    }
        //}

        //public string BookSubTitle
        //{
        //    get
        //    {
        //        return bookSubTitle;
        //    }

        //    set
        //    {
        //        bookSubTitle = value;
        //    }
        //}

        //public string BookAuthor
        //{
        //    get
        //    {
        //        return bookAuthor;
        //    }

        //    set
        //    {
        //        bookAuthor = value;
        //    }
        //}

        //public string BookGenre
        //{
        //    get
        //    {
        //        return bookGenre;
        //    }

        //    set
        //    {
        //        bookGenre = value;
        //    }
        //}

        //public string BookLanguage
        //{
        //    get
        //    {
        //        return bookLanguage;
        //    }

        //    set
        //    {
        //        bookLanguage = value;
        //    }
        //}

        //public string BookDescription
        //{
        //    get
        //    {
        //        return bookDescription;
        //    }

        //    set
        //    {
        //        bookDescription = value;
        //    }
        //}

        //public string BookPublisher
        //{
        //    get
        //    {
        //        return bookPublisher;
        //    }

        //    set
        //    {
        //        bookPublisher = value;
        //    }
        //}

        //public clBook()
        //{
        //}

        //public clBook(Volume gbook)
        //{
        //    try
        //    { 
        //    bookTitle =  gbook.VolumeInfo.Title;
        //    bookSubTitle = gbook.VolumeInfo.Subtitle;
        //    bookAuthor =  gbook.VolumeInfo.Authors.FirstOrDefault();
        //    bookGenre = gbook.VolumeInfo.Categories.FirstOrDefault();
        //    bookDescription = gbook.VolumeInfo.Description;
        //    BookPublisher = gbook.VolumeInfo.Publisher;
        //    bookLanguage = gbook.VolumeInfo.Language;
        //    bookISBN = gbook.Id;
        //}
        //    catch (ArgumentNullException argNull)
        //    {
        //        System.Diagnostics.Debug.WriteLine("clBook.clBook(Volume gBook) = " + argNull.Message);
        //    }
        //    catch (NullReferenceException refNull)
        //    {
        //        System.Diagnostics.Debug.WriteLine("clBook.clBook(Volume gBook) = " + refNull.Message);
        //    }
        //}
        //public clBook(string title, string author, string publisher, string genre, string language)
        //{
        //    bookTitle = title;
        //    bookAuthor = author;
        //    bookGenre = genre;
        //    BookPublisher = publisher;
        //    bookLanguage = language;
        //}
    }
}
