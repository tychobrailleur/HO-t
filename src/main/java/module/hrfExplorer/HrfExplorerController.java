package module.hrfExplorer;

import core.db.DBManager;
import core.file.hrf.HRF;

import java.util.*;
import java.util.stream.Collectors;

public class HrfExplorerController {

    public List<HRF> getImportedHrfs() {
        HRF[] hrfs = DBManager.instance().getAllHRFs(-1, -1, true);
        List<HRF> hrfList = Arrays.asList(hrfs);
        Collections.sort(hrfList, Comparator.comparing(HRF::getDatum));
        return hrfList;
    }

    public Map<Integer, List<HRF>> getHrfsByYear() {
        List<HRF> hrfs = getImportedHrfs();
        return hrfs.stream().collect(Collectors.groupingBy(HRF::getYear, Collectors.toList()));
    }
}
