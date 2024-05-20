package gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import core.Patient;
import core.Doctor;
import database.PatientDAO;
import database.DoctorDAO;
import gui.PatientService;
import gui.DoctorService;

public class MainWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Medical Office Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Create a panel for patients
        JPanel patientsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Patients", patientsPanel);

        JPanel patientInputPanel = new JPanel(new GridLayout(5, 2));
        patientsPanel.add(patientInputPanel, BorderLayout.NORTH);

        JLabel firstNameLabel = new JLabel("First Name");
        JTextField firstNameText = new JTextField(20);
        patientInputPanel.add(firstNameLabel);
        patientInputPanel.add(firstNameText);

        JLabel lastNameLabel = new JLabel("Last Name");
        JTextField lastNameText = new JTextField(20);
        patientInputPanel.add(lastNameLabel);
        patientInputPanel.add(lastNameText);

        JLabel phoneLabel = new JLabel("Phone Number");
        JTextField phoneText = new JTextField(20);
        patientInputPanel.add(phoneLabel);
        patientInputPanel.add(phoneText);

        JLabel emailLabel = new JLabel("Email");
        JTextField emailText = new JTextField(20);
        patientInputPanel.add(emailLabel);
        patientInputPanel.add(emailText);

        JButton addPatientButton = new JButton("Add Patient");
        patientInputPanel.add(addPatientButton);

        JButton viewPatientsButton = new JButton("View All Patients");
        patientInputPanel.add(viewPatientsButton);

        DefaultTableModel patientModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        JTable patientTable = new JTable(patientModel);
        patientModel.addColumn("ID");
        patientModel.addColumn("First Name");
        patientModel.addColumn("Last Name");
        patientModel.addColumn("Phone Number");
        patientModel.addColumn("Email");

        JScrollPane patientScrollPane = new JScrollPane(patientTable);
        patientsPanel.add(patientScrollPane, BorderLayout.CENTER);

        // Disable column reordering
        patientTable.getTableHeader().setReorderingAllowed(false);

        // Add context menu for deleting rows
        JPopupMenu patientContextMenu = new JPopupMenu();
        JMenuItem deletePatientItem = new JMenuItem("Delete");
        patientContextMenu.add(deletePatientItem);

        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = patientTable.rowAtPoint(e.getPoint());
                    patientTable.setRowSelectionInterval(row, row);
                    patientContextMenu.show(patientTable, e.getX(), e.getY());
                }
            }
        });

        deletePatientItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    int patientId = (int) patientTable.getValueAt(row, 0);
                    PatientService service = new PatientService();
                    service.deletePatient(patientId);
                    patientModel.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Patient deleted successfully!");
                }
            }
        });

        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String phoneNumber = phoneText.getText();
                String email = emailText.getText();
                PatientService service = new PatientService();

                if (firstName.isEmpty() || lastName.isEmpty() || !phoneNumber.matches("\\d+") || !email.contains("@")) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                service.addPatient(firstName, lastName, phoneNumber, email);
                JOptionPane.showMessageDialog(null, "Patient added successfully!");
            }
        });

        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patientModel.setRowCount(0);  // Clear existing data
                PatientService service = new PatientService();
                List<Patient> patients = service.getAllPatients();
                for (Patient patient : patients) {
                    patientModel.addRow(new Object[]{patient.getPatientId(), patient.getFirstName(), patient.getLastName(), patient.getPhoneNumber(), patient.getEmail()});
                }
            }
        });

        // Add key listener for Enter key to start editing
        patientTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = patientTable.getSelectedRow();
                    int column = patientTable.getSelectedColumn();
                    if (!patientTable.isEditing()) {
                        patientTable.editCellAt(row, column);
                        Component editor = patientTable.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                        e.consume();
                    }
                }
            }
        });

        // Add global mouse listener to confirm edit on click outside table
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (patientTable.isEditing()) {
                    TableCellEditor editor = patientTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the patient table directly to the database
        patientTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int patientId = (int) patientTable.getValueAt(row, 0);
                String firstName = (String) patientTable.getValueAt(row, 1);
                String lastName = (String) patientTable.getValueAt(row, 2);
                String phoneNumber = (String) patientTable.getValueAt(row, 3);
                String email = (String) patientTable.getValueAt(row, 4);

                // Validate inputs
                if (firstName.isEmpty() || lastName.isEmpty() || !phoneNumber.matches("\\d+") || !email.contains("@")) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PatientService service = new PatientService();
                service.updatePatient(patientId, firstName, lastName, phoneNumber, email);
                JOptionPane.showMessageDialog(null, "Patient updated successfully!");
            }
        });

        // Create a panel for doctors
        JPanel doctorsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Doctors", doctorsPanel);

        JPanel doctorInputPanel = new JPanel(new GridLayout(6, 2));
        doctorsPanel.add(doctorInputPanel, BorderLayout.NORTH);

        JLabel doctorFirstNameLabel = new JLabel("First Name");
        JTextField doctorFirstNameText = new JTextField(20);
        doctorInputPanel.add(doctorFirstNameLabel);
        doctorInputPanel.add(doctorFirstNameText);

        JLabel doctorLastNameLabel = new JLabel("Last Name");
        JTextField doctorLastNameText = new JTextField(20);
        doctorInputPanel.add(doctorLastNameLabel);
        doctorInputPanel.add(doctorLastNameText);

        JLabel doctorPhoneLabel = new JLabel("Phone Number");
        JTextField doctorPhoneText = new JTextField(20);
        doctorInputPanel.add(doctorPhoneLabel);
        doctorInputPanel.add(doctorPhoneText);

        JLabel doctorEmailLabel = new JLabel("Email");
        JTextField doctorEmailText = new JTextField(20);
        doctorInputPanel.add(doctorEmailLabel);
        doctorInputPanel.add(doctorEmailText);

        JLabel specialtyLabel = new JLabel("Specialty");
        JTextField specialtyText = new JTextField(20);
        doctorInputPanel.add(specialtyLabel);
        doctorInputPanel.add(specialtyText);

        JButton addDoctorButton = new JButton("Add Doctor");
        doctorInputPanel.add(addDoctorButton);

        JButton viewDoctorsButton = new JButton("View All Doctors");
        doctorInputPanel.add(viewDoctorsButton);

        DefaultTableModel doctorModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        JTable doctorTable = new JTable(doctorModel);
        doctorModel.addColumn("ID");
        doctorModel.addColumn("First Name");
        doctorModel.addColumn("Last Name");
        doctorModel.addColumn("Phone Number");
        doctorModel.addColumn("Email");
        doctorModel.addColumn("Specialty");

        JScrollPane doctorScrollPane = new JScrollPane(doctorTable);
        doctorsPanel.add(doctorScrollPane, BorderLayout.CENTER);

        // Disable column reordering
        doctorTable.getTableHeader().setReorderingAllowed(false);

        // Add context menu for deleting rows
        JPopupMenu doctorContextMenu = new JPopupMenu();
        JMenuItem deleteDoctorItem = new JMenuItem("Delete");
        doctorContextMenu.add(deleteDoctorItem);

        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = doctorTable.rowAtPoint(e.getPoint());
                    doctorTable.setRowSelectionInterval(row, row);
                    doctorContextMenu.show(doctorTable, e.getX(), e.getY());
                }
            }
        });

        deleteDoctorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = doctorTable.getSelectedRow();
                if (row != -1) {
                    int doctorId = (int) doctorTable.getValueAt(row, 0);
                    DoctorService service = new DoctorService();
                    service.deleteDoctor(doctorId);
                    doctorModel.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Doctor deleted successfully!");
                }
            }
        });

        addDoctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = doctorFirstNameText.getText();
                String lastName = doctorLastNameText.getText();
                String phoneNumber = doctorPhoneText.getText();
                String email = doctorEmailText.getText();
                String specialty = specialtyText.getText();
                DoctorService service = new DoctorService();

                if (firstName.isEmpty() || lastName.isEmpty() || !phoneNumber.matches("\\d+") || !email.contains("@") || specialty.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                service.addDoctor(firstName, lastName, phoneNumber, email, specialty);
                JOptionPane.showMessageDialog(null, "Doctor added successfully!");
            }
        });

        viewDoctorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doctorModel.setRowCount(0);  // Clear existing data
                DoctorService service = new DoctorService();
                List<Doctor> doctors = service.getAllDoctors();
                for (Doctor doctor : doctors) {
                    doctorModel.addRow(new Object[]{doctor.getDoctorId(), doctor.getFirstName(), doctor.getLastName(), doctor.getPhoneNumber(), doctor.getEmail(), doctor.getSpecialty()});
                }
            }
        });

        // Add key listener for Enter key to start editing
        doctorTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = doctorTable.getSelectedRow();
                    int column = doctorTable.getSelectedColumn();
                    if (!doctorTable.isEditing()) {
                        doctorTable.editCellAt(row, column);
                        Component editor = doctorTable.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                        e.consume();
                    }
                }
            }
        });

        // Add global mouse listener to confirm edit on click outside table
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (patientTable.isEditing()) {
                    TableCellEditor editor = patientTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
                if (doctorTable.isEditing()) {
                    TableCellEditor editor = doctorTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the doctor table directly to the database
        doctorTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int doctorId = (int) doctorTable.getValueAt(row, 0);
                String firstName = (String) doctorTable.getValueAt(row, 1);
                String lastName = (String) doctorTable.getValueAt(row, 2);
                String phoneNumber = (String) doctorTable.getValueAt(row, 3);
                String email = (String) doctorTable.getValueAt(row, 4);
                String specialty = (String) doctorTable.getValueAt(row, 5);

                // Validate inputs
                if (firstName.isEmpty() || lastName.isEmpty() || !phoneNumber.matches("\\d+") || !email.contains("@") || specialty.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DoctorService service = new DoctorService();
                service.updateDoctor(doctorId, firstName, lastName, phoneNumber, email, specialty);
                JOptionPane.showMessageDialog(null, "Doctor updated successfully!");
            }
        });

        // Add buttons for other tabs (Work in Progress)
        tabbedPane.addTab("Appointments", new JLabel("Work in Progress"));
        tabbedPane.addTab("Consultations", new JLabel("Work in Progress"));
        tabbedPane.addTab("Medical Records", new JLabel("Work in Progress"));

        frame.setVisible(true);
    }
}
