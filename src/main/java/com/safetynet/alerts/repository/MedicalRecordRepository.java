package com.safetynet.alerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.config.DataConfig;
import com.safetynet.alerts.model.MedicalRecord;

@Repository
public class MedicalRecordRepository {

    private final List<MedicalRecord> medicalRecords;
    
    @Autowired
    public MedicalRecordRepository(DataConfig dataConfig) {
        this.medicalRecords = dataConfig.getMedicalRecords();
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecords;
    }

    public MedicalRecord getMedicalRecordByFullName(String firstName, String lastName) {
		return getAllMedicalRecords().stream().filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
				&& medicalRecord.getLastName().equals(lastName)).findFirst().orElse(null);
	}
    
    public void addMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecords.add(medicalRecord);
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecords.stream()
                .filter(record -> record.getFirstName().equals(medicalRecord.getFirstName())
                        && record.getLastName().equals(medicalRecord.getLastName()))
                .findFirst()
                .ifPresent(record -> {
                    int index = medicalRecords.indexOf(record);
                    medicalRecords.set(index, medicalRecord);
                });
    }

    public void deleteMedicalRecord(String firstName, String lastName) {
        medicalRecords.removeIf(medicalRecord ->
                medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName));
    }
}
