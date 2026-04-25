CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(32) not null,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('EMPLOYEE', 'MANAGER', 'OWNER')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Optional case-insensitive uniqueness helper
CREATE UNIQUE INDEX uq_app_user_email_lower
ON app_user (LOWER(email));

select * from APP_USER;

select * from workstation;
select * from workstation_status ws;

select * from component_assignment_log cal;




-- =========================================
-- 2. WORKSTATION MANAGEMENT
-- =========================================
CREATE TABLE workstation_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(9) NULL DEFAULT '#FFFFFF'
);

CREATE TABLE workstation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    status_id INT NULL REFERENCES workstation_status(id),
    grid_x INT NULL,
    grid_y INT NULL,
    floor INT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Prevent two workstations from using the same position
-- only when all coordinates are actually set
CREATE UNIQUE INDEX uq_workstation_position
ON workstation (floor, grid_x, grid_y)
WHERE floor IS NOT NULL
  AND grid_x IS NOT NULL
  AND grid_y IS NOT NULL;


-- =========================================
-- 3. COMPONENT / ASSET TRACKING
-- =========================================
CREATE TABLE component_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE component_status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE component (
    id SERIAL PRIMARY KEY,

    -- Manufacturer serial is optional and NOT unique
    serial_number VARCHAR(100),

    name VARCHAR(200) NOT NULL,
    notes TEXT,
    category_id INT NOT NULL REFERENCES component_category(id),
    status_id INT NOT NULL REFERENCES component_status(id),
    workstation_id INT REFERENCES workstation(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);


-- =========================================
-- 4. COMPONENT ASSIGNMENT HISTORY
-- =========================================
CREATE TABLE component_assignment_log (
    id SERIAL PRIMARY KEY,
    component_id INT NOT NULL REFERENCES component(id) ON DELETE CASCADE,
    workstation_id INT REFERENCES workstation(id) ON DELETE SET NULL,
    assigned_by_user_id INT REFERENCES app_user(id) ON DELETE SET NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    removed_at TIMESTAMPTZ NULL,
    notes TEXT,

    CONSTRAINT chk_assignment_dates
        CHECK (removed_at IS NULL OR removed_at >= assigned_at)
);
truncate workstation, component, component_assignment_log, maintenance_log;


truncate app_user, component_assignment_log, maintenance_log;

select * from app_user;

-- Only one active assignment record per component
CREATE UNIQUE INDEX uq_component_assignment_log_active
ON component_assignment_log (component_id)
WHERE removed_at IS NULL;


-- =========================================
-- 5. MAINTENANCE
-- =========================================
CREATE TABLE maintenance_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    interval_days INT NOT NULL CHECK (interval_days > 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE maintenance_log (
    id SERIAL PRIMARY KEY,
    workstation_id INT NOT NULL REFERENCES workstation(id) ON DELETE CASCADE,
    maintenance_type_id INT NOT NULL REFERENCES maintenance_type(id),
    performed_by_user_id INT REFERENCES app_user(id) ON DELETE SET NULL,
    performed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes TEXT
);


-- =========================================
-- 6. SAFETY RULE
-- Prevent deleting a component while assigned
-- =========================================
CREATE OR REPLACE FUNCTION check_component_assignment()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.workstation_id IS NOT NULL THEN
        RAISE EXCEPTION
            'Cannot delete component % while it is assigned to a workstation. Move it to storage first.',
            OLD.asset_tag;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_restrict_component_delete
BEFORE DELETE ON component
FOR EACH ROW
EXECUTE FUNCTION check_component_assignment();


-- =========================================
-- 7. MAINTENANCE STATUS VIEW
-- =========================================
CREATE OR REPLACE VIEW view_maintenance_status AS
WITH last_logs AS (
    SELECT
        workstation_id,
        maintenance_type_id,
        MAX(performed_at) AS last_performed
    FROM maintenance_log
    GROUP BY workstation_id, maintenance_type_id
)
SELECT
    w.id AS workstation_id,
    w.name AS workstation_name,
    mt.id AS maintenance_type_id,
    mt.name AS maintenance_name,
    mt.interval_days,
    ll.last_performed,
    (ll.last_performed + (mt.interval_days * INTERVAL '1 day')) AS next_due_date,
    CASE
        WHEN ll.last_performed IS NULL THEN 'NEVER_DONE'
        WHEN (ll.last_performed + (mt.interval_days * INTERVAL '1 day')) < now() THEN 'OVERDUE'
        WHEN (ll.last_performed + (mt.interval_days * INTERVAL '1 day')) < (now() + INTERVAL '3 days') THEN 'DUE_SOON'
        ELSE 'OK'
    END AS status
FROM workstation w
CROSS JOIN maintenance_type mt
LEFT JOIN last_logs ll
    ON ll.workstation_id = w.id
   AND ll.maintenance_type_id = mt.id
WHERE mt.is_active = TRUE;


-- =========================================
-- 8. PERFORMANCE INDEXES
-- =========================================
CREATE INDEX idx_workstation_status_id
ON workstation(status_id);

CREATE INDEX idx_component_category_id
ON component(category_id);

CREATE INDEX idx_component_status_id
ON component(status_id);

CREATE INDEX idx_component_workstation_id
ON component(workstation_id);

CREATE INDEX idx_assignment_log_component_id
ON component_assignment_log(component_id);

CREATE INDEX idx_assignment_log_workstation_id
ON component_assignment_log(workstation_id);

CREATE INDEX idx_assignment_log_assigned_by_user_id
ON component_assignment_log(assigned_by_user_id);

CREATE INDEX idx_assignment_log_assigned_at
ON component_assignment_log(assigned_at);

CREATE INDEX idx_maintenance_log_workstation_id
ON maintenance_log(workstation_id);

CREATE INDEX idx_maintenance_log_type_id
ON maintenance_log(maintenance_type_id);

CREATE INDEX idx_maintenance_log_performed_by_user_id
ON maintenance_log(performed_by_user_id);

CREATE INDEX idx_maintenance_log_performed_at
ON maintenance_log(performed_at);












-- =================================================================================
-- TrackRig Seed Data Script
-- Includes:
-- - 3 Workstation Statuses
-- - 3 Component Statuses
-- - 3 Component Categories
-- - 2 Maintenance Types
-- - 5 Workstations
-- - 5 Components
-- - Maintenance Logs to simulate OK, DUE_SOON, OVERDUE, and NEVER_DONE statuses
-- =================================================================================

-- NOTE: If you are running this in a database that already has data, 
-- uncomment the following line to clear existing data and reset auto-increment IDs.
-- TRUNCATE TABLE maintenance_log, component, workstation, workstation_status, component_status, component_category, maintenance_type RESTART IDENTITY CASCADE;

-- 1. Insert Workstation Statuses (3 entries)
INSERT INTO workstation_status (name, color) VALUES 
('Active', '#28a745'),
('Inactive', '#dc3545'),
('Under Maintenance', '#ffc107');

-- 2. Insert Component Categories (3 entries)
INSERT INTO component_category (name, description) VALUES 
('Computer', 'Desktop PCs and mini PCs'),
('Peripheral', 'Keyboards, mice, and other accessories');

-- 3. Insert Component Statuses (3 entries)
INSERT INTO component_status (name) VALUES 
('Broken'),
('In Storage');

-- 4. Insert Maintenance Types (Required to demonstrate due/overdue statuses)
INSERT INTO maintenance_type (name, description, interval_days, is_active) VALUES 
('Weekly Cleaning', 'General cleaning of the workstation', 7, TRUE),
('Monthly Calibration', 'Hardware calibration and testing', 30, TRUE);

-- 5. Insert Workstations (5 entries)
-- Using subqueries ensures we grab the correct IDs even if they don't start at 1
INSERT INTO workstation (name, status_id, grid_x, grid_y, floor) VALUES 
('WS-Alpha', (SELECT id FROM workstation_status WHERE name = 'Active'), 1, 1, 1),
('WS-Beta', (SELECT id FROM workstation_status WHERE name = 'Active'), 2, 1, 1),
('WS-Gamma', (SELECT id FROM workstation_status WHERE name = 'Inactive'), 1, 2, 1),
('WS-Delta', (SELECT id FROM workstation_status WHERE name = 'Under Maintenance'), 2, 2, 1),
('WS-Epsilon', (SELECT id FROM workstation_status WHERE name = 'Active'), 3, 1, 1);

-- 6. Insert Components (5 entries)
INSERT INTO component (serial_number, name, notes, category_id, status_id, workstation_id) VALUES 
('SN-MON-001', 'Dell UltraSharp 27', 'Primary monitor', 
    (SELECT id FROM component_category WHERE name = 'Monitor'), 
    (SELECT id FROM component_status WHERE name = 'Operational'), 
    (SELECT id FROM workstation WHERE name = 'WS-Alpha')),

('SN-PC-001', 'OptiPlex 7090', 'Main processing unit', 
    (SELECT id FROM component_category WHERE name = 'Computer'), 
    (SELECT id FROM component_status WHERE name = 'Operational'), 
    (SELECT id FROM workstation WHERE name = 'WS-Alpha')),

('SN-PER-001', 'Logitech MX Master', 'Mouse for Beta', 
    (SELECT id FROM component_category WHERE name = 'Peripheral'), 
    (SELECT id FROM component_status WHERE name = 'Operational'), 
    (SELECT id FROM workstation WHERE name = 'WS-Beta')),

('SN-MON-002', 'BenQ Designer 27', 'Flickering issue', 
    (SELECT id FROM component_category WHERE name = 'Monitor'), 
    (SELECT id FROM component_status WHERE name = 'Broken'), 
    (SELECT id FROM workstation WHERE name = 'WS-Gamma')),

('SN-PC-002', 'OptiPlex 7090', 'Spare unit', 
    (SELECT id FROM component_category WHERE name = 'Computer'), 
    (SELECT id FROM component_status WHERE name = 'In Storage'), 
    NULL);

-- 7. Insert Maintenance Logs to simulate statuses (OK, DUE_SOON, OVERDUE, NEVER_DONE)
-- WS-Alpha: OK (Cleaning performed 1 day ago)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Alpha'), 
    (SELECT id FROM maintenance_type WHERE name = 'Weekly Cleaning'), 
    now() - INTERVAL '1 day', 'Routine cleaning'
);

-- WS-Beta: DUE_SOON (Cleaning performed 6 days ago, interval is 7 days, so due in 1 day)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Beta'), 
    (SELECT id FROM maintenance_type WHERE name = 'Weekly Cleaning'), 
    now() - INTERVAL '6 days', 'Last cleaning was a bit rushed'
);

-- WS-Gamma: OVERDUE (Calibration performed 40 days ago, interval is 30 days, so 10 days late)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Gamma'), 
    (SELECT id FROM maintenance_type WHERE name = 'Monthly Calibration'), 
    now() - INTERVAL '40 days', 'Needs recalibration'
);

-- WS-Delta: NEVER_DONE
-- (We intentionally skip inserting a log for WS-Delta so it shows up as NEVER_DONE in the view)

-- WS-Epsilon: DUE_SOON (Calibration performed 28 days ago, interval is 30 days, due in 2 days)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Epsilon'), 
    (SELECT id FROM maintenance_type WHERE name = 'Monthly Calibration'), 
    now() - INTERVAL '28 days', 'Upcoming calibration needed'
);




INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Delta'), 
    (SELECT id FROM maintenance_type WHERE name = 'Monthly Calibration'), 
    now() - INTERVAL '29 days', 'Almost due for calibration'
);

select * from app_user;

-- WS-Delta: OVERDUE for Cleaning (Performed 14 days ago, interval 7 days)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Delta'), 
    (SELECT id FROM maintenance_type WHERE name = 'Weekly Cleaning'), 
    now() - INTERVAL '14 days', 'Cleaning neglected during maintenance'
);

-- WS-Epsilon: OVERDUE for Cleaning (Performed 12 days ago, interval 7 days)
INSERT INTO maintenance_log (workstation_id, maintenance_type_id, performed_at, notes) 
VALUES (
    (SELECT id FROM workstation WHERE name = 'WS-Epsilon'), 
    (SELECT id FROM maintenance_type WHERE name = 'Weekly Cleaning'), 
    now() - INTERVAL '12 days', 'Overdue for weekly cleaning'
);


