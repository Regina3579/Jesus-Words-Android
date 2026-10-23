import SwiftUI

// MARK: - Share Image View (1080x1080 — Square IG Post)

struct ShareImageView: View {
    let word: JesusWord
    let theme: AppTheme
    let size: CGSize
    let appIconImage: UIImage?
    var language: AppLanguage = .english
    var wallpaper: Wallpaper? = nil

    private var displayQuote: String {
        if language == .tamil, let tq = word.tamilQuote { return tq }
        return word.quote
    }

    private var displayTheme: String {
        if language == .tamil, let tt = word.tamilTheme { return tt }
        return word.theme
    }

    private var displayCategory: String {
        language == .tamil ? word.tamilCategoryDisplay : word.categoryDisplay
    }

    private var displayReference: String {
        if language == .tamil, let tr = word.tamilReference { return tr }
        return word.reference
    }

    // Fixed layout heights (based on 1080x1080)
    private var topSectionHeight: CGFloat { size.height * 0.18 }
    private var bottomSectionHeight: CGFloat { size.height * 0.13 }
    private var verticalPadding: CGFloat { size.height * 0.05 }
    private var cardGap: CGFloat { size.height * 0.02 }

    private var cardMaxHeight: CGFloat {
        size.height - topSectionHeight - bottomSectionHeight - (verticalPadding * 2) - (cardGap * 2)
    }

    var body: some View {
        ZStack {
            if let wallpaper = wallpaper {
                wallpaperBackground(wallpaper)
                // Soft dark gradient overlay for text readability
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.black.opacity(0.15),
                        Color.black.opacity(0.35),
                        Color.black.opacity(0.15)
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            } else {
                themeBackground
                decorativeOverlays
                lightRaysEffect
            }
            contentLayout
        }
        .frame(width: size.width, height: size.height)
    }

    // MARK: - Background

    private var themeBackground: some View {
        LinearGradient(
            gradient: Gradient(colors: theme.gradientColors),
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }

    private func wallpaperBackground(_ wallpaper: Wallpaper) -> some View {
        Image(wallpaper.imageName)
            .resizable()
            .scaledToFill()
            .frame(width: size.width, height: size.height)
            .clipped()
    }

    // MARK: - Decorative Overlays

    private var decorativeOverlays: some View {
        ForEach(Array(theme.overlayElements.enumerated()), id: \.offset) { _, overlay in
            ThemeIcon(systemName: overlay.systemIcon)
                .frame(width: overlay.size * 2.0, height: overlay.size * 2.0)
                .foregroundColor(.white.opacity(overlay.opacity))
                .rotationEffect(.degrees(overlay.rotation))
                .position(
                    x: size.width * overlay.xOffset,
                    y: size.height * overlay.yOffset
                )
        }
    }

    // MARK: - Light Rays

    private var lightRaysEffect: some View {
        Path { path in
            let center = CGPoint(x: size.width * 0.80, y: -30)
            for i in stride(from: 0, to: 360, by: 30) {
                let angle = Double(i) * .pi / 180
                let length = max(size.width, size.height) * 1.5
                path.move(to: center)
                path.addLine(to: CGPoint(
                    x: center.x + cos(angle) * length,
                    y: center.y + sin(angle) * length
                ))
            }
        }
        .stroke(Color.white.opacity(0.04), lineWidth: 50)
    }

    // MARK: - Content Layout

    private var contentLayout: some View {
        VStack(spacing: 0) {
            // Top section: cross + title (fixed height)
            topSection
                .frame(height: topSectionHeight)

            Spacer().frame(height: cardGap)

            // Card: fills remaining space, text shrinks to fit
            quoteCard
                .frame(maxHeight: cardMaxHeight)

            Spacer().frame(height: cardGap)

            // Bottom branding (fixed height)
            bottomBranding
                .frame(height: bottomSectionHeight)
        }
        .padding(.horizontal, 52)
        .padding(.vertical, verticalPadding)
    }

    // MARK: - Top Section (fixed)

    private var topSection: some View {
        VStack(spacing: 12) {
            Spacer(minLength: 0)
            LatinCrossIcon(size: 72)
                .foregroundColor(.white.opacity(0.92))
                .shadow(color: .black.opacity(0.25), radius: 6)
            Text(language == .tamil ? "இயேசுவின் வார்த்தைகள்" : "Jesus Words")
                .font(.system(size: 56, weight: .bold, design: .serif))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.2), radius: 4)
                .minimumScaleFactor(0.7)
                .lineLimit(1)
        }
    }

    // MARK: - Quote Card (flexible, text shrinks to fit)

    private var quoteCard: some View {
        VStack(spacing: 0) {
            Spacer(minLength: 4)

            categoryBadge

            Spacer(minLength: 4)

            themeTitle

            Spacer(minLength: 2)

            decorativeDivider

            Spacer(minLength: 4)

            quoteText

            Spacer(minLength: 4)

            referenceText

            Spacer(minLength: 4)
        }
        .padding(.horizontal, 40)
        .padding(.vertical, 16)
        .frame(maxWidth: .infinity)
        .background(
            RoundedRectangle(cornerRadius: 28, style: .continuous)
                .fill(Color.black.opacity(0.22))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 28, style: .continuous)
                .stroke(Color.white.opacity(0.08), lineWidth: 1)
        )
    }

    private var categoryBadge: some View {
        HStack(spacing: 10) {
            Text(word.categoryEmoji)
                .font(.system(size: 30))
            Text(displayCategory)
                .font(.system(size: 28, weight: .semibold, design: .serif))
                .foregroundColor(.white)
                .lineLimit(1)
                .minimumScaleFactor(0.6)
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 10)
        .background(Color.white.opacity(0.15))
        .clipShape(Capsule())
    }

    private var themeTitle: some View {
        Text(displayTheme)
            .font(.system(size: 48, weight: .bold, design: .serif))
            .foregroundColor(.white)
            .multilineTextAlignment(.center)
            .lineLimit(2)
            .minimumScaleFactor(0.5)
            .shadow(color: .black.opacity(0.15), radius: 3)
    }

    private var decorativeDivider: some View {
        HStack(spacing: 12) {
            Rectangle()
                .fill(Color.white.opacity(0.25))
                .frame(width: 70, height: 2)
            Image(systemName: "sparkle")
                .font(.system(size: 18))
                .foregroundColor(.white.opacity(0.4))
            Rectangle()
                .fill(Color.white.opacity(0.25))
                .frame(width: 70, height: 2)
        }
    }

    private var quoteText: some View {
        Text("\u{201C}\(displayQuote)\u{201D}")
            .font(.system(size: 52, weight: .medium, design: .serif))
            .italic()
            .foregroundColor(.white)
            .multilineTextAlignment(.center)
            .lineSpacing(10)
            .minimumScaleFactor(0.3)
            .shadow(color: .black.opacity(0.1), radius: 2)
    }

    private var referenceText: some View {
        Text("— \(displayReference)")
            .font(.system(size: 36, weight: .semibold, design: .serif))
            .italic()
            .foregroundColor(.white.opacity(0.80))
            .lineLimit(1)
            .minimumScaleFactor(0.5)
    }

    // MARK: - Bottom Branding (fixed)

    private var bottomBranding: some View {
        HStack(spacing: 14) {
            if let uiImage = appIconImage {
                Image(uiImage: uiImage)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 100, height: 100)
                    .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                    .shadow(color: .black.opacity(0.3), radius: 6, y: 3)
            }

            VStack(alignment: .leading, spacing: 3) {
                Text("Jesus Words")
                    .font(.system(size: 34, weight: .bold, design: .serif))
                    .foregroundColor(.white)
                    .lineLimit(1)
                    .minimumScaleFactor(0.6)
                Text("Daily Blessings")
                    .font(.system(size: 22, weight: .regular, design: .serif))
                    .foregroundColor(.white.opacity(0.55))
                    .lineLimit(1)
                    .minimumScaleFactor(0.6)
            }

            Spacer()
        }
    }
}

// MARK: - Image Renderer

struct ShareImageRenderer {
    private static func loadAppIcon() -> UIImage? {
        UIImage(named: "ShareAppIcon")
    }

    @MainActor
    static func renderImage(word: JesusWord, theme: AppTheme, language: AppLanguage = .english, wallpaper: Wallpaper? = nil) -> UIImage? {
        let size = CGSize(width: 1080, height: 1080)
        let appIcon = loadAppIcon()
        let view = ShareImageView(word: word, theme: theme, size: size, appIconImage: appIcon, language: language, wallpaper: wallpaper)
        let renderer = ImageRenderer(content: view)
        renderer.scale = 1.0
        return renderer.uiImage
    }

    @MainActor
    static func renderSquareImage(word: JesusWord, theme: AppTheme, language: AppLanguage = .english, wallpaper: Wallpaper? = nil) -> UIImage? {
        let size = CGSize(width: 1080, height: 1080)
        let appIcon = loadAppIcon()
        let view = ShareImageView(word: word, theme: theme, size: size, appIconImage: appIcon, language: language, wallpaper: wallpaper)
        let renderer = ImageRenderer(content: view)
        renderer.scale = 1.0
        return renderer.uiImage
    }
}
