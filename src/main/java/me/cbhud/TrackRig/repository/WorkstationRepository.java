package me.cbhud.TrackRig.repository;

import me.cbhud.TrackRig.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkstationRepository extends JpaRepository<Workstation, Integer> {

    // Filter workstations by their status (e.g., all "Out of Order" stations)
    List<Workstation> findByWorkstationStatusId(Integer statusId);
}
