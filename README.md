## 🌟 Welcome to my first project!
I feel very happy that you took the time to visit my project. I hope it can be of some help to you on your path of growth and development.🏆
# 💻 Tech Stack: 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Firebase](https://img.shields.io/badge/firebase-%23039BE5.svg?style=for-the-badge&logo=firebase)  ![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white) ![MicrosoftSQLServer](https://img.shields.io/badge/Microsoft%20SQL%20Server-CC2927?style=for-the-badge&logo=microsoft%20sql%20server&logoColor=white) ![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white) 

## Features

- Online booking system for spa services
- Therapist selection and scheduling
- Service management
- Google Authentication for easy login
- Payment integration with VNPay

## Authentication

The app supports both traditional username/password authentication and Google Sign-In through Firebase Authentication. See [Firebase Setup Guide](./docs/FIREBASE_SETUP.md) for details on setting up Google Authentication.

## Preview

```
Run production build with:

bash
npm run build
npm run start
# or
yarn build
yarn start
```

## Setup

1. Clone the repository
2. Install dependencies: `npm install`
3. Configure environment variables:
   - Copy `.env.local.example` to `.env.local` and fill in the values
   - Add Firebase configuration for Google Authentication
4. Run the development server: `npm run dev`

## Firebase Google Authentication

This project uses Firebase for Google Authentication. To set it up:

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Set up Google Authentication in Firebase
3. Add the Firebase configuration to your `.env.local` file
4. See the detailed [Firebase Setup Guide](./docs/FIREBASE_SETUP.md)
