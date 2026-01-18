package module.teamAnalyzer.ui;

import core.model.TranslationFacility;
import core.util.HODateTime;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays information about the team currently selected in the dropdown in
 * {@link module.teamAnalyzer.ui.FilterPanel}.
 */
public class TeamInfoPanel extends JPanel {

    private boolean isBot(Map<String, String> details) {
        String isBot = details.getOrDefault("IsBot", "False");
        return "True".equalsIgnoreCase(isBot);
    }

    public void setTeam(Map<String, String> details) {
        removeAll();
        boolean isBot = isBot(details);

        setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("ls.teamanalyzer.info")));
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel managerLabel = new JLabel(TranslationFacility.tr("ls.teamanalyzer.manager"));
        Font boldFont = managerLabel.getFont().deriveFont(Font.BOLD);
        managerLabel.setFont(boldFont);
        add(managerLabel, gbc);
        gbc.gridy++;

        JLabel lastLoginLabel = new JLabel(TranslationFacility.tr("ls.teamanalyzer.last_login"));
        lastLoginLabel.setFont(boldFont);
        add(lastLoginLabel, gbc);

        if (isBot) {
            gbc.gridy++;
            JLabel botStatusLabel = new JLabel(TranslationFacility.tr("ls.teamanalyzer.bot"));
            botStatusLabel.setFont(boldFont);
            add(botStatusLabel, gbc);
        }

        if (details.containsKey("LeaguePosition")) {
            gbc.gridy++;
            JLabel leaguePositionLabel = new JLabel(TranslationFacility.tr("ls.teamanalyzer.league_position"));
            leaguePositionLabel.setFont(boldFont);
            add(leaguePositionLabel, gbc);
        }

        // Column 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel loginValueLabel = new JLabel();
        String loginName = details.get("Loginname");
        loginValueLabel.setText((loginName == null || loginName.isBlank())
                ? TranslationFacility.tr("ls.teamanalyzer.na")
                : loginName);
        add(loginValueLabel, gbc);
        gbc.gridy++;

        JLabel lastLoginDateLabel = new JLabel();
        String lastLoginDateStr = details.get("LastLoginDate");
        HODateTime lastLoginDate = HODateTime.fromHT(lastLoginDateStr);
        lastLoginDateLabel.setText((loginName == null || loginName.isBlank())
                ? TranslationFacility.tr("ls.teamanalyzer.na")
                : lastLoginDate.toLocaleDateTime());
        add(lastLoginDateLabel, gbc);

        if (isBot) {
            gbc.gridy++;
            JLabel botStatusValueLabel = new JLabel();
            String botStatusDate = details.get("BotSince");
            botStatusValueLabel.setText(TranslationFacility.tr("ls.teamanalyzer.bot_since",
                    HODateTime.fromHT(botStatusDate).toLocaleDate()));
            add(botStatusValueLabel, gbc);
        }

        if (details.containsKey("LeaguePosition")) {
            gbc.gridy++;
            String leaguePosText = TranslationFacility.tr("ls.teamanalyzer.league_position_val",
                    details.get("LeaguePosition"), details.get("LeagueLevelUnitName"), details.get("CountryName"));
            JLabel leaguePositionValue = new JLabel(leaguePosText); // Typo in original code? JLabel constructor takes
            // String
            leaguePositionValue.setText(leaguePosText);
            add(leaguePositionValue, gbc);
        }

        revalidate();
        repaint();
    }
}
