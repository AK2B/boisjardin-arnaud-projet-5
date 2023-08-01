package com.safetynet.alerts.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.config.DataConfig;
import com.safetynet.alerts.model.FireStation;

@Repository
public class FireStationRepository {

    private final List<FireStation> fireStations;
    @Autowired
    public FireStationRepository(DataConfig dataConfig) {
        this.fireStations = dataConfig.getFireStations();
    }

    public List<FireStation> getAllFireStations() {
        return new ArrayList<>(fireStations);
    }

    public FireStation getFireStationByAddress(String address) {
		return getAllFireStations().stream().filter(fireStation -> fireStation.getAddress().equals(address)).findFirst()
				.orElse(null);
	}

    public List<FireStation> getFireStationByStation(int fireStationNumber) {
        return getAllFireStations().stream()
                .filter(fireStation -> fireStation.getStation() == fireStationNumber)
                .collect(Collectors.toList());
    }
    
    public void addFireStation(FireStation fireStation) {
        fireStations.add(fireStation);
    }

    public void updateFireStation(FireStation fireStation) {
        fireStations.stream()
                .filter(station -> station.getAddress().equals(fireStation.getAddress()))
                .findFirst()
                .ifPresent(station -> {
                    int index = fireStations.indexOf(station);
                    fireStations.set(index, fireStation);
                });
    }

    public void deleteFireStation(String address) {
        fireStations.removeIf(fireStation -> fireStation.getAddress().equals(address));
    }
}
