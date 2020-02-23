package module.series.promotion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import core.util.HOLogger;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;


public class HttpDataSubmitter implements DataSubmitter {

    // TODO Make configurable.
    private final static String HOSERVER_BASEURL = "https://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD";

    // Singleton.
    private HttpDataSubmitter() {}

    private static HttpDataSubmitter instance = null;

    public static HttpDataSubmitter instance() {
        if (instance == null) {
            instance = new HttpDataSubmitter();
        }

        return instance;
    }

    public List<Integer> fetchSupportedLeagues() {
        try {


            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(HOSERVER_BASEURL + "/league/supported")
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            List<Integer> supportedLeagues = new ArrayList<>();
            if (response.isSuccessful()) {
                String bodyAsString = response.body().string();
                Gson gson = new Gson();
                JsonArray array = gson.fromJson(bodyAsString, JsonArray.class);

                for (JsonElement arr : array) {
                    supportedLeagues.add(arr.getAsJsonArray().get(0).getAsInt());
                }

                return supportedLeagues;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }


    public void getLeagueStatus(int leagueId, Function<String, Void> callback) {

        try {
            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/status", leagueId))
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                callback.apply(response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void submitData(String json) {
        HOLogger.instance().info(HttpDataSubmitter.class, "Sending data to HO Server...");

        try {
            final OkHttpClient client = initializeHttpsClient();

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(HOSERVER_BASEURL + "/push-data")
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                System.out.println("Got HTTP response with status " + response.code() + " " + response.message());
            }

        } catch (Exception e) {
            HOLogger.instance().error(HttpDataSubmitter.class, e.getMessage());
        }
    }

    private OkHttpClient initializeHttpsClient() throws Exception {
        final InputStream trustStoreStream = this.getClass().getClassLoader().getResourceAsStream("keystore.jks");

        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(trustStoreStream, "password".toCharArray());

        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "password".toCharArray());
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        final X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
    }
}
