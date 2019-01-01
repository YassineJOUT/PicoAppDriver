package ma.fstm.ilisi.pico.picoappdriver.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ma.fstm.ilisi.pico.picoappdriver.model.Hospital;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.ConfigClass;
import ma.fstm.ilisi.pico.picoappdriver.repository.PicoWebRestClient;
/**
 * HospitalsViewModel class
 * This class is responsible for data binding and data observable with the hospitals view
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class HospitalViewModel extends ViewModel{


    public LiveData<List<Hospital>> getHospitalList() {
        return hospitalList;
    }

    private LiveData<List<Hospital>> hospitalList;
    private String successMessage = "Sign in was successful";
    private String errorMessage = "field invalid not valid";
    LiveData<List<Hospital>> hospitals ;




    public HospitalViewModel() {


    }

    public LiveData<List<Hospital>> onRefreshClicked()  {
        final MutableLiveData<List<Hospital>> data = new MutableLiveData<>();
        Log.e("Response in Error" ,ConfigClass.isLoggedIn+"");
        if(ConfigClass.isLoggedIn){

            PicoWebRestClient.setUp("Authorization",ConfigClass.token);

            PicoWebRestClient.get("hospitals/citizens/", null, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        //  ConfigClass.token = responseString.getString("");

                        //  ConfigClass.isLoggedIn = true;

                        errorResponse.getString(0);
                        //  StartActivity(SignupActivity.this,MainActivity.class);
                        data.setValue(null);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                    try {
                        //  ConfigClass.token = responseString.getString("");

                        //  ConfigClass.isLoggedIn = true;

                        Gson gson = new Gson();

                        String content = responseString.getString("hospitals").toString()+"";
                        Type listType = new TypeToken<List<Hospital>>() {}.getType();
                        // hospitals =
                        Log.e("Response in success" ,content);
                        data.setValue(gson.fromJson(content,listType));
                        //   Log.e("Response in success" , hospitals.get(1).toString());

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            });

        }
        return data;
    }
}
