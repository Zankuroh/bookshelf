using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Google.Apis.Books.v1.Data;
using Google.Apis.Books.v1;
using Google.Apis.Services;

namespace BookShelf
{
    class clISBNsearch
    {
        public static BooksService service = new BooksService(
            new BaseClientService.Initializer
            {
                ApplicationName = "ISBNsearch",
                ApiKey = "AIzaSyDXRTiTVEJV3Lk0hZRxKEnBJG181fCjTbA",
            });
        public static async Task<Volume> SearchISBNVol(string isbn)
        {
            var result = await service.Volumes.List(isbn).ExecuteAsync();
            if (result != null && result.Items != null)
            {
                var item = result.Items.FirstOrDefault();
                return item;
            }
            return null;
        }
        public static async Task<clBook> SearchISBNclBook(string isbn)
        {
            var result = await service.Volumes.List(isbn).ExecuteAsync();
            if (result != null && result.Items != null)
            {
                Volume item = result.Items.FirstOrDefault();
                return (new clBook(isbn, item));
            }
            return null;
        }
    }
}
