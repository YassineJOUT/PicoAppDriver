package ma.fstm.ilisi.pico.picoappdriver.repository;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * PicoWebRestClient class
 * This class is responsible for calling the API to post json data or get json data
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class PicoWebRestClient  {
    // Url to the api
    public static final String IPAddr = "pico.ossrv.nl";
   // public static final String IPAddr = "192.168.43.163";
    private static final String BASE_URL = "http://"+IPAddr+":9090/api/";
    // Asynchronous Http client
    private static AsyncHttpClient client = new AsyncHttpClient();
    // adding the content type to the header

    /**
     * setup function
     * this method adds a header to an http request
     * @param header headers key
     * @param value the value for the header key
     */
    public  static void setUp(String header,String value){
        client.addHeader(header,value);
    }

    /**
     * get function
     * this method is a http get implementation to get json data
     * @param url the url to the api
     * @param params parameters to encapsulate in the http packet
     * @param responseHandler the http response object
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    /**
     * post function
     * this method is a http post implementation to post parameters to the api
     * @param url the url to the api
     * @param params parameters to encapsulate in the http packet
     * @param responseHandler the http response object
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    /**
     * getAbsoluteUrl function
     * this method concatenates the base url with the relative URL
     * @param relativeUrl the relative url to the api call
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}