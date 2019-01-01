package ma.fstm.ilisi.pico.picoappdriver.Utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleAPIClient {

    public static Retrofit client;

    public static Retrofit getInstance(String baseurl){

        if(client == null){
            client = new Retrofit.Builder()
                    .baseUrl(baseurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return client;

    }

}
