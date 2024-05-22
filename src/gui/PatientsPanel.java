package gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import core.Patient;
import core.Validator;

public class PatientsPanel extends JPanel {
    private JTextField firstNameText, lastNameText, phoneText, emailText;
    private DefaultTableModel patientModel;
    private JTable patientTable;

    public PatientsPanel() {
        setLayout(new BorderLayout());

        JPanel patientInputPanel = new JPanel(new GridLayout(5, 2));
        add(patientInputPanel, BorderLayout.NORTH);

        JLabel firstNameLabel = new JLabel("First Name");
        firstNameText = new JTextField(20);
        patientInputPanel.add(firstNameLabel);
        patientInputPanel.add(firstNameText);

        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameText = new JTextField(20);
        patientInputPanel.add(lastNameLabel);
        patientInputPanel.add(lastNameText);

        JLabel phoneLabel = new JLabel("Phone Number");
        phoneText = new JTextField(20);
        patientInputPanel.add(phoneLabel);
        patientInputPanel.add(phoneText);

        JLabel emailLabel = new JLabel("Email");
        emailText = new JTextField(20);
        patientInputPanel.add(emailLabel);
        patientInputPanel.add(emailText);

        JButton addPatientButton = new JButton("Add Patient");
        patientInputPanel.add(addPatientButton);

        JButton viewPatientsButton = new JButton("View All Patients");
        patientInputPanel.add(viewPatientsButton);

        patientModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        patientTable = new JTable(patientModel);
        patientModel.addColumn("ID");
        patientModel.addColumn("First Name");
        patientModel.addColumn("Last Name");
        patientModel.addColumn("Phone Number");
        patientModel.addColumn("Email");

        JScrollPane patientScrollPane = new JScrollPane(patientTable);
        add(patientScrollPane, BorderLayout.CENTER);

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

                try {
                    service.addPatient(firstName, lastName, phoneNumber, email);
                    JOptionPane.showMessageDialog(null, "Patient added successfully!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
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
        addMouseListener(new MouseAdapter() {
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
                if (!Validator.isValidPersonInput(firstName, lastName, phoneNumber, email)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    viewPatientsButton.doClick();
                    return;
                }

                PatientService service = new PatientService();
                service.updatePatient(patientId, firstName, lastName, phoneNumber, email);
                JOptionPane.showMessageDialog(null, "Patient updated successfully!");
            }
        });
    }
    public void highlightPatientById(int patientId) {
        for (int row = 0; row < patientTable.getRowCount(); row++) {
            if ((int) patientTable.getValueAt(row, 0) == patientId) {
                patientTable.setRowSelectionInterval(row, row);
                patientTable.scrollRectToVisible(new Rectangle(patientTable.getCellRect(row, 0, true)));
                break;
            }
        }
    }
    public void scrollToPatient(int patientId) {
        for (int row = 0; row < patientTable.getRowCount(); row++) {
            if ((int) patientTable.getValueAt(row, 0) == patientId) {
                patientTable.setRowSelectionInterval(row, row);
                patientTable.scrollRectToVisible(patientTable.getCellRect(row, 0, true));
                break;
            }
        }
    }

}
