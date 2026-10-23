import SwiftUI

// Original gradient-based background (used as fallback)
struct BackgroundView: View {
    let dayOfYear: Int

    private var gradientColors: [Color] {
        let gradients: [[Color]] = [
            // 1. Golden Sunrise
            [Color(red: 0.98, green: 0.85, blue: 0.37), Color(red: 0.95, green: 0.55, blue: 0.25), Color(red: 0.55, green: 0.25, blue: 0.45)],
            // 2. Peaceful Dawn
            [Color(red: 0.95, green: 0.75, blue: 0.80), Color(red: 0.70, green: 0.55, blue: 0.85), Color(red: 0.35, green: 0.30, blue: 0.65)],
            // 3. Heavenly Blue
            [Color(red: 0.53, green: 0.81, blue: 0.92), Color(red: 0.28, green: 0.47, blue: 0.82), Color(red: 0.15, green: 0.20, blue: 0.55)],
            // 4. Sacred Gold
            [Color(red: 1.0, green: 0.95, blue: 0.70), Color(red: 0.90, green: 0.75, blue: 0.30), Color(red: 0.65, green: 0.45, blue: 0.15)],
            // 5. Olive Garden
            [Color(red: 0.76, green: 0.88, blue: 0.62), Color(red: 0.40, green: 0.65, blue: 0.45), Color(red: 0.20, green: 0.35, blue: 0.30)],
            // 6. Royal Purple
            [Color(red: 0.85, green: 0.70, blue: 0.95), Color(red: 0.55, green: 0.30, blue: 0.75), Color(red: 0.25, green: 0.10, blue: 0.45)],
            // 7. Rose of Sharon
            [Color(red: 1.0, green: 0.80, blue: 0.82), Color(red: 0.90, green: 0.45, blue: 0.55), Color(red: 0.55, green: 0.20, blue: 0.35)],
            // 8. Living Water
            [Color(red: 0.65, green: 0.92, blue: 0.90), Color(red: 0.25, green: 0.65, blue: 0.75), Color(red: 0.10, green: 0.30, blue: 0.50)],
            // 9. Burning Bush
            [Color(red: 1.0, green: 0.70, blue: 0.30), Color(red: 0.90, green: 0.40, blue: 0.20), Color(red: 0.55, green: 0.15, blue: 0.15)],
            // 10. Starlight
            [Color(red: 0.15, green: 0.15, blue: 0.35), Color(red: 0.25, green: 0.20, blue: 0.55), Color(red: 0.45, green: 0.35, blue: 0.75)],
            // 11. Morning Glory
            [Color(red: 1.0, green: 0.90, blue: 0.75), Color(red: 0.95, green: 0.65, blue: 0.50), Color(red: 0.75, green: 0.35, blue: 0.45)],
            // 12. Covenant Rainbow
            [Color(red: 0.90, green: 0.85, blue: 0.95), Color(red: 0.60, green: 0.75, blue: 0.90), Color(red: 0.40, green: 0.55, blue: 0.80)],
            // 13. Shepherd's Field
            [Color(red: 0.85, green: 0.92, blue: 0.75), Color(red: 0.55, green: 0.75, blue: 0.50), Color(red: 0.30, green: 0.45, blue: 0.35)],
            // 14. Bethlehem Star
            [Color(red: 1.0, green: 1.0, blue: 0.85), Color(red: 0.90, green: 0.80, blue: 0.50), Color(red: 0.50, green: 0.35, blue: 0.25)],
            // 15. Galilee Sunset
            [Color(red: 0.95, green: 0.60, blue: 0.40), Color(red: 0.80, green: 0.30, blue: 0.35), Color(red: 0.30, green: 0.15, blue: 0.35)],
            // 16. Garden of Eden
            [Color(red: 0.70, green: 0.95, blue: 0.70), Color(red: 0.30, green: 0.70, blue: 0.50), Color(red: 0.15, green: 0.40, blue: 0.35)],
            // 17. Mercy Seat
            [Color(red: 0.95, green: 0.90, blue: 0.80), Color(red: 0.80, green: 0.65, blue: 0.50), Color(red: 0.50, green: 0.35, blue: 0.30)],
            // 18. Jordan River
            [Color(red: 0.50, green: 0.80, blue: 0.85), Color(red: 0.25, green: 0.55, blue: 0.70), Color(red: 0.15, green: 0.30, blue: 0.45)],
            // 19. Crown of Glory
            [Color(red: 1.0, green: 0.92, blue: 0.55), Color(red: 0.85, green: 0.68, blue: 0.25), Color(red: 0.55, green: 0.35, blue: 0.20)],
            // 20. Lily of Valley
            [Color(red: 1.0, green: 0.98, blue: 0.95), Color(red: 0.90, green: 0.82, blue: 0.78), Color(red: 0.70, green: 0.50, blue: 0.55)]
        ]
        let index = (dayOfYear - 1) % gradients.count
        return gradients[index]
    }

    var body: some View {
        ZStack {
            LinearGradient(
                gradient: Gradient(colors: gradientColors),
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()

            // Decorative cross watermark
            VStack {
                Spacer()
                LatinCrossShape()
                    .fill(Color.white.opacity(0.06))
                    .aspectRatio(0.55, contentMode: .fit)
                    .frame(height: 200)
                    .rotationEffect(.degrees(-15))
                Spacer()
            }

            // Light rays effect
            GeometryReader { geo in
                Path { path in
                    let center = CGPoint(x: geo.size.width * 0.75, y: -50)
                    for i in stride(from: 0, to: 360, by: 30) {
                        let angle = Double(i) * .pi / 180
                        let length = max(geo.size.width, geo.size.height) * 1.5
                        path.move(to: center)
                        path.addLine(to: CGPoint(
                            x: center.x + cos(angle) * length,
                            y: center.y + sin(angle) * length
                        ))
                    }
                }
                .stroke(Color.white.opacity(0.03), lineWidth: 40)
            }
            .ignoresSafeArea()
        }
    }
}

// Theme-based background view
struct ThemeBackgroundView: View {
    let theme: AppTheme

    var body: some View {
        ZStack {
            if let imageName = theme.backgroundImageName {
                // Color.clear takes exactly the proposed size; the image lives in an
                // overlay so its scaled-to-fill size can never widen the layout.
                Color.clear
                    .overlay(
                        Image(imageName)
                            .resizable()
                            .scaledToFill()
                    )
                    .clipped()
                    .ignoresSafeArea()

                // Subtle dark overlay for text readability
                Color.black.opacity(0.15)
                    .ignoresSafeArea()
            } else {
                // Main gradient
                LinearGradient(
                    gradient: Gradient(colors: theme.gradientColors),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()

                // Decorative overlay elements
                GeometryReader { geo in
                    ForEach(Array(theme.overlayElements.enumerated()), id: \.offset) { _, overlay in
                        ThemeIcon(systemName: overlay.systemIcon)
                            .frame(width: overlay.size, height: overlay.size)
                            .foregroundColor(.white.opacity(overlay.opacity))
                            .rotationEffect(.degrees(overlay.rotation))
                            .position(
                                x: geo.size.width * overlay.xOffset,
                                y: geo.size.height * overlay.yOffset
                            )
                    }
                }
                .ignoresSafeArea()

                // Cross watermark
                VStack {
                    Spacer()
                    LatinCrossShape()
                        .fill(Color.white.opacity(0.04))
                        .aspectRatio(0.55, contentMode: .fit)
                        .frame(height: 200)
                        .rotationEffect(.degrees(-15))
                    Spacer()
                }

                // Light rays effect
                GeometryReader { geo in
                    Path { path in
                        let center = CGPoint(x: geo.size.width * 0.75, y: -50)
                        for i in stride(from: 0, to: 360, by: 30) {
                            let angle = Double(i) * .pi / 180
                            let length = max(geo.size.width, geo.size.height) * 1.5
                            path.move(to: center)
                            path.addLine(to: CGPoint(
                                x: center.x + cos(angle) * length,
                                y: center.y + sin(angle) * length
                            ))
                        }
                    }
                    .stroke(Color.white.opacity(0.03), lineWidth: 40)
                }
                .ignoresSafeArea()
            }
        }
    }
}
