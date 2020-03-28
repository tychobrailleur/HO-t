package module.series.promotion;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.db.DBManager;
import core.gui.event.ChangeEventHandler;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import core.util.HOLogger;

import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main class for the League Promotion/Demotion prediction tool.
 *
 * TODO Describe process here.
 * Leagues are processed in blocks.
 */
public class LeaguePromotionHandler extends ChangeEventHandler {

    static class DownloadDetails {
        int blockNumber;
        int blockNumberReady;
        int blockNumberInProgress;
    }


    private LeagueStatus leagueStatus;
    private DownloadDetails downloadDetails;
    private boolean continueProcessing;


    /**
     * Promotion Manager is active only in weeks 14 and 15, and for the supported leagues.
     *
     * @return boolean – true if promotion manager can be used, false otherwise.
     */
    public boolean isActive() {
        List<Integer> supportedLeagues = HttpDataSubmitter.instance().fetchSupportedLeagues();
        int leagueId = DBManager.instance().getBasics(HOVerwaltung.instance().getId()).getLiga();
        //  int week = HOVerwaltung.instance().getModel().getBasics().getSpieltag();
        // TODO Uncomment above when testing complete.
        int week = 14;
        return Arrays.asList(14, 15).contains(week) && supportedLeagues.contains(leagueId);
    }

    public void initLeagueStatus() {
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                fetchLeagueStatus();
                return null;
            }
        }.execute();
    }

    public LeagueStatus getLeagueStatus() {
        if (leagueStatus == null) {
            leagueStatus = fetchLeagueStatus();
        }

        return leagueStatus;
    }

    private LeagueStatus fetchLeagueStatus() {
        final Basics basics = DBManager.instance().getBasics(HOVerwaltung.instance().getId());
        int leagueId = basics.getLiga();

        HttpDataSubmitter submitter = HttpDataSubmitter.instance();
        submitter.getLeagueStatus(leagueId, s -> {
            HOLogger.instance().info(LeaguePromotionHandler.class, "Status of league: " + leagueId + " : " + s);
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(s, JsonObject.class);
            leagueStatus = LeagueStatus.valueOf(obj.get("status_desc").getAsString());

            if (leagueStatus == LeagueStatus.NOT_AVAILABLE) {
                downloadDetails = new DownloadDetails();
                downloadDetails.blockNumber = obj.get("nbBlocks").getAsInt();
                downloadDetails.blockNumberReady = obj.get("nbBlocksReady").getAsInt();
                downloadDetails.blockNumberInProgress = obj.get("nbBlocksInProgress").getAsInt();
            }

            return null;
        });

        return leagueStatus;
    }

    public void downloadLeagueData(int leagueId) {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                continueProcessing = (leagueStatus == LeagueStatus.NOT_AVAILABLE);
                do {
                    final BlockInfo blockInfo = lockBlock(leagueId);

                    if (blockInfo != null) {
                        DownloadCountryDetails downloadCountryDetails = new DownloadCountryDetails();
                        downloadCountryDetails.processSeries(blockInfo);
                    } // TODO Figure out how to handle different cases of locking.

                    LeagueStatus status = fetchLeagueStatus();
                    continueProcessing = (status == LeagueStatus.NOT_AVAILABLE);
                } while (continueProcessing);

                fireChangeEvent(new ChangeEvent(LeaguePromotionHandler.this));

                return null;
            }
        };

        worker.execute();
    }

    public BlockInfo lockBlock(int leagueId) {
        DataSubmitter submitter = HttpDataSubmitter.instance();
        return submitter.lockBlock(leagueId);
    }

    public LeaguePromotionInfo getPromotionStatus(int leagueId, int teamId) {
        DataSubmitter submitter = HttpDataSubmitter.instance();

        String promotionInfo = submitter.getPromotionStatus(leagueId, teamId);

        final Gson gson = new Gson();
        final JsonObject obj = gson.fromJson(promotionInfo, JsonObject.class);

        LeaguePromotionInfo leaguePromotionInfo = new LeaguePromotionInfo();
        leaguePromotionInfo.status = LeaguePromotionStatus.codeToStatus(obj.get("status_desc").getAsString());

        List<Integer> teams = new ArrayList<>();
        for (JsonElement o: obj.get("oppTeamIDs").getAsJsonArray()) {
            teams.add(o.getAsInt());
        }
        leaguePromotionInfo.teams = teams;

        return leaguePromotionInfo;
    }
}
