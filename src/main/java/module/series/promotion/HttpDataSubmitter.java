package module.series.promotion;

import core.util.HOLogger;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;



public class HttpDataSubmitter implements DataSubmitter {

    private final static String HOSERVER_URL = "https://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD/";

    @Override
    public void submitData(String json) {
        HOLogger.instance().info(HttpDataSubmitter.class, "Sending data to HO Server...");

        // FIXME Use a lighter dependency like OkHttp for this.
        // Other parts of the app use pure Java.
        final Vertx vertx = Vertx.vertx();
        final String trustStorePath = this.getClass().getClassLoader().getResource("keystore.jks").getPath();

        WebClient client = WebClient.create(vertx,
                new WebClientOptions()
                        .setSsl(true)
                        .setLogActivity(true)
                        .setTrustStoreOptions(new JksOptions()
                                .setPath(trustStorePath)
                                .setPassword("password")));

        final Buffer buffer = Buffer.buffer();
        buffer.appendString(json);

        client.post(443, "UNF6X7OJB7PFLVEQ.anvil.app", "/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD/push-data")
                .putHeader("Content-Type", "application/json")
                .sendBuffer(buffer, ar -> {
            System.out.println(ar);
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                System.out.println("Got HTTP response with status " + response.statusCode() + " " + response.bodyAsString());
            } else {
                ar.cause().printStackTrace();
            }
        });
    }
}
