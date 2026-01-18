package tool.dbcleanup;

import core.gui.HOMainFrame;
import core.model.TranslationFacility;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Database Cleanup Dialog
 *
 * @author flattermann <HO@flattermann.net>
 */
class DBCleanupDialog extends JDialog {

    private final DBCleanupTool cleanupTool;
    private final WeekSelectionPanel mainPanelOwnMatches = new WeekSelectionPanel(DBCleanupTool.REMOVE_NONE);
    private final WeekSelectionPanel mainPanelOtherMatches = new WeekSelectionPanel(16);

    private final MatchTypeSelectionPanel ownMatchesTypeSelectionPanel = new MatchTypeSelectionPanel();
    private final MatchTypeSelectionPanel othersMatchesTypeSelectionPanel = new MatchTypeSelectionPanel();

    /**
     * Creates a new DBCleanupDialog object.
     */
    public DBCleanupDialog(JFrame owner, DBCleanupTool cleanupTool) {
        super(owner, TranslationFacility.tr("ls.menu.file.database.databasecleanup"), true);
        this.cleanupTool = cleanupTool;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 5;
        gbc.ipadx = 10;

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridwidth = 2;
        JLabel textIntro = new JLabel(
                "<html>" + TranslationFacility.tr("dbcleanup.intro")
                        .replace("\n", "<br>") + "</html>");
        mainPanel.add(textIntro, gbc);

        gbc.gridwidth = 1;
        JLabel labelOwnMatches = new JLabel(TranslationFacility.tr("dbcleanup.yourMatches"));
        labelOwnMatches.setFont(labelOwnMatches.getFont().deriveFont(Font.BOLD));

        JLabel labelOtherMatches = new JLabel(TranslationFacility.tr("dbcleanup.otherTeamsMatches"));
        labelOtherMatches.setFont(labelOtherMatches.getFont().deriveFont(Font.BOLD));

        JLabel labelHrf = new JLabel(TranslationFacility.tr("dbcleanup.hrf"));
        labelHrf.setFont(labelHrf.getFont().deriveFont(Font.BOLD));

        JCheckBox hrfAutoRemove = new JCheckBox(TranslationFacility.tr("dbcleanup.hrfSmartRemove"));
        hrfAutoRemove.setSelected(true);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_START;

        mainPanel.add(labelOwnMatches, gbc);
        gbc.gridy = 3;
        mainPanel.add(labelOtherMatches, gbc);
        gbc.gridy = 5;
        mainPanel.add(labelHrf, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(mainPanelOwnMatches, gbc);
        gbc.gridy++;
        mainPanel.add(ownMatchesTypeSelectionPanel, gbc);
        gbc.gridy++;
        mainPanel.add(mainPanelOtherMatches, gbc);
        gbc.gridy++;
        mainPanel.add(othersMatchesTypeSelectionPanel, gbc);
        gbc.gridy++;
        mainPanel.add(hrfAutoRemove, gbc);

        addNewStrut(gbc, mainPanel, 20);

        // Add current statistics on DB Records
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        mainPanel.add(new JLabel(TranslationFacility.tr("dbcleanup.numMatches")), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 3.0;
        mainPanel.add(new JLabel(String.valueOf(cleanupTool.getMatchesCount())), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(new JLabel(TranslationFacility.tr("dbcleanup.numHrfs")), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(String.valueOf(cleanupTool.getHrfCount())), gbc);
        gbc.gridy++;

        addNewStrut(gbc, mainPanel, 20);

        gbc.gridwidth = 2;

        // Add Buttons
        JPanel buttonPanel = new JPanel();
        JButton cleanupNowButton = new JButton(TranslationFacility.tr("dbcleanup.cleanupnow"));
        cleanupNowButton.setFont(cleanupNowButton.getFont().deriveFont(Font.BOLD));
        cleanupNowButton.addActionListener(e -> {
            cleanupTool.cleanupMatches(
                    new CleanupDetails(
                            ownMatchesTypeSelectionPanel.getSelectedMatchTypes(),
                            othersMatchesTypeSelectionPanel.getSelectedMatchTypes(),
                            mainPanelOwnMatches.getWeeks(),
                            mainPanelOtherMatches.getWeeks()));
            cleanupTool.cleanupHRFs(DBCleanupTool.REMOVE_NONE, hrfAutoRemove.isSelected());
            setVisible(false);
            dispose();
        });

        JButton cancelButton = new JButton(TranslationFacility.tr("ls.button.cancel"));
        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        buttonPanel.add(cleanupNowButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        pack();

        Dimension screenSize = HOMainFrame.instance().getToolkit().getScreenSize();
        if (screenSize.width > getSize().width) {
            // Place in the middle
            this.setLocation(
                    (screenSize.width / 2) - (getSize().width / 2),
                    (screenSize.height / 2) - (getSize().height / 2));
        }

        setVisible(true);
    }

    private void addNewStrut(GridBagConstraints gbc, JPanel mainPanel, int height) {
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        JPanel strut = new JPanel();
        strut.setPreferredSize(new Dimension(20, height));
        mainPanel.add(strut, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
    }
}
