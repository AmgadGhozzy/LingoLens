<div align="center">

<img src="./images/logo/logo.png" width="200" alt="LingoLens Logo" />

# 🌍 LingoLens – AI English Learning & Translator

![API 21+](https://img.shields.io/badge/API%2021+-3ddc84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material You](https://img.shields.io/badge/Material%20You-6200EA?style=for-the-badge&logo=materialdesign&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

[![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen?style=for-the-badge&logo=semantic-release)](https://github.com/AmgadGhozzy/LingoLens/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange?style=for-the-badge&logo=apache&logoColor=white)](https://opensource.org/licenses/Apache-2.0)

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=com.venom.lingolens)

# 🗺️ Project Overview

**A smart, feature-rich, AI-powered app that helps users translate, learn and practice English with tools like real-time OCR, speech translation, flashcard quizzes, sentence builders, and more.**

Built with **Jetpack Compose**, **MVVM architecture**, and **modern Android libraries** for a smooth, accessible, and customizable learning experience.


</div>

<p align="middle">
    <img src="./images/banner/banner1.png" width="99%" alt="LingoLens Banner" />
    <img src="./images/phoneScreenshots/01.png" width="13%" />
    <img src="./images/phoneScreenshots/02.png" width="13%" />
    <img src="./images/phoneScreenshots/03.png" width="13%" />
    <img src="./images/phoneScreenshots/04.png" width="13%" />
    <img src="./images/phoneScreenshots/05.png" width="13%" />
    <img src="./images/phoneScreenshots/06.png" width="13%" />
    <img src="./images/phoneScreenshots/07.png" width="13%" />
</p>

## ✨ Features

### 📖 Translation & Transliteration
- **Instant Translation**: Translate text, voice, and images in real-time
- **Voice Support**: Speak and hear accurate translations with natural pronunciation
- **Multiple Engines**: Choose between Google Translate, DeepL, and other translation services
- **Script Transliteration**: Convert between different writing systems
- **Offline Mode**: Basic translation features work without internet connection

### 📷 OCR Image Translation
- **Smart Text Detection**: Extract and translate text from photos or screenshots
- **Flexible Scanning**: Process full paragraphs or focus on specific words
- **Interactive Selection**: Crop, select, and interact with detected text using bounding boxes

### 🧠 Vocabulary & Learning Tools
- **Interactive Flashcards**: Swipe through engaging word cards for vocabulary building
- **Floating Word Cards**: Access vocabulary tools from anywhere in the app
- **Level-Based Learning**: Progress through structured word stacks by difficulty
- **Typing Practice**: Improve spelling with interactive typing drills
- **Custom Quizzes**: Create personalized tests with real-time scoring and feedback
- **Multi-Modal Practice**: Speaking and listening exercises included

### ✍️ Sentence Tools & Phrasebook
- **Extensive Phrase Library**: Access 1000+ categorized phrases for travel, shopping, business, and daily conversations
- **Sentence Builder**: Construct grammatically correct sentences with guided assistance
- **Native Pronunciation**: Search YouTube for real-world pronunciation examples

### 🗣️ Speech Practice
- **Text-to-Speech (TTS)**: Listen to natural English pronunciation
- **Speech-to-Text (STT)**: Practice speaking for translation and vocabulary building
- **Pronunciation Feedback**: Get instant feedback on your pronunciation accuracy

### ⚙️ Personalization & Settings
- **Theme Customization**: Choose from multiple themes, colors, and font options
- **Multi-Language Interface**: Support for various interface languages
- **Personal Library**: Save favorites and view translation history
- **Progress Tracking**: Monitor learning statistics and performance metrics

## 🛠️ Tech Stack

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material You](https://img.shields.io/badge/Material%20You-6200EA?style=for-the-badge&logo=materialdesign&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

</div>

### Core Technologies
- **[Kotlin](https://kotlinlang.org/)** - Modern, expressive programming language for Android
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Declarative UI toolkit for native Android apps
- **[MVVM Architecture](https://developer.android.com/jetpack/guide)** - Clean separation of concerns with ViewModel pattern

### Key Libraries & Frameworks
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection framework
- **[Retrofit](https://square.github.io/retrofit/)** - Type-safe HTTP client for REST API integration
- **[Room Database](https://developer.android.com/training/data-storage/room)** - Local persistent storage solution
- **[ML Kit](https://developers.google.com/ml-kit)** - Machine learning capabilities for text recognition
- **[CameraX](https://developer.android.com/camerax)** - Camera functionality and image processing
- **[Firebase](https://firebase.google.com/)** - Crashlytics, Analytics, and Remote Configuration

## 🏗️ Architecture

LingoLens follows a clean, modular architecture designed for scalability and maintainability:

| Module | Purpose |
|--------|---------|
| `core/ui` | Shared UI components, themes, and styling |
| `features/ocr` | Image scanning, text recognition, and OCR functionality |
| `features/translation` | Translation services and language processing tools |
| `features/stackcard` | Flashcards, quizzes, tests, and level progression |
| `features/phrase` | Phrasebook management and contextual learning |
| `features/settings` | User preferences, configurations, and app settings |

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 21+
- Kotlin 1.8+

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/AmgadGhozzy/LingoLens.git
   ```

2. **Open the project in Android Studio**

3. **Sync project with Gradle files**

4. **Build and run the app on your device or emulator**

## 🤝 Contributing

We welcome contributions from the community! Whether it's bug fixes, feature enhancements, or documentation improvements, your help is appreciated.

### How to Contribute
1. **Fork the repository**
2. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature
   ```
3. **Commit your changes:**
   ```bash
   git commit -m "Add new feature"
   ```
4. **Push to the branch:**
   ```bash
   git push origin feature/your-feature
   ```
5. **Open a Pull Request**
<div align="center">

## 💬 Support & Feedback

Need help? Found a bug? Have suggestions?

📧 **Email:** [AmgadGhozzy@gmail.com](mailto:AmgadGhozzy@gmail.com)

We're always looking for ways to improve LingoLens, so please don't hesitate to reach out with your feedback and ideas.

## ⚖️ License

```
Copyright 2025 Amgad Ghozzy
  Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, softwaredistributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the specific language governing permissions andlimitations under the License.
```

---

## 💫 Love LingoLens?

**Star us on GitHub and join our community!**

[![Star Repository](https://img.shields.io/github/stars/AmgadGhozzy/LingoLens?style=for-the-badge&logo=github&color=yellow&logoColor=white)](https://github.com/AmgadGhozzy/LingoLens/stargazers)
[![Follow Developer](https://img.shields.io/github/followers/AmgadGhozzy?style=for-the-badge&logo=github&color=blue&logoColor=white)](https://github.com/AmgadGhozzy)

### *Helping learners become fluent, one word at a time* ✨

### Connect With Us

[![GitHub](https://img.shields.io/badge/GitHub-1A1A1A?style=for-the-badge&logo=github&logoColor=white)](https://github.com/AmgadGhozzy/LingoLens)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/amgadghozzy/)
[![Email](https://img.shields.io/badge/Email-EA4335?style=for-the-badge&logo=gmail&logoColor=white)](mailto:AmgadGhozzy@gmail.com)

**Made with ❤️ by [Amgad Ghozzy](https://www.linkedin.com/in/amgadghozzy)**

</div>