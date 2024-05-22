package gui;

import core.MedicalRecord;
import core.Patient;
import core.Consultation;
import core.Validator;
import database.MedicalRecordDAO;
import gui.PatientService;
import gui.ConsultationService;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MedicalRecordsPanel extends JPanel {
    private JTextField patientIdText;
    private JTextField consultationIdText;
    private JTextField prescriptionsText;
    private JTextField medicalCertificateText;
    private DefaultTableModel medicalRecordModel;
    private JTable medicalRecordTable;
    private MedicalRecordDAO medicalRecordDAO;
    private Set<Integer> validPatientIds;
    private Set<Integer> validConsultationIds;

    public MedicalRecordsPanel() {
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.validPatientIds = new PatientService().getAllPatients().stream().map(Patient::getPatientId).collect(Collectors.toSet());
        this.validConsultationIds = new ConsultationService().getAllConsultations().stream().map(Consultation::getConsultationId).collect(Collectors.toSet());

        setLayout(new BorderLayout());

        JPanel medicalRecordInputPanel = new JPanel(new GridLayout(5, 2));
        add(medicalRecordInputPanel, BorderLayout.NORTH);

        JLabel patientIdLabel = new JLabel("Patient ID");
        patientIdText = new JTextField(20);
        medicalRecordInputPanel.add(patientIdLabel);
        medicalRecordInputPanel.add(patientIdText);

        JLabel consultationIdLabel = new JLabel("Consultation ID");
        consultationIdText = new JTextField(20);
        medicalRecordInputPanel.add(consultationIdLabel);
        medicalRecordInputPanel.add(consultationIdText);

        JLabel prescriptionsLabel = new JLabel("Prescriptions");
        prescriptionsText = new JTextField(20);
        medicalRecordInputPanel.add(prescriptionsLabel);
        medicalRecordInputPanel.add(prescriptionsText);

        JLabel medicalCertificateLabel = new JLabel("Medical Certificate");
        medicalCertificateText = new JTextField(20);
        medicalRecordInputPanel.add(medicalCertificateLabel);
        medicalRecordInputPanel.add(medicalCertificateText);

        JButton addMedicalRecordButton = new JButton("Add Medical Record");
        medicalRecordInputPanel.add(addMedicalRecordButton);

        JButton viewMedicalRecordsButton = new JButton("View All Medical Records");
        medicalRecordInputPanel.add(viewMedicalRecordsButton);

        medicalRecordModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        medicalRecordTable = new JTable(medicalRecordModel);
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

        deleteMedicalRecordItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = medicalRecordTable.getSelectedRow();
                if (row != -1) {
                    int recordId = (int) medicalRecordTable.getValueAt(row, 0);
                    medicalRecordDAO.deleteMedicalRecord(recordId);
                    medicalRecordModel.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Medical record deleted successfully!");
                }
            }
        });

        addMedicalRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                    Patient patient = new PatientService().getPatientById(patientId);
                    Consultation consultation = new ConsultationService().getConsultationById(consultationId);

                    MedicalRecord medicalRecord = new MedicalRecord(0, patient, consultation, prescriptions, medicalCertificate);
                    medicalRecordDAO.insertMedicalRecord(medicalRecord);
                    JOptionPane.showMessageDialog(null, "Medical record added successfully!");
                    viewMedicalRecordsButton.doClick(); // Refresh the table
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewMedicalRecordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                medicalRecordModel.setRowCount(0);  // Clear existing data
                List<MedicalRecord> medicalRecords = medicalRecordDAO.selectAllMedicalRecords();
                for (MedicalRecord medicalRecord : medicalRecords) {
                    medicalRecordModel.addRow(new Object[]{medicalRecord.getRecordId(), medicalRecord.getPatient().getPatientId(), medicalRecord.getConsultation().getConsultationId(), medicalRecord.getPrescriptions(), medicalRecord.getMedicalCertificate()});
                }
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
                int recordId = (int) medicalRecordTable.getValueAt(row, 0);
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

                    Patient patient = new PatientService().getPatientById(patientId);
                    Consultation consultation = new ConsultationService().getConsultationById(consultationId);

                    MedicalRecord medicalRecord = new MedicalRecord(recordId, patient, consultation, prescriptions, medicalCertificate);
                    medicalRecordDAO.updateMedicalRecord(medicalRecord);
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
}
