using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Graphics.Imaging;
using Windows.UI.Xaml.Media.Imaging;
using Windows.Media.Ocr;
using Windows.Media.Capture;
using Windows.Storage.Streams;
using Windows.Storage;
using Windows.Foundation;
using ZXing;

namespace BookShelf
{
    class clBarCode
    {

        static public async Task<string> readBarcode()
        {
            // create a barcode reader instance
            string ret = "";
            try
            {
                CameraCaptureUI captureUI = new CameraCaptureUI();
                captureUI.PhotoSettings.Format = CameraCaptureUIPhotoFormat.Jpeg;
                StorageFile photo = await captureUI.CaptureFileAsync(CameraCaptureUIMode.Photo);
                if (photo == null)
                {
                    // User cancelled photo capture
                    return null;
                }
                SoftwareBitmap softwareBitmap;
                using (Windows.Storage.Streams.IRandomAccessStream stream = await photo.OpenAsync(Windows.Storage.FileAccessMode.Read))
                {
                    // Create the decoder from the stream
                    BitmapDecoder decoder = await BitmapDecoder.CreateAsync(stream);
                    // Get the SoftwareBitmap representation of the file
                    softwareBitmap = await decoder.GetSoftwareBitmapAsync();
                    //tester le softwareBitmap.CopyToBuffer sur le pixek index d'un writeablebitmap avec un vrai exemple de code barre pour être sur
                }
                OcrEngine eng = OcrEngine.TryCreateFromUserProfileLanguages();
                OcrResult res = await eng.RecognizeAsync(softwareBitmap);
                ret = res.Text;
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("readBarcode", ex);
            }
            return ret;
        }

        static public async Task<string> readBarcodebis()
        {
            try
            {
                CameraCaptureUI captureUI = new CameraCaptureUI();
                captureUI.PhotoSettings.Format = CameraCaptureUIPhotoFormat.Jpeg;
                captureUI.PhotoSettings.CroppedSizeInPixels = new Size(2592, 1456);
                StorageFile photo = await captureUI.CaptureFileAsync(CameraCaptureUIMode.Photo);
                if (photo == null)
                {
                    // User cancelled photo capture
                    return null;
                }
                //IRandomAccessStream stream = await photo.OpenAsync(FileAccessMode.Read);
                var stream = await photo.OpenAsync(FileAccessMode.Read);
                // initialize with 1,1 to get the current size of the image
                var writeableBmp = new WriteableBitmap(1, 1);
                writeableBmp.SetSource(stream);
                // and create it again because otherwise the WB isn't fully initialized and decoding
                // results in a IndexOutOfRange
                writeableBmp = new WriteableBitmap(writeableBmp.PixelWidth, writeableBmp.PixelHeight);
                stream.Seek(0);
                writeableBmp.SetSource(stream);
                //await wrt.SetSourceAsync(stream);
                IBarcodeReader reader = new BarcodeReader();
                //    // detect and decode the barcode inside the bitmap
                var result = reader.Decode(writeableBmp);
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("fuck", ex);
            }
            return null;
        }
        static public async Task<string> testPicker()
        {
            string ret = "";
            try
            {
                SoftwareBitmap softwareBitmap = null; ;
                Windows.Storage.Pickers.FileOpenPicker fileOpenPicker = new Windows.Storage.Pickers.FileOpenPicker();
                fileOpenPicker.SuggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.DocumentsLibrary;
                fileOpenPicker.FileTypeFilter.Add(".jpg");
                fileOpenPicker.ViewMode = Windows.Storage.Pickers.PickerViewMode.Thumbnail;

                var inputFile = await fileOpenPicker.PickSingleFileAsync();
                if (inputFile == null)
                {
                    // The user cancelled the picking operation
                    return null;
                }
                using (Windows.Storage.Streams.IRandomAccessStream stream = await inputFile.OpenAsync(Windows.Storage.FileAccessMode.Read))
                {
                    // Create the decoder from the stream
                    BitmapDecoder decoder = await BitmapDecoder.CreateAsync(stream);
                    // Get the SoftwareBitmap representation of the file
                    softwareBitmap = await decoder.GetSoftwareBitmapAsync();
                    //tester le softwareBitmap.CopyToBuffer sur le pixek index d'un writeablebitmap avec un vrai exemple de code barre pour être sur
                }
                OcrEngine eng = OcrEngine.TryCreateFromUserProfileLanguages();
                OcrResult res = await eng.RecognizeAsync(softwareBitmap);
                ret = res.Text;
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("readBarcode", ex);
            }
            return ret;
        }

    }
}
