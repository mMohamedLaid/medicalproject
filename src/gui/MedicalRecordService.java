package gui;

import core.MedicalRecord;
import database.MedicalRecordDAO;

import java.util.List;

public class MedicalRecordService {
    private MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    public void addMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecordDAO.insertMedicalRecord(medicalRecord);
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordDAO.selectAllMedicalRecords();
    }

    public void updateMedicalRecord(int recordId, MedicalRecord medicalRecord) {
        medicalRecordDAO.updateMedicalRecord(medicalRecord);
    }

    public void deleteMedicalRecord(int recordId) {
        medicalRecordDAO.deleteMedicalRecord(recordId);
    }
}

