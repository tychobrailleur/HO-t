package module.series.promotion;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class PromotionInfoPanel extends JPanel {

    private final LeaguePromotionHandler promotionHandler;

    public PromotionInfoPanel(LeaguePromotionHandler promotionHandler) {
        this.promotionHandler = promotionHandler;
    }

    public void initComponents() {
        setOpaque(false);
        setLayout(new FlowLayout());

        JButton downloadLeagueButton = new JButton(ThemeManager.getIcon(HOIconName.DOWNLOAD_MATCH));
        downloadLeagueButton.setToolTipText("Download Country Data"); // FIXME l10n
        downloadLeagueButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                    "Do you accept to download the data for your league?\n" +
                            "This is a lengthy operation."
                    , "League Data Download", // FIXME l10n
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.OK_OPTION) {
                promotionHandler.downloadLeagueData();
            }
        });
        this.add(downloadLeagueButton);

        JLabel infoLeagueData = new JLabel("Promotion League Data not available.  Click button to process for your league."); // FIXME l10n
        this.add(infoLeagueData);
    }
}
