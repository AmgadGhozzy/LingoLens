# LingoLens

<div align="center">
  <img src="./screenshots/logo.png" width="200" />
</div>

<h1 align="center">LingoLens: Advanced Language Translation & Learning Tool</h1>

## Overview
LingoLens is a comprehensive language translation and learning application that combines powerful translation capabilities with interactive learning features. The app leverages advanced OCR technology, real-time speech recognition, and extensive dictionary integration to provide users with a seamless language learning experience across multiple languages.

## Key Features

### Transliteration & Translation
- **Advanced Text Translation**: Translate text between multiple languages with high accuracy
- **Real-time Transliteration**: Convert text from one script to another while preserving pronunciation
- **Voice-to-Text Translation**: Speak in one language and get instant translations
- **Language Detection**: Automatically detect the source language of input text
- **Offline Translation Support**: Access basic translation features without internet connection

### OCR Image Translation
- **Text Recognition**: Extract text from images using advanced OCR technology
- **Image Cropping**: Crop images to focus on specific text areas for better recognition
- **Bounding Box Selection**: Select specific text portions from recognized text in images
- **Paragraph Mode**: Detect and extract complete paragraphs from images
- **Error Recovery**: Intelligent error handling for poor quality images

### Interactive Learning
- **Swipeable Word Cards**: Learn vocabulary through interactive flashcard system
- **Floating Word Cards**: Keep words accessible even outside the app
- **Custom Word Groups**: Create and organize vocabulary by themes or difficulty
- **Word Typing Practice**: Improve spelling and typing skills
- **Custom Quizzes**: Test your knowledge with adjustable parameters
- **Quiz Levels**: Progress through different difficulty levels (Easy, Intermediate, Advanced)
- **Multi-format Tests**: Grammar, sentence completion, situations, TOEFL, IELTS, and more
- **Real-time Scoring**: Immediate feedback on quiz performance

[//]: # (### Dictionary & Reference)

[//]: # (- **Oxford Dictionary Integration**: Access comprehensive word definitions)

[//]: # (- **Wikipedia Integration**: Get additional contextual information about terms)

[//]: # (- **Thesaurus View**: Find synonyms and related words)

[//]: # (- **Example Sentences**: See words used in context)

[//]: # (- **Multi-language Dictionary**: Look up words across different languages)

[//]: # (- **Word Categories**: Browse words by topic or category)

### Speech & Pronunciation
- **Text-to-Speech**: Listen to correct pronunciation of words and phrases
- **Speech Recognition**: Practice pronunciation with immediate feedback
- **Voice Input**: Dictate text instead of typing

### Organization & Personalization
- **Bookmarks**: Save important words, phrases, or translations
- **History Tracking**: Review past translations and searches
- **Custom Categories**: Organize content by user-defined categories
- **Search Functionality**: Quickly find previously translated content
- **Customizable UI**: Adjust themes, fonts, and display settings

### Accessibility & User Experience
- **Material You Design**: Modern and responsive user interface
- **Dynamic Color Schemes**: Personalized color options with dark mode support
- **Font Customization**: Multiple font styles for better readability
- **Multi-language UI**: Interface available in multiple languages
- **Drag Handles**: Intuitive UI manipulation
- **Bottom Sheets**: Convenient access to additional features

## Built With 🛠

- [Kotlin](https://kotlinlang.org/): Primary programming language for Android development
- [Jetpack Compose](https://developer.android.com/jetpack/compose): Modern UI toolkit for native Android UI
- [Android Architecture Components](https://developer.android.com/topic/architecture): Robust app architecture
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): UI-related data management
  - [Navigation component](https://developer.android.com/guide/navigation): In-app navigation handling
  - [Room Database](https://developer.android.com/training/data-storage/room): Local database storage
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata): Observable data holder patterns
  - [Data Binding](https://developer.android.com/topic/libraries/data-binding): Declarative UI binding
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines): Asynchronous programming
- [Retrofit](https://square.github.io/retrofit/): Type-safe HTTP client
- [MVVM Architecture](https://developer.android.com/topic/architecture): Separation of concerns pattern
- [Material 3 Design](https://m3.material.io/): Latest Material Design guidelines
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android): Dependency injection
- [ML Kit](https://developers.google.com/ml-kit): Machine learning for mobile developers
- [CameraX](https://developer.android.com/training/camerax): Camera API simplification
- [Firebase](https://firebase.google.com/): Analytics, crash reporting, and cloud services

## Technical Architecture

The app follows a modular architecture with distinct feature modules:

- **core/ui**: Common UI components and themes
- **features/dialog**: Conversational practice functionality
- **features/ocr**: Image text recognition and translation
- **features/phrase**: Phrasebook and language resources
- **features/settings**: User preferences and configuration
- **features/stackcard**: Flashcard system for vocabulary building
- **features/translation**: Core translation functionality

## Permissions Required

- Camera: For OCR text recognition
- Microphone: For speech recognition and input
- Storage: For saving and loading user data
- Internet: For translation services and dictionary lookups

## Getting Started

### Installation
1. Clone the repository: `git clone https://github.com/AmgadGhozzy/LingoLens.git`
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Support & Feedback
For questions, feedback, or support, please contact us at [support@lingolens.app](mailto:support@lingolens.app)

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
LingoLens is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
