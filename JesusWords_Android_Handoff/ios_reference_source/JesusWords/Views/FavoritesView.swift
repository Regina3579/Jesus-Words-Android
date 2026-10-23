import SwiftUI

// MARK: - Heart button

struct FavoriteHeartButton: View {
    @EnvironmentObject var viewModel: WordsViewModel
    let word: JesusWord
    var size: CGFloat = 22

    @State private var bounce = false

    private var isFavorite: Bool { viewModel.isFavorite(word) }

    var body: some View {
        Button {
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            withAnimation(.spring(response: 0.3, dampingFraction: 0.5)) {
                viewModel.toggleFavorite(word)
                bounce = true
            }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) { bounce = false }
        } label: {
            Image(systemName: isFavorite ? "heart.fill" : "heart")
                .font(.system(size: size, weight: .semibold))
                .foregroundColor(isFavorite ? Color(red: 1.0, green: 0.36, blue: 0.48) : .white)
                .shadow(color: .black.opacity(0.4), radius: 2)
                .scaleEffect(bounce ? 1.3 : 1.0)
                .frame(width: size + 22, height: size + 22)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(isFavorite ? "Remove from favourites" : "Add to favourites")
    }
}

// MARK: - Favourites list (organised by topic)

struct FavoritesListView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    let onSelect: (JesusWord) -> Void

    @State private var selectedTopic: String? = nil

    // Fixed topic order so sections don't jump around.
    private static let topicOrder = [
        "DontBeAfraid", "DontWorry", "DontBeSad", "IAmThereForYou",
        "YouCanDoIt", "YouAreBlessed", "PrayYouWillGet", "ParableMoral",
    ]

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    private var favorites: [JesusWord] { viewModel.favoriteWords }

    private var topics: [String] {
        let present = Set(favorites.map(\.category))
        return Self.topicOrder.filter(present.contains) + present.subtracting(Self.topicOrder).sorted()
    }

    private var visibleTopics: [String] {
        if let selectedTopic, topics.contains(selectedTopic) { return [selectedTopic] }
        return topics
    }

    var body: some View {
        if favorites.isEmpty {
            emptyState
        } else {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    topicChips

                    ForEach(visibleTopics, id: \.self) { topic in
                        let words = favorites.filter { $0.category == topic }
                        if let first = words.first {
                            VStack(alignment: .leading, spacing: 10) {
                                HStack(spacing: 8) {
                                    Text(first.categoryEmoji)
                                    Text(isTamil ? first.tamilCategoryDisplay : first.categoryDisplay)
                                        .font(.system(size: 17, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                                    Text("\(words.count)")
                                        .font(.system(size: 12, weight: .bold))
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 2)
                                        .background(Color.white.opacity(0.25), in: Capsule())
                                }
                                .foregroundColor(.white)
                                .shadow(color: .black.opacity(0.5), radius: 2)
                                .padding(.leading, 4)

                                ForEach(words) { word in
                                    VerseRowCard(word: word, date: nil)
                                        .contentShape(Rectangle())
                                        .onTapGesture { onSelect(word) }
                                }
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 100)
                .animation(.easeInOut(duration: 0.25), value: favorites.map(\.id))
            }
        }
    }

    private var topicChips: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                chip(title: isTamil ? "அனைத்தும்" : "All", count: favorites.count, isOn: selectedTopic == nil) {
                    selectedTopic = nil
                }
                ForEach(topics, id: \.self) { topic in
                    let sample = favorites.first { $0.category == topic }
                    let name = sample.map { isTamil ? $0.tamilCategoryDisplay : $0.categoryDisplay } ?? topic
                    chip(title: "\(sample?.categoryEmoji ?? "") \(name)",
                         count: favorites.filter { $0.category == topic }.count,
                         isOn: selectedTopic == topic) {
                        selectedTopic = topic
                    }
                }
            }
            .padding(.vertical, 2)
        }
    }

    private func chip(title: String, count: Int, isOn: Bool, action: @escaping () -> Void) -> some View {
        Button(action: { withAnimation(.easeInOut(duration: 0.2)) { action() } }) {
            Text("\(title) · \(count)")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(isOn ? .black.opacity(0.8) : .white)
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .background(
                    Capsule().fill(isOn ? Color.white.opacity(0.9) : Color.black.opacity(0.3))
                )
                .overlay(Capsule().stroke(Color.white.opacity(0.5), lineWidth: 1))
        }
        .buttonStyle(.plain)
    }

    private var emptyState: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "heart")
                .font(.system(size: 60))
                .foregroundColor(.white.opacity(0.7))
            Text(isTamil ? "இதுவரை பிடித்த வசனங்கள் இல்லை" : "No favourites yet")
                .font(.title2)
                .foregroundColor(.white.opacity(0.9))
            Text(isTamil
                 ? "எந்த வசனத்திலும் ♡ ஐத் தட்டி இங்கே சேமியுங்கள்"
                 : "Tap the ♡ on any verse to save it here, ready whenever you need encouragement.")
                .font(.body)
                .foregroundColor(.white.opacity(0.75))
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)
            Spacer()
        }
        .shadow(color: .black.opacity(0.5), radius: 2)
    }
}
