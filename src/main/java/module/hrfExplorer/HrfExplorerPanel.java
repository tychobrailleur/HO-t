package module.hrfExplorer;

import core.file.hrf.HRF;
import core.gui.comp.panel.LazyPanel;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class HrfExplorerPanel extends LazyPanel {

    private final HrfExplorerController controller = new HrfExplorerController();

    @Override
    protected void initialize() {
        List<HRF> hrfs = controller.getImportedHrfs();

        Map<Integer, List<HRF>> grouped = controller.getHrfsByYear();

        System.out.println(hrfs.get(0).getYear());

        setLayout(new BorderLayout());
        add(new LinearCalendarPanel(grouped), BorderLayout.NORTH);
    }

    @Override
    protected void update() {

    }
}
