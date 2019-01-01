package ma.fstm.ilisi.pico.picoappdriver.repository;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class PicoWebRestClientSync{
    // Url to the api
    private static final String BASE_URL = "http://"+PicoWebRestClient.IPAddr+":9090/api/";
    // Asynchronous Http client
    private static SyncHttpClient client = new SyncHttpClient();
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