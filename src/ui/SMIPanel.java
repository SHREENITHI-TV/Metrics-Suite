/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui;

import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author shree
 */
public class SMIPanel extends javax.swing.JPanel {

    private DefaultTableModel model;
    private static final int COL_SMI = 0;
    private static final int COL_ADDED = 1;
    private static final int COL_CHANGED = 2;
    private static final int COL_DELETED = 3;
    private static final int COL_TOTAL = 4;

    /**
     * Creates new form SMIPanel
     */
    public SMIPanel() {
        initComponents();
        setupTableModel();
        enableReplaceExistingValueOnEdit();
    }
    private boolean updatingTable = false;

    private void setupTableModel() {
        model = new DefaultTableModel(
                new Object[]{"SMI", "Modules Added", "Modules Changed", "Modules Deleted", "Total Modules"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                // SMI and Total Modules are computed, so they are not editable
                if (column == COL_SMI || column == COL_TOTAL) {
                    return false;
                }

                // First row restrictions:
                // Changed Modules and Deleted Modules should be 0 in the first row
                if (row == 0) {
                    if (column == COL_CHANGED || column == COL_DELETED) {
                        return false;
                    }
                }

                return true;
            }
        };

        tblSMI.setModel(model);
        model.setRowCount(0);

        model.addTableModelListener(e -> {
            if (updatingTable) {
                return;
            }

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0 || col < 0) {
                return;
            }

            // Only react when user edits input columns:
            // Modules Added, Modules Changed, Modules Deleted
            if (col == COL_ADDED || col == COL_CHANGED || col == COL_DELETED) {
                updatingTable = true;

                // Clear computed values for that row
                model.setValueAt("", row, COL_SMI);
                model.setValueAt("", row, COL_TOTAL);

                updatingTable = false;
                notifyDirty();
            }
        });
    }
    
    private void enableReplaceExistingValueOnEdit() {
    javax.swing.JTextField textField = new javax.swing.JTextField();

    javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(textField) {
        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {

            java.awt.Component component = super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);

            SwingUtilities.invokeLater(() -> {
                textField.requestFocusInWindow();
                textField.selectAll();
            });

            return component;
        }
    };

    editor.setClickCountToStart(1);

    tblSMI.setDefaultEditor(Object.class, editor);
    tblSMI.setSurrendersFocusOnKeystroke(true);
    tblSMI.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
}

    private int parseIntValue(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.toString().trim());
    }

    private String formatSMI(double smi) {
        if (smi == 0.0) {
            return "0.0";
        }

        java.text.DecimalFormat df = new java.text.DecimalFormat("0.0################");
        df.setRoundingMode(java.math.RoundingMode.HALF_UP);

        return df.format(smi);
    }

    public Properties toProperties() {
        Properties props = new Properties();

        props.setProperty("rowCount", String.valueOf(model.getRowCount()));

        for (int i = 0; i < model.getRowCount(); i++) {
            props.setProperty("row." + i + ".mt", String.valueOf(model.getValueAt(i, COL_TOTAL)));
            props.setProperty("row." + i + ".fc", String.valueOf(model.getValueAt(i, COL_CHANGED)));
            props.setProperty("row." + i + ".fa", String.valueOf(model.getValueAt(i, COL_ADDED)));
            props.setProperty("row." + i + ".fd", String.valueOf(model.getValueAt(i, COL_DELETED)));
            props.setProperty("row." + i + ".smi", String.valueOf(model.getValueAt(i, COL_SMI)));
        }

        return props;
    }

    public void loadFromProperties(Properties props) {
        model.setRowCount(0);

        int rowCount = Integer.parseInt(props.getProperty("rowCount", "0"));

        for (int i = 0; i < rowCount; i++) {
            Object mt = props.getProperty("row." + i + ".mt", "");
            Object fc = props.getProperty("row." + i + ".fc", "0");
            Object fa = props.getProperty("row." + i + ".fa", "0");
            Object fd = props.getProperty("row." + i + ".fd", "0");
            Object smi = props.getProperty("row." + i + ".smi", "");

            model.addRow(new Object[]{smi, fa, fc, fd, mt});
        }
    }

    private boolean lastRowComputed() {
        if (model.getRowCount() == 0) {
            return true;
        }

        int lastRow = model.getRowCount() - 1;

        Object smiValue = model.getValueAt(lastRow, COL_SMI);
        Object totalValue = model.getValueAt(lastRow, COL_TOTAL);

        String smi = (smiValue == null) ? "" : smiValue.toString().trim();
        String total = (totalValue == null) ? "" : totalValue.toString().trim();

        return !smi.isEmpty() && !total.isEmpty();
    }

    private void notifyDirty() {
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame frame) {
            frame.markDirty();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnComputeIndex = new javax.swing.JButton();
        btnAddRow = new javax.swing.JButton();
        scrollSMI = new javax.swing.JScrollPane();
        tblSMI = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(900, 600));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel1.setText("Software Maturity Index (SMI)");

        btnComputeIndex.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        btnComputeIndex.setText("Compute Index");
        btnComputeIndex.addActionListener(this::btnComputeIndexActionPerformed);

        btnAddRow.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        btnAddRow.setText("Add Row");
        btnAddRow.addActionListener(this::btnAddRowActionPerformed);

        tblSMI.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "SMI", "Modules Added", "Modules Changed", "Modules Deleted", "Total Modules"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSMI.setColumnSelectionAllowed(true);
        tblSMI.getTableHeader().setReorderingAllowed(false);
        scrollSMI.setViewportView(tblSMI);
        tblSMI.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(278, 278, 278))
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddRow)
                        .addGap(92, 92, 92)
                        .addComponent(btnComputeIndex))
                    .addComponent(scrollSMI, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(154, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(scrollSMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddRow)
                    .addComponent(btnComputeIndex))
                .addGap(63, 63, 63))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRowActionPerformed
        if (tblSMI.isEditing()) {
            tblSMI.getCellEditor().stopCellEditing();
        }
        if (!lastRowComputed()) {
            JOptionPane.showMessageDialog(this,
                    "Please click Compute Index before adding another row.");
            return;
        }
        int rowIndex = model.getRowCount();

        if (rowIndex == 0) {
            model.addRow(new Object[]{"", "", "0", "0", ""});
        } else {
            model.addRow(new Object[]{"", "", "", "", ""});
        }
        notifyDirty();
    }//GEN-LAST:event_btnAddRowActionPerformed

    private void btnComputeIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComputeIndexActionPerformed
        if (tblSMI.isEditing()) {
            tblSMI.getCellEditor().stopCellEditing();
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one row.");
            return;
        }

        try {
            int previousTotal = 0;

            for (int i = 0; i < model.getRowCount(); i++) {
                int fa = parseIntValue(model.getValueAt(i, COL_ADDED));   // Modules Added
                int fc = parseIntValue(model.getValueAt(i, COL_CHANGED)); // Modules Changed
                int fd = parseIntValue(model.getValueAt(i, COL_DELETED)); // Modules Deleted

                // First row should not have changed or deleted modules
                if (i == 0) {
                    fc = 0;
                    fd = 0;
                }

                if (fa < 0 || fc < 0 || fd < 0) {
                    JOptionPane.showMessageDialog(this, "Values cannot be negative.");
                    return;
                }

                int mt;

                if (i == 0) {
                    mt = fa;
                } else {
                    mt = previousTotal + fa - fd;
                }

                if (mt <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Total Modules must be greater than 0 at row " + (i + 1) + ".");
                    return;
                }

                double smi = (mt - (fa + fc + fd)) / (double) mt;

                updatingTable = true;

                model.setValueAt(formatSMI(smi), i, COL_SMI);
                model.setValueAt(fa, i, COL_ADDED);
                model.setValueAt(fc, i, COL_CHANGED);
                model.setValueAt(fd, i, COL_DELETED);
                model.setValueAt(mt, i, COL_TOTAL);

                updatingTable = false;

                previousTotal = mt;
            }

        } catch (NumberFormatException ex) {
            updatingTable = false;
            JOptionPane.showMessageDialog(this, "Please enter valid integer values in the table.");
        }

        notifyDirty();
    }//GEN-LAST:event_btnComputeIndexActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRow;
    private javax.swing.JButton btnComputeIndex;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane scrollSMI;
    private javax.swing.JTable tblSMI;
    // End of variables declaration//GEN-END:variables
}
