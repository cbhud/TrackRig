# TrackRig API Endpoints

> **Base URL:** `http://localhost:8080`  
> **Auth:** All endpoints (except `/api/auth/**`) require `Authorization: Bearer <JWT>` header.  
> **Content-Type:** `application/json`

---

## 🔑 Authentication

| Method | Endpoint | Body | Response
|--------|----------|------|----------
| `POST` | `/api/auth/register` | `RegisterRequest` | `201` UserResponse 
| `POST` | `/api/auth/login` | `LoginRequest` | `200` `{ "token": "jwt..." }`

### Register
```json
POST /api/auth/register
{
    "email": "tech@trackrig.com",
    "password": "password123",
    "fullName": "Charlie Tech"
}
```

### Login
```json
POST /api/auth/login
{
    "email": "owner@trackrig.com",
    "password": "password123"
}
// Response: { "token": "eyJhbGciOi..." }
```

---

## 🖥️ Workstations

| Method | Endpoint | Body | Response | Role |
|--------|----------|------|----------|------|
| `GET` | `/api/workstations` | — | `200` List\<WorkstationResponse\> | Any |
| `GET` | `/api/workstations?statusId=3` | — | `200` Filtered list | Any |
| `GET` | `/api/workstations/{id}` | — | `200` WorkstationResponse | Any |
| `POST` | `/api/workstations` | `WorkstationRequest` | `201` WorkstationResponse | Any |
| `PUT` | `/api/workstations/{id}` | `WorkstationRequest` | `200` WorkstationResponse | Any |
| `DELETE` | `/api/workstations/{id}` | — | `204` No Content | 🔒 MANAGER, OWNER |
| `PATCH` | `/api/workstations/{id}/status` | `{ "statusId": 2 }` | `200` WorkstationResponse | Any |
| `PATCH` | `/api/workstations/{id}/position` | `{ "gridX": 2, "gridY": 1 }` | `200` WorkstationResponse | Any |
| `GET` | `/api/workstations/{id}/components` | — | `200` List\<ComponentResponse\> | Any |
| `GET` | `/api/workstations/{id}/maintenance-status` | — | `200` List\<MaintenanceStatusResponse\> | Any |

### Create Workstation
```json
POST /api/workstations
{
    "name": "Station-D1",
    "statusId": 1,
    "gridX": 0,
    "gridY": 3
}
```

### Update Workstation
```json
PUT /api/workstations/1
{
    "name": "Station-A1-Updated",
    "statusId": 2,
    "gridX": 0,
    "gridY": 0
}
```

### Change Status Only
```json
PATCH /api/workstations/1/status
{
    "statusId": 3
}
```

### Move on Floor Map
```json
PATCH /api/workstations/1/position
{
    "gridX": 4,
    "gridY": 2
}
```

---

## 🔧 Components

| Method | Endpoint | Body | Response | Role |
|--------|----------|------|----------|------|
| `GET` | `/api/components` | — | `200` List\<ComponentResponse\> | Any |
| `GET` | `/api/components/{id}` | — | `200` ComponentResponse | Any |
| `POST` | `/api/components` | `ComponentRequest` | `201` ComponentResponse | 🔒 MANAGER, OWNER |
| `PUT` | `/api/components/{id}` | `ComponentRequest` | `200` ComponentResponse | Any |
| `DELETE` | `/api/components/{id}` | — | `204` No Content | 🔒 OWNER only |
| `GET` | `/api/components/storage` | — | `200` List\<ComponentResponse\> | Any |
| `PATCH` | `/api/components/{id}/assign` | `{ "workstationId": 5 }` | `200` ComponentResponse | Any |
| `PATCH` | `/api/components/{id}/storage` | — | `200` ComponentResponse | Any |

### Create Component
```json
POST /api/components
{
    "serialNumber": "SN-GPU-NEW",
    "name": "NVIDIA RTX 5090",
    "categoryId": 1,
    "statusId": 1,
    "workstationId": null,
    "purchaseDate": "2026-02-18",
    "warrantyExpiry": "2029-02-18",
    "notes": "Brand new card"
}
```

### Create Component in Storage (no workstation)
```json
POST /api/components
{
    "serialNumber": "SN-SPARE-001",
    "name": "Backup Keyboard",
    "categoryId": 5,
    "statusId": 1,
    "workstationId": null,
    "purchaseDate": "2026-01-01",
    "notes": "Spare for replacement"
}
```

### Assign to Workstation
```json
PATCH /api/components/12/assign
{
    "workstationId": 5
}
```

### Move to Storage (no body needed)
```
PATCH /api/components/12/storage
```

### Delete Component  
⚠️ **Note:** If the component is assigned to a workstation, the SQL trigger will block deletion and return `409 Conflict`. Move it to storage first.
```
DELETE /api/components/12
```

---

## 🔨 Maintenance

| Method | Endpoint | Body | Response | Role |
|--------|----------|------|----------|------|
| `POST` | `/api/maintenance/log` | `MaintenanceLogRequest` | `201` MaintenanceLogResponse | Any |
| `GET` | `/api/maintenance/logs/workstation/{id}` | — | `200` List\<MaintenanceLogResponse\> | Any |
| `GET` | `/api/maintenance/overdue` | — | `200` List\<MaintenanceStatusResponse\> | Any |

### Log Maintenance
```json
POST /api/maintenance/log
{
    "workstationId": 1,
    "maintenanceTypeId": 1,
    "notes": "Cleaned dust filters with compressed air"
}
// performed_by → auto-set from JWT user
// performed_at → auto-set to current timestamp
```

### Get Maintenance History for Workstation
```
GET /api/maintenance/logs/workstation/1
```

### Get All Overdue / Due Soon (Dashboard)
```
GET /api/maintenance/overdue
```
Response uses PostgreSQL `view_maintenance_status` directly:
```json
[
    {
        "workstationId": 3,
        "workstationName": "Station-A3",
        "maintenanceTypeId": 1,
        "maintenanceName": "Dust Cleaning",
        "intervalDays": 30,
        "lastPerformed": "2026-01-04T...",
        "nextDueDate": "2026-02-03T...",
        "status": "OVERDUE"
    }
]
```

---

## 📋 Lookup / Reference Data

> **Read:** All authenticated users  
> **Write (POST/PUT/DELETE):** 🔒 OWNER only

### Component Categories

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| `GET` | `/api/lookup/component-categories` | — | `200` List |
| `POST` | `/api/lookup/component-categories` | `{ "name": "SSD", "description": "..." }` | `201` 🔒 |
| `PUT` | `/api/lookup/component-categories/{id}` | `{ "name": "...", "description": "..." }` | `200` 🔒 |
| `DELETE` | `/api/lookup/component-categories/{id}` | — | `204` 🔒 |

### Component Statuses

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| `GET` | `/api/lookup/component-statuses` | — | `200` List |
| `POST` | `/api/lookup/component-statuses` | `{ "name": "Retired" }` | `201` 🔒 |
| `PUT` | `/api/lookup/component-statuses/{id}` | `{ "name": "..." }` | `200` 🔒 |
| `DELETE` | `/api/lookup/component-statuses/{id}` | — | `204` 🔒 |

### Workstation Statuses

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| `GET` | `/api/lookup/workstation-statuses` | — | `200` List |
| `POST` | `/api/lookup/workstation-statuses` | `{ "name": "Decommissioned" }` | `201` 🔒 |
| `PUT` | `/api/lookup/workstation-statuses/{id}` | `{ "name": "..." }` | `200` 🔒 |
| `DELETE` | `/api/lookup/workstation-statuses/{id}` | — | `204` 🔒 |

### Maintenance Types

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| `GET` | `/api/lookup/maintenance-types` | — | `200` List (all) |
| `GET` | `/api/lookup/maintenance-types/active` | — | `200` List (active only) |
| `POST` | `/api/lookup/maintenance-types` | see below | `201` 🔒 |
| `PUT` | `/api/lookup/maintenance-types/{id}` | see below | `200` 🔒 |
| `DELETE` | `/api/lookup/maintenance-types/{id}` | — | `204` 🔒 |

```json
POST /api/lookup/maintenance-types
{
    "name": "Cable Check",
    "description": "Inspect all cable connections",
    "intervalDays": 90,
    "isActive": true
}
```

---


## 🔒 Role-Based Access Summary

| Action | EMPLOYEE | MANAGER | OWNER |
|--------|----------|---------|-------|
| Read anything | ✅ | ✅ | ✅ |
| Create component | ❌ 403 | ✅ | ✅ |
| Delete component | ❌ 403 | ❌ 403 | ✅ |
| Delete workstation | ❌ 403 | ✅ | ✅ |
| Modify lookup tables | ❌ 403 | ❌ 403 | ✅ |
| Log maintenance | ✅ | ✅ | ✅ |

---

## ⚠️ Error Responses

| Status | When |
|--------|------|
| `400 Bad Request` | Validation fails (missing required fields) |
| `403 Forbidden` | User doesn't have the required role |
| `404 Not Found` | Entity not found by ID |
| `409 Conflict` | FK constraint violation (e.g., delete component while assigned) |
| `500 Internal Server Error` | Unexpected error |

---

## 📊 Total: 41 Endpoints

| Controller | Endpoints |
|------------|----------|
| AuthController | 2 |
| WorkstationController | 10 |
| ComponentController | 8 |
| MaintenanceController | 3 |
| LookupController | 17 |
| SimpleController (test) | 1 |
| **Total** | **41** |
