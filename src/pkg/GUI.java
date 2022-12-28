package pkg;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GUI extends javax.swing.JFrame {

    private static final String DEFAULT_PATH = "C:\\Users\\User\\Music";
    private static final String DEFAULT_DELIMITER = "_";
    private static final String APPLICATION_NAME = "FileManager";
    private static final int DEFAULT_START_INDEX = 1;
    private static final int DEFAULT_INCREMENT_BY = 1;
    private ButtonGroup buttonGroupSort;    
    private ButtonGroup buttonGroupFilter;   
    private ButtonGroup buttonGroupFilterPhrase; 
    private ButtonGroup buttonGroupFilterDelete;
    
    public GUI() {
        initComponents();
        
        this.setResizable(false);
        ImageIcon icon = new ImageIcon("fileIcon.png");
        this.setIconImage(icon.getImage());
        
        this.setLocationRelativeTo(null);
        
        buttonGroupSort = new ButtonGroup();
        buttonGroupSort.add(radioButtonSortLastModified);
        buttonGroupSort.add(radioButtonSortFileName);
        
        buttonGroupFilterPhrase = new ButtonGroup();
        buttonGroupFilterPhrase.add(radioButtonFilterPhraseText);
        buttonGroupFilterPhrase.add(radioButtonFilterPhraseNonAscii);
        
        buttonGroupFilter = new ButtonGroup();
        buttonGroupFilter.add(radioButtonFilterRemove);
        buttonGroupFilter.add(radioButtonFilterReplace);
        buttonGroupFilter.add(radioButtonFilterDelete);
        
        buttonGroupFilterDelete = new ButtonGroup();
        buttonGroupFilterDelete.add(radioButtonFilterDeleteWith);
        buttonGroupFilterDelete.add(radioButtonFilterDeleteWithout);
        
        clear();
    }

    private void clear() {
        textSourceDirectoryPath.setText(DEFAULT_PATH);
        textDestinationDirectoryPath.setText(DEFAULT_PATH);
        setRenameDefault();
        setFilterDefault();
        
        reset();
    }
    
    private void setRenameDefault(){
        textPrefix.setText(Helper.getDateOfToday());
        textIndexStart.setText(String.valueOf(DEFAULT_START_INDEX));
        textIncrementBy.setText(String.valueOf(DEFAULT_INCREMENT_BY));
        textSuffix.setText("");
        textDelimiter.setText(DEFAULT_DELIMITER);
        radioButtonSortLastModified.setSelected(true);
        checkboxSortReverse.setSelected(false);
        checkboxKeepFileName.setSelected(false);
        checkboxIndexWithZero.setSelected(false);
    }
    
    private void setFilterDefault(){
        radioButtonFilterPhraseText.setSelected(true);
        radioButtonFilterRemove.setSelected(true);
        radioButtonFilterDeleteWith.setSelected(true);
        textPhrase.setText("");
        textReplace.setText("");
    }

    private void reset(){
        updateTitle();
        setComponentsEditable(true);
    }
    
    private void setComponentsEditable(boolean b){
        textSourceDirectoryPath.setEditable(b);
        textDestinationDirectoryPath.setEditable(b);
        textPrefix.setEditable(b);
        textIndexStart.setEditable(b);
        textSuffix.setEditable(b);
        textDelimiter.setEditable(b);
        textPhrase.setEditable(b);
        textReplace.setEditable(b);
        textIncrementBy.setEditable(b);
        buttonRename.setEnabled(b);
        buttonRenameDefault.setEnabled(b);
        buttonMove.setEnabled(b);
        buttonFilterDefault.setEnabled(b);
        buttonFilter.setEnabled(b);
        buttonDelete.setEnabled(b);
        checkboxSortReverse.setEnabled(b);
        checkboxKeepFileName.setEnabled(b);
        checkboxIndexWithZero.setEnabled(b);
        radioButtonFilterPhraseText.setEnabled(b);
        radioButtonFilterPhraseNonAscii.setEnabled(b);
        radioButtonFilterDelete.setEnabled(b);
        radioButtonFilterDeleteWith.setEnabled(b);
        radioButtonFilterDeleteWithout.setEnabled(b);
        radioButtonFilterRemove.setEnabled(b);
        radioButtonFilterReplace.setEnabled(b);
        radioButtonSortLastModified.setEnabled(b);
        radioButtonSortFileName.setEnabled(b);
    }
    
    private void rename(){
        try {
            setComponentsEditable(false);

            String sourcePath = textSourceDirectoryPath.getText();
            String destinationPath = textDestinationDirectoryPath.getText();

            File sourceDirectory;
            try {
                sourceDirectory = parseSourceDirectory(sourcePath);
            } catch (Exception e) {
                reset();
                return;
            }
            File destinationDirectory;
            try {
                destinationDirectory = parseDestinationDirectory(destinationPath);
            } catch (Exception e) {
                reset();
                return;
            }
            
            int startIndex = DEFAULT_START_INDEX;
            try {
                startIndex = Integer.parseInt(textIndexStart.getText());
            } catch (NumberFormatException e) {
                errorMessage(this, "Invalid startIndex!");
                reset();
                return;
            }
            int incrementBy = DEFAULT_INCREMENT_BY;
            try {
                incrementBy = Integer.parseInt(textIncrementBy.getText());
            } catch (NumberFormatException e) {
                errorMessage(this, "Invalid incrementBy!");
                reset();
                return;
            }
            String prefix = textPrefix.getText();
            String suffix = textSuffix.getText();
            String delimiter = textDelimiter.getText();
            boolean keepFileName = checkboxKeepFileName.isSelected();
            boolean fillIndexWithZero = checkboxIndexWithZero.isSelected();
            
            File[] files = sourceDirectory.listFiles();
            if (files.length==0) {
                errorMessage(this, "Empty SourceDirectory!");
                reset();
                return;
            }
            if(radioButtonSortLastModified.isSelected()){
                if(checkboxSortReverse.isSelected()){
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                }else{
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                }
            }else if(radioButtonSortFileName.isSelected()){
                if(checkboxSortReverse.isSelected()){
                    Arrays.sort(files, new Comparator(){
                        @Override
                        public int compare(Object f1, Object f2) {
                            return ((File) f1).getName().compareTo(((File) f2).getName());
                        }
                    }.reversed());
                }else{
                    Arrays.sort(files, new Comparator(){
                        @Override
                        public int compare(Object f1, Object f2) {
                            return ((File) f1).getName().compareTo(((File) f2).getName());
                        }
                    });
                }
            }
            
            int maxFileIndex = (files.length-1)*incrementBy+startIndex;
            for (int i = 0; i < files.length; i++) {
                String newBase = "";
                newBase += prefix.isEmpty() ? "" : prefix + delimiter;
                int fileIndex = (i*incrementBy) + startIndex;
                newBase += fillIndexWithZero ? Helper.fillWithZeros(fileIndex, maxFileIndex) : String.valueOf(fileIndex);        
                newBase += keepFileName ? delimiter + Helper.getFileBase(files[i]) : "";
                newBase += suffix.isEmpty() ? "" : delimiter + suffix;
                Helper.renameElement(destinationDirectory, files[i], newBase);
                updateTitle((i+1), files.length);
            }
        } catch (Exception e) {
            errorMessage(this, "Error!");
        }
        reset();
    }
    
    private void filter(){
        try {
            setComponentsEditable(false);

            String sourcePath = textSourceDirectoryPath.getText();
            String destinationPath = textDestinationDirectoryPath.getText();

            File sourceDirectory;
            try {
                sourceDirectory = parseSourceDirectory(sourcePath);
            } catch (Exception e) {
                reset();
                return;
            }
            File destinationDirectory;
            try {
                destinationDirectory = parseDestinationDirectory(destinationPath);
            } catch (Exception e) {
                reset();
                return;
            }
            
            String phrase = textPhrase.getText();
            String replacement = textReplace.getText();
            
            File[] files = sourceDirectory.listFiles();
            if (files.length==0) {
                errorMessage(this, "Empty SourceDirectory!");
                reset();
                return;
            }
            if(radioButtonFilterRemove.isSelected()){
                for(int i = 0; i < files.length; i++) {
                    if(radioButtonFilterPhraseText.isSelected()){
                        Helper.replaceFileNamePhraseWith(destinationDirectory, files[i], phrase, "");
                    }else if(radioButtonFilterPhraseNonAscii.isSelected()){
                        Helper.replaceFileNameNonAsciiWith(destinationDirectory, files[i], "");
                    }
                    updateTitle((i+1), files.length);
                }
            }else if(radioButtonFilterReplace.isSelected()){
                for(int i = 0; i < files.length; i++) {
                    if(radioButtonFilterPhraseText.isSelected()){
                        Helper.replaceFileNamePhraseWith(destinationDirectory, files[i], phrase, replacement);
                    }else if(radioButtonFilterPhraseNonAscii.isSelected()){
                        Helper.replaceFileNameNonAsciiWith(destinationDirectory, files[i], replacement);
                    }
                    updateTitle((i+1), files.length);
                }
            }else if(radioButtonFilterDelete.isSelected()){
                boolean deleteContaining = true;
                if(radioButtonFilterDeleteWith.isSelected()){
                    deleteContaining = true;
                }else if(radioButtonFilterDeleteWithout.isSelected()){
                    deleteContaining = false;
                }
                for(int i = 0; i < files.length; i++) {
                    if(radioButtonFilterPhraseText.isSelected()){
                        Helper.deleteFileContainingPhrase(files[i], phrase, deleteContaining);
                    }else if(radioButtonFilterPhraseNonAscii.isSelected()){
                        Helper.deleteFileContainingNonAscii(files[i], deleteContaining);
                    }
                    updateTitle((i+1), files.length);
                }
            }
        } catch (Exception ex) {
            errorMessage(this, "Error!");
        }
        reset();
    }
    
    private void delete() {
        String sourcePath = textSourceDirectoryPath.getText();

        File sourceDirectory;
        try {
            sourceDirectory = parseSourceDirectory(sourcePath);
        } catch (Exception e) {
            reset();
            return;
        }
        
        File[] files = sourceDirectory.listFiles();
        if (files.length==0) {
            errorMessage(this, "Empty SourceDirectory!");
            reset();
            return;
        }
        
        int n = JOptionPane.showConfirmDialog(this, "Are you sure to delete all files and directories in sourceDirectory?");

        if(n==0){
            for (int i = 0; i < files.length; i++) {
                Helper.deleteElement(files[i]);
                updateTitle((i+1), files.length);
            }
        }
        reset();
    }
    
    private void move() {
        String sourcePath = textSourceDirectoryPath.getText();
        String destinationPath = textDestinationDirectoryPath.getText();

        File sourceDirectory;
        try {
            sourceDirectory = parseSourceDirectory(sourcePath);
        } catch (Exception e) {
            reset();
            return;
        }
        File destinationDirectory;
        try {
            destinationDirectory = parseDestinationDirectory(destinationPath);
        } catch (Exception e) {
            reset();
            return;
        }
            
        File[] files = sourceDirectory.listFiles();
        if (files.length==0) {
            errorMessage(this, "Empty SourceDirectory!");
            reset();
            return;
        }
        for (int i = 0; i < files.length; i++) {
            files[i].renameTo(new File(destinationDirectory, files[i].getName()));
            updateTitle((i+1), files.length);
        }
        reset();
    }
    
    private void updateTitle(){
        this.setTitle(APPLICATION_NAME);
    }
    
    private void updateTitle(int value, int max){
        this.setTitle(APPLICATION_NAME+" - "+value+" of "+max);
    }
    
    private void errorMessage(JFrame frame, String message){
        JOptionPane.showMessageDialog(frame, message);
    }
    
    private File parseSourceDirectory(String sourcePath) throws Exception{
        File sourceDirectory = new File(sourcePath);
        if (!sourceDirectory.isDirectory()) {
            errorMessage(this, "SourcePath is not a directory!");
            throw new Exception();
        }
        return sourceDirectory;
    }
    
    private File parseDestinationDirectory(String destinationPath) throws Exception{
        File destinationDirectory = new File(destinationPath);
        if (!destinationDirectory.isDirectory()) {
                int n = JOptionPane.showConfirmDialog(this, "Do you want to create the destinationDirectory?");
                if(n==0){
                    Helper.createDirectory(destinationDirectory);
                }else{
                    errorMessage(this, "DestinationPath is not a directory!");
                    throw new Exception();
                }
            }
        return destinationDirectory;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelDirectory = new javax.swing.JPanel();
        labelDestinationDirectoryPath = new javax.swing.JLabel();
        textDestinationDirectoryPath = new javax.swing.JTextField();
        textSourceDirectoryPath = new javax.swing.JTextField();
        labelSourceDirectoryPath = new javax.swing.JLabel();
        panelRename = new javax.swing.JPanel();
        textIncrementBy = new javax.swing.JTextField();
        labelIncrementBy = new javax.swing.JLabel();
        checkboxKeepFileName = new javax.swing.JCheckBox();
        checkboxIndexWithZero = new javax.swing.JCheckBox();
        textSuffix = new javax.swing.JTextField();
        labelDelimiter = new javax.swing.JLabel();
        textDelimiter = new javax.swing.JTextField();
        buttonRename = new javax.swing.JButton();
        buttonRenameDefault = new javax.swing.JButton();
        radioButtonSortLastModified = new javax.swing.JRadioButton();
        labelRenameOperations = new javax.swing.JLabel();
        labelPrefix = new javax.swing.JLabel();
        textPrefix = new javax.swing.JTextField();
        labelIndexStart = new javax.swing.JLabel();
        textIndexStart = new javax.swing.JTextField();
        labelSuffix = new javax.swing.JLabel();
        radioButtonSortFileName = new javax.swing.JRadioButton();
        checkboxSortReverse = new javax.swing.JCheckBox();
        panelFilter = new javax.swing.JPanel();
        textPhrase = new javax.swing.JTextField();
        buttonFilter = new javax.swing.JButton();
        labelPhrase = new javax.swing.JLabel();
        labelFilterOperations = new javax.swing.JLabel();
        radioButtonFilterReplace = new javax.swing.JRadioButton();
        radioButtonFilterDelete = new javax.swing.JRadioButton();
        radioButtonFilterDeleteWith = new javax.swing.JRadioButton();
        radioButtonFilterDeleteWithout = new javax.swing.JRadioButton();
        radioButtonFilterRemove = new javax.swing.JRadioButton();
        textReplace = new javax.swing.JTextField();
        buttonFilterDefault = new javax.swing.JButton();
        radioButtonFilterPhraseText = new javax.swing.JRadioButton();
        radioButtonFilterPhraseNonAscii = new javax.swing.JRadioButton();
        buttonDelete = new javax.swing.JButton();
        buttonMove = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(189, 224, 254));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(189, 224, 254));

        panelDirectory.setBackground(new java.awt.Color(189, 224, 254));

        labelDestinationDirectoryPath.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelDestinationDirectoryPath.setText("DestinationDirectoryPath");

        textDestinationDirectoryPath.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textDestinationDirectoryPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textDestinationDirectoryPathActionPerformed(evt);
            }
        });

        textSourceDirectoryPath.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textSourceDirectoryPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textSourceDirectoryPathActionPerformed(evt);
            }
        });

        labelSourceDirectoryPath.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelSourceDirectoryPath.setText("SourceDirectoryPath");

        javax.swing.GroupLayout panelDirectoryLayout = new javax.swing.GroupLayout(panelDirectory);
        panelDirectory.setLayout(panelDirectoryLayout);
        panelDirectoryLayout.setHorizontalGroup(
            panelDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDirectoryLayout.createSequentialGroup()
                        .addGroup(panelDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSourceDirectoryPath)
                            .addComponent(labelDestinationDirectoryPath))
                        .addGap(0, 561, Short.MAX_VALUE))
                    .addComponent(textSourceDirectoryPath)
                    .addComponent(textDestinationDirectoryPath, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelDirectoryLayout.setVerticalGroup(
            panelDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSourceDirectoryPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textSourceDirectoryPath, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDestinationDirectoryPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textDestinationDirectoryPath, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelRename.setBackground(new java.awt.Color(189, 224, 254));

        textIncrementBy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textIncrementBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textIncrementByActionPerformed(evt);
            }
        });

        labelIncrementBy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelIncrementBy.setText("IncrementBy");

        checkboxKeepFileName.setBackground(new java.awt.Color(189, 224, 254));
        checkboxKeepFileName.setText("KeepFileName");

        checkboxIndexWithZero.setBackground(new java.awt.Color(189, 224, 254));
        checkboxIndexWithZero.setText("IndexWithZero");

        textSuffix.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textSuffix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textSuffixActionPerformed(evt);
            }
        });

        labelDelimiter.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelDelimiter.setText("Delimiter");

        textDelimiter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textDelimiterActionPerformed(evt);
            }
        });

        buttonRename.setBackground(new java.awt.Color(93, 176, 252));
        buttonRename.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonRename.setForeground(new java.awt.Color(255, 255, 255));
        buttonRename.setText("Rename");
        buttonRename.setToolTipText("");
        buttonRename.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });

        buttonRenameDefault.setBackground(new java.awt.Color(93, 176, 252));
        buttonRenameDefault.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonRenameDefault.setForeground(new java.awt.Color(255, 255, 255));
        buttonRenameDefault.setText("Default");
        buttonRenameDefault.setToolTipText("");
        buttonRenameDefault.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonRenameDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameDefaultActionPerformed(evt);
            }
        });

        radioButtonSortLastModified.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonSortLastModified.setText("LastModified");

        labelRenameOperations.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelRenameOperations.setText("Operations");

        labelPrefix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPrefix.setText("Prefix");

        textPrefix.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPrefixActionPerformed(evt);
            }
        });

        labelIndexStart.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelIndexStart.setText("IndexStart");

        textIndexStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textIndexStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textIndexStartActionPerformed(evt);
            }
        });

        labelSuffix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelSuffix.setText("Suffix");

        radioButtonSortFileName.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonSortFileName.setText("FileName");

        checkboxSortReverse.setBackground(new java.awt.Color(189, 224, 254));
        checkboxSortReverse.setText("Reverse");

        panelFilter.setBackground(new java.awt.Color(189, 224, 254));

        textPhrase.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textPhrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPhraseActionPerformed(evt);
            }
        });

        buttonFilter.setBackground(new java.awt.Color(93, 176, 252));
        buttonFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonFilter.setForeground(new java.awt.Color(255, 255, 255));
        buttonFilter.setText("Filter");
        buttonFilter.setToolTipText("");
        buttonFilter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFilterActionPerformed(evt);
            }
        });

        labelPhrase.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelPhrase.setText("Phrase");

        labelFilterOperations.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelFilterOperations.setText("Operations");

        radioButtonFilterReplace.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterReplace.setText("Replace");
        radioButtonFilterReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFilterReplaceActionPerformed(evt);
            }
        });

        radioButtonFilterDelete.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterDelete.setText("Delete");

        radioButtonFilterDeleteWith.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterDeleteWith.setText("With");

        radioButtonFilterDeleteWithout.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterDeleteWithout.setText("Without");

        radioButtonFilterRemove.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterRemove.setText("Remove");

        textReplace.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(93, 176, 252)));
        textReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textReplaceActionPerformed(evt);
            }
        });

        buttonFilterDefault.setBackground(new java.awt.Color(93, 176, 252));
        buttonFilterDefault.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonFilterDefault.setForeground(new java.awt.Color(255, 255, 255));
        buttonFilterDefault.setText("Default");
        buttonFilterDefault.setToolTipText("");
        buttonFilterDefault.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonFilterDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFilterDefaultActionPerformed(evt);
            }
        });

        radioButtonFilterPhraseText.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterPhraseText.setText("Text");

        radioButtonFilterPhraseNonAscii.setBackground(new java.awt.Color(189, 224, 254));
        radioButtonFilterPhraseNonAscii.setText("Non-ASCII");

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addComponent(radioButtonFilterPhraseText)
                        .addGap(31, 31, 31)
                        .addComponent(textPhrase))
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addComponent(radioButtonFilterReplace)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textReplace))
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelPhrase)
                            .addGroup(panelFilterLayout.createSequentialGroup()
                                .addComponent(buttonFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonFilterDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(radioButtonFilterPhraseNonAscii)
                            .addComponent(radioButtonFilterRemove)
                            .addComponent(labelFilterOperations)
                            .addGroup(panelFilterLayout.createSequentialGroup()
                                .addComponent(radioButtonFilterDelete)
                                .addGap(18, 18, 18)
                                .addComponent(radioButtonFilterDeleteWith)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radioButtonFilterDeleteWithout)))
                        .addGap(0, 72, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(labelPhrase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textPhrase, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioButtonFilterPhraseText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonFilterPhraseNonAscii)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFilterOperations)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonFilterRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonFilterReplace)
                    .addComponent(textReplace, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonFilterDelete)
                    .addComponent(radioButtonFilterDeleteWith)
                    .addComponent(radioButtonFilterDeleteWithout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonFilterDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout panelRenameLayout = new javax.swing.GroupLayout(panelRename);
        panelRename.setLayout(panelRenameLayout);
        panelRenameLayout.setHorizontalGroup(
            panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRenameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRenameLayout.createSequentialGroup()
                        .addComponent(buttonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRenameDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(textSuffix, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textPrefix, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRenameLayout.createSequentialGroup()
                                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelIndexStart)
                                    .addComponent(textIndexStart, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelIncrementBy)
                                    .addComponent(textIncrementBy, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelDelimiter)
                                    .addComponent(textDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRenameLayout.createSequentialGroup()
                                .addComponent(checkboxKeepFileName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxIndexWithZero))
                            .addComponent(labelSuffix, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelPrefix, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelRenameOperations, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRenameLayout.createSequentialGroup()
                                .addComponent(radioButtonSortLastModified)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radioButtonSortFileName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxSortReverse)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );
        panelRenameLayout.setVerticalGroup(
            panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelRenameLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIndexStart)
                    .addComponent(labelDelimiter)
                    .addComponent(labelIncrementBy))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textIndexStart, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textIncrementBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPrefix)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSuffix)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(labelRenameOperations)
                .addGap(7, 7, 7)
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkboxKeepFileName)
                    .addComponent(checkboxIndexWithZero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonSortLastModified)
                    .addComponent(radioButtonSortFileName)
                    .addComponent(checkboxSortReverse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRenameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonRenameDefault, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonRename, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonDelete.setBackground(new java.awt.Color(93, 176, 252));
        buttonDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonDelete.setForeground(new java.awt.Color(255, 255, 255));
        buttonDelete.setText("Delete");
        buttonDelete.setToolTipText("");
        buttonDelete.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonMove.setBackground(new java.awt.Color(93, 176, 252));
        buttonMove.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonMove.setForeground(new java.awt.Color(255, 255, 255));
        buttonMove.setText("Move");
        buttonMove.setToolTipText("");
        buttonMove.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(34, 88, 112)));
        buttonMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonMove, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelRename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMove, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addComponent(panelRename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textSourceDirectoryPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textSourceDirectoryPathActionPerformed

    }//GEN-LAST:event_textSourceDirectoryPathActionPerformed

    private void textPrefixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPrefixActionPerformed

    }//GEN-LAST:event_textPrefixActionPerformed

    private void textIndexStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textIndexStartActionPerformed

    }//GEN-LAST:event_textIndexStartActionPerformed

    private void textSuffixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textSuffixActionPerformed

    }//GEN-LAST:event_textSuffixActionPerformed

    private void textDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textDelimiterActionPerformed

    }//GEN-LAST:event_textDelimiterActionPerformed

    private void buttonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameActionPerformed
        rename();
    }//GEN-LAST:event_buttonRenameActionPerformed

    private void buttonRenameDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameDefaultActionPerformed
        setRenameDefault();
    }//GEN-LAST:event_buttonRenameDefaultActionPerformed

    private void textDestinationDirectoryPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textDestinationDirectoryPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textDestinationDirectoryPathActionPerformed

    private void textIncrementByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textIncrementByActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textIncrementByActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        delete();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void textPhraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPhraseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textPhraseActionPerformed

    private void buttonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFilterActionPerformed
        filter();
    }//GEN-LAST:event_buttonFilterActionPerformed

    private void radioButtonFilterReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonFilterReplaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioButtonFilterReplaceActionPerformed

    private void textReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textReplaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textReplaceActionPerformed

    private void buttonMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveActionPerformed
        move();
    }//GEN-LAST:event_buttonMoveActionPerformed

    private void buttonFilterDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFilterDefaultActionPerformed
        setFilterDefault();
    }//GEN-LAST:event_buttonFilterDefaultActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonFilter;
    private javax.swing.JButton buttonFilterDefault;
    private javax.swing.JButton buttonMove;
    private javax.swing.JButton buttonRename;
    private javax.swing.JButton buttonRenameDefault;
    private javax.swing.JCheckBox checkboxIndexWithZero;
    private javax.swing.JCheckBox checkboxKeepFileName;
    private javax.swing.JCheckBox checkboxSortReverse;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelDelimiter;
    private javax.swing.JLabel labelDestinationDirectoryPath;
    private javax.swing.JLabel labelFilterOperations;
    private javax.swing.JLabel labelIncrementBy;
    private javax.swing.JLabel labelIndexStart;
    private javax.swing.JLabel labelPhrase;
    private javax.swing.JLabel labelPrefix;
    private javax.swing.JLabel labelRenameOperations;
    private javax.swing.JLabel labelSourceDirectoryPath;
    private javax.swing.JLabel labelSuffix;
    private javax.swing.JPanel panelDirectory;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelRename;
    private javax.swing.JRadioButton radioButtonFilterDelete;
    private javax.swing.JRadioButton radioButtonFilterDeleteWith;
    private javax.swing.JRadioButton radioButtonFilterDeleteWithout;
    private javax.swing.JRadioButton radioButtonFilterPhraseNonAscii;
    private javax.swing.JRadioButton radioButtonFilterPhraseText;
    private javax.swing.JRadioButton radioButtonFilterRemove;
    private javax.swing.JRadioButton radioButtonFilterReplace;
    private javax.swing.JRadioButton radioButtonSortFileName;
    private javax.swing.JRadioButton radioButtonSortLastModified;
    private javax.swing.JTextField textDelimiter;
    private javax.swing.JTextField textDestinationDirectoryPath;
    private javax.swing.JTextField textIncrementBy;
    private javax.swing.JTextField textIndexStart;
    private javax.swing.JTextField textPhrase;
    private javax.swing.JTextField textPrefix;
    private javax.swing.JTextField textReplace;
    private javax.swing.JTextField textSourceDirectoryPath;
    private javax.swing.JTextField textSuffix;
    // End of variables declaration//GEN-END:variables

}
