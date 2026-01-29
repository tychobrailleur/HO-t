package module.playerOverview;

import core.model.match.MatchKurzInfo; // Updated import
import core.model.enums.MatchType;
import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.Refreshable;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.PlayerOverviewTableModel;
import core.gui.model.UserColumnController;
import core.model.HOModelManager;
import core.model.TranslationFacility;
import core.model.player.Player;
import core.net.HattrickLink;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

/**
 * The Squad table, listing all the players on the team.
 * <p>
 * The actual model for that table is defined in
 * {@link PlayerOverviewTableModel}, which defines
 * all the columns to be displayed; the columns are initiated by a factory,
 * {@link UserColumnFactory},
 * which in particular sets their preferred width.
 * <p>
 * Sorting in the table is handled by {@link TableSorter} which decorates the
 * model, and is set
 * as the {@link javax.swing.table.TableModel} for this table. Triggering
 * sorting by a click sorts
 * the entries in the table model itself. The new sorting order is then used by
 * re-displaying the
 * table. This approach differs from the “normal” Swing approach of using
 * {@link javax.swing.JTable#setRowSorter(javax.swing.RowSorter)}.
 *
 * @author Thorsten Dietz
 */
public class PlayerOverviewTable extends FixedColumnsTable implements Refreshable {

    @Serial
    private static final long serialVersionUID = -6074136156090331418L;

    private final PlayerOverviewTableModel playerTableModel;

    public PlayerOverviewTable() {
        super(UserColumnController.instance().getPlayerOverviewModel());
        this.playerTableModel = (PlayerOverviewTableModel) getModel();

        playerTableModel.setValues(HOModelManager.instance().getModel().getCurrentPlayers());
        setOpaque(false);
        RefreshManager.instance().registerRefreshable(this);

        // Add a mouse listener that, when clicking on the “Last match” column
        // - opens the Hattrick page for the player if you shift-click,
        // - or opens the match in HO if you double-click.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Player player = getSelectedPlayer();
                if (player != null) {
                    // Last match column
                    int columnAtPoint = columnAtPoint(e.getPoint());
                    // Get name of the actual column at columnAtPoint, i.e. post-ordering of the
                    // columns
                    // based on preferences.
                    String columnName = playerTableModel.getColumnName(columnAtPoint);
                    String lastMatchRating = TranslationFacility.tr("LastMatchRating");
                    if (columnName != null && columnName.equalsIgnoreCase(lastMatchRating)) {
                        if (e.isShiftDown()) {
                            int matchId = player.getLastMatchId();
                            MatchType matchType = player.getLastMatchType();
                            MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, matchType);
                            HattrickLink.showMatch(String.valueOf(matchId), info.getMatchType().isOfficial());
                        } else if (e.getClickCount() == 2) {
                            HOMainFrame.instance().showMatch(player.getLastMatchId());
                        }
                    }
                }
            }
        });
    }

    public Player getSelectedPlayer() {
        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            return playerTableModel.getPlayers().get(convertRowIndexToModel(rowIndex));
        }
        return null;
    }

    public void selectPlayer(int playerId) {
        playerTableModel.selectPlayer(playerId);
    }

    @Override
    public void reInit() {
        Player player = getSelectedPlayer();
        resetPlayers();
        repaint();
        if (player != null) {
            selectPlayer(player.getPlayerId());
        }
    }

    public void reInitModel() {
        playerTableModel.reInitData();
    }

    public void reInitModelHRFComparison() {
        playerTableModel.reInitDataHRFComparison();
    }

    @Override
    public void refresh() {
        reInitModel();
        repaint();
    }

    public void refreshHRFComparison() {
        reInitModelHRFComparison();
        repaint();
    }

    private void resetPlayers() {
        playerTableModel.setValues(HOModelManager.instance().getModel().getCurrentPlayers());
    }

    public PlayerOverviewTableModel getPlayerTableModel() {
        return playerTableModel;
    }
}
