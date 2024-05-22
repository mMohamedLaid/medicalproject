package gui;

import core.Consultation;
import database.ConsultationDAO;

import java.util.List;

public class ConsultationService {
    private ConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }

    public void addConsultation(Consultation consultation) {
        consultationDAO.insertConsultation(consultation);
    }

    public List<Consultation> getAllConsultations() {
        return consultationDAO.selectAllConsultations();
    }

    public Consultation getConsultationById(int consultationId) {
        return consultationDAO.selectConsultationById(consultationId);
    }

    public void updateConsultation(int consultationId, Consultation consultation) {
        consultationDAO.updateConsultation(consultation);
    }

    public void deleteConsultation(int consultationId) {
        consultationDAO.deleteConsultation(consultationId);
    }
}
