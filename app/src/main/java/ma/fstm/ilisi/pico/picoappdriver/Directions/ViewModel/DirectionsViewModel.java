package ma.fstm.ilisi.pico.picoappdriver.Directions.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import ma.fstm.ilisi.pico.picoappdriver.Directions.Model.Direction;
import ma.fstm.ilisi.pico.picoappdriver.Directions.Repository.DirectionsRepository;

public class DirectionsViewModel extends ViewModel {

    public LiveData<Direction> getDirectionsLiveData(String origin,String destination,String key){

        Log.e("Key DirectionLD : ", key);
        Log.e("Key origin : ", origin);
        Log.e("Key dest : ", destination);
        return DirectionsRepository.getInstance().getDirectionTo(origin,destination,key);
    }

    public LiveData<String[]> getPoylineLiveData(Direction direction){
        Log.e("Key PolygoneLD : ", "OK");
        return DirectionsRepository.getInstance().getPolyline(direction);
    }
}
