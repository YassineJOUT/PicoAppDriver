package ma.fstm.ilisi.pico.picoappdriver.viewmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ma.fstm.ilisi.pico.picoappdriver.BR;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.ConfigClass;
import ma.fstm.ilisi.pico.picoappdriver.model.Citizen;
import ma.fstm.ilisi.pico.picoappdriver.R;
import ma.fstm.ilisi.pico.picoappdriver.model.Driver;
import ma.fstm.ilisi.pico.picoappdriver.repository.PicoWebRestClient;
import ma.fstm.ilisi.pico.picoappdriver.view.LoginActivity;
import ma.fstm.ilisi.pico.picoappdriver.view.MapsActivity;

/**
 * LoginViewModel class
 * This class is responsible for data binding and data observable with the Login view
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class LoginViewModel extends BaseObservable {

    private Driver driver;
    private String errorMessage = "Phone Number or Password not valid";


    @Bindable
    public String toastMessage = null;
    /**
     * Method getToastMessage
     * getter to the message that will be displayed in the toast in the login view
     * @return Message to be displayed as a string
     */
    public String getToastMessage() {
        return toastMessage;
    }

    private void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }
    /**
     * LoginViewModel Constructor
     * initialize an empty citizen object
     */

    public LoginViewModel() {
        driver = new Driver();
    }
    /**
     * Method afterPhoneTextChanged
     * After the phone entry is changed in the login activity this method attributes the values typed
     * into the phone field of a citizen
     * @param phoneN char sequence representing
     */
    public void afterPhoneTextChanged(CharSequence phoneN) {
        driver.setPhone_number(phoneN.toString());
    }
    /**
     * Method afterPasswordTextChanged
     * After the password entry is changed in the login activity this method attributes the values typed
     * into the password field of a citizen
     * @param pass char sequence representing
     */
    public void afterPasswordTextChanged(CharSequence pass) {
        driver.setPassword(pass.toString());
    }
    /**
     * Method onLoginClicked
     * This method handles the click on the login button
     * if the phone and the password typed by the user are valid then this method calls the signIn method
     * located in the citizen class
     *
     */
    public void onLoginClicked(View view) {



        // if typed data is valid
        if (driver.isDataInputValidForLogin()){
            // call Sign In function

            View mLoginFormView = view.getRootView().findViewById(R.id.login_form);
            View mProgressView = view.getRootView().findViewById(R.id.login_progress);
            View mSignInProgressIcon = view.getRootView().findViewById(R.id.ProgressIcon);
            mProgressView.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);
            mSignInProgressIcon.setVisibility(View.VISIBLE);

            String MsgErr = "Success";
            /* adding parameters to the http request */
            RequestParams params = new RequestParams();

            params.put("phone_number", driver.getPhone_number());

            params.put("password", driver.getPassword());

            Log.e("Phone",driver.getPhone_number());
            /* setting the request header */
            PicoWebRestClient.setUp("Content-Type","application/x-www-form-urlencoded");
            /* Sending a http post request to sign in uri of the api*/
            PicoWebRestClient.post("drivers/signin", params, new JsonHttpResponseHandler() {
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
        else{
            Log.e(" MSG2 ","Error");
            // if data is not valid then show error message in the toast
            // data.setValue("Invalid data");
            setToastMessage(errorMessage);
        }

    }
   /* @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mView.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/


}