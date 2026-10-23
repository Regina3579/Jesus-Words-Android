import SwiftUI

struct ShareableImage: Identifiable {
    let id = UUID()
    let image: UIImage
}

struct HomeView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @State private var isAnimating = false
    @State private var shareFlowWord: JesusWord? = nil
    // Shown once ever, on first launch. The "_v3" key makes the redesigned tip
    // appear once more for people who saw an earlier version.
    @AppStorage("hasSeenLanguageTip_v3") private var hasSeenLanguageTip = false
    @State private var showLanguageTip = false
    @State private var languageToggleFrame: CGRect = .zero

    var body: some View {
        ZStack {
            ThemeBackgroundView(theme: viewModel.selectedTheme)

            ScrollView {
                VStack(spacing: 24) {
                    Spacer().frame(height: 8)

                    // Language Toggle at top
                    HStack {
                        Spacer()
                        LanguageToggle(selectedLanguage: $viewModel.selectedLanguage) { language in
                            viewModel.setLanguage(language)
                        }
                        .background(
                            GeometryReader { g in
                                Color.clear.preference(key: LanguageToggleFrameKey.self, value: g.frame(in: .global))
                            }
                        )
                    }
                    .padding(.trailing, 24)
                    .padding(.top, 16)

                    // App Title (double-shadow keeps white legible on both dark and light backgrounds)
                    VStack(spacing: 4) {
                        LatinCrossIcon(size: 36)
                            .foregroundColor(.white)
                            .shadow(color: .black.opacity(0.55), radius: 2)
                            .shadow(color: .black.opacity(0.35), radius: 8)

                        Text(viewModel.selectedLanguage == .tamil ? "இயேசுவின் வார்த்தைகள்" : "Jesus Words")
                            .font(.system(size: 32, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                            .foregroundColor(.white)
                            .shadow(color: .black.opacity(0.6), radius: 2)
                            .shadow(color: .black.opacity(0.4), radius: 10)
                    }
                    .padding(.horizontal, 20)

                    // Today's Word Card
                    if let word = viewModel.todaysWord {
                        VStack(spacing: 16) {
                            // Category Badge
                            HStack {
                                Text(word.categoryEmoji)
                                Text(viewModel.selectedLanguage == .tamil ? word.tamilCategoryDisplay : word.categoryDisplay)
                                    .font(.system(size: 14, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(Color.white.opacity(0.25))
                            .clipShape(Capsule())
                            .foregroundColor(.white)

                            // Theme
                            Text(viewModel.selectedLanguage == .tamil ? (word.tamilTheme ?? word.theme) : word.theme)
                                .font(.system(size: 18, weight: .bold, design: viewModel.selectedTheme.fontDesign))
                                .foregroundColor(.white)

                            // Divider
                            Rectangle()
                                .fill(Color.white.opacity(0.3))
                                .frame(width: 60, height: 2)

                            // Quote
                            let displayQuote = (viewModel.selectedLanguage == .tamil && word.tamilQuote != nil) ? word.tamilQuote! : word.quote
                            Text("\u{201C}\(displayQuote)\u{201D}")
                                .font(.system(size: 20, weight: .medium, design: viewModel.selectedTheme.fontDesign))
                                .foregroundColor(.white)
                                .multilineTextAlignment(.center)
                                .lineSpacing(6)
                                .padding(.horizontal, 8)

                            // Reference
                            let displayRef = (viewModel.selectedLanguage == .tamil && word.tamilReference != nil) ? word.tamilReference! : word.reference
                            Text("— \(displayRef)")
                                .font(.system(size: 16, weight: .semibold, design: viewModel.selectedTheme.fontDesign))
                                .foregroundColor(.white.opacity(0.85))
                                .italic()
                        }
                        .shadow(color: .black.opacity(0.6), radius: 3)
                        .padding(28)
                        .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: 24)
                                .fill(Color.black.opacity(0.18))
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
                        .scaleEffect(isAnimating ? 1.0 : 0.95)
                        .opacity(isAnimating ? 1.0 : 0.0)
                        .animation(.easeOut(duration: 0.8), value: isAnimating)
                    }

                    // Share as Image Button
                    if viewModel.todaysWord != nil {
                        Button(action: {
                            if let word = viewModel.todaysWord {
                                shareFlowWord = word
                            }
                        }) {
                            HStack {
                                Image(systemName: "photo.on.rectangle.angled")
                                Text(viewModel.selectedLanguage == .tamil ? "இந்த ஆசீர்வாதத்தைப் பகிரு" : "Share This Blessing")
                            }
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .shadow(color: .black.opacity(0.5), radius: 2)
                            .padding(.horizontal, 28)
                            .padding(.vertical, 14)
                            .background(
                                ZStack {
                                    Capsule().fill(.ultraThinMaterial)
                                    Capsule().fill(Color.black.opacity(0.38))
                                }
                                    .overlay(
                                        Capsule()
                                            .stroke(Color.white.opacity(0.55), lineWidth: 1.2)
                                    )
                                    .shadow(color: .black.opacity(0.3), radius: 8, y: 3)
                            )
                        }
                    }

                    Spacer().frame(height: 100)
                }
            }
        }
        .onAppear {
            withAnimation {
                isAnimating = true
            }
            if !hasSeenLanguageTip {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.9) {
                    showLanguageTip = true
                }
            }
        }
        .fullScreenCover(item: $shareFlowWord) { word in
            ShareFlowView(word: word, initialWallpaperId: viewModel.selectedWallpaper.id)
                .environmentObject(viewModel)
        }
        .onPreferenceChange(LanguageToggleFrameKey.self) { languageToggleFrame = $0 }
        .onChange(of: hasSeenLanguageTip) { seen in
            if !seen {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) { showLanguageTip = true }
            }
        }
        .overlay {
            if showLanguageTip && languageToggleFrame != .zero {
                LanguageTipView(toggleFrame: languageToggleFrame) {
                    hasSeenLanguageTip = true
                    showLanguageTip = false
                }
                .transition(.opacity)
                .zIndex(100)
            }
        }
    }
}

// MARK: - Language Toggle
struct LanguageToggle: View {
    @Binding var selectedLanguage: AppLanguage
    var onToggle: (AppLanguage) -> Void

    var body: some View {
        HStack(spacing: 0) {
            toggleButton(for: .english, label: "En")
            toggleButton(for: .tamil, label: "த")
        }
        .background(
            ZStack {
                Capsule().fill(.ultraThinMaterial)
                Capsule().fill(Color.black.opacity(0.45))
            }
        )
        .overlay(
            Capsule()
                .stroke(Color.white.opacity(0.55), lineWidth: 1.2)
        )
        .shadow(color: .black.opacity(0.35), radius: 6, y: 2)
    }

    private func toggleButton(for language: AppLanguage, label: String) -> some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.2)) {
                selectedLanguage = language
                onToggle(language)
            }
        }) {
            Text(label)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.5), radius: 2)
                .padding(.horizontal, 14)
                .padding(.vertical, 7)
                .background(
                    selectedLanguage == language
                        ? Color.white.opacity(0.28)
                        : Color.clear
                )
                .clipShape(Capsule())
        }
    }
}

// UIKit share sheet wrapper
struct ShareSheet: UIViewControllerRepresentable {
    let activityItems: [Any]

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
