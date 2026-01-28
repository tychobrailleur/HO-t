package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.transfer.ui.sorter.AbstractTableSorter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.TableModel;

public class RecapTableSorter extends AbstractTableSorter {
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    private static final long serialVersionUID = -3606200720032237171L;
    private List<String> skills;

    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * Creates a new RecapTableSorter object.
     */
    public RecapTableSorter(TableModel tableModel) {
        super(tableModel);
        skills = new ArrayList<>();

        for (int i = 1; i < 23; i++) {
            skills.add(PlayerAbility.getNameForSkill(i, false, false));
        }
    }

    @Override
    public Comparator<String> getCustomComparator(int column) {
        if (column == 3) {
            return Comparator.comparingInt(this::parseToInt);
        }
        if ((column > 4) && (column < 12)) {
            return Comparator.comparingDouble(this::getRating5_11);
        }

        if ((column > 11) && (column < 16)) {
            DecimalFormat df = new DecimalFormat("###.#");
            return Comparator.comparingDouble(s -> parseDouble(s, df));
        }

        if (column == 16) {
            DecimalFormat df = new DecimalFormat("###.##");
            return Comparator.comparingDouble(s -> parseDouble(s, df));
        }

        if (column == 18) {
            return Comparator.comparingDouble(this::getRating18);
        }

        return null;
    }

    private int parseToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private double getRating5_11(String s) {
        try {
            return RatingUtil.getRating(s, SystemManager.isNumericRating.isSet(),
                    SystemManager.isDescriptionRating.isSet(), skills);
        } catch (Exception e) {
            return 0;
        }
    }

    private double getRating18(String s) {
        try {
            return RatingUtil.getRating(s, false, true, skills);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String s, DecimalFormat df) {
        try {
            return df.parse(s).doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean hasHeaderLine() {
        return true;
    }

    @Override
    public int minSortableColumn() {
        return 3;
    }
}
