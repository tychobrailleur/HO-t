package module.series.promotion;

import core.util.HOLogger;
import okhttp3.*;

public class HttpDataSubmitter implements DataSubmitter {
    // FIXME fix SSL cert issue.
    private final static String HOSERVER_URL = "http://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD/";

    @Override
    public void submitData(String json) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(HOSERVER_URL + "/push-data")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            HOLogger.instance().info(DownloadCountryDetails.class, String.format("Upload complete: %s", res));
        } catch (Exception e) {
            HOLogger.instance().log(DownloadCountryDetails.class, e);
        }
    }
}
