package me.cbhud.TrackRig.repository;

import me.cbhud.TrackRig.model.ComponentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentStatusRepository extends JpaRepository <ComponentStatus, Integer> {
}
