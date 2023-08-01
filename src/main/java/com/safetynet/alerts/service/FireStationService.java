package com.safetynet.alerts.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;

@Service
public class FireStationService {
	private final FireStationRepository fireStationRepository;

	@Autowired
	public FireStationService(FireStationRepository fireStationRepository) {
		this.fireStationRepository = fireStationRepository;
	}

	public List<FireStation> getAllFireStations() {
		return fireStationRepository.getAllFireStations();
	}

	public FireStation getFireStationByAddress(String address) {
		return getAllFireStations().stream().filter(fireStation -> fireStation.getAddress().equals(address)).findFirst()
				.orElse(null);
	}

	public List<FireStation> getFireStationByStation(int station) {
	    return getAllFireStations().stream()
	            .filter(fireStation -> fireStation.getStation() == station)
	            .collect(Collectors.toList());
	}

	public void addFireStation(FireStation fireStation) {
		fireStationRepository.addFireStation(fireStation);
	}

	public void updateFireStation(FireStation fireStation) {
		fireStationRepository.updateFireStation(fireStation);
	}

	public void deleteFireStation(String address) {
		fireStationRepository.deleteFireStation(address);
	}
}
