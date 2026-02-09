<div align="center">

<img src="./images/logo/logo.png" width="160" alt="LingoLens Logo" />

# 🌍 LingoLens: AI-Powered English Learning

[![API 24+](https://img.shields.io/badge/API%2024+-3ddc84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material You](https://img.shields.io/badge/Material%20You-6200EA?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Hilt](https://img.shields.io/badge/Hilt-Injection-orange?style=for-the-badge)](https://developer.android.com/training/dependency-injection/hilt-android)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)

**LingoLens is a professional, AI-driven language learning ecosystem designed to help you master English through real-world context, instant camera translation, and scientifically-backed memory techniques.**

[**Explore Features**](#✨-core-features) • [**Tech Stack**](#🛠️-technical-stack) • [**Getting Started**](#🚀-getting-started) • [**Privacy Policy**](./Privacy.md)

---

<img src="./images/banner/banner1.png" width="100%" alt="LingoLens Banner" />

<p align="center">
  <img src="./images/phoneScreenshots/01.png" width="13%" alt="Intuitive Dashboard" />
  <img src="./images/phoneScreenshots/02.png" width="13%" alt="Real-time Camera OCR" />
  <img src="./images/phoneScreenshots/03.png" width="13%" alt="WordCraftAI Deep Explanations" />
  <img src="./images/phoneScreenshots/04.png" width="13%" alt="SRS Flashcard System" />
  <img src="./images/phoneScreenshots/05.png" width="13%" alt="Interactive Quizzes" />
  <img src="./images/phoneScreenshots/06.png" width="13%" alt="Categorized Phrasebook" />
  <img src="./images/phoneScreenshots/07.png" width="13%" alt="Customizable Material You Themes" />
</p>
<p align="center">
  <i>1. Dashboard • 2. Camera OCR • 3. WordCraftAI • 4. SRS Learning • 5. Quizzes • 6. Phrasebook • 7. Personalization</i>
</p>

</div>

---

## ✨ Core Features

### 📸 Smart Camera Translator (OCR)
- **Instant Recognition:** Point your camera at any surface—books, signs, or screens—to extract and translate text.
- **Interactive Bounding Boxes:** Select specific words or paragraphs directly from the image.
- **Contextual Learning:** Immediately send any scanned text to **WordCraftAI** for a deeper dive.

### 🧠 WordCraftAI: Your Personal Tutor
- **Deep Explanations:** Get more than just a translation. Understand grammar, usage, and nuance.
- **Sentence Generator:** See how words are used in real-world scenarios with AI-generated examples.
- **Smart Insights:** Discover synonyms, antonyms, and linguistic roots.

### 🃏 Vocabulary Mastery (SRS)
- **Scientific Learning:** Uses a **Spaced Repetition System (SRS)** engine to optimize your memory retention.
- **Level-Based Progression:** Move from "Novice" to "Master" as you interact with your vocabulary stacks.
- **Engaging Flashcards:** Beautifully animated cards designed for quick daily reviews.

### ✍️ Multi-Modal Practice
- **Speech-to-Text:** Practice speaking and get feedback on your pronunciation.
- **Text-to-Speech:** Listen to natural-sounding native pronunciations (British & American).
- **Gamified Quizzes:** Typing drills, multiple-choice, and listening tests with real-time scoring.

---

## 🛠️ Technical Stack

LingoLens is built using the latest industry standards for Android development, ensuring a smooth, scalable, and maintainable experience.

| Category | Technologies |
|----------|--------------|
| **Language** | Kotlin (Coroutines, Flow) |
| **UI Framework** | Jetpack Compose, Material 3 (Material You) |
| **Architecture** | MVVM, Clean Architecture, Multi-Module |
| **DI** | Hilt (Dagger) |
| **Networking** | Retrofit, OkHttp |
| **Persistence** | Room (Multi-Database), DataStore |
| **AI & ML** | Google ML Kit (OCR), GPT-4o, Gemini, Groq |
| **Animation** | Lottie, Compose Animation |
| **Utilities** | Coil (Images), Firebase (Analytics, Crashlytics) |

---

## 🏗️ Project Structure

The project follows a modular **Clean Architecture** approach to maximize code reuse and maintainability:

```text
├── app/                # Main entry, Navigation, DI Setup
├── core/               # Shared logic
│   ├── data/           # Repositories & Databases
│   ├── domain/         # Use Cases & Business Models
│   ├── ui/             # Shared Composables & Theme
│   └── utils/          # Extensions & Helper classes
└── features/           # Independent Feature Modules
    ├── ocr/            # Camera & Text Recognition
    ├── wordcraftai/    # AI Explanation Engine
    ├── translation/    # Core Translation Services
    └── stackcard/      # SRS & Learning Logic
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later.
- Android SDK API 24+.
- A device/emulator with Camera support (for OCR).

### Installation
1. **Clone the Repo:**
   ```bash
   git clone https://github.com/AmgadGhozzy/LingoLens.git
   ```
2. **Open in Android Studio:** Let Gradle sync all dependencies.
3. **Configuration:** Ensure you have a valid `google-services.json` in the `app/` directory if you wish to use Firebase features.
4. **Run:** Deploy to your device and start learning!

---

## 🤝 Contributing

We welcome contributions! Whether you're fixing a bug, adding a feature, or improving documentation, your help is appreciated.

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## ❓ FAQ

**Q: Does LingoLens support offline translation?**
A: Yes! Basic translation and vocabulary practice features are available offline. Advanced AI features like WordCraftAI require an internet connection.

**Q: Is the App free to use?**
A: LingoLens offers a robust set of core features for free. We also offer a Premium subscription for an ad-free experience and unlimited access to advanced AI tools.

**Q: Which AI models are used?**
A: We integrate multiple state-of-the-art models including GPT-4o, Gemini, and DeepSeek to ensure the highest accuracy for linguistic explanations.

**Q: Is my data safe?**
A: Absolutely. Most of your learning data is stored locally on your device. For more details, see our [Privacy Policy](./Privacy.md).

---

## ⚖️ License & Contact

Distributed under the **Apache License 2.0**. See `LICENSE` for more information.

📧 **Developer:** Amgad Ghozzy - [AmgadGhozzy@gmail.com](mailto:AmgadGhozzy@gmail.com)
🔗 **LinkedIn:** [amgadghozzy](https://www.linkedin.com/in/amgadghozzy)

---

<div align="center">
  <b>Made with ❤️ for English Learners everywhere.</b>
</div>
