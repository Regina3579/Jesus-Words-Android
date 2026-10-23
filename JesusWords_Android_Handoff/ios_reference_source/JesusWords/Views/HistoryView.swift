import SwiftUI

struct VerseDetailItem: Identifiable {
    let id = UUID()
    let word: JesusWord
    let date: Date?
}

struct HistoryView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @State private var selectedItem: VerseDetailItem?
    @State private var showingFavorites = false

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    var body: some View {
        ZStack {
            ThemeBackgroundView(theme: viewModel.selectedTheme)

            VStack(spacing: 0) {
                // Header
                VStack(spacing: 8) {
                    Text(showingFavorites
                         ? (isTamil ? "பிடித்தவை" : "My Favourites")
                         : (isTamil ? "வரலாறு" : "History"))
                        .font(.system(size: 32, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                        .foregroundColor(.white)
                    Text(showingFavorites
                         ? (isTamil ? "நீங்கள் சேமித்த வசனங்கள்" : "Verses you saved, by topic")
                         : (isTamil ? "உங்கள் தினசரி ஆசீர்வாதங்கள்" : "Your Daily Blessings"))
                        .font(.subheadline)
                        .foregroundColor(.white.opacity(0.8))

                    sectionSwitch
                        .padding(.top, 6)
                }
                .shadow(color: .black.opacity(0.5), radius: 2)
                .padding(.top, 20)
                .padding(.bottom, 16)

                if showingFavorites {
                    FavoritesListView { word in
                        selectedItem = VerseDetailItem(word: word, date: nil)
                    }
                } else if viewModel.history.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "book.closed")
                            .font(.system(size: 60))
                            .foregroundColor(.white.opacity(0.5))
                        Text(viewModel.selectedLanguage == .tamil ? "இதுவரை வரலாறு இல்லை" : "No history yet")
                            .font(.title2)
                            .foregroundColor(.white.opacity(0.7))
                        Text(viewModel.selectedLanguage == .tamil ? "உங்கள் தினசரி வார்த்தைகள் இங்கே தோன்றும்" : "Your daily words will appear here")
                            .font(.body)
                            .foregroundColor(.white.opacity(0.5))
                        Spacer()
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(viewModel.history) { entry in
                                VerseRowCard(word: entry.word, date: entry.dateShown)
                                    .contentShape(Rectangle())
                                    .onTapGesture {
                                        selectedItem = VerseDetailItem(word: entry.word, date: entry.dateShown)
                                    }
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.bottom, 100)
                    }
                }
            }
        }
        .sheet(item: $selectedItem) { item in
            VerseDetailView(word: item.word, date: item.date)
                .environmentObject(viewModel)
        }
    }

    private var sectionSwitch: some View {
        HStack(spacing: 0) {
            switchButton(title: isTamil ? "வரலாறு" : "History", icon: "clock", on: !showingFavorites) {
                showingFavorites = false
            }
            switchButton(title: isTamil ? "பிடித்தவை" : "Favourites", icon: "heart.fill", on: showingFavorites) {
                showingFavorites = true
            }
        }
        .padding(3)
        .background(Capsule().fill(Color.black.opacity(0.35)))
        .overlay(Capsule().stroke(Color.white.opacity(0.5), lineWidth: 1))
    }

    private func switchButton(title: String, icon: String, on: Bool, action: @escaping () -> Void) -> some View {
        Button(action: { withAnimation(.easeInOut(duration: 0.2)) { action() } }) {
            HStack(spacing: 6) {
                Image(systemName: icon)
                Text(title)
            }
            .font(.system(size: 14, weight: .semibold))
            .foregroundColor(on ? .black.opacity(0.8) : .white)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Capsule().fill(on ? Color.white.opacity(0.9) : Color.clear))
        }
        .buttonStyle(.plain)
    }
}

struct VerseRowCard: View {
    @EnvironmentObject var viewModel: WordsViewModel
    let word: JesusWord
    let date: Date?

    private var language: AppLanguage { viewModel.selectedLanguage }
    private var fontDesign: Font.Design { viewModel.selectedTheme.fontDesign }

    private var dateString: String? {
        guard let date else { return nil }
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter.string(from: date)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(word.categoryEmoji)
                Text(language == .tamil ? word.tamilCategoryDisplay : word.categoryDisplay)
                    .font(.system(size: 12, weight: .semibold, design: fontDesign))
                    .foregroundColor(.white.opacity(0.8))
                Spacer()
                if let dateString {
                    Text(dateString)
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.6))
                }
                FavoriteHeartButton(word: word, size: 17)
                    .padding(.trailing, -10)
                    .padding(.vertical, -10)
            }

            Text(language == .tamil ? (word.tamilTheme ?? word.theme) : word.theme)
                .font(.system(size: 16, weight: .bold, design: fontDesign))
                .foregroundColor(.white)

            let displayQuote = (language == .tamil && word.tamilQuote != nil) ? word.tamilQuote! : word.quote
            Text("\u{201C}\(displayQuote)\u{201D}")
                .font(.system(size: 14, design: fontDesign))
                .foregroundColor(.white.opacity(0.9))
                .lineLimit(3)
                .lineSpacing(3)

            let displayRef = (language == .tamil && word.tamilReference != nil) ? word.tamilReference! : word.reference
            Text("— \(displayRef)")
                .font(.system(size: 13, weight: .medium, design: fontDesign))
                .foregroundColor(.white.opacity(0.7))
                .italic()
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(.ultraThinMaterial)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.black.opacity(0.20))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(Color.white.opacity(0.22), lineWidth: 1)
                )
        )
    }
}

struct VerseDetailView: View {
    let word: JesusWord
    let date: Date?
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var viewModel: WordsViewModel
    @State private var shareFlowWord: JesusWord? = nil

    var body: some View {
        ZStack {
            ThemeBackgroundView(theme: viewModel.selectedTheme)

            VStack(spacing: 20) {
                HStack {
                    Spacer()
                    Button(action: { dismiss() }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundColor(.white.opacity(0.7))
                    }
                }
                .padding()

                Spacer()

                VStack(spacing: 16) {
                    Text(word.categoryEmoji)
                        .font(.system(size: 44))

                    Text(viewModel.selectedLanguage == .tamil ? word.tamilCategoryDisplay : word.categoryDisplay)
                        .font(.system(size: 16, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                        .foregroundColor(.white.opacity(0.8))

                    Text(viewModel.selectedLanguage == .tamil ? (word.tamilTheme ?? word.theme) : word.theme)
                        .font(.system(size: 24, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                        .foregroundColor(.white)

                    Rectangle()
                        .fill(Color.white.opacity(0.3))
                        .frame(width: 60, height: 2)

                    let displayQuote = (viewModel.selectedLanguage == .tamil && word.tamilQuote != nil) ? word.tamilQuote! : word.quote
                    Text("\u{201C}\(displayQuote)\u{201D}")
                        .font(.system(size: 22, weight: .medium, design: viewModel.selectedTheme.fontDesign))
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .lineSpacing(8)
                        .padding(.horizontal, 20)

                    let detailRef = (viewModel.selectedLanguage == .tamil && word.tamilReference != nil) ? word.tamilReference! : word.reference
                    Text("— \(detailRef)")
                        .font(.system(size: 18, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                        .foregroundColor(.white.opacity(0.85))
                        .italic()

                    if let date {
                        let formatted = date.formatted(date: .long, time: .omitted)
                        Text(viewModel.selectedLanguage == .tamil
                            ? "\(formatted) அன்று காட்டப்பட்டது"
                            : "Shown on \(formatted)")
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.6))
                    }
                }
                .padding(28)
                .frame(maxWidth: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: 24)
                        .fill(.ultraThinMaterial)
                        .overlay(
                            RoundedRectangle(cornerRadius: 24)
                                .fill(Color.black.opacity(0.22))
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 24)
                                .stroke(Color.white.opacity(0.28), lineWidth: 1)
                        )
                        .shadow(color: .black.opacity(0.25), radius: 20, y: 10)
                )
                .overlay(alignment: .topTrailing) {
                    FavoriteHeartButton(word: word, size: 24)
                        .padding(10)
                }
                .padding(.horizontal, 20)

                // Share as Image button
                Button(action: {
                    shareFlowWord = word
                }) {
                    HStack {
                        Image(systemName: "photo.on.rectangle.angled")
                        Text(viewModel.selectedLanguage == .tamil ? "படமாகப் பகிரு" : "Share as Image")
                    }
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 28)
                    .padding(.vertical, 12)
                    .background(Capsule().fill(Color.white.opacity(0.2)))
                }

                Spacer()
            }
        }
        .fullScreenCover(item: $shareFlowWord) { word in
            ShareFlowView(word: word, initialWallpaperId: viewModel.selectedWallpaper.id)
                .environmentObject(viewModel)
        }
    }
}
