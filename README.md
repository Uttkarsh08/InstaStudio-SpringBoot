# InstaStudio

## Introduction

Welcome to **InstaStudio**, your all-in-one studio management solution tailored specifically for Indian photographers and clients. From managing bookings and team members to handling payments and client communications, InstaStudio streamlines every part of a photography business in one place—both on the web and mobile.

This monorepo contains the full codebase for both the Android frontend and the Spring Boot backend, making development, collaboration, and CI/CD setup more efficient and centralized.

## Features

- 📸 **Client Booking & Event Scheduling**: Clients can book services, select event types, and manage schedules.
- 👥 **Team & Role Management**: Admins can assign roles and manage studio team members with proper permissions.
- 💰 **Payment Integration**: Built-in UPI, Razorpay, and Stripe support for seamless transaction experiences.
- 🛡️ **Secure Authentication**: JWT-based login system with refresh/access token mechanism.
- 🔔 **Notifications**: Real-time alerts for booking updates, payments, and new messages.
- 📊 **Dashboard Analytics**: View earnings, booking trends, and team performance all in one place.

## Project Structure

```bash
InstaStudio/
├── android/       # Jetpack Compose Android App
├── backend/       # Spring Boot Backend with RESTful APIs
└── .gitmodules    # Submodule definitions
```

## How to Get Started

- 1. Clone the Monorepo with submodules
```bash
git clone --recurse-submodules https://github.com/Uttkarsh08/InstaStudio.git
```
- 2. Navigate into the project folder
```bash
cd InstaStudio
```

- 3. Initialize and update submodules
```bash
git submodule update --init --recursive
```

### Android Setup

- Open the 'android' folder in Android Studio
```bash
cd android
```

-**Then**:
 - Build the project
 - Connect your emulator or Android device
 - Run the app


### Backend Setup

- Navigate to the backend folder
```bash
cd backend
```

- Make sure you have Java 17+ and Maven installed
- Then run the Spring Boot app:
```bash
./mvnw spring-boot:run
```

## Tech Stack

- **Frontend (Android)**:
  - Language: Kotlin
  - UI Framework: Jetpack Compose
  - IDE: Android Studio

- **Backend**:
  - Language: Java
  - Framework: Spring Boot
  - Security: Spring Security + JWT (Access & Refresh Tokens)
  - Database: PostgreSQL
  - Build Tool: Maven

- **Monorepo Tools**:
  - Git Submodules for managing `android/` and `backend/`
