package com.venom.data.mock

import com.venom.domain.model.CefrLevel
import com.venom.domain.model.RelatedWords
import com.venom.domain.model.WordFamily
import com.venom.domain.model.WordMaster

object MockWordData {

    // 1. A2 - Journey (The Essential Movement)
    val journeyWord = WordMaster(
        id = 105,
        wordEn = "journey",
        pos = "noun",
        cefrLevel = CefrLevel.A2,
        fromOxford = 1,
        rank = 850,
        frequency = 1,
        difficultyScore = 2,
        phoneticUs = "ˈdʒɜːrni",
        phoneticUk = "ˈdʒɜːni",
        phoneticAr = "جِيرْنِي",
        translit = "riḥla",
        syllabify = "jour•ney",
        definitionEn = "An act of traveling from one place to another, especially when they are far apart.",
        definitionAr = "فعل الانتقال من مكان إلى آخر، خاصة عندما تكون المسافات بعيدة.",
        primarySense = "Movement",
        semanticTags = listOf("#Travel", "#Adventure", "#Life"),
        usageNote = "A 'journey' is usually longer than a 'trip'. It often carries a deeper meaning, like a life journey.",
        register = "Neutral",
        category = "Travel & Movement",
        mnemonicAr = "افتكر إنك في الـ (Journey) بتحتاج (Gear) أو معدات عشان تروح (Near) و (Far).",
        wordFamily = WordFamily(noun = "journey", verb = "journey"),
        synonyms = listOf("trip", "voyage", "expedition"),
        antonyms = listOf("stasis", "stay"),
        examples = mapOf(
            CefrLevel.A1 to "It's a long journey from London to New York.",
            CefrLevel.B2 to "She documented her spiritual journey in a book."
        ),
        collocations = listOf("long journey", "safe journey", "spiritual journey"),
        relatedWords = RelatedWords(
            english = listOf("travel", "destination", "ticket"),
            arabic = listOf("سفر", "وجهة", "تذكرة")
        ),
        arabicAr = "رحلة",
        frenchFr = "voyage",
        germanDe = "Reise",
        spanishEs = "viaje",
        chineseZh = "旅程 (lǚchéng)",
        russianRu = "путешествие (puteshestviye)",
        portuguesePt = "jornada",
        japaneseJa = "旅 (tabi)",
        italianIt = "viaggio",
        turkishTr = "yolculuk"
    )

    // 2. B1 - Curious (The Cognitive Trigger)
    val curiousWord = WordMaster(
        id = 2140,
        wordEn = "curious",
        pos = "adjective",
        cefrLevel = CefrLevel.B1,
        fromOxford = 1,
        rank = 1800,
        frequency = 3,
        difficultyScore = 4,
        phoneticUs = "ˈkjʊriəs",
        phoneticUk = "ˈkjʊəriəs",
        phoneticAr = "كِيُورِيَسْ",
        translit = "fudūlī",
        syllabify = "cu•ri•ous",
        definitionEn = "Eager to know or learn something.",
        definitionAr = "الرغبة الشديدة في معرفة أو تعلم شيء ما.",
        primarySense = "Mind",
        semanticTags = listOf("#Learning", "#Intelligence", "#Personality"),
        usageNote = "Can mean both 'eager to learn' and 'strange'. In Science, it often means unusual.",
        register = "Neutral",
        category = "Mind & Thinking",
        mnemonicAr = "الشخص الـ (Curious) دايماً بيحب يـ (Cure) أو يشفي جهله بالأسئلة الكتير.",
        wordFamily = WordFamily(noun = "curiosity", adj = "curious", adv = "curiously"),
        synonyms = listOf("inquisitive", "nosy", "strange"),
        antonyms = listOf("indifferent", "bored"),
        examples = mapOf(
            CefrLevel.A2 to "I was curious about what was inside the box.",
            CefrLevel.C1 to "It is a curious fact that humans share 50% of DNA with bananas."
        ),
        collocations = listOf("curious mind", "intellectually curious", "curious about"),
        relatedWords = RelatedWords(
            english = listOf("wonder", "inquiry", "interest"),
            arabic = listOf("تساؤل", "تحقيق", "اهتمام")
        ),
        arabicAr = "فضولي",
        frenchFr = "curieux",
        germanDe = "neugierig",
        spanishEs = "curioso",
        chineseZh = "好奇 (hàoqí)",
        russianRu = "любопытный (lyubopytnyy)",
        portuguesePt = "curioso",
        japaneseJa = "好奇心旺盛な (kōkishin ōseina)",
        italianIt = "curioso",
        turkishTr = "meraklı"
    )

    // 3. B2 - Evaluate (The Business/Academic Engine)
    val evaluateWord = WordMaster(
        id = 3520,
        wordEn = "evaluate",
        pos = "verb",
        cefrLevel = CefrLevel.B2,
        fromOxford = 1,
        rank = 2500,
        frequency = 4,
        difficultyScore = 6,
        phoneticUs = "ɪˈvæljueɪt",
        phoneticUk = "ɪˈvæljueɪt",
        phoneticAr = "إِيفَالِيُوِيتْ",
        translit = "yuqayyim",
        syllabify = "e•val•u•ate",
        definitionEn = "To form an idea of the amount, number, or value of something; assess.",
        definitionAr = "تكوين فكرة عن كمية أو عدد أو قيمة شيء ما؛ التقييم.",
        primarySense = "Measure",
        semanticTags = listOf("#Analysis", "#Education", "#Work"),
        usageNote = "'Evaluate' is formal. It focuses on the judgment of quality or value.",
        register = "Formal",
        category = "Work & Business",
        mnemonicAr = "كلمة (Evaluate) فيها (Value) يعني قيمة.. فأنا بـ (Evaluate) عشان أحدد القيمة.",
        wordFamily = WordFamily(noun = "evaluation", verb = "evaluate", adj = "evaluative"),
        synonyms = listOf("assess", "appraise", "judge"),
        antonyms = listOf("neglect", "ignore"),
        examples = mapOf(
            CefrLevel.B2 to "We need to evaluate the risks before investing.",
            CefrLevel.C2 to "Strict criteria were used to evaluate performance."
        ),
        collocations = listOf("evaluate performance", "carefully evaluate", "reevaluate"),
        relatedWords = RelatedWords(
            english = listOf("score", "criteria", "judgment"),
            arabic = listOf("درجة", "معايير", "حكم")
        ),
        arabicAr = "يقيّم",
        frenchFr = "évaluer",
        germanDe = "bewerten",
        spanishEs = "evaluar",
        chineseZh = "评价 (píngjià)",
        russianRu = "оценивать (otsenivat')",
        portuguesePt = "avaliar",
        japaneseJa = "評価する (hyōka suru)",
        italianIt = "valutare",
        turkishTr = "değerlendirmek"
    )

    // 4. C1 - Resilient (The Emotional Shield)
    val resilientWord = WordMaster(
        id = 5890,
        wordEn = "resilient",
        pos = "adjective",
        cefrLevel = CefrLevel.C1,
        fromOxford = 1,
        rank = 4200,
        frequency = 5,
        difficultyScore = 7,
        phoneticUs = "rɪˈzɪliənt",
        phoneticUk = "rɪˈzɪliənt",
        phoneticAr = "رِزِيلِيَنْتْ",
        translit = "marin",
        syllabify = "re•sil•ient",
        definitionEn = "Able to withstand or recover quickly from difficult conditions.",
        definitionAr = "القدرة على الصمود أو التعافي بسرعة من الظروف الصعبة.",
        primarySense = "Quality",
        semanticTags = listOf("#Strength", "#MentalHealth", "#Nature"),
        usageNote = "Common in psychology and engineering to describe systems or people that bounce back.",
        register = "Neutral",
        category = "Emotions & Feelings",
        mnemonicAr = "الشخص الـ (Resilient) زي السوستة (Silent but strong).. مهما تضغط عليه بيرجع أقوى.",
        wordFamily = WordFamily(noun = "resilience", adj = "resilient", adv = "resiliently"),
        synonyms = listOf("tough", "flexible", "hardy"),
        antonyms = listOf("fragile", "vulnerable"),
        examples = mapOf(
            CefrLevel.B2 to "She is a resilient girl who overcame many problems.",
            CefrLevel.C2 to "The economy is surprisingly resilient despite the global crisis."
        ),
        collocations = listOf("resilient economy", "highly resilient", "resilient spirit"),
        relatedWords = RelatedWords(
            english = listOf("rebound", "survivor", "grit"),
            arabic = listOf("ارتداد", "ناجي", "ثبات")
        ),
        arabicAr = "صامد",
        frenchFr = "résilient",
        germanDe = "belastbar",
        spanishEs = "resiliente",
        chineseZh = "韧性 (rènxìng)",
        russianRu = "жизнестойкий (zhiznestoykiy)",
        portuguesePt = "resiliente",
        japaneseJa = "回復力のある (kaifukuryoku no aru)",
        italianIt = "resiliente",
        turkishTr = "esnek / dayanıklı"
    )

    // 5. C2 - Fluctuate (The Sophisticated Change)
    val fluctuateWord = WordMaster(
        id = 6500,
        wordEn = "fluctuate",
        pos = "verb",
        cefrLevel = CefrLevel.C2,
        fromOxford = 1,
        rank = 5800,
        frequency = 6,
        difficultyScore = 9,
        phoneticUs = "ˈflʌktʃueɪt",
        phoneticUk = "ˈflʌktʃueɪt",
        phoneticAr = "فْلَكْتْشُوِيتْ",
        translit = "yataḏaḏḏab",
        syllabify = "fluc•tu•ate",
        definitionEn = "Rise and fall irregularly in number or amount.",
        definitionAr = "الارتفاع والانخفاض بشكل غير منتظم في العدد أو القيمة.",
        primarySense = "Change",
        semanticTags = listOf("#Economics", "#Data", "#Nature"),
        usageNote = "Exclusively used for values like temperature, prices, or statistics. Not for movement.",
        register = "Academic",
        category = "Numbers, Time & Math",
        mnemonicAr = "كلمة (Fluctuate) فيها (Flu) زي الانفلونزا.. حرارة جسمك فيها بتطلع وتنزل طول الوقت.",
        wordFamily = WordFamily(noun = "fluctuation", verb = "fluctuate"),
        synonyms = listOf("vary", "waver", "oscillate"),
        antonyms = listOf("stabilize", "remain constant"),
        examples = mapOf(
            CefrLevel.C1 to "Oil prices continue to fluctuate on the world market.",
            CefrLevel.C2 to "Student interest fluctuated wildly throughout the academic year."
        ),
        collocations = listOf("fluctuating prices", "widely fluctuate", "seasonal fluctuations"),
        relatedWords = RelatedWords(
            english = listOf("graph", "volatility", "unstable"),
            arabic = listOf("رسم بياني", "تقلب", "غير مستقر")
        ),
        arabicAr = "يتقلب",
        frenchFr = "fluctuer",
        germanDe = "schwanken",
        spanishEs = "fluctuar",
        chineseZh = "波动 (bōdòng)",
        russianRu = "колебаться (kolebat'sya)",
        portuguesePt = "flutuar",
        japaneseJa = "変動する (hendō suru)",
        italianIt = "fluttuare",
        turkishTr = "dalgalanmak"
    )

    /**
     * Complete mock list for testing swipe decks and navigation.
     */
    val mockWordList: List<WordMaster> = listOf(
        journeyWord,
        curiousWord,
        evaluateWord,
        resilientWord,
        fluctuateWord
    )
}