package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.MaintenanceLogRequest;
import me.cbhud.TrackRig.dto.MaintenanceLogResponse;
import me.cbhud.TrackRig.dto.MaintenanceStatusResponse;
import me.cbhud.TrackRig.exception.ResourceNotFoundException;
import me.cbhud.TrackRig.model.AppUser;
import me.cbhud.TrackRig.model.MaintenanceLog;
import me.cbhud.TrackRig.model.MaintenanceType;
import me.cbhud.TrackRig.model.Workstation;
import me.cbhud.TrackRig.repository.MaintenanceLogRepository;
import me.cbhud.TrackRig.repository.MaintenanceTypeRepository;
import me.cbhud.TrackRig.repository.WorkstationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceLogRepository logRepository;
    private final MaintenanceTypeRepository typeRepository;
    private final WorkstationRepository workstationRepository;

    public MaintenanceServiceImpl(
            MaintenanceLogRepository logRepository,
            MaintenanceTypeRepository typeRepository,
            WorkstationRepository workstationRepository) {
        this.logRepository = logRepository;
        this.typeRepository = typeRepository;
        this.workstationRepository = workstationRepository;
    }

    // LOG MAINTENANCE: Creates a new maintenance_log entry.
    //
    // The request only contains workstationId, maintenanceTypeId, and notes.
    // Two fields are auto-populated:
    // - performed_by_user_id → from the currently authenticated user
    // (SecurityContext)
    // - performed_at → current timestamp
    //
    // This is the most common action: a technician finishes cleaning a PC
    // and logs it, which resets the "next due" timer for that maintenance type.
    @Override
    @Transactional
    public MaintenanceLogResponse logMaintenance(MaintenanceLogRequest request) {
        Workstation workstation = workstationRepository.findById(request.getWorkstationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workstation not found with id: " + request.getWorkstationId()));

        MaintenanceType type = typeRepository.findById(request.getMaintenanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance type not found with id: " + request.getMaintenanceTypeId()));

        // Get the currently authenticated user from Spring Security.
        // The JwtAuthenticationFilter sets this when a valid JWT is provided.
        AppUser currentUser = (AppUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        MaintenanceLog log = new MaintenanceLog();
        log.setWorkstation(workstation);
        log.setMaintenanceType(type);
        log.setPerformedBy(currentUser);
        log.setPerformedAt(LocalDateTime.now());
        log.setNotes(request.getNotes());

        MaintenanceLog saved = logRepository.save(log);
        return MaintenanceLogResponse.from(saved);
    }

    // GET LOGS BY WORKSTATION: Returns maintenance history for a specific
    // workstation.
    @Override
    public List<MaintenanceLogResponse> getLogsByWorkstationId(Integer workstationId) {
        workstationRepository.findById(workstationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workstation not found with id: " + workstationId));

        return logRepository.findByWorkstationId(workstationId)
                .stream()
                .map(MaintenanceLogResponse::from)
                .toList();
    }

    // GET ALL OVERDUE: Queries the PostgreSQL view 'view_maintenance_status'
    // directly.
    //
    // The view computes status for every workstation × active maintenance type
    // combo.
    // This method filters to only return OVERDUE and DUE_SOON items — used for
    // dashboard alerts (e.g., "3 workstations need dust cleaning").
    //
    // The native query handles all the computation in the DB:
    // WHERE status IN ('OVERDUE', 'DUE_SOON')
    // ORDER BY next_due_date ASC
    @Override
    public List<MaintenanceStatusResponse> getAllOverdue() {
        return logRepository.findAllOverdue()
                .stream()
                .map(MaintenanceStatusResponse::from)
                .toList();
    }
}
