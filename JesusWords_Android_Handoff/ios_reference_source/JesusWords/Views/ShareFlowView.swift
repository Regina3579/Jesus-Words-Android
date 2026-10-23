import SwiftUI

struct ShareFlowView: View {
    @EnvironmentObject var viewModel: WordsViewModel
    @Environment(\.dismiss) private var dismiss

    let word: JesusWord
    @State private var localWallpaperId: String
    @State private var shareableImage: ShareableImage? = nil
    @State private var isRendering = false

    init(word: JesusWord, initialWallpaperId: String) {
        self.word = word
        _localWallpaperId = State(initialValue: initialWallpaperId)
    }

    private var isTamil: Bool { viewModel.selectedLanguage == .tamil }

    private var selectedWallpaper: Wallpaper {
        viewModel.wallpaperManager.allWallpapers.first(where: { $0.id == localWallpaperId })
            ?? viewModel.wallpaperManager.allWallpapers[0]
    }

    var body: some View {
        ZStack {
            // Dim background behind the sheet-like card
            Color.black.ignoresSafeArea()

            VStack(spacing: 0) {
                topBar

                ScrollView {
                    VStack(spacing: 24) {
                        previewSection
                        wallpaperPickerSection
                        shareButton
                        Spacer().frame(height: 20)
                    }
                    .padding(.top, 8)
                }
            }
        }
        .sheet(item: $shareableImage) { item in
            ShareSheet(activityItems: [item.image])
        }
    }

    // MARK: - Top bar

    private var topBar: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "xmark")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.white)
                    .frame(width: 36, height: 36)
                    .background(Color.white.opacity(0.15))
                    .clipShape(Circle())
            }

            Spacer()

            Text(isTamil ? "ஆசீர்வாதத்தைப் பகிரு" : "Share Blessing")
                .font(.system(size: 18, weight: .semibold, design: .serif))
                .foregroundColor(.white)

            Spacer()

            // Invisible spacer to balance the X button
            Color.clear.frame(width: 36, height: 36)
        }
        .padding(.horizontal, 20)
        .padding(.top, 12)
        .padding(.bottom, 16)
    }

    // MARK: - Preview

    private var previewSection: some View {
        GeometryReader { geo in
            let side = min(geo.size.width - 40, 480)
            VStack {
                SharePreviewCard(
                    word: word,
                    wallpaper: selectedWallpaper,
                    theme: viewModel.selectedTheme,
                    language: viewModel.selectedLanguage
                )
                .frame(width: side, height: side)
                .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                .shadow(color: .black.opacity(0.4), radius: 20, y: 8)
            }
            .frame(maxWidth: .infinity)
        }
        .frame(height: min(UIScreen.main.bounds.width - 40, 480))
    }

    // MARK: - Wallpaper picker

    private var wallpaperPickerSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(isTamil ? "வால்பேப்பர் தேர்வு" : "Choose Wallpaper")
                .font(.system(size: 15, weight: .semibold, design: .serif))
                .foregroundColor(.white.opacity(0.9))
                .padding(.horizontal, 20)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(viewModel.wallpaperManager.allWallpapers) { wallpaper in
                        wallpaperThumb(wallpaper: wallpaper)
                            .contentShape(Rectangle())
                            .onTapGesture {
                                withAnimation(.easeInOut(duration: 0.25)) {
                                    localWallpaperId = wallpaper.id
                                }
                                // Also persist as default selection
                                viewModel.selectWallpaper(wallpaper)
                            }
                    }
                }
                .padding(.horizontal, 20)
            }
        }
    }

    private func wallpaperThumb(wallpaper: Wallpaper) -> some View {
        let isSelected = wallpaper.id == localWallpaperId
        return VStack(spacing: 6) {
            Image(wallpaper.imageName)
                .resizable()
                .scaledToFill()
                .allowsHitTesting(false)
                .frame(width: 72, height: 96)
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                .overlay(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .stroke(isSelected ? Color.white : Color.white.opacity(0.2),
                                lineWidth: isSelected ? 2.5 : 1)
                )
                .overlay(alignment: .topTrailing) {
                    if isSelected {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .background(Circle().fill(Color.green).padding(-1))
                            .padding(4)
                    }
                }

            Text(isTamil ? wallpaper.tamilName : wallpaper.name)
                .font(.system(size: 11, weight: isSelected ? .bold : .regular))
                .foregroundColor(.white.opacity(isSelected ? 1.0 : 0.6))
                .lineLimit(1)
                .frame(width: 72)
        }
    }

    // MARK: - Share

    private var shareButton: some View {
        Button(action: handleShare) {
            HStack {
                if isRendering {
                    ProgressView()
                        .tint(.white)
                } else {
                    Image(systemName: "square.and.arrow.up")
                    Text(isTamil ? "பகிரு" : "Share")
                }
            }
            .font(.system(size: 17, weight: .semibold))
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(
                Capsule()
                    .fill(
                        LinearGradient(
                            colors: [Color.white.opacity(0.25), Color.white.opacity(0.15)],
                            startPoint: .top, endPoint: .bottom
                        )
                    )
                    .overlay(Capsule().stroke(Color.white.opacity(0.4), lineWidth: 1))
            )
        }
        .padding(.horizontal, 20)
        .padding(.top, 8)
        .disabled(isRendering)
    }

    private func handleShare() {
        guard !isRendering else { return }
        isRendering = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            if let rendered = ShareImageRenderer.renderImage(
                word: word,
                theme: viewModel.selectedTheme,
                language: viewModel.selectedLanguage,
                wallpaper: selectedWallpaper
            ) {
                shareableImage = ShareableImage(image: rendered)
            }
            isRendering = false
        }
    }
}

// MARK: - Preview card (the real 1080x1080 share layout, drawn live and scaled to fit)

struct SharePreviewCard: View {
    let word: JesusWord
    let wallpaper: Wallpaper
    let theme: AppTheme
    let language: AppLanguage

    private static let canvas: CGFloat = 1080

    var body: some View {
        GeometryReader { geo in
            let scale = min(geo.size.width, geo.size.height) / Self.canvas
            ShareImageView(
                word: word,
                theme: theme,
                size: CGSize(width: Self.canvas, height: Self.canvas),
                appIconImage: UIImage(named: "ShareAppIcon"),
                language: language,
                wallpaper: wallpaper
            )
            .frame(width: Self.canvas, height: Self.canvas)
            .scaleEffect(scale, anchor: .topLeading)
            .frame(width: geo.size.width, height: geo.size.height, alignment: .topLeading)
        }
        .id(wallpaper.id + language.rawValue)
        .allowsHitTesting(false)
    }
}
