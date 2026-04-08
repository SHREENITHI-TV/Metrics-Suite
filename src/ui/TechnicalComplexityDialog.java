/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author bharg
 */
public class TechnicalComplexityDialog extends JDialog{
   
    private boolean approved = false;
    private final JComboBox<Integer>[] boxes;
    private int weightedSum = 0;
    private double tcf = 1.00;

    private static final String[] FACTORS = {
        "Distributed System",
        "Response/Performance Objectives",
        "End-user Efficiency",
        "Complex Internal Processing",
        "Reusability",
        "Easy to install",
        "Easy to use",
        "Portable",
        "Easy to change",//system maintenance
        "Concurrent",
        "Special Security Features",
        "Direct Access for External Data",
        "Special User Training"
    };

    private static final double[] WEIGHTS = {
        2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0
    };

    public boolean isApproved() {
        return approved;
    }

    public int[] getValues() {
        int[] v = new int[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            v[i] = (Integer) boxes[i].getSelectedItem();
        }
        return v;
    }

    public int getWeightedSum() {
        return weightedSum;
    }

    public double getTCF() {
        return tcf;
    }

    @SuppressWarnings("unchecked")
    public TechnicalComplexityDialog(Frame parent, boolean modal, int[] initialValues) {
        super(parent, modal);
        setTitle("Technical Complexity Factor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        boxes = new JComboBox[FACTORS.length];

        JPanel grid = new JPanel(new GridLayout(FACTORS.length, 2, 10, 6));
        grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < FACTORS.length; i++) {
            JLabel label = new JLabel(FACTORS[i] + " (weight " + WEIGHTS[i] + ")");
            JComboBox<Integer> cb = new JComboBox<>();
            for (int v = 0; v <= 5; v++) {
                cb.addItem(v);
            }

            int init = (initialValues != null && initialValues.length == FACTORS.length) ? initialValues[i] : 0;
            if (init < 0 || init > 5) {
                init = 0;
            }
            cb.setSelectedItem(init);

            boxes[i] = cb;
            grid.add(label);
            grid.add(cb);
        }

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            weightedSum = 0;

            for (int i = 0; i < boxes.length; i++) {
                int rating = (Integer) boxes[i].getSelectedItem();
                weightedSum += (int) Math.round(rating * WEIGHTS[i]);
            }

            double exactSum = 0.0;
            for (int i = 0; i < boxes.length; i++) {
                int rating = (Integer) boxes[i].getSelectedItem();
                exactSum += rating * WEIGHTS[i];
            }

            tcf = 0.6 + (0.01 * exactSum);

            approved = true;
            dispose();
        });

        cancel.addActionListener(e -> {
            approved = false;
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(ok);
        buttons.add(cancel);

        JLabel header = new JLabel(
                "<html><b>Assign a value from 0 to 5</b> for each Technical Complexity Factor.</html>"
        );
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