package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;

@Service
public class MedicalRecordService {
	private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
		this.medicalRecordRepository = medicalRecordRepository;
	}

	public List<MedicalRecord> getAllMedicalRecords() {
		return medicalRecordRepository.getAllMedicalRecords();
	}

	public MedicalRecord getMedicalRecordByFullName(String firstName, String lastName) {
		return getAllMedicalRecords().stream().filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
				&& medicalRecord.getLastName().equals(lastName)).findFirst().orElse(null);
	}

	public void addMedicalRecord(MedicalRecord medicalRecord) {
		medicalRecordRepository.addMedicalRecord(medicalRecord);
	}

	public void updateMedicalRecord(MedicalRecord medicalRecord) {
		medicalRecordRepository.updateMedicalRecord(medicalRecord);
	}

	public void deleteMedicalRecord(String firstName, String lastName) {
		medicalRecordRepository.deleteMedicalRecord(firstName, lastName);
	}
}
