# Onboarding Queue Mini Program Backend

Java/Spring Boot backend that powers a WeChat mini program for onboarding appointments and in-person queue management. It supports:

- Configurable booking window (X weeks ahead) and daily time slots with capacity limits.
- Booking, availability lookup, and automatic over-capacity protection.
- 24-hour pre-arrival reminders (WeChat template/subscription message integration points). 
- Universal QR-based check-in with automatic booking fallback and denial logging when full.
- Queue operations (call/skip/serve) and statistics for admins inside the mini program via a static token.

## How the backend is organized

- `pom.xml` — Spring Boot + validation + scheduling dependencies.
- `src/main/java/com/example/onboardingqueue` — application code.
  - `config` — application properties and admin token interceptor.
  - `controller` — public and admin REST endpoints for the mini program.
  - `model` — booking/slot/queue/stat view models.
  - `repository` — in-memory data store (swap with a database later).
  - `service` — booking, queueing, scheduling, check-in, reminder, and stats logic.
- `src/main/resources/application.yml` — default admin token (`letmein`), booking window, and reminder settings.

## REST endpoints summary

All admin endpoints require `X-Admin-Token: <static-token>`.

### Public
- `GET /api/config` — current booking window and slot templates.
- `GET /api/slots?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` — availability with capacity/booking counts.
- `POST /api/bookings` — body `{openId, date, slotLabel}` to reserve a slot.
- `POST /api/check-in` — body `{openId}` triggered when scanning the universal QR; auto-books if space remains.

### Admin
- `PUT /api/admin/config/templates` — update slot templates, e.g. `[ {"label":"上午","startTime":"09:00","endTime":"12:00","capacity":20} ]`.
- `PUT /api/admin/config/weeks-ahead?weeksAhead=4` — adjust booking window.
- `GET /api/admin/bookings?date=YYYY-MM-DD` — list bookings for the day.
- `GET /api/admin/queue?date=YYYY-MM-DD&slotLabel=上午` — inspect queue.
- `POST /api/admin/queue/call-next` — parameters `date`, `slotLabel`; marks next as CALLED and sends reminder log.
- `POST /api/admin/queue/skip` — parameters `date`, `slotLabel`, `number`; mark skipped.
- `POST /api/admin/queue/serve` — parameters `date`, `slotLabel`, `number`; mark served.
- `POST /api/admin/remind` — body `{openId, date, startTime, number}` to push a manual queue reminder.
- `GET /api/admin/stats` — bookings, check-ins, pending queue size, and denial counts.

## Running locally

```bash
./mvnw spring-boot:run   # or: mvn spring-boot:run
```

The app starts on `http://localhost:8080`. Adjust defaults in `src/main/resources/application.yml`.


### Frontend (Umi + Ant Design Pro)

A lightweight React/H5 front-end suitable for embedding in a WeChat 小程序 WebView lives under `frontend/`. It provides:

- `/` — user booking form that posts `openId` / date / slot to the backend.
- `/admin` — hidden admin surface unlocked via the static token; supports slot configuration, queue actions (叫号/过号/提醒), and stats viewing.

Quick start:

```bash
cd frontend
npm install
npm start          # runs at http://localhost:8000 by default
# npm run build    # outputs production assets to dist/
```

Configure the backend URL with the `BASE_URL` environment variable when deploying behind a different host.

## Universal QR code flow

1. Generate a static mini program QR (or “场景码”) that launches a hidden admin/scan page in your mini program.
2. That page reads the user’s `openId`, calls `POST /api/check-in`, and shows the returned queue number or a “full” message.
3. Admin token entry can live in the same hidden page; once entered, store it securely (e.g., local storage) and attach it as `X-Admin-Token` on admin API calls to reveal the management UI.

## 24-hour reminders

`NotificationService` runs every 15 minutes to find bookings whose start time is ~24 hours away (configurable via `app.reminder-hours-before`) and logs a reminder hook. Replace the logging call with your WeChat subscription/template message sender.

## Deploying to WeChat Mini Program — end-to-end tutorial

1. **Backend build & deploy**
   - Install Java 17 and Maven.
   - `mvn -DskipTests package` to produce `target/onboarding-queue-0.0.1-SNAPSHOT.jar`.
   - Deploy the JAR to your server (Linux VM, K8s, or Serverless) and expose port 8080 (or configure `server.port`).
   - Configure environment variables or `application.yml` for `app.admin-token`, `app.weeks-ahead`, and `app.reminder-hours-before`.
   - (Optional) Put NGINX/Ingress in front with HTTPS; WeChat requires HTTPS for production traffic.

2. **Mini program project setup (WeChat Developer Tools)**
   - Create a new mini program project and enable **Cloud/Server** calls.
   - In `app.js` or a global service, store the backend base URL (e.g., `https://your-domain/api`).
   - Implement login to obtain `openId` via `wx.login` + your auth service; pass `openId` to backend bookings.

3. **User booking page**
   - On load, call `GET /api/config` and `GET /api/slots` to render the next X weeks of available dates and time slots.
   - Disable “Book” buttons when `booked >= capacity`.
   - On booking, call `POST /api/bookings` with `openId`, selected `date`, and `slotLabel`; show confirmation.
   - Subscribe the user to a 24-hour reminder template message when they book.

4. **Check-in (universal QR) page**
   - Page is launched via the static QR’s scene parameter.
   - Call `POST /api/check-in` with `openId` immediately after page load; display success message and queue number or “full” prompt.

5. **Hidden admin entrance**
   - Add a gesture/long-press area; prompt for the static admin token.
   - Store the token locally and attach it as `X-Admin-Token` for admin API calls.
   - Admin UI can surface:
     - Slot configuration form tied to `PUT /api/admin/config/templates` and `/weeks-ahead`.
     - Booking list for the day.
     - Queue board with actions that call `/queue/call-next`, `/queue/skip`, `/queue/serve`, and `/remind`.
     - Stats screen powered by `GET /api/admin/stats`.

6. **Message templates**
   - Create two subscription/template messages in the WeChat console: **Booking reminder (24h)** and **Queue called**.
   - Wire them into `NotificationService` and `manualReminder` calls by replacing the logging statements with API invocations to WeChat’s `subscribeMessage.send` or `uniformMessage.send`.

7. **Production readiness checklist**
   - Replace the in-memory repository with MySQL/Redis to survive restarts.
   - Add authentication for your login service (session + token exchange) before trusting `openId` from the client.
   - Configure HTTPS certificates and domain ICP filing as required by WeChat.
   - Add rate limiting for booking/check-in endpoints to prevent abuse.
   - Set up monitoring on the reminder scheduler to ensure it runs as expected.

## Default static admin token

For local testing the token is `letmein`. Override via environment variable:

```bash
export APP_ADMIN_TOKEN=super-secret
mvn spring-boot:run
```

## Data persistence note

The current repository is in-memory for simplicity. Swap `InMemoryRepository` with JDBC/JPA/Redis implementations to keep data across restarts and to scale horizontally.
