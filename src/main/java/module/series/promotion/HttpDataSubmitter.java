package module.series.promotion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    public void submitData(BlockInfo blockInfo, String json) {
        HOLogger.instance().info(HttpDataSubmitter.class, "Sending data to HO Server...");

        try {
            final OkHttpClient client = initializeHttpsClient();

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/block/%s/push/", blockInfo.leagueId, blockInfo.blockId))
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                System.out.println("Got HTTP response with status " + response.code() + " " + response.message());
            } else {
                HOLogger.instance().error(HttpDataSubmitter.class, "Error submitting data to HO Server: " + response.body().string());
            }

        } catch (Exception e) {
            HOLogger.instance().error(HttpDataSubmitter.class, e.getMessage());
        }
    }

    public BlockInfo lockBlock(int leagueId) {
        HOLogger.instance().info(HttpDataSubmitter.class, String.format("Lock block for league %s...", leagueId));

        try {
            final OkHttpClient client = initializeHttpsClient();

            Request request = new Request.Builder()
                    .url(String.format(HOSERVER_BASEURL + "/league/%s/next-block?accept-job=true", leagueId))
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            List<Integer> series = new ArrayList<>();
            if (response.isSuccessful()) {
                String body = response.body().string();

                Gson gson = new Gson();
                JsonObject obj = gson.fromJson(body, JsonObject.class);

                String blockContent = obj.get("BlockContent").getAsString();

                // FIXME Shouldn't need to parse content again.  Talk to @akasolace.
                JsonArray array = gson.fromJson(blockContent, JsonArray.class);

                for (JsonElement elt: array) {
                    series.add(elt.getAsInt());
                }

                BlockInfo blockInfo = new BlockInfo();
                blockInfo.blockId = obj.get("BlockID").getAsInt();
                blockInfo.series = series;
                blockInfo.leagueId = leagueId;

                return blockInfo;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
