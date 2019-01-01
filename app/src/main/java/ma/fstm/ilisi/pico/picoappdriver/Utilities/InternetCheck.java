package ma.fstm.ilisi.pico.picoappdriver.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {
        //extends AsyncTask<Void,Void,Boolean> {
        public static boolean isConnected(Context context) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();

            if (netinfo != null && netinfo.isConnectedOrConnecting()) {
                NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
            } else
            return false;
        }



    /*private Consumer mConsumer;
    public  interface Consumer { void accept(Boolean internet); }

    public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }

    @Override protected Boolean doInBackground(Void... voids) { try {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
        sock.close();
        return true;
    } catch (IOException e) { return false; } }

+/
/*
    public static boolean haveInternet(Context ctx){
        boolean have_WIFI = false;
        boolean have_DATA = false;

        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = conMgr.getAllNetworkInfo();

        for(NetworkInfo inf : netInfo){
            if(inf.getTypeName().equalsIgnoreCase("WIFI"))
                if(inf.isConnected())
                    have_WIFI = true;
            if(inf.getTypeName().equalsIgnoreCase("MOBILE"))
                if(inf.isConnected())
                    have_DATA = true;

        }
        return have_WIFI || have_DATA;
    }
    */
}