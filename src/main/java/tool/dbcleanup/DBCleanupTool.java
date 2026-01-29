package tool.dbcleanup;

import core.db.DBManager;
import core.gui.RefreshManager;
import core.model.HOModelManager;
import core.model.match.MatchKurzInfo;
import core.file.hrf.HRF;
import core.util.HODateTime;
import core.util.HOLogger;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Arrays;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;

/**
 * HO Database Cleanup Tool
 * Removes old HRFs and old matches from the DB to speedup HO
 *
 * @author flattermann <HO@flattermann.net>
 */
public class DBCleanupTool {

    public static final int REMOVE_NONE = -1;
    public static final int REMOVE_ALL = 0;

    public void showDialog(JFrame owner) {
        new DBCleanupDialog(owner, this);
    }

    /**
     * Remove old HRFs
     *
     * @param keepWeeks  remove HRFs older than x weeks (-1=keep All, 0=remove All)
     * @param autoRemove if true, automatically remove all HRFs except the first per
     *                   training week
     */
    public void cleanupHRFs(int keepWeeks, boolean autoRemove) {
        Timestamp removeDate = null;
        if (keepWeeks >= 0) {
            HODateTime hrfDateFrom = HODateTime.now().minus(keepWeeks * 7, ChronoUnit.DAYS);
            removeDate = hrfDateFrom.toDbTimestamp();
        }
        if (removeDate != null || autoRemove) {
            cleanupHRFsInternal(removeDate, autoRemove);
        }
    }

    /**
     * Remove old HRFs
     *
     * @param removeDate remove HRFs older than x (null=keep all)
     * @param autoRemove if true, automatically remove all HRFs except the first per
     *                   training week
     */
    private void cleanupHRFsInternal(Timestamp removeDate, boolean autoRemove) {
        HOLogger.instance().debug(
                getClass(),
                "Removing old HRFs: removeDate=" + removeDate + ", autoRemove=" + autoRemove);
        List<HRF> allHrfs = Arrays.asList(DBManager.instance().loadAllHRFs(true));
        HRF latestHrf = DBManager.instance().getLatestHRF();
        int lastSeason = -1;
        int lastWeek = -1;
        int counter = 0;

        for (HRF curHrf : allHrfs) {
            int curId = curHrf.getHrfId();
            HODateTime curDate = curHrf.getDatum();
            HODateTime.HTWeek htWeek = curDate.toTrainingWeek();
            int curHtSeasonTraining = htWeek.season;
            int curHtWeekTraining = htWeek.week;
            boolean remove = false;

            if (removeDate != null && removeDate.after(curDate.toDbTimestamp())) {
                remove = true;
            } else if (autoRemove) {
                if (lastSeason == curHtSeasonTraining && lastWeek == curHtWeekTraining) {
                    remove = true;
                } else {
                    lastSeason = curHtSeasonTraining;
                    lastWeek = curHtWeekTraining;
                }
            }
            // Do not remove the latest HRF
            if (remove && curId != latestHrf.getHrfId()) {
                HOLogger.instance().debug(
                        getClass(),
                        "Removing Hrf: " + curId + " @ " + curDate + " (" + curHtSeasonTraining + "/"
                                + curHtWeekTraining + ")");
                DBManager.instance().deleteHRF(curId);
                counter++;
            }
        }
        HOLogger.instance().debug(getClass(), "Removed " + counter + "/" + allHrfs.size() + " HRFs from DB!");
        if (counter > 0) {
            reInitHO();
        }
    }

    private Timestamp calculateDateLimit(int numWeeks) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.WEEK_OF_YEAR, -numWeeks);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Remove old matches from DB (by date)
     *
     * @param cleanupDetails Parameters for cleanup
     */
    public void cleanupMatches(CleanupDetails cleanupDetails) {
        HOLogger.instance().debug(
                getClass(),
                "Removing old matches: ownTeamMatchTypes=" + cleanupDetails.ownTeamMatchTypes() + ", " +
                        "ownTeamWeeks=" + cleanupDetails.ownTeamWeeks() + ", " +
                        "otherTeamMatchTypes=" + cleanupDetails.otherTeamMatchTypes() + ", " +
                        "otherTeamWeeks=" + cleanupDetails.otherTeamWeeks());

        int counter = 0;
        // In Kotlin this was DBManager.instance().getMatchesKurzInfo(-1), check java
        // signature.
        // Assuming getMatchesKurzInfo returns ListOrArray.
        List<MatchKurzInfo> kurzInfos = DBManager.instance().getMatchesKurzInfo(-1);
        int myTeamId = HOModelManager.instance().getModel().getBasics().getTeamId();

        for (MatchKurzInfo curKurzInfo : kurzInfos) {
            int curMatchId = curKurzInfo.getMatchID();
            boolean removeMatch = false;

            if (checkDeleteMatch(myTeamId, curKurzInfo, cleanupDetails)) {
                HOLogger.instance().info(
                        getClass(),
                        "Match to be deleted: " + curKurzInfo.getMatchID() + ", " +
                                "matchType=" + curKurzInfo.getMatchType() + ", matchDate="
                                + curKurzInfo.getMatchSchedule());
                removeMatch = true;
            }

            if (removeMatch) {
                // Remove match
                HOLogger.instance().debug(
                        getClass(),
                        "Removing match " + curMatchId);
                DBManager.instance().deleteMatch(curKurzInfo);
                counter++;
            }
        }
        HOLogger.instance().debug(getClass(), "Removed " + counter + "/" + kurzInfos.size() + " matches from DB!");

        if (counter > 0) {
            reInitHO();
        }
    }

    private boolean checkDeleteMatch(
            int myTeamId,
            MatchKurzInfo curKurzInfo,
            CleanupDetails cleanupDetails) {
        Timestamp curMatchDate = curKurzInfo.getMatchSchedule().toDbTimestamp();
        int curMatchType = curKurzInfo.getMatchType().getId(); // Assuming getMatchType returns enum or MatchType
                                                               // object, and we need ID or object.
        // MatchType in CleanDetails is List<MatchType>.
        // curKurzInfo.getMatchType() likely returns MatchType enum.

        boolean isMyMatch = (curKurzInfo.getHomeTeamID() == myTeamId || curKurzInfo.getGuestTeamID() == myTeamId);

        boolean ownMatchToDelete = (isMyMatch && cleanupDetails.ownTeamMatchTypes().contains(curKurzInfo.getMatchType())
                && calculateDateLimit(cleanupDetails.ownTeamWeeks()).after(curMatchDate));
        boolean otherMatchToDelete = (!isMyMatch
                && cleanupDetails.otherTeamMatchTypes().contains(curKurzInfo.getMatchType())
                && calculateDateLimit(cleanupDetails.otherTeamWeeks()).after(curMatchDate));

        return ownMatchToDelete || otherMatchToDelete;

    }

    /**
     * Returns the number of matches stored in the database.
     *
     * @return Int – Number of matches stored in DB.
     */
    public int getMatchesCount() {
        List<MatchKurzInfo> kurzInfos = DBManager.instance().getMatchesKurzInfo(-1);
        return kurzInfos.size();
    }

    /**
     * Returns the number of HRF entries stored in the database.
     *
     * @return Int – Number of HRF records in DB.
     */
    public int getHrfCount() {
        List<HRF> allHrfs = Arrays.asList(DBManager.instance().loadAllHRFs(true));
        return allHrfs.size();
    }

    private void reInitHO() {
        RefreshManager.instance().doReInit();
    }
}
