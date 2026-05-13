# Todo: Persistent Backend for Whitelisted Users

## Phase 1 — Database Setup
- [ ] Add dependencies: `hibernate-orm-panache`, `jdbc-postgresql`, `jdbc-h2` (dev), `flyway`
- [ ] Configure datasource in `application.properties` (dev + prod profiles)
- [ ] Create Flyway migration: `whitelist_user`, `learning_progress`, `quiz_session`, `quiz_session_card`
- [ ] Store only hashed API keys — never plaintext

## Phase 2 — Whitelist & Auth
- [ ] Create `WhitelistUser` entity, repository, service
- [ ] Generate API keys on backend; hash before storing (SHA-256 or bcrypt)
- [ ] Add `@ServerRequestFilter`: read `X-API-Key`, resolve user, block missing/invalid/inactive with `403`
- [ ] Never trust client-supplied `userId` after auth is active
- [ ] `GET /api/auth/me` — return resolved user info
- [ ] `POST /api/admin/whitelist` — admin-only (env var `ADMIN_TOKEN`); return plaintext key once

## Phase 3 — Learning Progress API
- [ ] Create `LearningProgress` entity, repository, service
- [ ] Upsert logic: increment `times_seen`, `times_correct`, update `last_seen_at`
- [ ] Derive `userId` from API key — do not accept it from frontend
- [ ] `GET /api/progress` — all progress for authenticated user
- [ ] `GET /api/progress/{cardSlug}` — single card progress
- [ ] `POST /api/progress/{cardSlug}` — body: `{ "wasCorrect": true }`
- [ ] `DELETE /api/progress` — idempotent; return `204` even if nothing existed

## Phase 4 — Quiz Session API
- [ ] Create `QuizSession` + `QuizSessionCard` entities, repositories, service
- [ ] Tie sessions to authenticated user; never accept `userId` from frontend
- [ ] Ownership check on every session endpoint — return `403` for other users' sessions
- [ ] `POST /api/sessions` — start session; body: `{ "module": "..." }`; return `{ "sessionId": "..." }`
- [ ] `POST /api/sessions/{id}/answer` — body: `{ "cardSlug": "...", "wasCorrect": true }`
- [ ] `POST /api/sessions/{id}/complete` — finalize score, set `completed_at`
- [ ] `GET /api/sessions` — all sessions for authenticated user
- [ ] `GET /api/sessions/{id}` — full session with card results (owner only)

## Phase 5 — Cards API Extension
- [ ] Keep `GET /api/cards/markdown` and `GET /api/cards/markdown/{slug}` unchanged
- [ ] If `X-API-Key` is valid, overlay progress data (`timesSeen`, `timesCorrect`, `lastSeen`) on card response
- [ ] No API key → normal public response, no change in behavior

## Phase 6 — Validation, Error Handling & Logging
- [ ] Add `@Valid`, `@NotNull`, `@NotBlank` to all new DTOs
- [ ] Reuse existing `ErrorResponseDto` format
- [ ] Add structured logging to all new services
- [ ] `404` for missing user/session/card-progress; `403` for auth failures; `409` for duplicate whitelist entry
- [ ] `204` for `DELETE /api/progress` always

## Phase 7 — Documentation
- [ ] Update backend `README.md`: all new endpoints, env vars, local setup, whitelist testing
- [ ] Document: `X-API-Key` header, API key hashing, one-time plaintext return, idempotent reset, server-derived `userId`
- [ ] Update CORS: allow `X-API-Key` header
  ```properties
  quarkus.http.cors.headers=Content-Type,X-API-Key