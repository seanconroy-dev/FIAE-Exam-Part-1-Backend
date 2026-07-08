# Todo: Persistent Backend for Whitelisted Users

## Phase 1 — Database Setup
- [x] Add dependencies: `hibernate-orm-panache`, `jdbc-postgresql`, `jdbc-h2` (dev), `flyway`
- [x] Configure datasource in `application.properties` (dev + prod profiles)
- [x] Create Flyway migration: `whitelist_user`, `learning_progress`, `quiz_session`, `quiz_session_card`
- [x] Database schema stores only api_key_hash, no plaintext API key column

## Phase 2 — Whitelist & Auth
- [x] Create WhitelistUser entity, WhitelistUserRepository, WhitelistService
- [x] Generate API keys on backend
- [x] Hash API key before storing it
- [x] Return plaintext API key only once during whitelist creation
- [x] Never return api_key_hash in API responses
- [x] Add auth filter: read X-API-Key, resolve user, block missing/invalid/inactive with 403
- [x] Never trust client-supplied userId after auth is active
- [x] GET /api/auth/me — return resolved user info
- [x] POST /api/admin/whitelist — admin-only via ADMIN_TOKEN, return plaintext key once

## Phase 3 — Learning Progress API
- [x] Create `LearningProgress` entity
- [x] Create `LearningProgressRepository`
- [x] Create `LearningProgressService`
- [x] Upsert logic: increment `times_seen`, `times_correct`, update `last_seen_at`
- [x] Derive `userId` from API key — do not accept it from frontend
- [x] `GET /api/progress` — all progress for authenticated user
- [x] `GET /api/progress/{cardSlug}` — single card progress
- [x] `POST /api/progress/{cardSlug}` — body: `{ "wasCorrect": true }`
- [x] `DELETE /api/progress` — idempotent; return `204` even if nothing existed

## Phase 4 — Quiz Session API
- [x] Create `QuizSession` + `QuizSessionCard` entities, repositories, service
- [x] Tie sessions to authenticated user; never accept `userId` from frontend
- [x] Ownership check on every session endpoint — return `403` for other users' sessions
- [x] `POST /api/sessions` — start session; body: `{ "module": "..." }`; return session summary
- [x] `POST /api/sessions/{id}/answer` — body: `{ "cardSlug": "...", "wasCorrect": true }`
- [x] `POST /api/sessions/{id}/complete` — finalize score, set `completed_at`
- [x] `GET /api/sessions` — all sessions for authenticated user
- [x] `GET /api/sessions/{id}` — session summary only, owner only
- [x] `GET /api/sessions/{id}/answers` — card results for session, owner only

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
