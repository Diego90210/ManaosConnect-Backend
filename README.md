# ManaosConnect Backend

REST API for managing a corporate food casino (comedor/cafetería): client companies, consumers, dishes, meal consumption records, and period reports with PDF export.

Stack: **Java 17 · Spring Boot 3.3.4 · Spring Security (JWT) · Spring Data JPA · PostgreSQL · iText 7**.

## Roles

| Rol | Capabilities |
|-----|--------------|
| `ADMIN` | Users, companies, consumers (with photo), dishes, view all consumptions/reports |
| `CAJERO` | Register/edit/delete consumptions, read dishes/companies/consumers |
| `CONTADOR` | Generate reports per company, PDF export, statistics, read consumptions |

Authentication: `POST /auth/login` with `{cedula, password}` → JWT (HS256, 24 h). Send `Authorization: Bearer <token>`.

## Project layout

```
src/main/java/com/diego/gestorcasino/
├── configuration/   Security (CORS → localhost:4200), startup admin setup
├── controllers/     HTTP layer
├── dto/             Request/response objects
├── models/          JPA entities (usuarios, consumos, platos, empresas, ...)
├── repositories/    Spring Data JPA
├── security/        JwtUtil, JwtRequestFilter
└── services/        Business logic, PDF generation, report building
```

### Main entities

- `Usuario` — login identity (cedula, email, bcrypt password, rol, soft-delete flags)
- `Administrador` / `Cajero` / `Contador` — profile data keyed by cedula → `Usuario`
- `EmpresaCliente` (NIT) → `Consumidor` (cedula, photo on disk) → `Consumo` (date, total) → `PlatoConsumo` (dish snapshot)
- `Plato` — menu item (name, price, category)
- `Reporte` — consumption summary for a company over a date range

## Endpoint map

| Base path | Method | Access |
|-----------|--------|--------|
| `/auth/**` | POST login | public |
| `/api/setup/**` | initial admin bootstrap | public (while `app.admin-setup.enabled`) |
| `/api/usuarios/**` | register/delete | public |
| `/admin/**` | users, companies, consumers, dishes, view consumos/reportes | ADMIN |
| `/cajero/**` | consumos, lectura de platos/empresas/consumidores | ADMIN, CAJERO |
| `/contador/**` | reportes, estadísticas, PDF | ADMIN, CONTADOR |

PDF download: `GET /contador/reportes/{id}/pdf` (also `/api/reportes/{id}/pdf`).

## Running

```bash
docker compose up -d        # PostgreSQL on localhost:5332 (db: manaosconnect_db)
./mvnw spring-boot:run      # API on port 8080
```

Config lives in `src/main/resources/application.yml`:

- `spring.datasource.*` — DB connection (user `user` / password `password`, local dev only)
- `app.admin-setup.enabled` / `default-password` — first-boot admin creation (`admin123` if no password given)
- `directorio.imagenes` — directory for consumer face photos (default `C:/imagenes_rostros/`)

JPA runs with `ddl-auto: update`, so the schema is created/extended automatically on startup.

## Notes

- First run: `GET /api/setup/estado` reports whether setup is pending; `POST /api/setup/admin-inicial` creates the first admin. Setup locks once an active admin exists.
- CORS is hardcoded to `http://localhost:4200` (Angular dev server) in `SecurityConfig`.
- JWT secret is generated at startup (`Keys.secretKeyFor`), so all tokens are invalidated on restart.
- Only `GestorcasinoApplicationTests` (context load) exists; no test coverage yet.
