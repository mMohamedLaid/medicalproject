package gui;

import core.MedicalRecord;
import core.Patient;
import core.Consultation;
import core.Validator;
import core.Doctor;
import database.MedicalRecordDAO;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicalRecordsPanel extends JPanel {
    private JTextField patientIdText;
    private JTextField consultationIdText;
    private JTextArea prescriptionsText;
    private JTextArea medicalCertificateText;
    private DefaultTableModel medicalRecordModel;
    private JTable medicalRecordTable;
    private MedicalRecordService medicalRecordService;
    private Set<Integer> validPatientIds;
    private Set<Integer> validConsultationIds;

    public MedicalRecordsPanel(JTabbedPane tabbedPane, PatientsPanel patientsPanel, ConsultationsPanel consultationsPanel) {
        this.medicalRecordService = new MedicalRecordService();
        this.validPatientIds = new PatientService().getAllPatients().stream().map(Patient::getPatientId).collect(Collectors.toSet());
        this.validConsultationIds = new ConsultationService().getAllConsultations().stream().map(Consultation::getConsultationId).collect(Collectors.toSet());

        setLayout(new BorderLayout());

        JPanel medicalRecordInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        add(medicalRecordInputPanel, BorderLayout.NORTH);

        JLabel patientIdLabel = new JLabel("Patient ID");
        patientIdText = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        medicalRecordInputPanel.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        medicalRecordInputPanel.add(patientIdText, gbc);

        JLabel consultationIdLabel = new JLabel("Consultation ID");
        consultationIdText = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        medicalRecordInputPanel.add(consultationIdLabel, gbc);
        gbc.gridx = 1;
        medicalRecordInputPanel.add(consultationIdText, gbc);

        JLabel prescriptionsLabel = new JLabel("Prescriptions");
        prescriptionsText = new JTextArea(3, 20);
        prescriptionsText.setLineWrap(true);
        prescriptionsText.setWrapStyleWord(true);
        JScrollPane prescriptionsScrollPane = new JScrollPane(prescriptionsText);
        gbc.gridx = 0;
        gbc.gridy = 2;
        medicalRecordInputPanel.add(prescriptionsLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        medicalRecordInputPanel.add(prescriptionsScrollPane, gbc);

        JLabel medicalCertificateLabel = new JLabel("Medical Certificate");
        medicalCertificateText = new JTextArea(3, 20);
        medicalCertificateText.setLineWrap(true);
        medicalCertificateText.setWrapStyleWord(true);
        JScrollPane medicalCertificateScrollPane = new JScrollPane(medicalCertificateText);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        medicalRecordInputPanel.add(medicalCertificateLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        medicalRecordInputPanel.add(medicalCertificateScrollPane, gbc);

        JButton addMedicalRecordButton = new JButton("Add Medical Record");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        medicalRecordInputPanel.add(addMedicalRecordButton, gbc);

        JButton viewMedicalRecordsButton = new JButton("View All Medical Records");
        gbc.gridy = 5;
        medicalRecordInputPanel.add(viewMedicalRecordsButton, gbc);

        medicalRecordModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        medicalRecordTable = new JTable(medicalRecordModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 3 || column == 4) {
                    return new TextAreaCellEditor();
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 3 || column == 4) {
                    return new TextAreaCellRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        medicalRecordModel.addColumn("ID");
        medicalRecordModel.addColumn("Patient ID");
        medicalRecordModel.addColumn("Consultation ID");
        medicalRecordModel.addColumn("Prescriptions");
        medicalRecordModel.addColumn("Medical Certificate");

        JScrollPane medicalRecordScrollPane = new JScrollPane(medicalRecordTable);
        add(medicalRecordScrollPane, BorderLayout.CENTER);

        // Disable column reordering
        medicalRecordTable.getTableHeader().setReorderingAllowed(false);

        // Add context menu for deleting rows
        JPopupMenu medicalRecordContextMenu = new JPopupMenu();
        JMenuItem deleteMedicalRecordItem = new JMenuItem("Delete");
        medicalRecordContextMenu.add(deleteMedicalRecordItem);

        // Right-click "goto" functionality
        JMenuItem gotoPatientItem = new JMenuItem("Go to Patient");
        medicalRecordContextMenu.add(gotoPatientItem);
        JMenuItem gotoConsultationItem = new JMenuItem("Go to Consultation");
        medicalRecordContextMenu.add(gotoConsultationItem);

        medicalRecordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = medicalRecordTable.rowAtPoint(e.getPoint());
                    medicalRecordTable.setRowSelectionInterval(row, row);
                    medicalRecordContextMenu.show(medicalRecordTable, e.getX(), e.getY());
                }
            }
        });

        gotoPatientItem.addActionListener(e -> {
            int row = medicalRecordTable.getSelectedRow();
            if (row != -1) {
                int patientId = Integer.parseInt(medicalRecordTable.getValueAt(row, 1).toString());
                tabbedPane.setSelectedComponent(patientsPanel);
                patientsPanel.scrollToPatient(patientId);
            }
        });

        gotoConsultationItem.addActionListener(e -> {
            int row = medicalRecordTable.getSelectedRow();
            if (row != -1) {
                int consultationId = Integer.parseInt(medicalRecordTable.getValueAt(row, 2).toString());
                tabbedPane.setSelectedComponent(consultationsPanel);
                consultationsPanel.scrollToConsultation(consultationId);
            }
        });

        deleteMedicalRecordItem.addActionListener(e -> {
            int row = medicalRecordTable.getSelectedRow();
            if (row != -1) {
                int recordId = Integer.parseInt(medicalRecordTable.getValueAt(row, 0).toString());
                medicalRecordService.deleteMedicalRecord(recordId);
                medicalRecordModel.removeRow(row);
                JOptionPane.showMessageDialog(null, "Medical record deleted successfully!");
            }
        });

        addMedicalRecordButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdText.getText());
                int consultationId = Integer.parseInt(consultationIdText.getText());
                String prescriptions = prescriptionsText.getText();
                String medicalCertificate = medicalCertificateText.getText();

                if (!Validator.isValidMedicalRecordInput(patientId, consultationId)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validPatientIds.contains(patientId) || !validConsultationIds.contains(consultationId)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                MedicalRecord medicalRecord = new MedicalRecord(0, new Patient(patientId, "", "", "", ""), new Consultation(consultationId, new Patient(patientId, "", "", "", ""), new Doctor(consultationId, "", "", "", "", ""), LocalDate.now(), "", ""), prescriptions, medicalCertificate);
                medicalRecordService.addMedicalRecord(medicalRecord);
                JOptionPane.showMessageDialog(null, "Medical record added successfully!");
                viewMedicalRecordsButton.doClick(); // Refresh the table
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewMedicalRecordsButton.addActionListener(e -> {
            medicalRecordModel.setRowCount(0);  // Clear existing data
            List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();
            for (MedicalRecord medicalRecord : medicalRecords) {
                medicalRecordModel.addRow(new Object[]{medicalRecord.getRecordId(), medicalRecord.getPatient().getPatientId(), medicalRecord.getConsultation().getConsultationId(), medicalRecord.getPrescriptions(), medicalRecord.getMedicalCertificate()});
            }
        });

        // Add key listener for Enter key to start editing
        medicalRecordTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = medicalRecordTable.getSelectedRow();
                    int column = medicalRecordTable.getSelectedColumn();
                    if (!medicalRecordTable.isEditing()) {
                        medicalRecordTable.editCellAt(row, column);
                        Component editor = medicalRecordTable.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                        e.consume();
                    }
                }
            }
        });

        // Add global mouse listener to confirm edit on click outside table
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (medicalRecordTable.isEditing()) {
                    TableCellEditor editor = medicalRecordTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the medical record table directly to the database
        medicalRecordTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int recordId = Integer.parseInt(medicalRecordTable.getValueAt(row, 0).toString());
                int patientId;
                int consultationId;
                String prescriptions;
                String medicalCertificate;

                try {
                    patientId = Integer.parseInt(medicalRecordTable.getValueAt(row, 1).toString());
                    consultationId = Integer.parseInt(medicalRecordTable.getValueAt(row, 2).toString());
                    prescriptions = medicalRecordTable.getValueAt(row, 3).toString();
                    medicalCertificate = medicalRecordTable.getValueAt(row, 4).toString();

                    // Validate inputs
                    if (!Validator.isValidMedicalRecordInput(patientId, consultationId) || !validPatientIds.contains(patientId) || !validConsultationIds.contains(consultationId)) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                        viewMedicalRecordsButton.doClick(); // Refresh table data
                        return;
                    }

                    MedicalRecord medicalRecord = new MedicalRecord(recordId, new Patient(patientId, "", "", "", ""), new Consultation(consultationId, new Patient(patientId, "", "", "", ""), new Doctor(consultationId, "", "", "", "", ""), LocalDate.now(), "", ""), prescriptions, medicalCertificate);
                    medicalRecordService.updateMedicalRecord(recordId, medicalRecord);
                    JOptionPane.showMessageDialog(null, "Medical record updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                    viewMedicalRecordsButton.doClick(); // Refresh table data
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    viewMedicalRecordsButton.doClick(); // Refresh table data
                }
            }
        });
    }

    class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JTextArea textArea;
        private JDialog dialog;
        private boolean confirmed;

        @Override
        public Object getCellEditorValue() {
            return textArea.getText();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            textArea = new JTextArea(value != null ? value.toString() : "");
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));

            dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(table), "Edit Text", true);
            dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            confirmButton.addActionListener(e -> {
                confirmed = true;
                dialog.dispose();
            });

            cancelButton.addActionListener(e -> {
                confirmed = false;
                dialog.dispose();
            });

            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(table);
            dialog.setVisible(true);

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    confirmed = false;
                }
            });

            textArea.setCaretPosition(textArea.getDocument().getLength());

            return textArea;
        }

        @Override
        public boolean stopCellEditing() {
            if (confirmed) {
                return super.stopCellEditing();
            } else {
                cancelCellEditing();
                return false;
            }
        }
    }

    class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setToolTipText(getText().length() > 20 ? getText().substring(0, 20) + "..." : getText());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            String[] lines = getText().split("\n");
            setText(lines.length > 0 ? lines[0] + (lines.length > 1 ? "..." : "") : "");
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }
}
