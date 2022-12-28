package module.teamAnalyzer.ui;

import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.ui.controller.RecapListSelectionListener;
import java.awt.*;
import java.io.Serial;
import javax.swing.*;


public class RecapPanel extends JPanel {

	@Serial
    private static final long serialVersionUID = 486150690031160261L;
    public static final String VALUE_NA = "---"; //$NON-NLS-1$

    //~ Instance fields ----------------------------------------------------------------------------
    private FixedColumnsTable table;

    private final RecapListSelectionListener recapListener = null;

    private RecapPanelTableModel tableModel;
    /**
     * Creates a new RecapPanel object.
     */
    public RecapPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload(TeamReport teamReport) {
        int selection = teamReport.getSelection(); // save selection
        // Empty model
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        if ( teamReport.size() < 2 ) return; // no matches loaded

        for ( int i =0; i < teamReport.size(); i++){
            tableModel.addRow(AddLineup(teamReport.getTeamMatchReport(i)));
        }
        teamReport.setSelection(selection); // restore selection

        setColumnWidth(0, 100);
        setColumnWidth(1, 20);
        setColumnWidth(2, 40);
        setColumnWidth(3, 50);
        setColumnWidth(4, 50);

        if (SystemManager.isStars.isSet()) {
            setColumnWidth(12, 50);
        } else {
            setColumnInvisible(12);
        }

        if (SystemManager.isTotalStrength.isSet()) {
            setColumnWidth(13, 50);
        } else {
            setColumnInvisible(13);
        }

        if (SystemManager.isSquad.isSet()) {
            setColumnWidth(14, 50);
        } else {
            setColumnInvisible(14);
        }

        if (SystemManager.isSmartSquad.isSet()) {
            setColumnWidth(15, 50);
        } else {
            setColumnInvisible(15);
        }

        if (SystemManager.isLoddarStats.isSet()) {
            setColumnWidth(16, 50);
        } else {
            setColumnInvisible(16);
        }

        // Hide 'match type' and 'is home match?' columns. (used by RecapTableRenderer)
        setColumnInvisible(22);
        setColumnInvisible(23);

    }

    private Vector<Object> AddLineup(TeamLineup lineup) {
        if ( lineup == null) return null;

        Vector<Object> rowData = new Vector<>();

        // Column 1
        rowData.add(lineup.getName());

        // Column 2
        IMatchType matchType = lineup.getMatchType();
        if ( matchType != MatchType.NONE){
            rowData.add(ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]));
        }
        else {
            rowData.add(VALUE_NA);
        }
        rowData.add(lineup.getResult());

        // Column 3
        int week = lineup.getWeek();
        if ( week > 0) rowData.add(week);
        else rowData.add(VALUE_NA);

        // Column 4
        int season = lineup.getSeason();
        if ( season>0)rowData.add(season);
        else rowData.add(VALUE_NA);

        // Columns 5-11
        setRating(rowData, lineup.getRating());

        DecimalFormat df = new DecimalFormat("###.#"); //$NON-NLS-1$

        // Columns 12-15
        rowData.add(df.format(lineup.getStars()));
        if (lineup.getRating().getHatStats() >= 0) {
            rowData.add(df.format(lineup.getRating().getHatStats()));
        } else {
            rowData.add("");
        }
        rowData.add(df.format(lineup.getRating().getSquad()));
        if (lineup.getStars() != 0.0) {
            rowData.add(df.format(lineup.getRating().getSquad() / lineup.getStars()));
        } else {
            rowData.add("");
        }

        DecimalFormat df2 = new DecimalFormat("###.##"); //$NON-NLS-1$

        // Columns 16-17
        rowData.add(df2.format(lineup.getRating().getLoddarStats()));
        rowData.add(formatTacticColumn(lineup));

        // Column 18
        if (lineup.getTacticCode() <= 0) {
            rowData.add(VALUE_NA);
        } else {
            rowData.add(PlayerAbility.getNameForSkill(lineup.getTacticLevel(), false));
        }

        // Columns 19-23
        rowData.add(lineup.getFormation());
        rowData.add(lineup.getMorale());
        rowData.add(lineup.getSelfConfidence());
        rowData.add(matchType.getMatchTypeId());
        rowData.add(lineup.isHomeMatch());

        return rowData;
    }

    private String formatTacticColumn(TeamLineup lineup) {
        var str = new StringBuilder();
        int tactic = lineup.getTacticCode();
        if (tactic != -1) {
            str.append(Matchdetails.getNameForTaktik(tactic));
        }
        if (lineup.getMatchDetail() != null && lineup.getMatchDetail().isManMarking()) {
            if (tactic != -1) str.append("/");
            str.append(HOVerwaltung.instance().getLanguageString("ls.teamanalyzer.manmarking"));
        }

        if (str.isEmpty()) {
            str.append(VALUE_NA);
        }
        return str.toString();
    }

    private void setColumnInvisible(int col) {
        table.getTableHeader().getColumnModel().getColumn(col).setWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private void setColumnWidth(int col, int width) {
        table.getTableHeader().getColumnModel().getColumn(col).setWidth(width);
        table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(width);
        table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(200);
        table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
    }

    private void setRating(Vector<Object> row, MatchRating rating) {
        if (rating == null) {
            for (int i = 0; i < 7; i++) {
                row.add(""); //$NON-NLS-1$
            }

            return;
        }

        row.add(getRating((int) rating.getMidfield()));
        row.add(getRating((int) rating.getRightDefense()));
        row.add(getRating((int) rating.getCentralDefense()));
        row.add(getRating((int) rating.getLeftDefense()));
        row.add(getRating((int) rating.getRightAttack()));
        row.add(getRating((int) rating.getCentralAttack()));
        row.add(getRating((int) rating.getLeftAttack()));
    }

    private String getRating(int rating) {
        return RatingUtil.getRating(rating,SystemManager.isNumericRating.isSet(), SystemManager.isDescriptionRating.isSet());
    }

    private void jbInit() {
        tableModel = UserColumnController.instance().getTeamAnalyzerRecapModell();
        tableModel.showTeamReport(null);
        table = new FixedColumnsTable(2, tableModel);
        table.setDefaultRenderer(Object.class, new RecapTableRenderer());
        table.setDefaultRenderer(ImageIcon.class, new RecapTableRenderer());
//        restoreUserSettings();

        table.addListSelectionListener( new RecapListSelectionListener(table.getTableSorter(), tableModel));
        setLayout(new BorderLayout());

//        JScrollPane scrollPane = new JScrollPane(table);
//
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(table);
    }

    public String getSelectedTacticType() {
    	return recapListener.getSelectedTacticType();
    }

    public String getSelectedTacticSkill() {
    	return recapListener.getSelectedTacticSkill();
    }

    public void storeUserSettings() {
        this.tableModel.storeUserSettings(table);
    }
}
