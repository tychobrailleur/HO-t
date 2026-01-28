package core.gui.comp.table;

import core.db.DBManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import core.util.HOLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.RowSorterEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 * Basic ColumnModel for all UserColumnModels
 *
 * @author Thorsten Dietz
 * @since 1.36
 */
public abstract class HOTableModel extends AbstractTableModel {

    public final int id;

    /** Name of the column model, shows in OptionsPanel */
    private final String name;

    /** All columns of this model */
    protected UserColumn[] columns;

    /** Only displayed columns */
    protected UserColumn[] displayedColumns;

    /** Data of table */
    protected Object[][] m_clData;

    /** Table component */
    protected List<JTable> tables = new ArrayList<>();

    private int selectedRow = -1;

    protected HOTableModel(UserColumnController.ColumnModelId id, String name) {
        this.id = id.getValue();
        this.name = name;
    }

    /**
     * Return the language dependent name of this model
     */
    @Override
    public String toString() {
        return TranslationFacility.tr(name);
    }

    /**
     * Return all column names of displayed columns
     *
     * @return String[]
     */
    public String[] getColumnNames() {
        UserColumn[] dispCols = getDisplayedColumns();
        String[] columnNames = new String[dispCols.length];
        for (int i = 0; i < dispCols.length; i++) {
            columnNames[i] = dispCols[i].getColumnName();
        }
        return columnNames;
    }

    /**
     * Return all tooltips of displayed columns
     *
     * @return String[]
     */
    public String[] getTooltips() {
        UserColumn[] dispCols = getDisplayedColumns();
        String[] tooltips = new String[dispCols.length];
        for (int i = 0; i < dispCols.length; i++) {
            tooltips[i] = dispCols[i].getTooltip();
        }
        return tooltips;
    }

    /**
     * Return all displayed columns
     *
     * @return UserColumn[]
     */
    public UserColumn[] getDisplayedColumns() {
        if (displayedColumns == null) {
            displayedColumns = Arrays.stream(columns)
                    .filter(UserColumn::isDisplay)
                    .toArray(UserColumn[]::new);
        }
        return displayedColumns;
    }

    /**
     * Return all columns
     *
     * @return UserColumn[]
     */
    public UserColumn[] getColumns() {
        return columns;
    }

    /**
     * Return count of displayed columns
     *
     * @return int
     */
    private int getDisplayedColumnCount() {
        return getDisplayedColumns().length;
    }

    /**
     * Returns count of displayed columns redundant method, but this is
     * overwritten method from AbstractTableModel
     */
    @Override
    public int getColumnCount() {
        return getDisplayedColumnCount();
    }

    /**
     * Return value of one table cell
     *
     * @param row    Row number
     * @param column Column number
     *
     * @return Object
     */
    @Override
    public Object getValueAt(int row, int column) {
        if (m_clData != null && m_clData.length > row && row > -1 && column > -1 && column < m_clData[row].length) {
            return m_clData[row][column];
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column > -1 && column < columns.length) {
            return columns[column].isEditable();
        }
        return false;
    }

    /**
     * Return row count
     * 
     * @return int
     */
    @Override
    public int getRowCount() {
        return m_clData != null ? m_clData.length : 0;
    }

    /**
     * Return class of a table column
     * 
     * @param columnIndex the column being queried
     * @return Class<?>
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object obj = getValueAt(0, columnIndex);

        if (obj != null) {
            return obj.getClass();
        }

        return String.class;
    }

    /**
     * Return the name of a table column
     * 
     * @param columnIndex the column being queried
     * @return String
     */
    @Override
    public String getColumnName(int columnIndex) {
        if (getDisplayedColumnCount() > columnIndex && columnIndex > -1) {
            return getDisplayedColumns()[columnIndex].getColumnName();
        }
        return null;
    }

    /**
     * Set the value of a table cell
     * 
     * @param value  value to assign to cell
     * @param row    row of cell
     * @param column column of cell
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (m_clData != null && m_clData.length > row && row > -1 && column > -1 && column < m_clData[row].length) {
            m_clData[row][column] = value;
        }
        for (JTable table : tables) {
            fireTableCellUpdated(table.convertRowIndexToView(row), table.convertColumnIndexToView(column));
        }
    }

    /**
     * Abstract init data method has to be provided by subclass
     */
    protected abstract void initData();

    /**
     * Return the array index from a Column id
     */
    public int getPositionInArray(int searchId) {
        UserColumn[] tmpColumns = getDisplayedColumns();
        for (int i = 0; i < tmpColumns.length; i++) {
            if (tmpColumns[i].getId() == searchId)
                return i;
        }
        return -1;
    }

    /**
     * Get the table column width and index from user column settings stored in the
     * database
     *
     * @param table Table
     */
    private void getUserColumnSettings(JTable table) {
        // Restore column order and width settings
        Arrays.stream(getDisplayedColumns())
                .sorted(Comparator.comparingInt(UserColumn::getIndex))
                .forEach(i -> getColumnSettings(i, table));
    }

    /**
     * Get column order and width from user column
     *
     * @param userColumn User column holding user's settings
     * @param table      Table object
     */
    private void getColumnSettings(UserColumn userColumn, JTable table) {
        TableColumn viewColumn = table.getColumn(userColumn.getId());
        viewColumn.setPreferredWidth(userColumn.getPreferredWidth());
        moveColumn(table, userColumn);
    }

    private void moveColumn(JTable table, UserColumn userColumn) {
        if (table instanceof FixedColumnsTable fixedTable) {
            int targetIndex = userColumn.getIndex() - fixedTable.getFixedColumnsCount();
            if (targetIndex >= 0) {
                try {
                    int index = table.getColumnModel().getColumnIndex(userColumn.getId());
                    if (index != targetIndex) {
                        table.moveColumn(index, targetIndex);
                    }
                } catch (IllegalArgumentException e) {
                    HOLogger.instance().info(
                            this.getClass(),
                            "Cannot move column to stored index " + userColumn.getId() + " "
                                    + userColumn.getColumnName() + " index=" + userColumn.getIndex() + ": "
                                    + e.getMessage());
                }
            }
        } else {
            int index = table.getColumnModel().getColumnIndex(userColumn.getId());
            if (index != userColumn.getIndex()) {
                table.moveColumn(index, (int) Math.max(0.0, userColumn.getIndex()));
            }
        }
    }

    /**
     * Set user column settings from the table instance
     * 
     * @param table Table object
     * @return True if one user setting is changed
     *         False, if no user settings are changed
     */
    private boolean setUserColumnSettings(JTable table) {
        boolean changed = false;
        UserColumn[] displayedColumns = getDisplayedColumns();
        for (int index = 0; index < table.getColumnCount(); index++) {
            TableColumn tableColumn = getTableColumn(table, index);
            UserColumn modelColumn = displayedColumns[tableColumn.getModelIndex()];

            if (modelColumn.getIndex() != index) {
                changed = true;
                modelColumn.setIndex(index);
            }

            int tableColumnWidth = tableColumn.getWidth();
            if (modelColumn.getPreferredWidth() != tableColumnWidth) {
                changed = true;
                modelColumn.setPreferredWidth(tableColumnWidth);
            }
        }
        return changed;
    }

    /**
     * User can disable columns
     * 
     * @return boolean
     */
    public boolean userCanDisableColumns() {
        return true;
    }

    /**
     * Initialize the table object with data from the model
     * Todo: Think about making HOTableModel supporting only FixedColumnsTable
     * (JTable==FixedColumnsTable(0 fixed columns))
     * Then initTable could be part of FixedColumnsTable (HOTable)
     * 
     * @param table Table object
     */
    public void initTable(JTable table) {
        tables.add(table);
        if (!(table instanceof FixedColumnsTable)) {
            ToolTipHeader header = new ToolTipHeader(table.getColumnModel());
            header.setToolTipStrings(getTooltips());
            header.setToolTipText("");
            table.setTableHeader(header);
            table.setModel(this);
        }

        // Copy user columns' identifiers to table's columns
        UserColumn[] displayedColumns = getDisplayedColumns();
        int i = 0;
        for (UserColumn userColumn : displayedColumns) {
            TableColumn tableColumn = getTableColumn(table, i++);
            tableColumn.setIdentifier(userColumn.getId());
        }
        getUserColumnSettings(table);

        TableRowSorter<HOTableModel> rowSorter = new TableRowSorter<>(this);
        rowSorter.addRowSorterListener(e -> {
            // Restore the previous selection when table rows were sorted
            // Sorting changed
            if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
                selectedRow = table.getSelectedRow();
            } else if (e.getType() == RowSorterEvent.Type.SORTED) {
                if (selectedRow > -1) {
                    try {
                        int modelIndex = e.convertPreviousRowIndexToModel(selectedRow);
                        if (modelIndex > -1) {
                            int newSelectedRow = table.convertRowIndexToView(modelIndex);
                            table.setRowSelectionInterval(newSelectedRow, newSelectedRow);
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        // ignore if selection is out of bounds
                    }
                }
            }
        });
        getRowOrderSettings(rowSorter);
        table.setRowSorter(rowSorter);
        table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
    }

    private TableColumn getTableColumn(JTable table, int i) {
        if (table instanceof FixedColumnsTable fixedTable) {
            return fixedTable.getTableColumn(i);
        }
        return table.getColumnModel().getColumn(i);
    }

    /**
     * Store user table settings in the database if they were changed by the user
     */
    public void storeUserSettings() {
        for (JTable table : tables) {
            boolean changed = setUserColumnSettings(table);
            @SuppressWarnings("unchecked")
            RowSorter<HOTableModel> sorter = (RowSorter<HOTableModel>) table.getRowSorter();
            if (setRowOrderSettings(sorter)) {
                changed = true;
            }
            if (changed) {
                DBManager.instance().saveHOColumnModel(this);
                break; // do not override with next table's setting
                // if more than one table changes setting the first one is the winner
            }
        }
    }

    /**
     * Get row order from user columns and restore it to the given row sorter
     * 
     * @param rowSorter Row sorter
     */
    private void getRowOrderSettings(RowSorter<HOTableModel> rowSorter) {
        // Restore row order setting
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        List<UserColumn> sortColumns = Arrays.stream(this.columns)
                .filter(col -> col.getSortPriority() != null)
                .sorted(Comparator.comparingInt(UserColumn::getSortPriority))
                .toList();

        if (!sortColumns.isEmpty()) {
            List<UserColumn> userColumns = Arrays.asList(this.columns);
            for (UserColumn col : sortColumns) {
                int index = userColumns.indexOf(col);
                if (index > -1) {
                    RowSorter.SortKey sortKey = new RowSorter.SortKey(index, col.getSortOrder());
                    if (sortKey.getColumn() > -1 && sortKey.getColumn() < rowSorter.getModel().getColumnCount()) {
                        sortKeys.add(sortKey);
                    }
                }
            }
        }
        rowSorter.setSortKeys(sortKeys);
    }

    /**
     * Set user columns sort priority and order from given row sorter
     * 
     * @param sorter Row sorter
     * @return True if one user setting is changed
     *         False, if no user settings are changed
     */
    private boolean setRowOrderSettings(RowSorter<HOTableModel> sorter) {
        boolean changed = false;
        List<? extends RowSorter.SortKey> rowSortKeys = sorter.getSortKeys();

        for (int i = 0; i < columns.length; i++) {
            final int finalI = i;
            Optional<? extends RowSorter.SortKey> rowSortKey = rowSortKeys.stream()
                    .filter(k -> k.getColumn() == finalI)
                    .findFirst();

            UserColumn userColumn = columns[i];

            if (rowSortKey.isPresent() && rowSortKey.get().getSortOrder() != SortOrder.UNSORTED) {
                RowSorter.SortKey k = rowSortKey.get();
                int priority = rowSortKeys.indexOf(k);
                if (userColumn.getSortPriority() == null || (userColumn.getSortPriority() != priority)
                        || (userColumn.getSortOrder() != k.getSortOrder())) {
                    userColumn.setSortOrder(k.getSortOrder());
                    userColumn.setSortPriority(priority);
                    changed = true;
                }
            } else if (userColumn.getSortPriority() != null) {
                userColumn.setSortPriority(null);
                userColumn.setSortOrder(null);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Returns the primary table
     * 
     * @return JTable
     */
    public JTable getTable() {
        if (!tables.isEmpty())
            return tables.get(0);
        return null;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }
}
