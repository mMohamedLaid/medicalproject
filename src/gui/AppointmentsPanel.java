package gui;

import core.Appointment;
import core.Doctor;
import core.Patient;
import core.Validator;
import database.AppointmentDAO;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AppointmentsPanel extends JPanel {
    private JTextField patientIdText;
    private JTextField doctorIdText;
    private JTextField appointmentDateText;
    private DefaultTableModel appointmentModel;
    private JTable appointmentTable;
    private AppointmentDAO appointmentDAO;
    private Set<Integer> validPatientIds;
    private Set<Integer> validDoctorIds;
    private JTabbedPane tabbedPane;
    private PatientsPanel patientsPanel;
    private DoctorsPanel doctorsPanel;

    public AppointmentsPanel(JTabbedPane tabbedPane, PatientsPanel patientsPanel, DoctorsPanel doctorsPanel) {
        this.appointmentDAO = new AppointmentDAO();
        this.validPatientIds = new PatientService().getAllPatients().stream().map(Patient::getPatientId).collect(Collectors.toSet());
        this.validDoctorIds = new DoctorService().getAllDoctors().stream().map(Doctor::getDoctorId).collect(Collectors.toSet());
        this.tabbedPane = tabbedPane;
        this.patientsPanel = patientsPanel;
        this.doctorsPanel = doctorsPanel;

        setLayout(new BorderLayout());

        JPanel appointmentInputPanel = new JPanel(new GridLayout(4, 2));
        add(appointmentInputPanel, BorderLayout.NORTH);

        JLabel patientIdLabel = new JLabel("Patient ID");
        patientIdText = new JTextField(20);
        appointmentInputPanel.add(patientIdLabel);
        appointmentInputPanel.add(patientIdText);

        JLabel doctorIdLabel = new JLabel("Doctor ID");
        doctorIdText = new JTextField(20);
        appointmentInputPanel.add(doctorIdLabel);
        appointmentInputPanel.add(doctorIdText);

        JLabel appointmentDateLabel = new JLabel("Appointment Date (dd-MM-yyyy HH:mm:ss)");
        appointmentDateText = new JTextField(20);
        appointmentInputPanel.add(appointmentDateLabel);
        appointmentInputPanel.add(appointmentDateText);

        JButton addAppointmentButton = new JButton("Add Appointment");
        appointmentInputPanel.add(addAppointmentButton);

        JButton viewAppointmentsButton = new JButton("View All Appointments");
        appointmentInputPanel.add(viewAppointmentsButton);

        appointmentModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        appointmentTable = new JTable(appointmentModel);
        appointmentModel.addColumn("ID");
        appointmentModel.addColumn("Patient ID");
        appointmentModel.addColumn("Doctor ID");
        appointmentModel.addColumn("Date");

        JScrollPane appointmentScrollPane = new JScrollPane(appointmentTable);
        add(appointmentScrollPane, BorderLayout.CENTER);

        // Disable column reordering
        appointmentTable.getTableHeader().setReorderingAllowed(false);

        // Add context menu for deleting rows and "Go to" functionality
        JPopupMenu appointmentContextMenu = new JPopupMenu();
        JMenuItem deleteAppointmentItem = new JMenuItem("Delete");
        appointmentContextMenu.add(deleteAppointmentItem);

        JMenuItem gotoPatientItem = new JMenuItem("Go to Patient");
        appointmentContextMenu.add(gotoPatientItem);
        JMenuItem gotoDoctorItem = new JMenuItem("Go to Doctor");
        appointmentContextMenu.add(gotoDoctorItem);

        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = appointmentTable.rowAtPoint(e.getPoint());
                    appointmentTable.setRowSelectionInterval(row, row);
                    appointmentContextMenu.show(appointmentTable, e.getX(), e.getY());
                }
            }
        });

        deleteAppointmentItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = appointmentTable.getSelectedRow();
                if (row != -1) {
                    int appointmentId = (int) appointmentTable.getValueAt(row, 0);
                    appointmentDAO.deleteAppointment(appointmentId);
                    appointmentModel.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Appointment deleted successfully!");
                }
            }
        });

        gotoPatientItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = appointmentTable.getSelectedRow();
                if (row != -1) {
                    try {
                        int patientId = Integer.parseInt(appointmentTable.getValueAt(row, 1).toString());
                        tabbedPane.setSelectedComponent(patientsPanel);
                        patientsPanel.highlightPatientById(patientId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid patient ID!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        gotoDoctorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = appointmentTable.getSelectedRow();
                if (row != -1) {
                    try {
                        int doctorId = Integer.parseInt(appointmentTable.getValueAt(row, 2).toString());
                        tabbedPane.setSelectedComponent(doctorsPanel);
                        doctorsPanel.highlightDoctorById(doctorId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid doctor ID!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        addAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int patientId = Integer.parseInt(patientIdText.getText());
                    int doctorId = Integer.parseInt(doctorIdText.getText());
                    LocalDateTime dateTime = LocalDateTime.parse(appointmentDateText.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                    if (!Validator.isValidAppointmentInput(patientId, doctorId, dateTime)) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!validPatientIds.contains(patientId) || !validDoctorIds.contains(doctorId)) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Appointment appointment = new Appointment(0, patientId, doctorId, dateTime);
                    appointmentDAO.insertAppointment(appointment);
                    JOptionPane.showMessageDialog(null, "Appointment added successfully!");
                    viewAppointmentsButton.doClick(); // Refresh the table
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appointmentModel.setRowCount(0);  // Clear existing data
                List<Appointment> appointments = appointmentDAO.selectAllAppointments();
                for (Appointment appointment : appointments) {
                    appointmentModel.addRow(new Object[]{appointment.getAppointmentId(), appointment.getPatient().getPatientId(), appointment.getDoctor().getDoctorId(), appointment.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))});
                }
            }
        });

        // Add key listener for Enter key to start editing
        appointmentTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = appointmentTable.getSelectedRow();
                    int column = appointmentTable.getSelectedColumn();
                    if (!appointmentTable.isEditing()) {
                        appointmentTable.editCellAt(row, column);
                        Component editor = appointmentTable.getEditorComponent();
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
                if (appointmentTable.isEditing()) {
                    TableCellEditor editor = appointmentTable.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the appointment table directly to the database
        appointmentTable.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int appointmentId = (int) appointmentTable.getValueAt(row, 0);
                int patientId;
                int doctorId;
                LocalDateTime dateTime;

                try {
                    patientId = Integer.parseInt(appointmentTable.getValueAt(row, 1).toString());
                    doctorId = Integer.parseInt(appointmentTable.getValueAt(row, 2).toString());
                    dateTime = LocalDateTime.parse(appointmentTable.getValueAt(row, 3).toString(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                    // Validate inputs
                    if (!Validator.isValidAppointmentInput(patientId, doctorId, dateTime) || !validPatientIds.contains(patientId) || !validDoctorIds.contains(doctorId)) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                        viewAppointmentsButton.doClick(); // Refresh table data
                        return;
                    }

                    Appointment appointment = new Appointment(appointmentId, patientId, doctorId, dateTime);
                    appointmentDAO.updateAppointment(appointment);
                    JOptionPane.showMessageDialog(null, "Appointment updated successfully!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid IDs.", "Error", JOptionPane.ERROR_MESSAGE);
                    viewAppointmentsButton.doClick(); // Refresh table data
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    viewAppointmentsButton.doClick(); // Refresh table data
                }
            }
        });
    }
}
