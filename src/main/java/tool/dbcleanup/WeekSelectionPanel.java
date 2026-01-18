package tool.dbcleanup;

import core.model.TranslationFacility;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class WeekSelectionPanel extends JPanel {

    private final JLabel labelRemoveOlderThan = new JLabel(TranslationFacility.tr("dbcleanup.removeOlderThan"));
    private final JLabel labelWeeks = new JLabel(TranslationFacility.tr("dbcleanup.weeks"));

    private final JCheckBox noneCheckBox = new JCheckBox(TranslationFacility.tr("dbcleanup.none"));
    private final JCheckBox allCheckBox = new JCheckBox(TranslationFacility.tr("dbcleanup.allTime"));
    private final JTextField weeksTextField = new JTextField(3);

    public WeekSelectionPanel(int weeks) {
        this(weeks, true);
    }

    public WeekSelectionPanel(int weeks, boolean showRemoveAll) {
        // Small downward shift to align the labels with the main label
        setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        noneCheckBox.setSelected(false);
        allCheckBox.setSelected(false);
        weeksTextField.setText("0");

        if (weeks <= DBCleanupTool.REMOVE_NONE) {
            noneCheckBox.setSelected(true);
        } else if (weeks == DBCleanupTool.REMOVE_ALL) {
            allCheckBox.setSelected(true);
        } else {
            weeksTextField.setText(String.valueOf(weeks));
        }

        if (!showRemoveAll) {
            allCheckBox.setVisible(false);
        }
        initComponents();
    }

    private void initComponents() {
        noneCheckBox.addActionListener(e -> {
            if (noneCheckBox.isSelected()) {
                allCheckBox.setSelected(false);
                weeksTextField.setText("0");
            }
        });
        allCheckBox.addActionListener(e -> {
            if (allCheckBox.isSelected()) {
                noneCheckBox.setSelected(false);
                weeksTextField.setText("0");
            }
        });
        weeksTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                noneCheckBox.setSelected(false);
                allCheckBox.setSelected(false);
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                // do nothing
            }
        });

        add(noneCheckBox);
        add(new JLabel("     "));
        add(allCheckBox);
        add(new JLabel("     "));
        add(labelRemoveOlderThan);
        add(weeksTextField);
        add(labelWeeks);
    }

    public int getWeeks() {
        if (noneCheckBox.isSelected()) {
            return DBCleanupTool.REMOVE_NONE;
        } else if (allCheckBox.isSelected()) {
            return DBCleanupTool.REMOVE_ALL;
        } else {
            int weeks = DBCleanupTool.REMOVE_NONE;
            try {
                weeks = Integer.parseInt(weeksTextField.getText());
            } catch (Exception e) {
                // be silent
            }
            return weeks > 0 ? weeks : DBCleanupTool.REMOVE_NONE;
        }
    }
}
