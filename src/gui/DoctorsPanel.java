package gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import core.Doctor;
import core.Validator;

public class DoctorsPanel extends JPanel {
    private JTextField firstNameText, lastNameText, phoneText, emailText;
    private JComboBox<String> specialtyComboBox;
    private DefaultTableModel doctorModel;
    private JTable doctorTable;
    private String[] specialties = {"Cardiology", "Dermatology", "Emergency Medicine", "Family Medicine", "Neurology", "Pediatrics", "Radiology"};

    public DoctorsPanel() {
        setLayout(new BorderLayout());

        JPanel doctorInputPanel = new JPanel(new GridLayout(6, 2));
        add(doctorInputPanel, BorderLayout.NORTH);

        JLabel doctorFirstNameLabel = new JLabel("First Name");
        firstNameText = new JTextField(20);
        doctorInputPanel.add(doctorFirstNameLabel);
        doctorInputPanel.add(firstNameText);

        JLabel doctorLastNameLabel = new JLabel("Last Name");
        lastNameText = new JTextField(20);
        doctorInputPanel.add(doctorLastNameLabel);
        doctorInputPanel.add(lastNameText);

        JLabel doctorPhoneLabel = new JLabel("Phone Number");
        phoneText = new JTextField(20);
        doctorInputPanel.add(doctorPhoneLabel);
        doctorInputPanel.add(phoneText);

        JLabel doctorEmailLabel = new JLabel("Email");
        emailText = new JTextField(20);
        doctorInputPanel.add(doctorEmailLabel);
        doctorInputPanel.add(emailText);

        JLabel specialtyLabel = new JLabel("Specialty");
        specialtyComboBox = new JComboBox<>(specialties);
        doctorInputPanel.add(specialtyLabel);
        doctorInputPanel.add(specialtyComboBox);

        JButton addDoctorButton = new JButton("Add Doctor");
        doctorInputPanel.add(addDoctorButton);

        JButton viewDoctorsButton = new JButton("View All Doctors");
        doctorInputPanel.add(viewDoctorsButton);

        doctorModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        doctorTable = new JTable(doctorModel);
        doctorModel.addColumn("ID");
        doctorModel.addColumn("First Name");
        doctorModel.addColumn("Last Name");
        doctorModel.addColumn("Phone Number");
        doctorModel.addColumn("Email");
        doctorModel.addColumn("Specialty");

        // Set JComboBox as the editor for the "Specialty" column
        TableColumn specialtyColumn = doctorTable.getColumnModel().getColumn(5);
        specialtyColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(specialties)));

        JScrollPane doctorScrollPane = new JScrollPane(doctorTable);
        add(doctorScrollPane, BorderLayout.CENTER);

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
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String phoneNumber = phoneText.getText();
                String email = emailText.getText();
                String specialty = (String) specialtyComboBox.getSelectedItem();
                DoctorService service = new DoctorService();

                if (!Validator.isValidPersonInput(firstName, lastName, phoneNumber, email)) {
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
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
                if (!Validator.isValidPersonInput(firstName, lastName, phoneNumber, email)) {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                    viewDoctorsButton.doClick();
                    return;
                }

                DoctorService service = new DoctorService();
                service.updateDoctor(doctorId, firstName, lastName, phoneNumber, email, specialty);
                JOptionPane.showMessageDialog(null, "Doctor updated successfully!");
            }
        });
    }
    public void highlightDoctorById(int doctorId) {
        for (int row = 0; row < doctorTable.getRowCount(); row++) {
            if ((int) doctorTable.getValueAt(row, 0) == doctorId) {
                doctorTable.setRowSelectionInterval(row, row);
                doctorTable.scrollRectToVisible(new Rectangle(doctorTable.getCellRect(row, 0, true)));
                break;
            }
        }
    }
    public void scrollToDoctor(int doctorId) {
        for (int row = 0; row < doctorTable.getRowCount(); row++) {
            if ((int) doctorTable.getValueAt(row, 0) == doctorId) {
                doctorTable.setRowSelectionInterval(row, row);
                doctorTable.scrollRectToVisible(doctorTable.getCellRect(row, 0, true));
                break;
            }
        }
    }
}
