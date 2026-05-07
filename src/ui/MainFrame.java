package ui;

import java.io.File;

/**
 *
 * @author shree
 */
public class MainFrame extends javax.swing.JFrame {

    //ProjectDialogComponents
    private String projectName = null;
    private String productName = null;
    private String creatorName = null;
    private String projectComments = null;
    private String selectedLanguage = "None";
    private boolean dirty = false;

    private static class ClosedTabState {

        String title;
        String type;
        java.util.Properties props;

        ClosedTabState(String title, String type, java.util.Properties props) {
            this.title = title;
            this.type = type;
            this.props = props;
        }
    }

    private final java.util.List<ClosedTabState> closedTabs = new java.util.ArrayList<>();

    //Project Title Name 
    private void updateTitleBar() {
        String baseTitle;

        if (projectName == null || projectName.isBlank()) {
            baseTitle = "CECS 544 Metrics Suite";
        } else {
            baseTitle = "CECS 544 Metrics Suite - " + projectName;
        }

        if (dirty) {
            setTitle(baseTitle + " *");
        } else {
            setTitle(baseTitle);
        }
    }

    //Function Points TAbbed Pane Name
    private boolean tabTitleExists(String candidate) {
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            if (candidate.equals(jTabbedPane1.getTitleAt(i))) {
                return true;
            }
        }
        return false;
    }

    //Current Language in FP pane
    public void setSelectedLanguage(String lang) {
        selectedLanguage = (lang == null || lang.isBlank()) ? "None" : lang;

    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    //To Save the whole project along with FP metric files- 2 helper methods
    private static void putAllWithPrefix(java.util.Properties dest,
            String prefix,
            java.util.Properties src) {
        for (String k : src.stringPropertyNames()) {
            dest.setProperty(prefix + k, src.getProperty(k));
        }
    }

    private static java.util.Properties subProperties(java.util.Properties src,
            String prefix) {
        java.util.Properties out = new java.util.Properties();
        for (String k : src.stringPropertyNames()) {
            if (k.startsWith(prefix)) {
                out.setProperty(k.substring(prefix.length()), src.getProperty(k));
            }
        }
        return out;
    }

    private void updateMetricsMenuState() {
        boolean smiOpen = hasOpenPanelType("smi");

        // Still only one SMI allowed open at a time
        jMenuItem8.setEnabled(hasProject() && !smiOpen);

        // Reopen any closed tab
        jMenuItem10.setEnabled(hasProject() && hasClosedTabs());

        // Allow closing any currently selected supported tab
        int index = jTabbedPane1.getSelectedIndex();
        if (index < 0) {
            jMenuItem11.setEnabled(false);
        } else {
            String type = getPanelType(jTabbedPane1.getComponentAt(index));
            jMenuItem11.setEnabled(hasProject() && !"unknown".equals(type));
        }
    }

    private boolean hasOpenPanelType(String type) {
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            java.awt.Component c = jTabbedPane1.getComponentAt(i);
            if (type.equals(getPanelType(c))) {
                return true;
            }
        }
        return false;
    }

    private String getPanelType(java.awt.Component c) {
        if (c instanceof FunctionPointsPanel) {
            return "fp";
        }
        if (c instanceof UseCasePointsPanel) {
            return "ucp";
        }
        if (c instanceof SMIPanel) {
            return "smi";
        }
        return "unknown";
    }

    private java.util.Properties getPanelProperties(java.awt.Component c) {
        if (c instanceof FunctionPointsPanel fp) {
            return fp.toProperties();
        }
        if (c instanceof UseCasePointsPanel ucp) {
            return ucp.toProperties();
        }
        if (c instanceof SMIPanel smi) {
            return smi.toProperties();
        }
        return new java.util.Properties();
    }

    private java.awt.Component buildPanelFromType(String type, java.util.Properties props) {
        switch (type) {
            case "fp" -> {
                FunctionPointsPanel fp = new FunctionPointsPanel();
                fp.loadFromProperties(props);
                return fp;
            }
            case "ucp" -> {
                UseCasePointsPanel ucp = new UseCasePointsPanel();
                ucp.loadFromProperties(props);
                return ucp;
            }
            case "smi" -> {
                SMIPanel smi = new SMIPanel();
                smi.loadFromProperties(props);
                return smi;
            }
            default -> {
                return null;
            }
        }
    }

    private boolean hasClosedTabs() {
        return !closedTabs.isEmpty();
    }

    //diable metircs before creating a project
    private boolean hasProject() {
        return projectName != null && !projectName.isBlank();
    }

    private void updateMetricsAvailability() {
        boolean enabled = hasProject();
        jMenu4.setEnabled(enabled);
    }

    public void markDirty() {
        dirty = true;
        updateTitleBar();
    }

    private void markClean() {
        dirty = false;
        updateTitleBar();
    }

    private void clearProjectAfterDiscard() {
        projectName = null;
        productName = null;
        creatorName = null;
        projectComments = null;
        selectedLanguage = "None";

        jTabbedPane1.removeAll();
        closedTabs.clear();

        markClean();
        updateMetricsAvailability();
        updateMetricsMenuState();
    }

    private boolean confirmDiscardOrSave() {
        return confirmDiscardOrSave(false);
    }

    private boolean confirmDiscardOrSave(boolean clearOnDiscard) {
        if (!hasProject() || !dirty) {
            return true;
        }

        Object[] options = {"Save", "Discard Changes", "Cancel"};

        int choice = javax.swing.JOptionPane.showOptionDialog(
                this,
                "You have unsaved changes.",
                "Unsaved Changes",
                javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            jMenuItem3ActionPerformed(null); // Save
            return !dirty; // true only if save succeeded
        } else if (choice == 1) {
            if (clearOnDiscard) {
                clearProjectAfterDiscard();
            }
            return true; // discard
        } else {
            return false; // cancel
        }
    }

    public MainFrame() {
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (confirmDiscardOrSave()) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        jMenu5.setVisible(false);
        updateTitleBar();
        updateMetricsAvailability();
        updateMetricsMenuState();
        jTabbedPane1.addChangeListener(e -> updateMetricsMenuState());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CECS 544 Metrics Suite");

        jTabbedPane1.setName("mainTabbedPane"); // NOI18N

        jMenu1.setText("File");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Open");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Save");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator1);

        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem10.setText("Reopen Closed Tab");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuItem11.setText("Close Current Tab");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Preferences");

        jMenuItem5.setText("Language");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Metrics");
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });

        jMenu7.setText("Function Points");

        jMenuItem6.setText("Enter FP Data");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem6);

        jMenu4.add(jMenu7);

        jMenu10.setText("Use Case Points");

        jMenuItem9.setText("Enter UCP Data");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem9);

        jMenu4.add(jMenu10);

        jMenuItem8.setText("Software Maturity Index");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Project Code");
        jMenu5.setEnabled(false);
        jMenuBar1.add(jMenu5);

        jMenu6.setText("Help");
        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // New Project
        if (!confirmDiscardOrSave(true)) {
            return;
        }
        NewProjectJDialog dlg = new NewProjectJDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        if (!dlg.isApproved()) {
            return; // user cancelled
        }

// Save project metadata
        projectName = dlg.getProjectName();
        productName = dlg.getProductName();
        creatorName = dlg.getCreator();
        projectComments = dlg.getComments();

// Clear ALL panes/tabs for a truly new project
        jTabbedPane1.removeAll();
        closedTabs.clear();

// Update window title
        updateTitleBar();
//update metrics
        updateMetricsAvailability();
        //state      
        updateMetricsMenuState();
//Changes made
        markDirty();

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        LanguageDialog dlg = new LanguageDialog(this, true, selectedLanguage);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        String chose = dlg.getChosenLanguage();
        if (chose != null) {

            String oldLanguage = selectedLanguage;   // ← ADD THIS LINE

            setSelectedLanguage(chose);

            // update all open FP panels
            for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
                java.awt.Component c = jTabbedPane1.getComponentAt(i);
                if (c instanceof FunctionPointsPanel fp) {
                    fp.setCurrentLanguage(selectedLanguage);
                }
            }

            // mark dirty ONLY if language actually changed
            if (!java.util.Objects.equals(oldLanguage, selectedLanguage)) {
                markDirty();
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        if (!hasProject()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Create a project first.");
            return;
        }
        String name = javax.swing.JOptionPane.showInputDialog(
                this,
                "Enter name for the Function Points pane:",
                "New Function Points Pane",
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        // Cancel pressed
        if (name == null) {
            return;
        }

        name = name.trim();

        // Empty name -> default
        if (name.isEmpty()) {
            name = "Function Points";
        }

        // Avoid duplicate tab titles by appending (2), (3), ...
        String base = name;
        int suffix = 2;
        while (tabTitleExists(name)) {
            name = base + " (" + suffix + ")";
            suffix++;
        }

        // Create pane + set current language + add tab
        FunctionPointsPanel fp = new FunctionPointsPanel();
        fp.setCurrentLanguage(selectedLanguage);
        jTabbedPane1.addTab(name, fp);
        jTabbedPane1.setSelectedComponent(fp);
        markDirty();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        // Must have a project name (optional, but typical)
        if (projectName == null || projectName.isBlank()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Create a project first (File → New).");
            return;
        }

        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Save Project");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Metrics Suite (*.ms)", "ms"));

        String safeProject = projectName.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        chooser.setSelectedFile(new java.io.File(safeProject + ".ms"));

        int result = chooser.showSaveDialog(this);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        File dir = file.getParentFile();
        File target = new File(dir, safeProject + ".ms");

// If user chose a different name, inform them (optional)
        if (!target.getName().equalsIgnoreCase(file.getName())) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Saving project using the project name:\n" + target.getName(),
                    "Filename adjusted",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        }

        file = target;

        if (file.exists()) {
            int choice = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "File already exists:\n" + file.getName() + "\n\nReplace it?",
                    "Confirm overwrite",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            if (choice != javax.swing.JOptionPane.YES_OPTION) {
                return; // user cancelled overwrite
            }
        }

        java.util.Properties props = new java.util.Properties();

        // ---- project metadata (4.35 / 4.37) ----
        if (projectName != null) {
            props.setProperty("project.name", projectName);
        }
        if (productName != null) {
            props.setProperty("product.name", productName);
        }
        if (creatorName != null) {
            props.setProperty("creator.name", creatorName);
        }
        if (projectComments != null) {
            props.setProperty("project.comments", projectComments);
        }
        if (selectedLanguage != null) {
            props.setProperty("project.language", selectedLanguage);
        }

        // ---- panes (4.35) ----
        int paneCount = jTabbedPane1.getTabCount();
        props.setProperty("panes.count", String.valueOf(paneCount));
        for (int i = 0; i < paneCount; i++) {
            java.awt.Component c = jTabbedPane1.getComponentAt(i);
            String title = jTabbedPane1.getTitleAt(i);

            props.setProperty("pane." + i + ".title", title);

            switch (c) {
                case FunctionPointsPanel fp -> {
                    props.setProperty("pane." + i + ".type", "fp");

                    java.util.Properties paneProps = fp.toProperties();
                    putAllWithPrefix(props, "pane." + i + ".", paneProps);

                }
                case UseCasePointsPanel ucp -> {
                    props.setProperty("pane." + i + ".type", "ucp");

                    java.util.Properties paneProps = ucp.toProperties();
                    putAllWithPrefix(props, "pane." + i + ".", paneProps);

                }
                case SMIPanel smi -> {
                    props.setProperty("pane." + i + ".type", "smi");

                    java.util.Properties paneProps = smi.toProperties();
                    putAllWithPrefix(props, "pane." + i + ".", paneProps);
                }
                default ->
                    props.setProperty("pane." + i + ".type", "unknown");
            }
        }
        props.setProperty("closedTabs.count", String.valueOf(closedTabs.size()));

        for (int i = 0; i < closedTabs.size(); i++) {
            ClosedTabState state = closedTabs.get(i);
            props.setProperty("closed." + i + ".title", state.title);
            props.setProperty("closed." + i + ".type", state.type);
            putAllWithPrefix(props, "closed." + i + ".", state.props);
        }
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {
            props.store(out, "CECS 544 Metrics Suite Project");
            javax.swing.JOptionPane.showMessageDialog(this, "Saved: " + file.getName());
            markClean();
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        //Open Project
        if (!confirmDiscardOrSave(true)) {
            return;
        }
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Open Project");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Metrics Suite (*.ms)", "ms"));

        int result = chooser.showOpenDialog(this);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File file = chooser.getSelectedFile();

        java.util.Properties props = new java.util.Properties();
        try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
            props.load(in);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Open failed: " + ex.getMessage());
            return;
        }

        // ---- project metadata ----
        projectName = props.getProperty("project.name", "");
        productName = props.getProperty("product.name", "");
        creatorName = props.getProperty("creator.name", "");
        projectComments = props.getProperty("project.comments", "");
        selectedLanguage = props.getProperty("project.language", "None");
        updateTitleBar();
        updateMetricsAvailability();
        markClean();

        // ---- clear current tabs ----
        jTabbedPane1.removeAll();
        closedTabs.clear();

        // ---- load panes ----
        int paneCount = 0;
        try {
            paneCount = Integer.parseInt(props.getProperty("panes.count", "0"));
        } catch (NumberFormatException ignored) {
        }

        for (int i = 0; i < paneCount; i++) {
            String type = props.getProperty("pane." + i + ".type", "unknown");
            String title = props.getProperty("pane." + i + ".title", "Pane " + (i + 1));

            if ("fp".equals(type)) {
                FunctionPointsPanel fp = new FunctionPointsPanel();

                java.util.Properties paneProps = subProperties(props, "pane." + i + ".");
                fp.loadFromProperties(paneProps);

                jTabbedPane1.addTab(title, fp);

            } else if ("ucp".equals(type)) {
                UseCasePointsPanel ucp = new UseCasePointsPanel();

                java.util.Properties paneProps = subProperties(props, "pane." + i + ".");
                ucp.loadFromProperties(paneProps);

                jTabbedPane1.addTab(title, ucp);

            } else if ("smi".equals(type)) {
                SMIPanel smi = new SMIPanel();

                java.util.Properties paneProps = subProperties(props, "pane." + i + ".");
                smi.loadFromProperties(paneProps);

                jTabbedPane1.addTab(title, smi);

            }
        }
        int closedCount = 0;
        try {
            closedCount = Integer.parseInt(props.getProperty("closedTabs.count", "0"));
        } catch (NumberFormatException ignored) {
        }

        for (int i = 0; i < closedCount; i++) {
            String title = props.getProperty("closed." + i + ".title", "Closed Tab");
            String type = props.getProperty("closed." + i + ".type", "unknown");
            java.util.Properties paneProps = subProperties(props, "closed." + i + ".");
            closedTabs.add(new ClosedTabState(title, type, paneProps));
        }
        updateMetricsMenuState();
        if (jTabbedPane1.getTabCount() > 0) {
            jTabbedPane1.setSelectedIndex(0);
        }

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (confirmDiscardOrSave()) {
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        if (!hasProject()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Create a project first.");
            return;
        }
        String name = javax.swing.JOptionPane.showInputDialog(
                this,
                "Enter name for the Use Case Points pane:",
                "New Use Case Points Pane",
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (name == null) {
            return;
        }

        name = name.trim();

        if (name.isEmpty()) {
            name = "Use Case Points";
        }

        String base = name;
        int suffix = 2;
        while (tabTitleExists(name)) {
            name = base + " (" + suffix + ")";
            suffix++;
        }

        UseCasePointsPanel ucp = new UseCasePointsPanel();
        jTabbedPane1.addTab(name, ucp);
        jTabbedPane1.setSelectedComponent(ucp);
        markDirty();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed

    }//GEN-LAST:event_jMenu4ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        if (!hasProject()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Create a project first.");
            return;
        }

        if (hasOpenPanelType("smi")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Only one Software Maturity Index panel is allowed per project.");
            return;
        }

        SMIPanel smiPanel = new SMIPanel();

        jTabbedPane1.addTab("Software Maturity Index", smiPanel);
        jTabbedPane1.setSelectedComponent(smiPanel);
        markDirty();
        updateMetricsMenuState();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        if (!hasProject()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Create or open a project first.");
            return;
        }

        if (closedTabs.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No closed tab is available to reopen.");
            return;
        }

        ClosedTabState state = closedTabs.remove(closedTabs.size() - 1);

        // Keep the one-SMI-open rule
        if ("smi".equals(state.type) && hasOpenPanelType("smi")) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Only one Software Maturity Index panel is allowed per project.");
            closedTabs.add(state); // put it back
            return;
        }

        java.awt.Component panel = buildPanelFromType(state.type, state.props);
        if (panel == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to reopen this tab.");
            return;
        }

        String reopenTitle = state.title;
        String base = reopenTitle;
        int suffix = 2;
        while (tabTitleExists(reopenTitle)) {
            reopenTitle = base + " (" + suffix + ")";
            suffix++;
        }

        jTabbedPane1.addTab(reopenTitle, panel);
        jTabbedPane1.setSelectedComponent(panel);

        markDirty();
        updateMetricsMenuState();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed

        int index = jTabbedPane1.getSelectedIndex();
        if (index < 0) {
            return;
        }

        java.awt.Component c = jTabbedPane1.getComponentAt(index);
        String title = jTabbedPane1.getTitleAt(index);
        String type = getPanelType(c);

        if ("unknown".equals(type)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "This tab type cannot be closed and reopened.");
            return;
        }

        // Still prevent more than one open SMI, but closing it is fine
        java.util.Properties props = getPanelProperties(c);
        closedTabs.add(new ClosedTabState(title, type, props));

        jTabbedPane1.removeTabAt(index);
        markDirty();
        updateMetricsMenuState();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
