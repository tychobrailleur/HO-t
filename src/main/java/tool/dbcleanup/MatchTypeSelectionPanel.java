package tool.dbcleanup;

import core.model.enums.MatchType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class MatchTypeSelectionPanel extends JPanel {

    private final JCheckBox leagueMatches = new JCheckBox("League");
    private final JCheckBox friendlyMatches = new JCheckBox("Friendly");
    private final JCheckBox cupMatches = new JCheckBox("Cup");
    private final JCheckBox qualificationMatches = new JCheckBox("Qualification");
    private final JCheckBox tournamentMatches = new JCheckBox("Tournament");

    public MatchTypeSelectionPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(leagueMatches, gbc);

        gbc.gridx++;
        add(friendlyMatches, gbc);

        gbc.gridx++;
        add(cupMatches, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(qualificationMatches, gbc);

        gbc.gridx++;
        add(tournamentMatches, gbc);
    }

    public List<MatchType> getSelectedMatchTypes() {
        List<MatchType> selectedMatchTypes = new ArrayList<>();

        if (leagueMatches.isSelected()) {
            selectedMatchTypes.add(MatchType.LEAGUE);
        }

        if (friendlyMatches.isSelected()) {
            selectedMatchTypes.addAll(MatchType.getFriendlyMatchTypes());
        }

        if (cupMatches.isSelected()) {
            selectedMatchTypes.addAll(MatchType.getCupMatchTypes());
        }

        if (qualificationMatches.isSelected()) {
            selectedMatchTypes.add(MatchType.QUALIFICATION);
        }

        if (tournamentMatches.isSelected()) {
            selectedMatchTypes.addAll(MatchType.getTournamentMatchTypes());
        }

        return selectedMatchTypes;
    }
}
