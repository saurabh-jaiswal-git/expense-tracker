# Expense Tracker Monorepo

This monorepo contains both the backend (Spring Boot, Java) and the mobile app (React Native + Expo) for the AI-powered expense tracker.

---

## 📦 Structure

```
expense-tracker/
├── backend/   # Spring Boot Java backend
├── mobile/    # React Native (Expo) mobile app (iOS & Android)
```

---

## 🚀 Running the Full Stack App

### 1. Backend (Spring Boot)

Open a new terminal:
```sh
cd backend
./mvnw spring-boot:run
```
- The backend will be available at `http://localhost:8080` by default.

### 2. Mobile App (React Native + Expo)

In another terminal:
```sh
cd mobile
npm start
# or
npx expo start
```
- Scan the QR code with the **Expo Go** app on your iPhone or Android device, or run on an emulator with `npm run ios` or `npm run android`.

---

## 🔗 Connecting Mobile App to Backend

- **On a real device:**
  - Your backend must be accessible from your phone (use your computer's local IP, e.g., `http://192.168.x.x:8080`).
  - Set the API base URL in your mobile app (see below).
- **On an emulator:**
  - Android: Use `http://10.0.2.2:8080` as the backend URL.
  - iOS: Use your Mac's IP or `localhost` if running on the same machine.

### API Base URL Configuration
- Create a file in `mobile/` called `.env`:
  ```env
  API_BASE_URL=http://192.168.x.x:8080
  ```
- Use a library like `react-native-dotenv` or Expo's `expo-constants` to load this in your app code.

---

## 📱 Development Workflow
- Run both backend and mobile app simultaneously.
- The mobile app will call backend APIs for all data and analytics.
- Update the API base URL as needed for your network setup.

---

## 📚 More Info
- Backend documentation: see `backend/README.md` and other docs in `backend/`
- Mobile app: see `mobile/README.md` (to be created)

---

Happy building! 🚀
