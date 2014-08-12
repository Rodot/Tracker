package tracker;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

public class PatternTable extends JTable {

    private static final long serialVersionUID = 7891649913732166602L;
    private static final String[] columnNames = {"Note/Cmd", "Note",
        "Duration", "Command", "X", "Y"};

    private static final String[] commandNames = {"0 Note Volume", "1 Instrument",
        "2 Volume Slide", "3 Arpeggio", "4 Tremolo"};
    private static final List<String> commandList = Arrays.asList(commandNames);

    private static final String[] noteNames = {"A#2", "B2", "C3", "C#3", "D3",
        "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4",
        "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4",
        "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5",
        "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6",
        "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D#7", "E7", "F#7",
        "G#7", "A#7", "C8", "D#8", "---"};
    private static final List<String> noteList = Arrays.asList(noteNames);
    public String patternName = "myPattern";

    public PatternTable() {
        super(new DefaultTableModel(columnNames, 32));
        tableSetup();
        setVisible(true);
    }

    private void tableSetup() {
        
        this.setShowGrid(false);
        this.setRowHeight(22);
        this.setFont(new Font("Consolas", Font.PLAIN, 11));
        PatternCellRenderer renderer = new PatternCellRenderer();
        this.setDefaultRenderer(Number.class, renderer);

        TableColumn col;
        // command/note
        col = getColumnModel().getColumn(0);
        col.setPreferredWidth(22);
        // pitch
        col = getColumnModel().getColumn(1);
        col.setPreferredWidth(40);
        col.setCellEditor(new SpinnerEditor(new SpinnerListModel(noteList)));
        // duration
        col = getColumnModel().getColumn(2);
        col.setPreferredWidth(40);
        col.setCellEditor(new SpinnerEditor(
                new SpinnerNumberModel(0, 0, 255, 1)));
        // command ID
        col = getColumnModel().getColumn(3);
        col.setPreferredWidth(100);
        col.setCellEditor(new SpinnerEditor(new SpinnerListModel(commandList)));
        // command X
        col = getColumnModel().getColumn(4);
        col.setPreferredWidth(40);
        col.setCellEditor(new SpinnerEditor(new SpinnerNumberModel(0, 0, 31, 1)));
        // command Y
        col = getColumnModel().getColumn(5);
        col.setPreferredWidth(40);
        col.setCellEditor(new SpinnerEditor(new SpinnerNumberModel(0, -16, 15,
                1)));

        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(false, i, 0);
            this.setValueAt("---", i, 1);
            this.setValueAt(0, i, 2);
            this.setValueAt(commandNames[0], i, 3);
            this.setValueAt(0, i, 4);
            this.setValueAt(0, i, 5);
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return Boolean.class;
        } else {
            return Number.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return true;
        }
        boolean isCommand = (boolean) this.getValueAt(row, 0);
        if (col <= 2) { // notes
            if (isCommand) {
                return false;
            } else {
                return true;
            }
        } else { // col > 2
            if (isCommand) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int col = e.getColumn();
        super.tableChanged(e);
        this.repaint();
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);
        final Component editor = getEditorComponent();
        if (editor == null || !(editor instanceof JTextComponent)) {
            return result;
        }
        if (e instanceof KeyEvent) {
            ((JTextComponent) editor).selectAll();
        }
        return result;
    }

    public String generateOutput() {
        String output = "";
        output = output.concat("const unsigned int ");
        output = output.concat(patternName);
        output = output.concat("[] PROGMEM = {");
        for (int row = 0; row < getRowCount(); row++) {
            int word = 0;
            boolean isCommand = (boolean) getValueAt(row, 0);
            if (isCommand) { //command
                int ID = commandList.indexOf((String) getValueAt(row,3));
                int X = (int) getValueAt(row, 4);
                int Y = 16 + (int) getValueAt(row, 5);
                word = Y;
                word <<= 5;
                word += X;
                word <<= 4;
                word += ID;
                word <<= 2;
                word ++; //set LSB to 1 to indicate it's a command
            } else { //note
                int pitch = noteList.indexOf((String) getValueAt(row,1));
                int duration = (int) getValueAt(row,2);
                if (duration == 0) continue;
                word = duration;
                word <<= 6;
                word += pitch;
                word <<= 2;
            }
            output = output.concat("0x");
            output = output.concat(Integer.toString(word, 16).toUpperCase());
            output = output.concat(",");
        }
        output = output.concat("0x000};");
        return output;
    }

}
