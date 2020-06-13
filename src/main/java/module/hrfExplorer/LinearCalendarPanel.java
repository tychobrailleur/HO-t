package module.hrfExplorer;

import core.file.hrf.HRF;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class LinearCalendarPanel extends JPanel {

    public LinearCalendarPanel(Map<Integer, List<HRF>> entries) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        List<Integer> years = new ArrayList<>(entries.keySet());
        Collections.sort(years);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        for (int i = years.get(0); i <= years.get(years.size()-1); i++) {
            add(new YearPanel(i, entries.get(i)));
        }

        Box.Filler hFill = new Box.Filler(new Dimension(10000,0),
                new Dimension(10000, 0),
                new Dimension(10000, 0));
        add(hFill);
    }

    class YearPanel extends JPanel {
        private Map<Integer, Integer> hrfCount = new HashMap<>();

        YearPanel(int year, List<HRF> entries) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setPreferredSize(new Dimension(12*10, 120));
            setMinimumSize(new Dimension(12*10, 120));
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel();
            mainPanel.setOpaque(true);
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setPreferredSize(new Dimension(120, 100));

            add(mainPanel, BorderLayout.CENTER);

            Map<Integer, List<HRF>> monthHrf = entries.stream().collect(Collectors.groupingBy(HRF::getMonth, Collectors.toList()));


            JLabel yearLabel = new JLabel(String.valueOf(year), SwingConstants.CENTER);
            add(yearLabel, BorderLayout.SOUTH);
        }
    }
}
