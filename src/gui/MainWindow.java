package gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import core.Patient;
import database.PatientDAO;

public class MainWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Medical Office Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        JLabel firstNameLabel = new JLabel("First Name");
        JTextField firstNameText = new JTextField(20);
        inputPanel.add(firstNameLabel);
        inputPanel.add(firstNameText);

        JLabel lastNameLabel = new JLabel("Last Name");
        JTextField lastNameText = new JTextField(20);
        inputPanel.add(lastNameLabel);
        inputPanel.add(lastNameText);

        JLabel phoneLabel = new JLabel("Phone Number");
        JTextField phoneText = new JTextField(20);
        inputPanel.add(phoneLabel);
        inputPanel.add(phoneText);

        JLabel emailLabel = new JLabel("Email");
        JTextField emailText = new JTextField(20);
        inputPanel.add(emailLabel);
        inputPanel.add(emailText);

        JButton addButton = new JButton("Add Patient");
        inputPanel.add(addButton);

        JButton viewButton = new JButton("View All Patients");
        inputPanel.add(viewButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID is not editable
            }
        };
        JTable table = new JTable(model);
        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Phone Number");
        model.addColumn("Email");

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add context menu for deleting rows
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        contextMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(row, row);
                    contextMenu.show(table, e.getX(), e.getY());
                }
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int patientId = (int) table.getValueAt(row, 0);
                    PatientDAO dao = new PatientDAO();
                    dao.deletePatient(patientId);
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Patient deleted successfully!");
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String phoneNumber = phoneText.getText();
                String email = emailText.getText();
                PatientDAO dao = new PatientDAO();
                dao.insertPatient(firstName, lastName, phoneNumber, email);
                JOptionPane.showMessageDialog(null, "Patient added successfully!");
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);  // Clear existing data
                PatientDAO dao = new PatientDAO();
                List<Patient> patients = dao.selectAllPatients();
                for (Patient patient : patients) {
                    model.addRow(new Object[]{patient.getPatientId(), patient.getFirstName(), patient.getLastName(), patient.getPhoneNumber(), patient.getEmail()});
                }
            }
        });

        // Add key listener for Enter key to start editing
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    int column = table.getSelectedColumn();
                    if (!table.isEditing()) {
                        table.editCellAt(row, column);
                        Component editor = table.getEditorComponent();
                        if (editor != null) {
                            editor.requestFocusInWindow();
                        }
                    }
                }
            }
        });

        // Add global mouse listener to confirm edit on click outside table
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.isEditing()) {
                    TableCellEditor editor = table.getCellEditor();
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }
            }
        });

        // Save changes made in the table directly to the database
        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int patientId = (int) table.getValueAt(row, 0);
                String firstName = (String) table.getValueAt(row, 1);
                String lastName = (String) table.getValueAt(row, 2);
                String phoneNumber = (String) table.getValueAt(row, 3);
                String email = (String) table.getValueAt(row, 4);
                PatientDAO dao = new PatientDAO();
                dao.updatePatient(patientId, firstName, lastName, phoneNumber, email);
                JOptionPane.showMessageDialog(null, "Patient updated successfully!");
            }
        });

        frame.setVisible(true);
    }
}
