package gui;

import core.Consultation;
import core.Patient;
import core.Doctor;
import core.Validator;
import database.ConsultationDAO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




public class ConsultationsPanel extends JPanel {
    private JTextField patientIdText;
    private JTextField doctorIdText;
    private JTextField consultationDateText;
    private JTextArea diagnosisText;
    private JTextArea treatmentText;
    private DefaultTableModel consultationModel;
    private JTable consultationTable;
    private ConsultationService consultationService;
    private Set<Integer> validPatientIds;
    private Set<Integer> validDoctorIds;

    public ConsultationsPanel(JTabbedPane tabbedPane, PatientsPanel patientsPanel, DoctorsPanel doctorsPanel) {
        this.consultationService = new ConsultationService();
        this.validPatientIds = new PatientService().getAllPatients().stream().map(Patient::getPatientId).collect(Collectors.toSet());
        this.validDoctorIds = new DoctorService().getAllDoctors().stream().map(Doctor::getDoctorId).collect(Collectors.toSet());

        setLayout(new BorderLayout());

        JPanel consultationInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        add(consultationInputPanel, BorderLayout.NORTH);

        JLabel patientIdLabel = new JLabel("Patient ID");
        patientIdText = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        consultationInputPanel.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        consultationInputPanel.add(patientIdText, gbc);

        JLabel doctorIdLabel = new JLabel("Doctor ID");
        doctorIdText = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        consultationInputPanel.add(doctorIdLabel, gbc);
        gbc.gridx = 1;
        consultationInputPanel.add(doctorIdText, gbc);

        JLabel consultationDateLabel = new JLabel("Consultation Date (dd-MM-yyyy)");
        consultationDateText = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        consultationInputPanel.add(consultationDateLabel, gbc);
        gbc.gridx = 1;
        consultationInputPanel.add(consultationDateText, gbc);

        JLabel diagnosisLabel = new JLabel("Diagnosis");
        diagnosisText = new JTextArea(3, 20);
        diagnosisText.setLineWrap(true);
        diagnosisText.setWrapStyleWord(true);
        JScrollPane diagnosisScrollPane = new JScrollPane(diagnosisText);
        gbc.gridx = 0;
        gbc.gridy = 3;
        consultationInputPanel.add(diagnosisLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        consultationInputPanel.add(diagnosisScrollPane, gbc);

        JLabel treatmentLabel = new JLabel("Treatment");
        treatmentText = new JTextArea(3, 20);
        treatmentText.setLineWrap(true);
        treatmentText.setWrapStyleWord(true);
        JScrollPane treatmentScrollPane = new JScrollPane(treatmentText);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        consultationInputPanel.add(treatmentLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        consultationInputPanel.add(treatmentScrollPane, gbc);

        JButton addConsultationButton = new JButton("Add Consultation");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        consultationInputPanel.add(addConsultationButton, gbc);

        JButton viewConsultationsButton = new JButton("View All Consultations");
        gbc.gridy = 6;
        consultationInputPanel.add(viewConsultationsButton, gbc);

        consultationModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        consultationTable = new JTable(consultationModel);
        consultationModel.addColumn("ID");
        consultationModel.addColumn("Patient ID");
        consultationModel.addColumn("Doctor ID");
        consultationModel.addColumn("Date");
        consultationModel.addColumn("Diagnosis");
        consultationModel.addColumn("Treatment");

        JScrollPane consultationScrollPane = new JScrollPane(consultationTable);
        add(consultationScrollPane, BorderLayout.CENTER);

        // Disable column reordering
        consultationTable.getTableHeader().setReorderingAllowed(false);

        // Add context menu for deleting rows
        JPopupMenu consultationContextMenu = new JPopupMenu();
        JMenuItem deleteConsultationItem = new JMenuItem("Delete");
        consultationContextMenu.add(deleteConsultationItem);

        // Right-click "goto" functionality
        JMenuItem gotoPatientItem = new JMenuItem("Go to Patient");
        consultationContextMenu.add(gotoPatientItem);
        JMenuItem gotoDoctorItem = new JMenuItem("Go to Doctor");
        consultationContextMenu.add(gotoDoctorItem);

        consultationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = consultationTable.rowAtPoint(e.getPoint());
                    consultationTable.setRowSelectionInterval(row, row);
                    consultationContextMenu.show(consultationTable, e.getX(), e.getY());
                }
            }
        });

        gotoPatientItem.addActionListener(e -> {
            int row = consultationTable.getSelectedRow();
            if (row != -1) {
                int patientId = (int) consultationTable.getValueAt(row, 1);
                tabbedPane.setSelectedComponent(patientsPanel);
                patientsPanel.scrollToPatient(patientId);
            }
        });

        gotoDoctorItem.addActionListener(e -> {
            int row = consultationTable.getSelectedRow();
            if (row != -1) {
                int doctorId = (int) consultationTable.getValueAt(row, 2);
                tabbedPane.setSelectedComponent(doctorsPanel);
                doctorsPanel.scrollToDoctor(doctorId);
            }
        });

        deleteConsultationItem.addActionListener(e -> {
            int row = consultationTable.getSelectedRow();
            if (row != -1) {
                int consultationId = (int) consultationTable.getValueAt(row, 0);
                consultationService.deleteConsultation(consultationId);
                consultationModel.removeRow(row);
                JOptionPane.showMessageDialog(null, "Consultation deleted successfully!");
            }
        });

        addConsultationButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdText.getText());
                int doctorId = Integer.parseInt(doctorIdText.getText());
                LocalDate date = LocalDate.parse(consultationDateText.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String diagnosis = diagnosisText.getText();
                String treatment = treatmentText.getText();

                if (!Validator.isValidConsultationInput(patientId, doctorId, date)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validPatientIds.contains(patientId) || !validDoctorIds.contains(doctorId)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Consultation consultation = new Consultation(0, new Patient(patientId, "", "", "", ""), new Doctor(doctorId, "", "", "", "", ""), date, diagnosis, treatment);
                consultationService.addConsultation(consultation);
                JOptionPane.showMessageDialog(null, "Consultation added successfully!");
                viewConsultationsButton.doClick(); // Refresh the table
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewConsultationsButton.addActionListener(e -> {
            consultationModel.setRowCount(0);  // Clear existing data
            List<Consultation> consultations = consultationService.getAllConsultations();
            for (Consultation consultation : consultations) {
                consultationModel.addRow(new Object[]{consultation.getConsultationId(), consultation.getPatient().getPatientId(), consultation.getDoctor().getDoctorId(), consultation.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), consultation.getDiagnosis(), consultation.getTreatment()});
            }
        });

        // Add key listener for Enter key to start editing
        consultationTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = consultationTable.getSelectedRow();
                    int column = consultationTable.getSelectedColumn();
                    if (!consultationTable.isEditing()) {
                        consultationTable.editCellAt(row, column);
                        Component editor = consultationTable.getEditorComponent();
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
                if (consultationTable.isEditing()) {
                    TableCellEditor editor = consultationTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the consultation table directly to the database
        consultationTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int consultationId = (int) consultationTable.getValueAt(row, 0);
                int patientId;
                int doctorId;
                LocalDate date;
                String diagnosis;
                String treatment;

                try {
                    patientId = Integer.parseInt(consultationTable.getValueAt(row, 1).toString());
                    doctorId = Integer.parseInt(consultationTable.getValueAt(row, 2).toString());
                    date = LocalDate.parse(consultationTable.getValueAt(row, 3).toString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    diagnosis = consultationTable.getValueAt(row, 4).toString();
                    treatment = consultationTable.getValueAt(row, 5).toString();

                    // Validate inputs
                    if (!Validator.isValidConsultationInput(patientId, doctorId, date) || !validPatientIds.contains(patientId) || !validDoctorIds.contains(doctorId)) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                        viewConsultationsButton.doClick(); // Refresh table data
                        return;
                    }

                    Consultation consultation = new Consultation(consultationId, new Patient(patientId, "", "", "", ""), new Doctor(doctorId, "", "", "", "", ""), date, diagnosis, treatment);
                    consultationService.updateConsultation(consultationId,consultation);
                    JOptionPane.showMessageDialog(null, "Consultation updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                    viewConsultationsButton.doClick(); // Refresh table data
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    viewConsultationsButton.doClick(); // Refresh table data
                }
            }
        });
    }
}

