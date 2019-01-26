package ma.fstm.ilisi.pico.picoappdriver.Directions.Repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import ma.fstm.ilisi.pico.picoappdriver.Directions.Model.Direction;
import ma.fstm.ilisi.pico.picoappdriver.Directions.Model.Step;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.GoogleAPIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DirectionsRepository {

    private static DirectionsRepository directionsRepository;
    private DirectionServiceApi directionServiceApi;
    private String baseUrl = "https://maps.googleapis.com/maps/api/";

    public DirectionsRepository() {
        Retrofit client = GoogleAPIClient.getInstance(baseUrl);

        directionServiceApi = client.create(DirectionServiceApi.class);
    }

    public synchronized static DirectionsRepository getInstance(){

        if(directionsRepository == null ){
            directionsRepository = new DirectionsRepository();

        }

        return directionsRepository;
    }

    public LiveData<Direction> getDirectionTo(String origin, String destination, String key){
        final MutableLiveData<Direction> data = new MutableLiveData<>();
        directionServiceApi.getDirectionTo(origin,destination, key).enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                if(response.isSuccessful()){
                    Log.e("OnResponse ","OK");
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                data.setValue(null );
                Log.e("OnResponse ","NOK");
            }
        });
        return data;
    }
    public LiveData<String[]> getPolyline(Direction direction){
        MutableLiveData<String[]> polyline = new MutableLiveData<>();
        String[] polylineArray = null;
        if(direction != null && direction.getStatus().equals("OK")){
            Log.e("Key DirectionStatus : ",direction.getStatus());
            int count = direction.getRoutes().get(0).getLegs().get(0).getSteps().size();
            Step[] steps = direction.getRoutes().get(0).
                    getLegs().get(0).getSteps().toArray(new Step[count]);
            polylineArray = new String[count];
            for(int i=0;i<count;i++){
                polylineArray[i] = steps[i].getPolyline().getPoints();
            }
        }
        polyline.setValue(polylineArray);
        return polyline;
    }
}
