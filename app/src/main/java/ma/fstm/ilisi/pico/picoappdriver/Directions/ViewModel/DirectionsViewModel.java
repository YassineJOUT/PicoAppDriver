package ma.fstm.ilisi.pico.picoappdriver.Directions.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import ma.fstm.ilisi.pico.picoappdriver.Directions.Model.Direction;
import ma.fstm.ilisi.pico.picoappdriver.Directions.Repository.DirectionsRepository;

public class DirectionsViewModel extends ViewModel {

    public LiveData<Direction> getDirectionsLiveData(String origin,String destination,String key){
        return DirectionsRepository.getInstance().getDirectionTo(origin,destination,key);
    }

    public LiveData<String[]> getPoylineLiveData(Direction direction){
        return DirectionsRepository.getInstance().getPolyline(direction);
    }
}
