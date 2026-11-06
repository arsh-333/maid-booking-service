# Maid Booking System

Backend for a home-services (maid) booking platform — Rupeek SDE-3 machine coding, Question B.
Java 21 + Spring Boot 3. In-memory persistence behind repository interfaces.

## Build & run

```bash
mvn spring-boot:run        # starts on :8080, seeds two demo maids (IDs in logs)
mvn test                   # unit tests for the core business rules
```

## API

| Endpoint | Purpose |
|---|---|
| `POST /maids` | onboard maid `{name, gender, locality, prices, availability, ratings}` |
| `GET /maids/search?locality=&service=&gender=&maxPrice=&minRating=&date=&start=&end=` | discovery; returns only maids free for the requested slot |
| `POST /bookings` | book. SCHEDULED: `date/start/end`; INSTANT: `durationMins`; RECURRING: `recurrence{days,start,durationMins,startDate,endDate}` |
| `POST /payments` | pay one booking `{bookingId, mode, idempotencyKey}` — idempotent |
| `POST /bookings/{id}/cancel` | cancel one booking / one series occurrence |
| `POST /series/{id}/cancel` | cancel all future occurrences of a series |

## Key design decisions

- **Availability is derived, not stored on the maid.** A maid holds weekly
  `AvailabilityWindow`s; busy-ness comes from active bookings. `AvailabilityService`
  is the single source of truth, used by both search and the booking write path.
- **Booking types behind `BookingStrategy`** (Instant / Scheduled / Recurring), resolved
  from a Spring-injected registry — a new type is a new `@Component`, no other change.
- **Recurring = series + materialised occurrences.** `RecurrencePattern` expands into
  one `Booking` per occurrence, each with its own slot and payment. Per-occurrence
  payment and single-vs-series cancellation fall out naturally. Series creation is
  atomic: all occurrences validated before any is saved.
- **Double-booking prevention at write time**: `BookingService` serialises bookings
  per maid with a striped lock; the availability re-check happens inside the lock.
- **Payment methods behind `PaymentMethodHandler`** delegating to a mocked
  `PaymentGateway`. CARD and UPI are implemented; a new method is one new `@Component`
  (e.g. WALLET is in the enum but unregistered → clean 400 until plugged in).
  `pay` is idempotent by client-supplied key.
- **Cancellation is one path** (`CancellationService`) with a pluggable `RefundPolicy`
  (full >= 24h before, 50% after, 0 once started) — deliberately orthogonal to booking type.
- **Filters are self-skipping beans**: new search filter = new `MaidFilter` `@Component`.
- **No Customer entity**: customer lifecycle is outside the functional requirements;
  bookings carry a `customerId` reference.

## Assumptions

- Single instance; per-maid locks are enough (no distributed locking).
- Each booking/occurrence is paid via `POST /payments` before its slot (enforced as a
  documented rule; no scheduler in this exercise).
- Ratings are provided at onboarding; no review flow in scope.
- Single timezone; slots do not cross midnight.

## With more time

- Auto-debit mandates for recurring occurrences (UPI Autopay-style) + a due-date scheduler.
- Concurrency stress test for simultaneous bookings on the same maid.
- Customer entity + management endpoints, pluggable pricing, OpenAPI docs,
  real persistence adapter.
