package ma.fstm.ilisi.pico.picoappdriver.view;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ma.fstm.ilisi.pico.picoappdriver.Directions.ViewModel.DirectionsViewModel;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.DownloadImageTask;
import ma.fstm.ilisi.pico.picoappdriver.model.Ambulance;
import ma.fstm.ilisi.pico.picoappdriver.model.Driver;
import ma.fstm.ilisi.pico.picoappdriver.model.Hospital;
import ma.fstm.ilisi.pico.picoappdriver.R;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.ConfigClass;
import ma.fstm.ilisi.pico.picoappdriver.Utilities.Sockets;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private String locationProvider;
    private Location lastLocation ;

    private Marker ambulanceMarker;
    private Marker targetMarker;
    private Driver driver;

    private JSONObject citizendata;
    DirectionsViewModel directionsViewModel;
    //HospitalsViewModel hospitalsViewModel;
    //AmbulanceViewModel ambulanceViewModel;

    private Polyline[] polylineArray;
    private int currentPolylineLenght;

    private HashMap<Marker,Hospital> hospitalMarkerHash;

    private Socket socket;

    private boolean isAmbBooked ;
    private String last_alarm_id;
    MapsActivity me = MapsActivity.this;



    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hospitalMarkerHash = new HashMap<>();

        isAmbBooked = false ;

        // get bs behavior
        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        // once the app is launched close the bottom sheet
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // get positive button from bottom sheet
       Button  bs_btn_positive = findViewById(R.id.bs_Book);
        // get positive button from bottom sheet
        Button  bs_btn_negative = findViewById(R.id.bs_Cancel);

        // on button positive click
        bs_btn_positive.setOnClickListener(v -> {
           // see if the button text equals to Approve
           if(((Button)v).getText().equals("Approve")){
               // object to send alarm id in
               JSONObject obj = new JSONObject();
               try {
                   if(last_alarm_id != null){
                       obj.put("alarm_id",last_alarm_id);
                       // send data
                       socket.emit("ACCEPTED_REQUEST_EVENT",  obj);

                       if(citizendata != null){
                           // change bottom sheet content
                           setBottomSheetContent("onMission",citizendata);
                           // set state of bottom sheet
                           sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                       }
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });
        // on button negative click
        bs_btn_negative.setOnClickListener(v->{
            // see if the button text equals to Reject
            if(((Button)v).getText().equals("Reject")){
                // show confirmation dialog
                MapsActivity.this.runOnUiThread(() -> new AlertDialog.Builder(MapsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reject request")
                        .setMessage("Do you really want to reject this request ?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // object to send alarm id in
                            JSONObject obj = new JSONObject();
                            try {
                                if(last_alarm_id != null){
                                    obj.put("alarm_id",last_alarm_id);
                                    // send data
                                    socket.emit("REJECTED_REQUEST_EVENT",  obj);
                                    // set state of bottom sheet to hidden
                                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }).setNegativeButton("No",((dialog, which) -> {

                        }))
                        .show());

            }// see if button text equals to fake alarm
            else if(((Button)v).getText().equals("Fake Alarm")){
                // show confirmation dialog
                MapsActivity.this.runOnUiThread(() -> new AlertDialog.Builder(MapsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Fake alarm")
                        .setMessage("Do you really want to declare this request as a fake alarm ?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // object to send alarm id in
                            JSONObject obj = new JSONObject();
                            try {
                                if(last_alarm_id != null){
                                    obj.put("alarm_id",last_alarm_id);
                                    // send data
                                    socket.emit("FAKE_ALARM_EVENT",  obj);
                                        // set state of bottom sheet to hidden
                                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }).setNegativeButton("No",((dialog, which) -> {

                        }))
                        .show());
            }
        });

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {


                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                         //  btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });



    }
    private void setBottomSheetContent(String type, JSONObject obj){
        runOnUiThread(()->{
            try {
                // update Citizen Position
                ((TextView)findViewById(R.id.bs_distance)).setText(
                        "Loc( "+obj.getString("latitude")+","
                                +obj.getString("longitude")+")");
                // update Citizen name
                ((TextView)findViewById(R.id.bs_amb_RN)).setText("Citizen name : "+obj.getString("full_name"));
                // update the image
                new DownloadImageTask(findViewById(R.id.bs_imageView))
                        .execute(ConfigClass.buildUrl("citizens",obj.getString("citizen_id")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (type){
                case "newAlarm" :
                {
                    // set Title
                    ((TextView)findViewById(R.id.bs_Title)).setText("New request");
                    // set Subtitle
                    ((TextView)findViewById(R.id.bs_Subtitle)).setText("Citizen");
                    // set  content message
                    ((TextView)findViewById(R.id.bs_hospitalName)).setText(" citizen is calling for your service..");
                    // set buttons texts
                    ((Button)findViewById(R.id.bs_Book)).setText("Approve");
                    ((Button)findViewById(R.id.bs_Cancel)).setText("Reject");
                }
                break;
                case "onMission"  :
                {
                    // set Title
                    ((TextView)findViewById(R.id.bs_Title)).setText("On mission");
                    // set Subtitle
                    ((TextView)findViewById(R.id.bs_Subtitle)).setText("Citizen");
                    (findViewById(R.id.bs_distance)).setVisibility(View.INVISIBLE);
                    // set content message
                    ((TextView)findViewById(R.id.bs_hospitalName)).setText("Status :  Mission en cours ..");
                    // set buttons text
                    ((Button)findViewById(R.id.bs_Book)).setText("Mission accomplished");
                    ((Button)findViewById(R.id.bs_Cancel)).setText("Fake Alarm");
                } break;
            }

        });


    }

    public  void removeHospitals(){
        if (!hospitalMarkerHash.isEmpty()) {
            for (Map.Entry<Marker, Hospital> entry : hospitalMarkerHash.entrySet()) {
                Marker key = entry.getKey();
                key.remove();
            }
        }
    }

    private void DrawPolyLine(Location loc){
        if(lastLocation == null ) return ;

        directionsViewModel = ViewModelProviders.of(this).get(DirectionsViewModel.class);

        directionsViewModel.getDirectionsLiveData(lastLocation.getLatitude()+","+lastLocation.getLongitude(),
                loc.getLatitude()+","+loc.getLongitude(),
                getString(R.string.google_maps_key)).observe(this,direction ->
        {
            directionsViewModel.getPoylineLiveData(direction).observe(this,
                    polylines -> {
                        Log.e("GetPolyLiveData ","Live data call back");
                        if (polylineArray != null) {
                            for (Polyline polyline : polylineArray) {
                                polyline.remove();
                            }
                        }
                        if (polylines != null) {

                            polylineArray = new Polyline[polylines.length];
                            currentPolylineLenght = polylines.length;
                                    Log.d("reached here", "reached here");
                            for (int i=0;i<polylines.length;i++) {
                                PolylineOptions options = new PolylineOptions();

                                options.color(Color.MAGENTA);
                                options.width(10);
                                options.addAll(PolyUtil.decode(polylines[i]));
                                polylineArray[i] = mMap.addPolyline(options);

                            }
                        }
                    });
        });
    }
    private void DrawDriverPosition(JSONObject obj){
        runOnUiThread(() -> {
            try {
                removeHospitals();

                if(ambulanceMarker != null) {
                    ambulanceMarker.remove();
                }
                double latitude = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                ambulanceMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer50x50)));
                Log.e("Draw polyline Latitude",latitude+"");
                Log.e("Draw polyline longitude",longitude+"");
                Location loc = new Location(locationProvider);
                loc.setLatitude(latitude);
                loc.setLongitude(longitude);
                DrawPolyLine(loc);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    public void socketAuthentication() {

            socket = Sockets.getInstance();
            // handle new alarm event
            socket.on("NEW_ALARM_EVENT", args -> {
                // display msg in log
                Log.e("NEW_ALARM_EVENT","");
                // get  received JSON object
                JSONObject jsonObject = (JSONObject)args[0];
                // json object to send first gps coordinates
                JSONObject obj = new JSONObject();
                if(args != null)
                         try {
                             // Store alarm id
                             last_alarm_id = jsonObject.getString("alarm_id");// get JSON object
                            obj.put("latitude",lastLocation.getLatitude());
                            obj.put("longitude",lastLocation.getLongitude());
                             // sent first coordinates
                            socket.emit("POSITION_CHANGE_EVENT", obj);
                         } catch (JSONException e) {
                            e.printStackTrace();
                    }
                    // store received info
                    citizendata = jsonObject;
                     // change Bottom sheet content
                    setBottomSheetContent("newAlarm",jsonObject);
                    // show bottom sheet
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


            }).on("CITIZEN_POSITION_CHANGE_EVENT", args -> {

                JSONObject obj = (JSONObject)args[0];
                DrawDriverPosition(obj);

            }).on("CITIZEN_FEEDBACK_EVENT", args -> {


            }).on("CANCEL_ALARM_EVENT", args -> {


            }).on("BAD_REQUEST_EVENT", args -> {


            }).on("SUCCESSFUL_FAKE_ALARM_DECLARATION_EVENT", args -> {


            }).on("ALARM_NOT_FOUND_EVENT", args -> {


            }).on("UNAUTHORIZED_MISSION_COMPLETION_EVENT", args -> {


            }).on("DRIVER_AUTH_SUCCESS_EVENT", args -> {

                Log.e("Socket status : ","Socket authenticated");
                //sendAlarm();

            }).on(Socket.EVENT_CONNECT, args -> {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("token",ConfigClass.token);
                    socket.emit("DRIVER_AUNTENTICATION_EVENT", obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        socket.connect();
        Log.e("socket","Connected");

    }

    @Override
    protected void onNewIntent(Intent intent) {
    /*    super.onNewIntent(intent);
        String resumedFromFb = intent.getStringExtra("feedBack");
        Log.e("resumedFromFb 1",resumedFromFb+"");
        if(mMap != null){

            if(resumedFromFb != null)
                if(resumedFromFb.equalsIgnoreCase("true")){

                    ambulanceMarker.remove();
                    isAmbBooked = false ;
                    if(ambulanceMarker != null && currentPolylineLenght != 0){

                        ambulanceMarker.remove();
                        for (int i = 0 ; i< currentPolylineLenght ;i++)
                            polylineArray[i].remove();
                    }
                    onMapReady(mMap);

                }

        }
        driver =  intent.getParcelableExtra("driver_info");
        boolean isbooked = intent.getBooleanExtra("isbooked",false);
        isAmbBooked = isbooked;
        Log.e("isbooked ",isbooked +"");
        if(driver != null){
           // setBottomSheetContent("driver");
            Log.e("new intent ","driver");

        }


*/

    }



    /**
     *
     * getHospital with the nearest distance
     */

    public LinkedHashMap<Hospital, Float> sortHashMapByValues(
            HashMap<Hospital, Float>  passedMap) {
        List<Hospital> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Float> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);

        LinkedHashMap<Hospital, Float> sortedMap =
                new LinkedHashMap<>();

        Iterator<Float> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Float val = valueIt.next();
            Iterator<Hospital> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Hospital key = keyIt.next();
                Float comp1 = passedMap.get(key);
                Float comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
   public LinkedHashMap<Hospital,Float> getNearestHospital(){

       HashMap<Hospital,Float> hospitalDistanceMap = new HashMap<>();
        if (!hospitalMarkerHash.isEmpty()) {
           double[] distances = new double[hospitalMarkerHash.size()];
           int i = 0;
           for (Map.Entry<Marker, Hospital> entry : hospitalMarkerHash.entrySet()) {
               Hospital h = entry.getValue();
               Location l = new Location("jps") ;

               l.setLatitude(h.getLatitude());
               l.setLongitude(h.getLongitude());

               lastLocation = mMap.getMyLocation();
               if(lastLocation != null){
                   hospitalDistanceMap.put(h,lastLocation.distanceTo(l));
               }
           }
           if(!hospitalDistanceMap.isEmpty()){
              return sortHashMapByValues(hospitalDistanceMap);
           }

           else return null;
       }

       return  null;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
      //  setBottomSheetContent("hospital");

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(33.697815, -7.385291);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Hopital molay abdelah"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,ConfigClass.zoomStreets));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    }, 0);
            return;
        }

       // String resumedFromFb = getIntent().getStringExtra("feedBack");
        //Log.e("resumedFromFb 1 onmapr",resumedFromFb+"");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(locationProvider, 1, 10, this);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        lastLocation = locationManager.getLastKnownLocation(locationProvider);
        // mMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())).title("Myloc"));
        mMap.setOnMapClickListener(this);

       // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(, 15));
        // get hospitals view model
      //  hospitalsViewModel = ViewModelProviders.of(this).get(HospitalsViewModel.class);
       // isAmbBooked =  getIntent().getBooleanExtra("isAmbBooked",false);
       // if(!isAmbBooked){
            // adding hospitals markers on the map
         /*   hospitalsViewModel.onRefreshClicked().observe(this,hospitals -> {
                if(hospitals != null){
                    for (Hospital h :hospitals) {
                        hospitalMarkerHash.put(
                                mMap.addMarker(
                                        new MarkerOptions()
                                                .position(new LatLng(h.getLatitude(),h.getLongitude()))
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                                .title(h.getName())),h);
                    }
                }
            });*/
            if(lastLocation == null){
                lastLocation = new Location("");
                lastLocation.setLatitude(33.699995 );
                lastLocation.setLongitude(-7.362469);}

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), ConfigClass.zoomStreets-0.5f));


            //socketAuthentication();
       // }
        socketAuthentication();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("Location ","changed");
        Log.d("location", "My location  " + location.getLatitude());
        lastLocation = location;

        JSONObject obj = new JSONObject();
        try {
            obj.put("latitude",location.getLatitude());
            obj.put("longitude",location.getLongitude());
            socket.emit("POSITION_CHANGE_EVENT", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("status ","changed");
    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.e("Provider ","enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("Provider ","disabled");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
     /*   if (targetMarker != null) targetMarker.remove();

        Location loc = new Location("");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        if (lastLocation == null) {

            lastLocation = mMap.getMyLocation();
        }
        double dist = lastLocation.distanceTo(loc);
        Log.e("Distance ",dist+"");
        */

    }
    @Override
    public boolean onMarkerClick(Marker marker) {


       // ambulanceViewModel = ViewModelProviders.of(this).get(AmbulanceViewModel.class);
       /* Intent intent = new Intent(this, AmbulanceListActivity.class);
        hospitalMarkerHash.get(marker);
        ambulanceViewModel.onRefreshClicked(hospitalMarkerHash.get(marker)).observe(this,ambulances -> {
            if(ambulances != null){
                intent.putExtra("myPosition",lastLocation);
                intent.putParcelableArrayListExtra("ambulances", (ArrayList<Ambulance>) ambulances);
                startActivity(intent);

            }
        });*/
/*
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    //        btnBottomSheet.setText("Expand sheet");
        }*/
        return false;
    }

    //menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.profile:
                // do your code
                return true;
            case R.id.settings:
                // do your code
                return true;
            case R.id.logout:

            {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Pico")
                        .setMessage("Are you sure you want to logout and exit the app ?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            ConfigClass.isLoggedIn = false ;
                            ConfigClass.token = "";
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
    /*private void UpdateLocation(double currentLat, double currentLon) {
        LatLng pos = new LatLng(currentLat, currentLon);

        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            List<Address> lstAddr = geocoder.getFromLocation(currentLat, currentLon, 1);
            String str = lstAddr.get(0).getLocality();
            str += lstAddr.get(0).getLocality();
            mMap.addMarker(new MarkerOptions().position(pos).title(str));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, ConfigClass.zoomStreets));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/