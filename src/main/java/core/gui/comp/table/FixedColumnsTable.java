package core.gui.comp.table;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOConfigurationIntParameter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Table with fixed columns on the left hand side
 * The other columns can be sorted or disabled by the user
 */
public class FixedColumnsTable extends JTable {

    /**
     * Number of fixed columns in table
     */
    private final int fixedColumnsCount;

    /**
     * Position of the divider between fixed and scrollable tables
     */
    private HOConfigurationIntParameter dividerLocation = null;

    /**
     * Fixed table part (left hand side)
     */
    private JTable fixed = null;

    /**
     * Container component for split pane of fixed and scrollable tables
     */
    private JPanel containerComponent;

    public FixedColumnsTable(HOTableModel tableModel) {
        this(tableModel, 1);
    }

    public FixedColumnsTable(HOTableModel tableModel, int fixedColumnsCount) {
        super(tableModel);
        this.fixedColumnsCount = fixedColumnsCount;
        init(tableModel);
    }

    private void init(HOTableModel tableModel) {
        // Handle tool tips
        TableCellRenderer headerRenderer = getTableHeader().getDefaultRenderer();
        getTableHeader().setDefaultRenderer((table, value, isSelected, hasFocus, row, column) -> {
            Component component = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            HOTableModel model = (HOTableModel) table.getModel();
            // Set header tool tip
            String tooltipString = model.getDisplayedColumns()[tableColumn.getModelIndex()].getTooltip();
            if (component instanceof JComponent) {
                ((JComponent) component).setToolTipText(tooltipString);
            }
            return component;
        });

        setAutoResizeMode(AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);

        if (fixedColumnsCount > 0) {
            fixed = new JTable(getModel());
            fixed.setFocusable(false);
            fixed.setSelectionModel(getSelectionModel());
            fixed.getTableHeader().setReorderingAllowed(false);
            fixed.setSelectionModel(getSelectionModel());

            // Remove the non-fixed columns from the fixed table
            while (fixed.getColumnCount() > fixedColumnsCount) {
                TableColumnModel columnModel = fixed.getColumnModel();
                columnModel.removeColumn(columnModel.getColumn(fixedColumnsCount));
            }

            // Remove the fixed columns from the main table
            int width = 0;
            int i = 0;
            while (i < fixedColumnsCount) {
                TableColumnModel columnModel = getColumnModel();
                TableColumn column = columnModel.getColumn(0);
                width += column.getPreferredWidth();
                columnModel.removeColumn(column);
                i++;
            }

            // Sync scroll bars of both tables
            JScrollPane fixedScrollPane = new JScrollPane(fixed);
            JScrollPane rightScrollPane = new JScrollPane(this);
            fixedScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            JScrollBar fixedScrollBar = fixedScrollPane.getVerticalScrollBar();
            JScrollBar rightScrollBar = rightScrollPane.getVerticalScrollBar();

            // setVisible(false) does not have an effect, so we set the size to
            // false. We can't disable the scrollbar with VERTICAL_SCROLLBAR_NEVER
            // because this will disable mouse wheel scrolling.
            fixedScrollBar.setPreferredSize(new Dimension(0, 0));

            // Synchronize vertical scrolling
            AdjustmentListener adjustmentListener = e -> {
                if (e.getSource() == rightScrollBar) {
                    fixedScrollBar.setValue(e.getValue());
                } else {
                    rightScrollBar.setValue(e.getValue());
                }
            };
            fixedScrollBar.addAdjustmentListener(adjustmentListener);
            rightScrollBar.addAdjustmentListener(adjustmentListener);
            rightScrollPane.getVerticalScrollBar().setModel(fixedScrollPane.getVerticalScrollBar().getModel());

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fixedScrollPane, rightScrollPane);
            if (width == 0)
                width = 60;
            this.dividerLocation = new HOConfigurationIntParameter("TableDividerLocation_" + tableModel.id, width);

            Integer divLoc = dividerLocation.getIntValue();
            if (divLoc != null) {
                splitPane.setDividerLocation(divLoc);
            }

            splitPane.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                String propertyName = evt.getPropertyName();
                if ("dividerLocation".equals(propertyName)) {
                    JSplitPane pane = (JSplitPane) evt.getSource();
                    dividerLocation.setIntValue(pane.getDividerLocation());
                }
            });

            containerComponent = new JPanel();
            containerComponent.setLayout(new BorderLayout());
            containerComponent.add(splitPane, BorderLayout.CENTER);
            tableModel.initTable(this);
        } else {
            // No fixed columns
            fixed = null;
            containerComponent = new JPanel();
            containerComponent.setLayout(new BorderLayout());
            containerComponent.add(new JScrollPane(this));
        }
    }

    /**
     * Set row selection interval of both tables synchronously
     * 
     * @param rowIndex0 one end of the interval
     * @param rowIndex1 the other end of the interval
     */
    @Override
    public void setRowSelectionInterval(int rowIndex0, int rowIndex1) {
        super.setRowSelectionInterval(rowIndex0, rowIndex1);
        if (fixed != null) {
            fixed.setRowSelectionInterval(rowIndex0, rowIndex1);
        }
    }

    /**
     * The provided renderer is set to both internal tables
     * 
     * @param columnClass set the default cell renderer for this columnClass
     * @param renderer    default cell renderer to be used for this columnClass
     */
    @Override
    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        super.setDefaultRenderer(columnClass, renderer);
        if (fixed != null) {
            fixed.setDefaultRenderer(columnClass, renderer);
        }
    }

    public int getSelectedModelIndex() {
        int viewRowIndex = getSelectedRow();
        if (viewRowIndex > -1) {
            return convertRowIndexToModel(viewRowIndex);
        }
        return -1;
    }

    public void selectModelIndex(int modelIndex) {
        if (modelIndex > -1) {
            int viewRowIndex = convertRowIndexToView(modelIndex);
            setRowSelectionInterval(viewRowIndex, viewRowIndex);
        }
    }

    /**
     * Add a list selection listener
     * 
     * @param listener ListSelectionListener
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        ListSelectionModel rowSM = getSelectionModel();
        rowSM.addListSelectionListener(listener);
    }

    /**
     * Set the row sorter to both internal tables
     * 
     * @param sorter Sorter
     */
    @Override
    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        super.setRowSorter(sorter);
        if (fixed != null) {
            fixed.setRowSorter(sorter);
        }
    }

    /**
     * Returns the outer container component of the fixed column table
     * 
     * @return Component
     */
    public Component getContainerComponent() {
        return this.containerComponent;
    }

    @Override
    public TableColumn getColumn(Object identifier) {
        try {
            return super.getColumn(identifier);
        } catch (IllegalArgumentException e) {
            if (fixed != null) {
                return fixed.getColumn(identifier);
            }
            throw e;
        }
    }

    /**
     * Return th table column of the fixed or right hand side table
     * 
     * @param i Column index
     * @return TableColumn
     */
    public TableColumn getTableColumn(int i) {
        if (fixed != null && i < fixedColumnsCount) {
            return fixed.getColumnModel().getColumn(i);
        }
        return super.getColumnModel().getColumn(i - fixedColumnsCount);
    }

    /**
     * Return the user column of the event
     */
    public UserColumn getUserColumn(TableModelEvent e) {
        if (e.getColumn() >= 0 && e.getSource().equals(getModel())) {
            int modelIndex = convertColumnIndexToModel(e.getColumn());
            if (modelIndex > -1) {
                HOTableModel hoTableModel = (HOTableModel) getModel();
                return hoTableModel.getDisplayedColumns()[modelIndex];
            }
        }
        return null;
    }

    public int getFixedColumnsCount() {
        return fixedColumnsCount;
    }
}
