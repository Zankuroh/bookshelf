using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Web.Http;

namespace BookShelf
{
    class clRequestAPI
    {
        private string serverIP = "http://79.137.87.198";
        private HttpClient httpClient = new HttpClient();
        private Uri serverUri = null;
        private string response = null;

        public Uri ServerUri
        {
            get
            {
                return serverUri;
            }

            set
            {
                serverUri = value;
            }
        }

        public clRequestAPI(string serverPath)
        {
            ServerUri = new Uri(this.serverIP + serverPath);
        }

        public clRequestAPI(string ip, string serverPath)
        {
            serverIP = ip;
            ServerUri = new Uri(this.serverIP + serverPath);
        }

        public bool addHeader(string header)
        {
            var headers = httpClient.DefaultRequestHeaders;
            bool res = false;
            if (res = headers.UserAgent.TryParseAdd(header))
            {
                res = true;
            }
            else
            {
                System.Diagnostics.Debug.WriteLine("Invalid header value: " + header);
            }
            return res;
        }

        public void addAuthorization(string scheme, string token)
        {
            var headers = httpClient.DefaultRequestHeaders;
            headers.Authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue(scheme, token);
        }

        public async Task<string> PostRequest(string requestContent, string mediaType)
        {
            //Send the POST request asynchronously and retrieve the response as a string.
            HttpResponseMessage httpResponse = new HttpResponseMessage();
            string httpResponseBody = "";
            string stringResponse = null;
            IHttpContent content = new HttpStringContent(requestContent, Windows.Storage.Streams.UnicodeEncoding.Utf8, mediaType);
            //IHttpContent content = new HttpStringContent(requestContent);

            try
            {
                //Send the POST request
                httpResponse = await httpClient.PostAsync(this.ServerUri, content);
                httpResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("PostRequest", ex, httpResponseBody.ToString());
            }
            finally
            {
                httpResponseBody = await httpResponse.Content.ReadAsStringAsync();
                stringResponse = httpResponseBody.ToString();
                response = stringResponse;
            }
            return (stringResponse);
        }

        public async Task<string> GetRequest()
        {
            HttpResponseMessage httpResponse = new Windows.Web.Http.HttpResponseMessage();
            string httpResponseBody = "";
            string stringResponse = null;

            try
            {
                //Send the GET request
                httpResponse = await httpClient.GetAsync(this.ServerUri);
                httpResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage(" GetRequest()", ex);
            }
            finally
            {
                httpResponseBody = await httpResponse.Content.ReadAsStringAsync();
                stringResponse = httpResponseBody.ToString();
                response = stringResponse;
            }
            return (stringResponse);
        }

        public async Task<string> DeletetRequest(string requestContent)
        {
            //Send the DELETE request asynchronously and retrieve the response as a string.
            HttpResponseMessage httpResponse = new Windows.Web.Http.HttpResponseMessage();
            string httpResponseBody = "";
            string stringResponse = null;

            try
            {
                Uri u = new Uri(this.ServerUri.ToString() + requestContent);
                //Send the DELETE request
                httpResponse = await httpClient.DeleteAsync(u);
                httpResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("DeletetRequest(string requestContent)", ex, httpResponseBody.ToString());
            }
            finally
            {
                httpResponseBody = await httpResponse.Content.ReadAsStringAsync();
                stringResponse = httpResponseBody.ToString();
                response = stringResponse;
            }
            return (stringResponse);
        }

        public async Task<string> PutRequest(string requestContent, string mediaType)
        {
            //Send the POST request asynchronously and retrieve the response as a string.
            HttpResponseMessage httpResponse = new HttpResponseMessage();
            string httpResponseBody = "";
            string stringResponse = null;
            IHttpContent content = new HttpStringContent(requestContent, Windows.Storage.Streams.UnicodeEncoding.Utf8, mediaType);
            //IHttpContent content = new HttpStringContent(requestContent);

            try
            {
                //Send the PUT request
                httpResponse = await httpClient.PutAsync(this.ServerUri, content);
                httpResponse.EnsureSuccessStatusCode();
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("PutRequest", ex, httpResponseBody.ToString());
            }
            finally
            {
                httpResponseBody = await httpResponse.Content.ReadAsStringAsync();
                stringResponse = httpResponseBody.ToString();
                response = stringResponse;
            }
            return (stringResponse);
        }
        //public async Task<string> DeleteRequestbis(string[] requestParameters)
        //{
        //    //Send the DELETE request asynchronously and retrieve the response as a string.
        //    HttpResponseMessage httpResponse = new HttpResponseMessage();
        //    string httpResponseBody = "";
        //    string stringResponse = null;
        //    string requestContent = "?";

        //    int max = requestParameters.Count();
        //    for (int i = 0; i < max; i++)
        //    {
        //        requestContent += requestParameters[i];
        //        if (i != (max - 1))
        //        requestContent += "&";
        //    }
        //    try
        //    {
        //        Uri u = new Uri(this.ServerUri.ToString() + requestContent);
        //        //Send the DELETE request
        //        httpResponse = await httpClient.DeleteAsync(u);
        //        httpResponse.EnsureSuccessStatusCode();
        //        httpResponseBody = await httpResponse.Content.ReadAsStringAsync();
        //        stringResponse = httpResponseBody.ToString();
        //        response = stringResponse;
        //    }
        //    catch (Exception ex)
        //    {
        //        clErrorHandling.ErrorMessage("DeleteRequest(string[] requestParameters)", ex, httpResponseBody.ToString());
        //    }
        //    return (stringResponse);
        //}

        public string findResponse(string data)
        {
            string[] parsed;

            try
            {
                parsed = response.Split("\" :{}".ToCharArray()).Where(s => !string.IsNullOrEmpty(s)).ToArray();
                int i = 0;
                foreach (string str in parsed)
                {
                    if (str == data && parsed.Length > (i + 1))
                    {
                        return (parsed[i + 1]);
                    }
                    i++;
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("findResponse(string data)", ex);
            }
            return null;
        }

        public List<string> findResponseS(string data)
        {
            string[] parsed;
            List<string> lst = new List<string>();

            try
            {
                parsed = response.Split("\" :{}".ToCharArray()).Where(s => !string.IsNullOrEmpty(s)).ToArray();
                int i = 0;
                foreach (string str in parsed)
                {
                    if (str == data && parsed.Length > (i + 1))
                    {
                        lst.Add(parsed[i + 1]);
                    }
                    i++;
                }
            }
            catch (Exception ex)
            {
                clErrorHandling.ErrorMessage("findResponse(string data)", ex);
            }
            return lst;
        }
    }
}
