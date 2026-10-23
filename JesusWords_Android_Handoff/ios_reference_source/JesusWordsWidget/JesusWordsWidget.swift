import WidgetKit
import SwiftUI
import AppIntents

// MARK: - Verse data

struct WidgetVerse: Decodable {
    let quote: String
    let reference: String
    let tamilQuote: String?
    let tamilReference: String?

    func quote(in language: VerseLanguage) -> String {
        language == .tamil ? (tamilQuote ?? quote) : quote
    }

    func reference(in language: VerseLanguage) -> String {
        language == .tamil ? (tamilReference ?? reference) : reference
    }
}

enum VerseStore {
    private static let all: [WidgetVerse] = {
        guard let url = Bundle.main.url(forResource: "jesus_words", withExtension: "json"),
              let data = try? Data(contentsOf: url),
              let verses = try? JSONDecoder().decode([WidgetVerse].self, from: data) else { return [] }
        return verses
    }()

    // Same rule as the app's Today screen, so the widget always matches it.
    static func verse(for date: Date) -> WidgetVerse? {
        guard !all.isEmpty else { return nil }
        let day = Calendar.current.ordinality(of: .day, in: .year, for: date) ?? 1
        return all[(day - 1) % all.count]
    }
}

// MARK: - Configuration (Edit Widget)

enum VerseLanguage: String, AppEnum {
    case english, tamil

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Language"
    static var caseDisplayRepresentations: [VerseLanguage: DisplayRepresentation] = [
        .english: "English",
        .tamil: "தமிழ் (Tamil)",
    ]
}

enum VerseDesign: String, AppEnum, CaseIterable {
    case dailySurprise
    case gatesOfHeaven, riverOfLife, goodShepherd, holySpirit, resurrection
    case treeOfLife, newJerusalem, stillWaters, starOfBethlehem, rainbowPromise
    case easterDawn, crystalParadise, pentecostLight
    case sacredHeart, crownOfGlory, nativityStar, roseOfSharon, gardenOfGrace
    case littleBlessings, lavenderGrace, friendsOnHigh, smilingSkies, stackedJoy

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Design"
    static var caseDisplayRepresentations: [VerseDesign: DisplayRepresentation] = [
        .dailySurprise: "Daily Surprise (new every day)",
        .gatesOfHeaven: "Gates of Heaven",
        .riverOfLife: "River of Life",
        .goodShepherd: "The Good Shepherd",
        .holySpirit: "Holy Spirit Glow",
        .resurrection: "Resurrection Glory",
        .treeOfLife: "Tree of Life",
        .newJerusalem: "New Jerusalem",
        .stillWaters: "Still Waters",
        .starOfBethlehem: "Star of Bethlehem",
        .rainbowPromise: "Rainbow Promise",
        .easterDawn: "Easter Dawn",
        .crystalParadise: "Crystal Paradise",
        .pentecostLight: "Pentecost Light",
        .sacredHeart: "Sacred Heart Cathedral",
        .crownOfGlory: "Crown of Glory",
        .nativityStar: "Nativity Star",
        .roseOfSharon: "Rose of Sharon",
        .gardenOfGrace: "Garden of Grace",
        .littleBlessings: "Little Blessings",
        .lavenderGrace: "Lavender Grace",
        .friendsOnHigh: "Friends on High",
        .smilingSkies: "Smiling Skies",
        .stackedJoy: "Stacked Joy",
    ]

    var imageName: String { "Widget_\(rawValue)" }

    /// The real background to show on `date`. Daily Surprise walks through all
    /// backgrounds in a shuffled order: never the same two days in a row, and
    /// every background appears once before any repeats.
    func resolved(for date: Date) -> VerseDesign {
        guard self == .dailySurprise else { return self }
        let pool = Self.allCases.filter { $0 != .dailySurprise }
        let day = Calendar.current.ordinality(of: .day, in: .year, for: date) ?? 1
        let year = Calendar.current.component(.year, from: date)
        // 7 shares no factor with the pool size (23), so this visits every
        // background once per cycle; the year offset varies the order yearly.
        return pool[(day * 7 + year) % pool.count]
    }
}

struct VerseWidgetIntent: WidgetConfigurationIntent {
    static var title: LocalizedStringResource = "Daily Verse"
    static var description = IntentDescription("Choose the language and background design.")

    @Parameter(title: "Language", default: .english)
    var language: VerseLanguage

    @Parameter(title: "Design", default: .dailySurprise)
    var design: VerseDesign
}

// MARK: - Timeline

struct VerseEntry: TimelineEntry {
    let date: Date
    let verse: WidgetVerse?
    let language: VerseLanguage
    let design: VerseDesign
}

struct VerseProvider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> VerseEntry {
        VerseEntry(date: .now, verse: VerseStore.verse(for: .now), language: .english, design: VerseDesign.dailySurprise.resolved(for: .now))
    }

    func snapshot(for configuration: VerseWidgetIntent, in context: Context) async -> VerseEntry {
        entry(for: configuration, at: .now)
    }

    func timeline(for configuration: VerseWidgetIntent, in context: Context) async -> Timeline<VerseEntry> {
        let now = Date()
        let calendar = Calendar.current
        let nextMidnight = calendar.startOfDay(for: calendar.date(byAdding: .day, value: 1, to: now) ?? now)
        // Second entry swaps in tomorrow's verse exactly at midnight.
        return Timeline(
            entries: [entry(for: configuration, at: now), entry(for: configuration, at: nextMidnight)],
            policy: .after(nextMidnight)
        )
    }

    private func entry(for configuration: VerseWidgetIntent, at date: Date) -> VerseEntry {
        VerseEntry(date: date, verse: VerseStore.verse(for: date),
                   language: configuration.language, design: configuration.design.resolved(for: date))
    }
}

// MARK: - Views

private struct LatinCross: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        let bar = w * 0.28
        var p = Path()
        p.addRect(CGRect(x: rect.midX - bar / 2, y: rect.minY, width: bar, height: h))
        p.addRect(CGRect(x: rect.minX, y: rect.minY + h * 0.26, width: w, height: bar))
        return p
    }
}

private extension View {
    func readable() -> some View {
        foregroundStyle(.white)
            .shadow(color: .black.opacity(0.55), radius: 2, y: 1)
    }
}

struct VerseWidgetView: View {
    @Environment(\.widgetFamily) private var family
    let entry: VerseEntry

    private var isTamil: Bool { entry.language == .tamil }
    private var title: String { isTamil ? "இயேசுவின் வார்த்தைகள்" : "Jesus Words" }
    private var blessing: String { isTamil ? "இன்றைய ஆசீர்வாதம்" : "Today's Blessing" }
    private var quote: String { entry.verse.map { "\u{201C}\($0.quote(in: entry.language))\u{201D}" } ?? "Open Jesus Words to receive today's blessing." }
    private var reference: String { entry.verse?.reference(in: entry.language) ?? "" }

    var body: some View {
        switch family {
        case .accessoryInline:
            Text("✝ \(reference)")
        case .accessoryRectangular:
            rectangular
        case .systemSmall:
            small
        default:
            wide
        }
    }

    // Lock Screen
    private var rectangular: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(entry.verse?.quote(in: entry.language) ?? "")
                .font(.system(size: 13, weight: .medium, design: .serif))
                .lineLimit(3)
                .minimumScaleFactor(0.8)
            Text(reference)
                .font(.system(size: 11, weight: .semibold))
                .opacity(0.8)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    // Home Screen — small
    private var small: some View {
        VStack(spacing: 4) {
            LatinCross().frame(width: 10, height: 16).readable()
            Text(title)
                .font(.system(size: 12, weight: .bold, design: .serif))
                .lineLimit(1)
                .minimumScaleFactor(0.6)
                .readable()
            Text(quote)
                .font(.system(size: 15, weight: .medium, design: .serif))
                .multilineTextAlignment(.center)
                .minimumScaleFactor(0.35)
                .frame(maxHeight: .infinity)
                .readable()
            referencePill(size: 10)
        }
    }

    // Home Screen — medium and large
    private var wide: some View {
        VStack(spacing: family == .systemLarge ? 14 : 6) {
            HStack(spacing: 6) {
                LatinCross().frame(width: 10, height: 16)
                Text(title)
                    .font(.system(size: 14, weight: .bold, design: .serif))
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
                Spacer(minLength: 4)
                Text("✨ \(blessing)")
                    .font(.system(size: 10, weight: .semibold))
                    .lineLimit(1)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 3)
                    .background(.white.opacity(0.22), in: Capsule())
            }
            .readable()

            Text(quote)
                .font(.system(size: family == .systemLarge ? 22 : 17, weight: .medium, design: .serif))
                .multilineTextAlignment(.center)
                .minimumScaleFactor(0.4)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .readable()

            referencePill(size: 11)
        }
    }

    private func referencePill(size: CGFloat) -> some View {
        Text(reference)
            .font(.system(size: size, weight: .semibold, design: .serif))
            .lineLimit(1)
            .minimumScaleFactor(0.7)
            .padding(.horizontal, 8)
            .padding(.vertical, 2)
            .background(.black.opacity(0.28), in: Capsule())
            .readable()
    }
}

private struct VerseBackground: View {
    @Environment(\.widgetFamily) private var family
    let design: VerseDesign

    var body: some View {
        switch family {
        case .accessoryInline, .accessoryRectangular, .accessoryCircular:
            Color.clear
        default:
            ZStack {
                Image(design.imageName)
                    .resizable()
                    .scaledToFill()
                // Soft scrim keeps white text readable on bright artwork.
                LinearGradient(colors: [.black.opacity(0.15), .black.opacity(0.35), .black.opacity(0.2)],
                               startPoint: .top, endPoint: .bottom)
            }
        }
    }
}

// MARK: - Widget

struct JesusWordsVerseWidget: Widget {
    let kind = "JesusWordsVerseWidget"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(kind: kind, intent: VerseWidgetIntent.self, provider: VerseProvider()) { entry in
            VerseWidgetView(entry: entry)
                .containerBackground(for: .widget) {
                    VerseBackground(design: entry.design)
                }
        }
        .configurationDisplayName("Daily Bible Verse")
        .description("Today's word of Jesus on your Home Screen or Lock Screen, in English or Tamil.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge, .accessoryRectangular, .accessoryInline])
    }
}

@main
struct JesusWordsWidgetBundle: WidgetBundle {
    var body: some Widget {
        JesusWordsVerseWidget()
    }
}
