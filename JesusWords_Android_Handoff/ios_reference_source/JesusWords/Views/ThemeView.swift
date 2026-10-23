import SwiftUI

struct ThemeView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @State private var selectedAnimation: String? = nil

    private let columns = [
        GridItem(.flexible(), spacing: 14),
        GridItem(.flexible(), spacing: 14)
    ]

    var body: some View {
        ZStack {
            ThemeBackgroundView(theme: viewModel.selectedTheme)

            ScrollView {
                VStack(spacing: 20) {
                    headerSection
                    currentThemeSection
                    themeGridSection
                    Spacer().frame(height: 100)
                }
            }
        }
    }

    private var headerSection: some View {
        VStack(spacing: 8) {
            Image(systemName: "paintpalette.fill")
                .font(.system(size: 36))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.3), radius: 5)

            Text(viewModel.selectedLanguage == .tamil ? "தீம்கள்" : "Themes")
                .font(.system(size: 32, weight: .bold, design: .serif))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.3), radius: 5)

            Text(viewModel.selectedLanguage == .tamil ? "உங்கள் காட்சி அனுபவத்தைத் தேர்வுசெய்யுங்கள்" : "Choose your visual experience")
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.8))
        }
        .padding(.top, 20)
    }

    private var currentThemeSection: some View {
        VStack(spacing: 12) {
            HStack {
                Image(systemName: "checkmark.seal.fill")
                    .foregroundColor(.white)
                Text(viewModel.selectedLanguage == .tamil ? "தற்போதைய தீம்" : "Current Theme")
                    .font(.system(size: 16, weight: .semibold, design: .serif))
                    .foregroundColor(.white)
                Spacer()
            }

            CurrentThemeRow(theme: viewModel.selectedTheme)
        }
        .padding(18)
        .frame(maxWidth: .infinity)
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(.ultraThinMaterial)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .fill(Color.black.opacity(0.22))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.white.opacity(0.28), lineWidth: 1)
                )
        )
        .padding(.horizontal, 16)
    }

    // Picture themes first, older gradient themes after.
    private var orderedThemes: [AppTheme] {
        let all = viewModel.themeManager.allThemes
        return all.filter { $0.backgroundImageName != nil } + all.filter { $0.backgroundImageName == nil }
    }

    private var themeGridSection: some View {
        LazyVGrid(columns: columns, spacing: 14) {
            ForEach(orderedThemes) { theme in
                ThemePreviewCard(
                    theme: theme,
                    isSelected: theme.id == viewModel.selectedTheme.id,
                    isAnimating: selectedAnimation == theme.id
                )
                .contentShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                .onTapGesture {
                    withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                        selectedAnimation = theme.id
                        viewModel.selectTheme(theme)
                    }
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                        selectedAnimation = nil
                    }
                }
            }
        }
        .padding(.horizontal, 16)
    }
}

struct CurrentThemeRow: View {
    let theme: AppTheme

    var body: some View {
        HStack(spacing: 14) {
            themeIcon
            themeInfo
            Spacer()
        }
    }

    @ViewBuilder
    private var themeIcon: some View {
        if let imageName = theme.backgroundImageName {
            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(width: 56, height: 56)
                .clipShape(Circle())
                .shadow(color: .black.opacity(0.2), radius: 8, y: 4)
        } else {
            Image(systemName: theme.decorativeIcon)
                .font(.system(size: 28))
                .foregroundColor(.white)
                .frame(width: 56, height: 56)
                .background(iconBackground)
        }
    }

    private var iconBackground: some View {
        Circle()
            .fill(
                LinearGradient(
                    colors: theme.gradientColors,
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            )
            .shadow(color: .black.opacity(0.2), radius: 8, y: 4)
    }

    private var themeInfo: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(theme.name)
                .font(.system(size: 18, weight: .bold, design: .serif))
                .foregroundColor(.white)
            Text(theme.subtitle)
                .font(.system(size: 14))
                .foregroundColor(.white.opacity(0.7))
        }
    }
}

struct ThemePreviewCard: View {
    let theme: AppTheme
    let isSelected: Bool
    let isAnimating: Bool

    private var shadowColor: Color {
        isSelected ? Color.white.opacity(0.2) : Color.black.opacity(0.15)
    }

    private var shadowRadius: CGFloat {
        isSelected ? 12 : 6
    }

    private var shadowY: CGFloat {
        isSelected ? 2 : 4
    }

    var body: some View {
        VStack(spacing: 0) {
            previewImage
            labelSection
        }
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
        .overlay(borderOverlay)
        .shadow(color: shadowColor, radius: shadowRadius, y: shadowY)
        .scaleEffect(isAnimating ? 0.92 : 1.0)
        .overlay(checkmarkOverlay)
    }

    // MARK: - Preview Image

    private var previewImage: some View {
        ZStack {
            if let imageName = theme.backgroundImageName {
                Color.clear
                    .overlay(
                        Image(imageName)
                            .resizable()
                            .scaledToFill()
                            .allowsHitTesting(false)
                    )
                    .clipped()
                Color.black.opacity(0.15)
                centralIcon
            } else {
                gradientBackground
                overlayElements
                centralIcon
                lightRays
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 120)
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }

    private var gradientBackground: some View {
        LinearGradient(
            colors: theme.gradientColors,
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }

    private var overlayElements: some View {
        GeometryReader { geo in
            ForEach(Array(theme.overlayElements.enumerated()), id: \.offset) { _, overlay in
                overlayIcon(overlay: overlay, size: geo.size)
            }
        }
    }

    private func overlayIcon(overlay: ThemeOverlay, size: CGSize) -> some View {
        ThemeIcon(systemName: overlay.systemIcon)
            .frame(width: overlay.size * 0.5, height: overlay.size * 0.5)
            .foregroundColor(.white.opacity(overlay.opacity * 2))
            .rotationEffect(.degrees(overlay.rotation))
            .position(
                x: size.width * overlay.xOffset,
                y: size.height * overlay.yOffset
            )
    }

    private var centralIcon: some View {
        VStack(spacing: 6) {
            Image(systemName: theme.decorativeIcon)
                .font(.system(size: 28))
                .foregroundColor(.white.opacity(0.9))
                .shadow(color: .black.opacity(0.3), radius: 4)

            LatinCrossIcon(size: 14)
                .foregroundColor(.white.opacity(0.6))
        }
    }

    private var lightRays: some View {
        GeometryReader { geo in
            Path { path in
                let center = CGPoint(x: geo.size.width * 0.75, y: -10)
                for i in stride(from: 0, to: 360, by: 45) {
                    let angle = Double(i) * .pi / 180
                    let length = max(geo.size.width, geo.size.height)
                    path.move(to: center)
                    path.addLine(to: CGPoint(
                        x: center.x + cos(angle) * length,
                        y: center.y + sin(angle) * length
                    ))
                }
            }
            .stroke(Color.white.opacity(0.05), lineWidth: 15)
        }
    }

    // MARK: - Label Section

    private var labelSection: some View {
        VStack(spacing: 3) {
            Text(theme.name)
                .font(.system(size: 13, weight: .bold, design: theme.fontDesign))
                .foregroundColor(.white)
                .lineLimit(1)

            Text(theme.subtitle)
                .font(.system(size: 10))
                .foregroundColor(.white.opacity(0.6))
                .lineLimit(1)
        }
        .padding(.vertical, 10)
        .frame(maxWidth: .infinity)
        .background(Color.white.opacity(0.08))
    }

    // MARK: - Overlays

    private var borderOverlay: some View {
        RoundedRectangle(cornerRadius: 16, style: .continuous)
            .stroke(
                isSelected ? Color.white : Color.white.opacity(0.15),
                lineWidth: isSelected ? 2.5 : 1
            )
    }

    @ViewBuilder
    private var checkmarkOverlay: some View {
        if isSelected {
            VStack {
                HStack {
                    Spacer()
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundColor(.white)
                        .background(Circle().fill(Color.green).padding(-2))
                        .shadow(color: .black.opacity(0.3), radius: 4)
                        .padding(8)
                }
                Spacer()
            }
        }
    }
}
