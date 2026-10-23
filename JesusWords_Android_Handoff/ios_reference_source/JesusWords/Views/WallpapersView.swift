import SwiftUI

struct WallpapersView: View {
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
                    currentWallpaperSection
                    wallpaperGridSection
                    Spacer().frame(height: 100)
                }
            }
        }
    }

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    private var headerSection: some View {
        VStack(spacing: 8) {
            Image(systemName: "photo.stack.fill")
                .font(.system(size: 36))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.3), radius: 5)

            Text(isTamil ? "பகிர் வால்பேப்பர்கள்" : "Share Wallpapers")
                .font(.system(size: 32, weight: .bold, design: .serif))
                .foregroundColor(.white)
                .shadow(color: .black.opacity(0.3), radius: 5)

            Text(isTamil ? "பகிரும் பட பின்னணியைத் தேர்வுசெய்யுங்கள்" : "Choose the background for shared verses")
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.8))
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)
        }
        .padding(.top, 20)
    }

    private var currentWallpaperSection: some View {
        VStack(spacing: 12) {
            HStack {
                Image(systemName: "checkmark.seal.fill")
                    .foregroundColor(.white)
                Text(isTamil ? "தற்போதைய வால்பேப்பர்" : "Current Wallpaper")
                    .font(.system(size: 16, weight: .semibold, design: .serif))
                    .foregroundColor(.white)
                Spacer()
            }

            CurrentWallpaperRow(wallpaper: viewModel.selectedWallpaper, isTamil: isTamil)
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

    private var wallpaperGridSection: some View {
        LazyVGrid(columns: columns, spacing: 14) {
            ForEach(viewModel.wallpaperManager.allWallpapers) { wallpaper in
                WallpaperPreviewCard(
                    wallpaper: wallpaper,
                    isSelected: wallpaper.id == viewModel.selectedWallpaper.id,
                    isAnimating: selectedAnimation == wallpaper.id,
                    isTamil: isTamil
                )
                .contentShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                .onTapGesture {
                    withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                        selectedAnimation = wallpaper.id
                        viewModel.selectWallpaper(wallpaper)
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

struct CurrentWallpaperRow: View {
    let wallpaper: Wallpaper
    let isTamil: Bool

    var body: some View {
        HStack(spacing: 14) {
            Image(wallpaper.imageName)
                .resizable()
                .scaledToFill()
                .frame(width: 56, height: 56)
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                .shadow(color: .black.opacity(0.2), radius: 8, y: 4)

            VStack(alignment: .leading, spacing: 4) {
                Text(isTamil ? wallpaper.tamilName : wallpaper.name)
                    .font(.system(size: 18, weight: .bold, design: .serif))
                    .foregroundColor(.white)
                Text(isTamil ? wallpaper.tamilSubtitle : wallpaper.subtitle)
                    .font(.system(size: 14))
                    .foregroundColor(.white.opacity(0.7))
                    .lineLimit(2)
            }

            Spacer()
        }
    }
}

struct WallpaperPreviewCard: View {
    let wallpaper: Wallpaper
    let isSelected: Bool
    let isAnimating: Bool
    let isTamil: Bool

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

    private var previewImage: some View {
        Color.clear
            .frame(maxWidth: .infinity)
            .frame(height: 160)
            .overlay(
                Image(wallpaper.imageName)
                    .resizable()
                    .scaledToFill()
                    .allowsHitTesting(false)
            )
            .clipped()
    }

    private var labelSection: some View {
        VStack(spacing: 3) {
            Text(isTamil ? wallpaper.tamilName : wallpaper.name)
                .font(.system(size: 13, weight: .bold, design: .serif))
                .foregroundColor(.white)
                .lineLimit(1)

            Text(isTamil ? wallpaper.tamilSubtitle : wallpaper.subtitle)
                .font(.system(size: 10))
                .foregroundColor(.white.opacity(0.7))
                .lineLimit(1)
        }
        .padding(.vertical, 10)
        .padding(.horizontal, 6)
        .frame(maxWidth: .infinity)
        .background(Color.black.opacity(0.5))
    }

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
