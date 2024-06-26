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
import javax.swing.table.TableCellRenderer;
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
        consultationTable = new JTable(consultationModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 4 || column == 5) {
                    return new TextAreaCellEditor();
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 4 || column == 5) {
                    return new TextAreaCellRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
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
                int patientId = Integer.parseInt(consultationTable.getValueAt(row, 1).toString());
                tabbedPane.setSelectedComponent(patientsPanel);
                patientsPanel.scrollToPatient(patientId);
            }
        });

        gotoDoctorItem.addActionListener(e -> {
            int row = consultationTable.getSelectedRow();
            if (row != -1) {
                int doctorId = Integer.parseInt(consultationTable.getValueAt(row, 2).toString());
                tabbedPane.setSelectedComponent(doctorsPanel);
                doctorsPanel.scrollToDoctor(doctorId);
            }
        });

        deleteConsultationItem.addActionListener(e -> {
            int row = consultationTable.getSelectedRow();
            if (row != -1) {
                int consultationId = Integer.parseInt(consultationTable.getValueAt(row, 0).toString());
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
                    if (column == 4 || column == 5) {
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
                int consultationId = Integer.parseInt(consultationTable.getValueAt(row, 0).toString());
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
                    consultationService.updateConsultation(consultationId, consultation);
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
    public void scrollToConsultation(int consultationId) {
        for (int i = 0; i < consultationTable.getRowCount(); i++) {
            if (Integer.parseInt(consultationTable.getValueAt(i, 0).toString()) == consultationId) {
                consultationTable.setRowSelectionInterval(i, i);
                consultationTable.scrollRectToVisible(consultationTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

}



