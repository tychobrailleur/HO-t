package module.series.promotion;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.util.*;
import java.util.List;

public class PMApp extends JFrame {

    static class League {
        int id;
        String name;

        public String toString() {
            return name;
        }
    }

    private Properties properties = new Properties();
    private DataSubmitter dataSubmitter;

    public PMApp() {
        this.dataSubmitter = HttpDataSubmitter.instance();
        java.util.List<Integer> supportedLeagues = this.dataSubmitter.fetchSupportedLeagues();

        List<League> leaguesList = initialiseLeagueList();
        initialiseHOInfra();

        setTitle("Promotion Manager App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(700, 480));

        final JPanel controlsPanel = new JPanel();

        controlsPanel.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.25;

        final JLabel countryLabel = new JLabel("Country");
        final JLabel teamLabel = new JLabel("Team ID");

        controlsPanel.add(countryLabel, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0.75;
        controlsPanel.add(teamLabel, constraints);

        final JComboBox<League> countryCombo = new JComboBox<>(leaguesList.toArray(new League[leaguesList.size()]));
        final JTextField teamTextField = new JTextField();
        teamTextField.setText(String.valueOf(HOVerwaltung.instance().getModel().getBasics().getTeamId()));

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0.25;
        controlsPanel.add(countryCombo, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0.75;
        controlsPanel.add(teamTextField, constraints);

        final JButton processButton = new JButton("Load League");

        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.weightx = 0.0;
        constraints.ipadx = 20;
        controlsPanel.add(processButton, constraints);

        final JButton clearButton = new JButton("Reset League");
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        constraints.ipadx = 20;
        controlsPanel.add(clearButton, constraints);

        final JButton teamStatusButton = new JButton("Team Status");
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        constraints.ipadx = 20;
        controlsPanel.add(teamStatusButton, constraints);

        JPanel topPanel = new JPanel();
        topPanel.add(controlsPanel);
        controlsPanel.setPreferredSize(new Dimension(400, 200));
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);

        final JTextArea textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        countryCombo.addActionListener(e -> {
            League selectedLeague = (League) countryCombo.getSelectedItem();
            if (selectedLeague != null) {
                dataSubmitter.getLeagueStatus(selectedLeague.id, s -> {
                    textArea.append(s);
                    return null;
                });
            }
        });
        processButton.addActionListener(e -> {
            League selectedLeague = (League) countryCombo.getSelectedItem();

            if (selectedLeague != null) {
                final LeaguePromotionHandler promotionHandler = new LeaguePromotionHandler();
                textArea.append(String.format(
                        "\nDownloading data about league %s (%s)...",
                        selectedLeague.id,
                        selectedLeague.name
                ));
                promotionHandler.addChangeListener(e1 -> {
                    textArea.append("\nDownload complete.");
                });
                promotionHandler.downloadLeagueData(selectedLeague.id);
            }
        });
        clearButton.addActionListener(e -> {
            League selectedLeague = (League) countryCombo.getSelectedItem();

            if (selectedLeague != null) {
                String output = resetLeague(selectedLeague.id);
                textArea.append("\n" + output);
            }
        });
        teamStatusButton.addActionListener(e -> {

            League selectedLeague = (League) countryCombo.getSelectedItem();
            if (selectedLeague != null) {
                final LeaguePromotionHandler promotionHandler = new LeaguePromotionHandler();
                final LeaguePromotionInfo promotionStatus = promotionHandler.getPromotionStatus(
                        selectedLeague.id,
                        Integer.parseInt(teamTextField.getText())
                );

                textArea.append("\nTeam status: " + promotionStatus.status);
            }
        });
    }

    private List<League> initialiseLeagueList() {
        final List<League> leagues = new ArrayList<>();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("leagues.properties"));
            properties.forEach((key, value) -> {
                League l = new League();
                l.id = Integer.parseInt(key.toString().replace("league.", ""));
                l.name = value.toString();
                leagues.add(l);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        leagues.sort(Comparator.comparing(o -> o.name));
        return leagues;
    }

    private String resetLeague(int leagueId) {

        try {
            final OkHttpClient client = initializeHttpsClient();
            String leagueReset = String.format(
                    "https://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD/league/%s/reset",
                    leagueId
            );

            Request request = new Request.Builder()
                    .url(leagueReset)
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            HOLogger.instance().error(
                    HttpDataSubmitter.class,
                    "Error locking block: " + e.getMessage()
            );
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

        int proxyPort = 3000;
        String proxyHost = "localhost";

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager);

        if (ModuleConfig.instance().getBoolean("PromotionStatus_DebugProxy", false)) {
            builder = builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        }

        return builder.build();
    }

    private void initialiseHOInfra() {
        DBManager.instance().loadUserParameter();

        HOVerwaltung.checkLanguageFile(UserParameter.instance().sprachDatei);
        HOVerwaltung.instance().setResource(UserParameter.instance().sprachDatei);
        HOVerwaltung.instance().loadLatestHoModel();

        UserParameter.instance().promotionManagerTest = true;
    }

    public static void main(String[] args) {
        final JFrame app = new PMApp();
        SwingUtilities.invokeLater(() -> app.setVisible(true));
    }
}
