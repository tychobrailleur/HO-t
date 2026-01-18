package core.gui.model;

import core.db.DBManager;
import core.gui.comp.table.BooleanColumn;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController.ColumnModelId;
import core.model.player.Player;
import core.file.hrf.HRF;
import core.util.HODateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import module.playerOverview.SpielerTrainingsVergleichsPanel;

/**
 * Model used to display players in the Squad table.
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
public class PlayerOverviewTableModel extends HOTableModel {

    /** all players */
    private List<Player> players;

    /**
     * constructor
     */
    public PlayerOverviewTableModel(ColumnModelId id) {
        super(id, "Spieleruebersicht");
        initColumns();
    }

    public PlayerOverviewTableModel(ColumnModelId id, String name) {
        super(id, name);
        initColumns();
    }

    private void initColumns() {
        PlayerColumn[] basic = UserColumnFactory.createPlayerBasicArray();
        UserColumn[] columns = new UserColumn[70];
        if (basic != null) {
            columns[0] = basic[0];
            columns[48] = basic[1];
        }

        PlayerSkillColumn[] skills = UserColumnFactory.createPlayerSkillArray();
        int skillIndex = 9; // - 20
        if (skills != null) {
            System.arraycopy(skills, 0, columns, skillIndex, skills.length);
        }

        PlayerPositionColumn[] positions = UserColumnFactory.createPlayerPositionArray();
        int positionIndex = 23; // - 41
        if (positions != null) {
            System.arraycopy(positions, 0, columns, positionIndex, positions.length);
        }

        PlayerColumn[] goals = UserColumnFactory.createGoalsColumnsArray();
        int goalsIndex = 42; // -46
        if (goals != null) {
            System.arraycopy(goals, 0, columns, goalsIndex, goals.length);
        }

        PlayerColumn[] additionalArray = UserColumnFactory.createPlayerAdditionalArray();
        if (additionalArray != null) {
            columns[1] = additionalArray[0];
            columns[2] = additionalArray[1];
            columns[4] = additionalArray[2];
            columns[21] = additionalArray[3]; // best position
            columns[5] = additionalArray[4];
            columns[6] = additionalArray[5];
            columns[7] = additionalArray[6];
            columns[58] = additionalArray[7];
            columns[8] = additionalArray[8]; // tsi
            columns[22] = additionalArray[9]; // lastmatch
            columns[47] = additionalArray[11];
            columns[3] = additionalArray[12]; // Motherclub
            columns[49] = additionalArray[10];
            columns[50] = additionalArray[16];
            columns[51] = additionalArray[17];
            columns[52] = additionalArray[18];
            columns[53] = additionalArray[13];
            columns[54] = additionalArray[14];
            columns[55] = additionalArray[15];
            columns[56] = additionalArray[19];
            columns[57] = additionalArray[20];
            columns[59] = additionalArray[21];
            columns[60] = additionalArray[22];
            columns[61] = additionalArray[23]; // schum-rank
            columns[62] = additionalArray[24]; // schum-rank benchmark
            columns[63] = new BooleanColumn(UserColumnFactory.AUTO_LINEUP, " ", "AutoAufstellung", 28);
            columns[64] = additionalArray[25];
            columns[65] = additionalArray[26];
            columns[66] = additionalArray[27];
            columns[67] = additionalArray[28];
            columns[68] = additionalArray[29];
            columns[69] = additionalArray[30];
        }

        this.columns = Arrays.stream(columns).filter(Objects::nonNull).toArray(UserColumn[]::new);

        // Assert logic replacement (optional runtime check)
        if (this.columns.length != Arrays.stream(columns).filter(Objects::nonNull).count()) {
            throw new AssertionError("Column count mismatch");
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return getValueAt(row, column) instanceof Boolean;
    }

    public int getRowIndexOfPlayer(int playerId) {
        int modelIndex = getPlayerIndex(playerId);
        if (modelIndex > -1 && modelIndex < getRowCount()) {
            if (getTable() != null) {
                return getTable().convertRowIndexToView(modelIndex);
            }
        }
        return -1;
    }

    public Player getSelectedPlayer() {
        if (getTable() == null)
            return null;
        int rowIndex = getTable().getSelectedRow();
        if (rowIndex >= 0 && rowIndex < getRowCount()) {
            return players.get(getTable().convertRowIndexToModel(rowIndex));
        }
        return null;
    }

    public void selectPlayer(int playerId) {
        int row = getRowIndexOfPlayer(playerId);
        if (row > -1 && row < getRowCount() && getTable() != null) {
            getTable().setRowSelectionInterval(row, row);
        }
    }

    public Player getPlayerAtRow(int tableRow) {
        if (players != null && tableRow > -1 && tableRow < getRowCount()) {
            if (getTable() != null) {
                int modelIndex = getTable().convertRowIndexToModel(tableRow);
                if (modelIndex > -1 && modelIndex < getRowCount()) {
                    return players.get(modelIndex);
                }
            }
        }
        return null;
    }

    public Player getPlayer(int playerId) {
        // Can be negative for temp player
        if (playerId != 0 && players != null) {
            for (Player player : players) {
                if (player.getPlayerId() == playerId) {
                    return player;
                }
            }
        }
        return null;
    }

    public int getPlayerIndex(int playerId) {
        if (players != null) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getPlayerId() == playerId) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Sets the new list of players.
     */
    public void setValues(List<Player> player) {
        this.players = player;
        initData();
    }

    /**
     * Resets the data for an HRF comparison.
     */
    public void reInitDataHRFComparison() {
        initData();
    }

    /**
     * Returns the {@link Player} with the same ID as the instance passed, or
     * `null`.
     */
    private Player getPreviousPlayerDevelopmentStage(Player currentDevelopmentStage) {
        int id = currentDevelopmentStage.getPlayerId();
        if (id >= 0) {
            // not a temporary player
            List<Player> selectedPlayerDevelopmentStage = SpielerTrainingsVergleichsPanel
                    .getSelectedPlayerDevelopmentStage();
            if (selectedPlayerDevelopmentStage != null) {
                for (Player selectedDevelopmentStage : selectedPlayerDevelopmentStage) {
                    if (selectedDevelopmentStage.getPlayerId() == id) {
                        return selectedDevelopmentStage;
                    }
                }
            }
            if (SpielerTrainingsVergleichsPanel.isDevelopmentStageSelected()) {
                Integer hrf = SpielerTrainingsVergleichsPanel.getSelectedHrfId();
                return getFirstPlayerDevelopmentStageAfterSelected(currentDevelopmentStage, hrf);
            }
        }
        return null;
    }

    /**
     * Returns the {@link Player} from the first HRF in which he appears.
     */
    private Player getFirstPlayerDevelopmentStageAfterSelected(Player vorlage, Integer hrfId) {
        HODateTime after = null;
        if (hrfId != null) {
            HRF hrf = DBManager.instance().loadHRF(hrfId);
            if (hrf != null) {
                after = hrf.getDatum();
            }
        }
        return DBManager.instance().loadPlayerFirstHRF(vorlage.getPlayerId(), after);
    }

    /**
     * create a data[][] from player-Vector
     */
    @Override
    protected void initData() {
        if (players == null) {
            m_clData = new Object[0][0];
            return;
        }

        UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
        m_clData = new Object[players.size()][tmpDisplayedColumns.length];
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            Player comparisonPlayer = getPreviousPlayerDevelopmentStage(currentPlayer);
            for (int j = 0; j < tmpDisplayedColumns.length; j++) {
                if (tmpDisplayedColumns[j] instanceof PlayerColumn) {
                    m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(currentPlayer,
                            comparisonPlayer);
                } else if (tmpDisplayedColumns[j] instanceof BooleanColumn) {
                    m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(currentPlayer);
                }
            }
        }
        fireTableDataChanged();
    }

    public void reInitData() {
        if (players == null)
            return;
        UserColumn[] tmpDisplayedColumns = getDisplayedColumns();
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            for (int j = 0; j < tmpDisplayedColumns.length; j++) {
                int colId = tmpDisplayedColumns[j].getId();
                if (colId == UserColumnFactory.NAME ||
                        colId == UserColumnFactory.LINEUP ||
                        colId == UserColumnFactory.BEST_POSITION ||
                        colId == UserColumnFactory.SCHUM_RANK_BENCHMARK ||
                        colId == UserColumnFactory.GROUP) {
                    m_clData[i][j] = ((PlayerColumn) tmpDisplayedColumns[j]).getTableEntry(currentPlayer, null);
                } else if (colId == UserColumnFactory.AUTO_LINEUP) {
                    m_clData[i][j] = ((BooleanColumn) tmpDisplayedColumns[j]).getValue(currentPlayer);
                }
            }
        }
    }
}
