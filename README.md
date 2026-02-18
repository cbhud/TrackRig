# TrackRig: Inventory Management & Workstation Maintenance System

TrackRig is a comprehensive inventory management and maintenance
tracking system built specifically for gaming centers, LAN cafés, and
esports venues. It is designed to be industry-agnostic, allowing any
business with hardware assets to manage workstations, components, and
maintenance schedules through a centralized, secure platform.

------------------------------------------------------------------------

## Problem Statement

Large gaming centers manage hundreds of components, peripherals, and
workstations daily. Without a centralized system, tracking which GPU is
installed in which PC, whether a workstation is due for thermal paste
replacement, or how many spare keyboards are sitting in storage becomes
a nightmare of spreadsheets and guesswork.

Components have individual lifecycles---one might be brand new while
another is near failure---and losing track of these statuses leads to
downtime, lost revenue, and frustrated customers.

------------------------------------------------------------------------

## Solution

TrackRig provides a single source of truth for all hardware inventory,
workstation status, and maintenance scheduling. The system leverages a
PostgreSQL database with role-based access control (RBAC), a visual
floor map, and automated maintenance calculation views to ensure
operational efficiency.

------------------------------------------------------------------------

# Features & Implementation Details

## Feature 1: Workstation Management

**Description:**\
Full CRUD management of workstations (gaming PCs) with status tracking
and spatial positioning.

**Database Implementation:**\
`workstation` table linked to `workstation_status`.

**Spatial Logic:**\
Uses `grid_x` and `grid_y` coordinates to support the visual map.

**Safety:**\
Workstation deletion automatically moves installed components to storage
(`ON DELETE SET NULL`).

**API Endpoints:** - GET `/api/workstations` - POST
`/api/workstations` - PUT `/api/workstations/{id}` - DELETE
`/api/workstations/{id}`

------------------------------------------------------------------------

## Feature 2: Component & Peripheral Inventory

**Description:**\
Track individual hardware components (GPUs, CPUs, RAM, peripherals) with
unique serial numbers, dynamic categories, and specific statuses.

**Database Implementation:**\
`component` table with foreign keys to `workstation` (nullable).

**Storage Concept:**\
If `workstation_id` is NULL, the item is considered **In Storage**.

**Safety Mechanism:**\
Database trigger (`trg_restrict_component_delete`) prevents deletion of
a component while it is installed.

**API Endpoints:** - GET `/api/components` - GET
`/api/components/workstation/{id}` - POST `/api/components` - DELETE
`/api/components/{id}`

------------------------------------------------------------------------

## Feature 3: Maintenance Type Configuration

**Description:**\
Define configurable maintenance rules (e.g., "Repaste CPU" every 180
days).

**Database Implementation:**\
`maintenance_type` table storing `interval_days`.

**API Endpoints:** - GET `/api/maintenance/types` - POST
`/api/maintenance/types` - PUT `/api/maintenance/types/{id}`

------------------------------------------------------------------------

## Feature 4: Maintenance Logging & Scheduling

**Description:**\
Log completed tasks and automatically compute workstation health status.

Status values: - OK - DUE_SOON - OVERDUE - NEVER_DONE

**Database Implementation:** - `maintenance_log` table - SQL View:
`view_maintenance_status`

**API Endpoints:** - GET `/api/maintenance/dashboard` - POST
`/api/maintenance/logs` - GET `/api/maintenance/logs/workstation/{id}`

------------------------------------------------------------------------

## Feature 5: Visual Floor Map

**Description:**\
2D grid-based visual layout of the gaming floor with color-coded
workstation statuses.

**Implementation:**\
Frontend grid using `grid_x` and `grid_y` from `workstation` table.

**API Endpoints:** - GET `/api/layout` - PUT `/api/layout/update`

------------------------------------------------------------------------

## Feature 6: Import & Export (Excel / PDF)

**Description:**\
Bulk import and export of inventory reports.

**Backend Implementation:** - Export: Apache POI (.xlsx), OpenPDF/iText
(.pdf) - Import: Batch insert with serial uniqueness validation

**API Endpoints:** - GET `/api/components/export/excel` - GET
`/api/components/export/pdf` - POST `/api/components/import/excel` -
POST `/api/components/import/pdf`

------------------------------------------------------------------------

## Feature 7: User Authentication & RBAC

**Description:**\
Secure login system with three roles.

**Database Implementation:**\
`app_user` table with constrained `role` column.

**Security:**\
Spring Security with JWT.

### Roles & Permissions

**Employee** - View map/inventory - Log maintenance - Move components to
storage

**Manager** - All Employee rights - Add/Edit components - Manage
maintenance types - Generate reports

**Owner** - Full access - Delete critical assets - Manage user accounts

------------------------------------------------------------------------

## Feature 8: Dynamic Categories

**Description:**\
Administrators can create custom component categories dynamically.

**Database Implementation:**\
`component_category` table.

**Relation:**\
`component` → Many-to-One → `component_category`

**Implementation Plan:**\
`CategoryController` with full CRUD.

------------------------------------------------------------------------

# Technical Stack

-   Database: PostgreSQL
-   Backend: Java (Spring Boot)
-   ORM: Hibernate / Spring Data JPA
-   Security: Spring Security (JWT)
-   Reporting: Apache POI, OpenPDF
