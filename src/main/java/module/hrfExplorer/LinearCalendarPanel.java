package module.hrfExplorer;

import core.file.hrf.HRF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        Box.Filler hFill = new Box.Filler(
                new Dimension(10000,0),
                new Dimension(10000, 0),
                new Dimension(10000, 0));
        add(hFill);
    }

    static class YearPanel extends JPanel {
        YearPanel(int year, List<HRF> entries) {
            setPreferredSize(new Dimension(12*10, 180));
            setMinimumSize(new Dimension(12*10, 180));
            setLayout(new BorderLayout());

            Map<Integer, List<HRF>> monthHrf = entries.stream().collect(
                    Collectors.groupingBy(HRF::getMonth, Collectors.toList())
            );

            JPanel mainPanel = new ChartPanel(year, monthHrf);
            mainPanel.setOpaque(true);
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setPreferredSize(new Dimension(120, 160));

            add(mainPanel, BorderLayout.CENTER);

            JLabel yearLabel = new JLabel(String.valueOf(year), SwingConstants.CENTER);
            yearLabel.setOpaque(true);
            yearLabel.setBackground(new Color(150, 150, 150));
            yearLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            add(yearLabel, BorderLayout.SOUTH);
        }
    }

    static class ChartPanel extends JPanel {
        private final Map<Integer, List<HRF>> entries;
        private final int year;

        public ChartPanel(int year, Map<Integer, List<HRF>> entries) {
            this.entries = entries;
            this.year = year;
        }

        @Override
        public void paint(Graphics g) {
            Dimension dim = getSize();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, dim.width, dim.height);

            g.setColor(Color.GREEN);
            for (int i = 0; i < 12; i++) {
                int month = entries.getOrDefault(i, Collections.EMPTY_LIST).size();
                int height = dim.height;
                g.fillRect(i*10, height-5*month, 9, 5*month);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point p = e.getPoint();
                    int clickedMonth = p.x / 10;
                    System.out.println("Month clicked: " + clickedMonth + "/" + year);
                }
            });
        }
    }
}
