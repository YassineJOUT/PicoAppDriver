package ma.fstm.ilisi.pico.picoappdriver.model;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ma.fstm.ilisi.pico.picoappdriver.R;
import ma.fstm.ilisi.pico.picoappdriver.repository.PicoWebRestClient;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.ConfigClass;
import ma.fstm.ilisi.pico.picoappdriver.view.MapsActivity;

/**
 * Citizen is a business logic class
 * This class alow a user to authenticate, command an ambulance...
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class Citizen {
    /**
     * @param full_name represent a none NULL full name of a citizen
     */
    @NonNull
    private String full_name ;
    /**
     * @param coordinates represent an instance of GpsCooridantes class
     */
    @NonNull
    private GpsCoordinates coordinates ;
    /**
     * @param phone_number citizen phone number
     */
    @NonNull
    private String phone_number ;
    /**
     * @param password citizen password
     */
    @NonNull
    private String password ;
    /**
     * @param image citizen profile image
     */
    private Image image;

    /**
     * Class constructor used in sign in case
     * @param phone_number citizen phone number
     * @param password citizen password
     */
    public Citizen(@NonNull String phone_number, @NonNull String password){
        this.phone_number = phone_number;
        this.password = password;
    }
    /**
     * Class constructor to initialize all parameters
     */
    public Citizen(@NonNull String full_name, @NonNull GpsCoordinates coordinates,
                   @NonNull String phone_number, @NonNull String password, Image image) {
        this.full_name = full_name;
        this.coordinates = coordinates;
        this.phone_number = phone_number;
        this.password = password;
        this.image = image;
    }
    /**
     * Class constructor to initialize all parameters but the image because it's optional
     */
    public Citizen(@NonNull String full_name, @NonNull GpsCoordinates coordinates,
                   @NonNull String phone_number, @NonNull String password) {
        this.full_name = full_name;
        this.coordinates = coordinates;
        this.phone_number = phone_number;
        this.password = password;
    }
    /**
     * full name getter
     * @return the full name of the citizen
     */
    @NonNull
    public String getFull_name() {
        return full_name;
    }
    /**
     * full name setter
     * @param full_name full name of a citizen
     */
    public void setFull_name(@NonNull String full_name) {
        this.full_name = full_name;
    }
    /**
     * gps coordinates getter
     * @return coordinates representing the current localisation of a citizen
     */
    @NonNull
    public GpsCoordinates getCoordinates() {
        return coordinates;
    }
    /**
     * sets the current position of a citizen
     * @param coordinates
     */
    public void setCoordinates(@NonNull GpsCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * phone number getter
     * @return citizen phone number
     */
    @NonNull
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * sets the phone number of the citizen
     * @param phone_number
     */
    public void setPhone_number(@NonNull String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * password getter
     * @return citizen password
     */
    @NonNull
    public String getPassword() {
        return password;
    }

    /**
     * sets the password of a citizen
     * @param password
     */
    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    /**
     * get profile image of the citizen
     * @return
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the profile image of a citizen
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Verifies if the data provided by the user is valid when sign in
     * @return true the data is valid, false otherwise
     */
    public boolean isDataInputValidForLogin(){

        return !TextUtils.isEmpty(getPhone_number()) && getPhone_number().length() == 10 &&
                Patterns.PHONE.matcher(getPhone_number()).matches() && getPassword().length() > 5;
    }
    public boolean isDataInputValidForSignUp(){

        return !TextUtils.isEmpty(getPhone_number()) && getPhone_number().length() == 10 &&
                getFull_name().length() >  4 && getFull_name().length() <= 20 &&
                Patterns.PHONE.matcher(getPhone_number()).matches() && getPassword().length() > 5;
    }

    /**
     * This function interogate the api to see if the user exists in the database
     * if so the api return the token for authorization
     * otherwise it returns a failure response with a error msg
     * @param view the view is used to get applciation context to change activity when
     *            authentication succeeded
     */
    public void SignIn(View view){
        View mLoginFormView = view.getRootView().findViewById(R.id.login_form);
        View mProgressView = view.getRootView().findViewById(R.id.login_progress);
        View mSignInProgressIcon = view.getRootView().findViewById(R.id.ProgressIcon);
        mProgressView.setVisibility(View.VISIBLE);
        mLoginFormView.setVisibility(View.GONE);
        mSignInProgressIcon.setVisibility(View.VISIBLE);

        String MsgErr = "Success";
        /* adding parameters to the http request */
        RequestParams params = new RequestParams();

        params.put("phone_number", this.getPhone_number());

        params.put("password", this.getPassword());

        Log.e("Phone",this.getPhone_number());
        /* setting the request header */
        PicoWebRestClient.setUp("Content-Type","application/x-www-form-urlencoded");
        /* Sending a http post request to sign in uri of the api*/
        PicoWebRestClient.post("citizens/signin", params, new JsonHttpResponseHandler() {
            /* if authentication failed */
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonresp) {

                ConfigClass.token = "";
                ConfigClass.isLoggedIn = false;
                try {
                    // msg = jsonresp.getString("msg");
                    mProgressView.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mSignInProgressIcon.setVisibility(View.GONE);
                    if(jsonresp != null)
                        Toast.makeText(view.getContext(), jsonresp.getString("msg"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(view.getContext(), "Unable to connect check your connection !", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /* if authentication succeeded*/
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                try {
                    /* String the token returned by the api*/
                    ConfigClass.token = responseString.getString("token");
                    /*change the state of the citizen to logged in*/
                    ConfigClass.isLoggedIn = true;

                    Log.e("Response in success" ,responseString.getString("token")+"");

                    /* Opening hospitals list activity*/
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MapsActivity.class);
                    context.startActivity(intent);
                    mProgressView.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mSignInProgressIcon.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * SignUp function
     */
    public void SignUp(View view){
        View mLoginFormView = view.getRootView().findViewById(R.id.login_form);
        View mProgressView = view.getRootView().findViewById(R.id.login_progress);
        View mSignUpProgressIcon = view.getRootView().findViewById(R.id.ProgressIcon);
        mProgressView.setVisibility(View.VISIBLE);
        mLoginFormView.setVisibility(View.GONE);
        mSignUpProgressIcon.setVisibility(View.VISIBLE);
        Citizen cit = this;
        // adding typed data into http request parameters
        RequestParams params = new RequestParams();


        params.put("full_name", this.getFull_name());

        params.put("latitude", this.getCoordinates().getLatitude());

        params.put("longitude", this.getCoordinates().getLongitude());

        params.put("phone_number", this.getPhone_number());

        params.put("password", this.getPassword());
        /* adding content type header to the http packet*/
        PicoWebRestClient.setUp("Content-Type","application/x-www-form-urlencoded");
        /* calling post function to interrogate picoweb API */
        PicoWebRestClient.post("citizens/signup", params, new JsonHttpResponseHandler() {
            /*if the call return failure response*/
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                try {

                    //  ConfigClass.token = responseString.getString("");

                    //  ConfigClass.isLoggedIn = true;
                    mProgressView.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mSignUpProgressIcon.setVisibility(View.GONE);
                    if(errorResponse != null)
                        Toast.makeText(view.getContext(),errorResponse.getString("error"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(view.getContext(), "Unable to connect check your connection !", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            /*if the call return success response*/
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                try {

                    //  ConfigClass.token = responseString.getString("");

                    //  ConfigClass.isLoggedIn = true;

                    Log.e("Response in success" ,responseString.getString("success"));
                    // StartActivity(SignupActivity.this,MainActivity.class);
                    cit.SignIn(view);
                    mProgressView.setVisibility(View.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mSignUpProgressIcon.setVisibility(View.GONE);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        });

    }
}
