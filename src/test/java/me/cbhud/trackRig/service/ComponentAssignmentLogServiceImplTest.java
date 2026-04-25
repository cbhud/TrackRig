package me.cbhud.trackRig.service;

import me.cbhud.trackRig.dto.request.ComponentAssignmentLogRequest;
import me.cbhud.trackRig.dto.response.ComponentAssignmentLogResponse;
import me.cbhud.trackRig.model.AppUser;
import me.cbhud.trackRig.model.Component;
import me.cbhud.trackRig.model.ComponentAssignmentLog;
import me.cbhud.trackRig.model.Role;
import me.cbhud.trackRig.model.Workstation;
import me.cbhud.trackRig.repository.AppUserRepository;
import me.cbhud.trackRig.repository.ComponentAssignmentLogRepository;
import me.cbhud.trackRig.repository.ComponentRepository;
import me.cbhud.trackRig.repository.WorkstationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentAssignmentLogServiceImplTest {

    @Mock
    private ComponentAssignmentLogRepository logRepository;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private WorkstationRepository workstationRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private ComponentAssignmentLogServiceImpl service;

    private Component component;
    private Workstation workstation;
    private AppUser user;

    @BeforeEach
    void setUp() {
        component = new Component();
        component.setId(4);
        component.setName("RTX 5090");

        workstation = new Workstation();
        workstation.setId(2);
        workstation.setName("Workstation A");

        user = new AppUser();
        user.setId(7);
        user.setUsername("owner");
        user.setRole(Role.OWNER);
    }

    @Test
    void createAssignment_ShouldSyncComponentAndClosePreviousActiveLog() {
        ComponentAssignmentLogRequest request = new ComponentAssignmentLogRequest(4, 2, "Move to workstation");

        Workstation previousWorkstation = new Workstation();
        previousWorkstation.setId(1);
        previousWorkstation.setName("Old Station");

        ComponentAssignmentLog existingLog = new ComponentAssignmentLog();
        existingLog.setId(11);
        existingLog.setComponent(component);
        existingLog.setWorkstation(previousWorkstation);

        when(componentRepository.findById(4)).thenReturn(Optional.of(component));
        when(logRepository.findByComponentIdAndRemovedAtIsNull(4)).thenReturn(Optional.of(existingLog));
        when(workstationRepository.findById(2)).thenReturn(Optional.of(workstation));
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));
        when(componentRepository.save(any(Component.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(logRepository.save(any(ComponentAssignmentLog.class))).thenAnswer(invocation -> {
            ComponentAssignmentLog saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(12);
            }
            return saved;
        });

        ComponentAssignmentLogResponse response = service.createAssignment(request, "owner");

        assertEquals(workstation, component.getWorkstation());
        assertNotNull(existingLog.getRemovedAt());
        assertEquals(12, response.id());
        assertEquals(4, response.componentId());
        assertEquals(2, response.workstationId());
        assertEquals("Workstation A", response.workstationName());
        verify(componentRepository).save(component);
        verify(logRepository).flush();
    }

    @Test
    void closeAssignment_ShouldClearCurrentComponentWorkstation() {
        component.setWorkstation(workstation);

        ComponentAssignmentLog activeLog = new ComponentAssignmentLog();
        activeLog.setId(15);
        activeLog.setComponent(component);
        activeLog.setWorkstation(workstation);

        when(logRepository.findByComponentIdAndRemovedAtIsNull(4)).thenReturn(Optional.of(activeLog));
        when(componentRepository.save(any(Component.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(logRepository.save(any(ComponentAssignmentLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComponentAssignmentLogResponse response = service.closeAssignment(4, "Move to storage");

        assertNull(component.getWorkstation());
        assertNotNull(activeLog.getRemovedAt());
        assertEquals("Move to storage", response.notes());
        assertEquals(2, response.workstationId());
        verify(componentRepository).save(component);
    }
}
