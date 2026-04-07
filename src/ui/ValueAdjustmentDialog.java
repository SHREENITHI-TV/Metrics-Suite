package ui;

import javax.swing.*;
import java.awt.*;

public class ValueAdjustmentDialog extends JDialog {

    private boolean approved = false;
    private final javax.swing.JComboBox<Integer>[] boxes;
    private int sum = 0;

    private static final String[] QUESTIONS = {
        "Does the system require reliable backup and recovery processes?",
        "Are specialized data communications required to transfer information to or from the application?",
        "Are there distributed processing functions?",
        "Is performance critical?",
        "Will the system run in an existing, heavily utilized operational environment?",
        "Does the system require online data entry?",
        "Does the online data entry require the input transaction to be built over multiple screens or operations?",
        "Are the internal logical files updated online?",
        "Are the inputs, outputs, files or inquiries complex?",
        "Is the internal processing complex?",
        "Is the code designed to be reusable?",
        "Are conversion and installation included in the design?",
        "Is the system designed for multiple installations in different organizations?",
        "Is the application designed to facilitate change and for ease of use by the user?"
    };

    public boolean isApproved() {
        return approved;
    }

    public int getSum() {
        return sum;
    }

    public int[] getValues() {
        int[] v = new int[14];
        for (int i = 0; i < 14; i++) {
            v[i] = (Integer) boxes[i].getSelectedItem();
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    public ValueAdjustmentDialog(Frame parent, boolean modal, int[] initialValues) {
        super(parent, modal);
        setTitle("Value Adjustment Factors");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        boxes = new JComboBox[14];

        JPanel grid = new JPanel(new GridLayout(14, 2, 10, 6));
        grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < 14; i++) {
            JLabel label = new JLabel(QUESTIONS[i]);
            JComboBox<Integer> cb = new JComboBox<>();
            for (int v = 0; v <= 5; v++) {
                cb.addItem(v);
            }

            int init = (initialValues != null && initialValues.length == 14) ? initialValues[i] : 0;
            if (init < 0 || init > 5) {
                init = 0;
            }
            cb.setSelectedItem(init);

            boxes[i] = cb;
            JPanel row = new JPanel(new BorderLayout());

            row.add(label, BorderLayout.CENTER);

            // Wrap combo box in a small panel to control its size
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            cb.setPreferredSize(new Dimension(50, 25));  // <-- adjust width here
            right.add(cb);

            row.add(right, BorderLayout.EAST);

            grid.add(row);
        }

        JButton done = new JButton("Done");
        JButton cancel = new JButton("Cancel");

        done.addActionListener(e -> {
            sum = 0;
            for (int i = 0; i < 14; i++) {
                sum += (Integer) boxes[i].getSelectedItem();
            }
            approved = true;
            dispose();
        });

        cancel.addActionListener(e -> {
            approved = false;
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        buttons.add(done);
        buttons.add(cancel);

        getContentPane().setLayout(new BorderLayout());
        //header at top
        JLabel header = new JLabel("<html><b>Assign a value from 0 to 5</b> for each of the following Value Adjustment Factors:</html>");
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(grid), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        pack();
        setLocationRelativeTo(parent);
    }
}
