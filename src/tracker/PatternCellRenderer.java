package tracker;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PatternCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -3365332286593967965L;

    public PatternCellRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        if (table.isCellEditable(row, column)) {
            cellComponent.setForeground(Color.black);
            cellComponent.setBackground(Color.white);
        } else {
            cellComponent.setForeground(Color.lightGray);
            cellComponent.setBackground(Color.lightGray);
        }
        return cellComponent;
    }
}
