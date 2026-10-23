import SwiftUI

/// Reports the language toggle's on-screen frame so the tip can point at it.
struct LanguageToggleFrameKey: PreferenceKey {
    static var defaultValue: CGRect = .zero
    static func reduce(value: inout CGRect, nextValue: () -> CGRect) {
        let next = nextValue()
        if next != .zero { value = next }
    }
}

/// First-launch tip: the angel artwork is placed so her arrow ends at the real
/// En | த toggle, which shows through a glowing spotlight in the dimmed screen.
struct LanguageTipView: View {
    /// Toggle frame in global coordinates.
    let toggleFrame: CGRect
    let onDismiss: () -> Void

    @State private var appeared = false

    // Artwork geometry, in pixels of LanguageTipArt.png.
    private static let artSize = CGSize(width: 1086, height: 1448)
    private static let artHoleCenter = CGPoint(x: 933, y: 320)
    private static let artHoleWidth: CGFloat = 256

    private let gold = Color(red: 1.0, green: 0.82, blue: 0.4)

    var body: some View {
        GeometryReader { geo in
            let origin = geo.frame(in: .global).origin
            let toggle = toggleFrame.offsetBy(dx: -origin.x, dy: -origin.y)
            let hole = toggle.insetBy(dx: -6, dy: -6)

            // Scale the art so its spotlight gap matches the real toggle,
            // but never wider than the screen.
            let fitScale = (geo.size.width - 12) / Self.artSize.width
            let matchScale = hole.width / Self.artHoleWidth
            let scale = min(matchScale, fitScale)
            let artWidth = Self.artSize.width * scale
            let artHeight = Self.artSize.height * scale
            let rawX = toggle.midX - Self.artHoleCenter.x * scale
            let artX = min(max(rawX, 6), geo.size.width - artWidth - 6)
            let artY = toggle.midY - Self.artHoleCenter.y * scale

            ZStack(alignment: .topLeading) {
                // Dimmed screen with a see-through, tappable hole over the real toggle.
                SpotlightShape(hole: hole)
                    .fill(Color.black.opacity(0.6), style: FillStyle(eoFill: true))
                    .contentShape(SpotlightShape(hole: hole), eoFill: true)
                    .onTapGesture { dismiss() }

                Capsule()
                    .stroke(gold, lineWidth: 2.5)
                    .shadow(color: gold.opacity(0.9), radius: 10)
                    .shadow(color: gold.opacity(0.6), radius: 18)
                    .frame(width: hole.width, height: hole.height)
                    .offset(x: hole.minX, y: hole.minY)
                    .allowsHitTesting(false)

                Image("LanguageTipArt")
                    .resizable()
                    .frame(width: artWidth, height: artHeight)
                    .offset(x: artX, y: artY)
                    .onTapGesture { dismiss() }
                    .accessibilityLabel("Tip: Tap the language button at the top right to view this Bible verse in English or Tamil. Got it.")
                    .accessibilityAddTraits(.isButton)
            }
            .opacity(appeared ? 1 : 0)
            .animation(.easeOut(duration: 0.35), value: appeared)
        }
        .ignoresSafeArea()
        .onAppear { appeared = true }
    }

    private func dismiss() {
        appeared = false
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) { onDismiss() }
    }
}

/// Reports each tab-bar button's on-screen frame, keyed by tab tag.
struct TabButtonFramesKey: PreferenceKey {
    static var defaultValue: [Int: CGRect] = [:]
    static func reduce(value: inout [Int: CGRect], nextValue: () -> [Int: CGRect]) {
        value.merge(nextValue()) { $1 }
    }
}

/// First-launch tip shown after the language tip: the sleeping-angel artwork sits
/// above the tab bar and a curved arrow ends at the real Themes tab, which glows
/// through a spotlight in the dimmed screen.
struct ThemesTipView: View {
    /// Themes tab button frame in global coordinates.
    let tabFrame: CGRect
    let onDismiss: () -> Void

    @State private var appeared = false

    // Artwork geometry, in pixels of ThemesTipArt.png.
    private static let artSize = CGSize(width: 881, height: 555)
    /// Where the arrow leaves the art (just under the Got it button).
    private static let artArrowStart = CGPoint(x: 322, y: 540)

    private let glow = Color(red: 1.0, green: 0.84, blue: 0.45)
    private let arrowColor = Color(red: 1.0, green: 0.97, blue: 0.88)

    var body: some View {
        GeometryReader { geo in
            let origin = geo.frame(in: .global).origin
            let tab = tabFrame.offsetBy(dx: -origin.x, dy: -origin.y)
            let side = max(tab.width, tab.height) + 16
            let hole = CGRect(x: tab.midX - side / 2, y: tab.midY - side / 2, width: side, height: side)

            let artWidth = min(geo.size.width - 16, 440)
            let scale = artWidth / Self.artSize.width
            let artHeight = Self.artSize.height * scale
            let artX = (geo.size.width - artWidth) / 2
            let artY = hole.minY - 56 - artHeight
            let arrowStart = CGPoint(x: artX + Self.artArrowStart.x * scale,
                                     y: artY + Self.artArrowStart.y * scale)
            let arrowEnd = CGPoint(x: hole.midX, y: hole.minY - 4)

            ZStack(alignment: .topLeading) {
                SpotlightShape(hole: hole, circular: true)
                    .fill(Color.black.opacity(0.6), style: FillStyle(eoFill: true))
                    .contentShape(SpotlightShape(hole: hole, circular: true), eoFill: true)
                    .onTapGesture { dismiss() }

                Circle()
                    .stroke(glow, lineWidth: 2.5)
                    .shadow(color: glow.opacity(0.9), radius: 10)
                    .shadow(color: glow.opacity(0.6), radius: 20)
                    .frame(width: hole.width, height: hole.height)
                    .offset(x: hole.minX, y: hole.minY)
                    .allowsHitTesting(false)

                CurvedArrow(start: arrowStart, end: arrowEnd)
                    .stroke(arrowColor, style: StrokeStyle(lineWidth: 3.5, lineCap: .round, lineJoin: .round))
                    .shadow(color: glow, radius: 6)
                    .allowsHitTesting(false)

                Image("ThemesTipArt")
                    .resizable()
                    .frame(width: artWidth, height: artHeight)
                    .offset(x: artX, y: artY)
                    .onTapGesture { dismiss() }
                    .accessibilityLabel("Tip: Explore the Themes tab below to discover beautiful new themes for your daily Bible verses. Got it.")
                    .accessibilityAddTraits(.isButton)
            }
            .opacity(appeared ? 1 : 0)
            .animation(.easeOut(duration: 0.35), value: appeared)
        }
        .ignoresSafeArea()
        .onAppear { appeared = true }
    }

    private func dismiss() {
        appeared = false
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) { onDismiss() }
    }
}

/// A gently curving arrow from `start` to `end`, with an arrowhead at `end`.
private struct CurvedArrow: Shape {
    let start: CGPoint
    let end: CGPoint

    func path(in rect: CGRect) -> Path {
        var p = Path()
        let control = CGPoint(x: start.x - 10, y: end.y - 10)
        p.move(to: start)
        p.addQuadCurve(to: end, control: control)
        // Arrowhead aligned with the curve's final direction (control -> end).
        let angle = atan2(end.y - control.y, end.x - control.x)
        let len: CGFloat = 14, spread: CGFloat = .pi / 6
        p.move(to: end)
        p.addLine(to: CGPoint(x: end.x - len * cos(angle - spread), y: end.y - len * sin(angle - spread)))
        p.move(to: end)
        p.addLine(to: CGPoint(x: end.x - len * cos(angle + spread), y: end.y - len * sin(angle + spread)))
        return p
    }
}

private struct SpotlightShape: Shape {
    let hole: CGRect
    var circular = false

    func path(in rect: CGRect) -> Path {
        var p = Path()
        p.addRect(rect)
        if circular {
            p.addEllipse(in: hole)
        } else {
            p.addRoundedRect(in: hole, cornerSize: CGSize(width: hole.height / 2, height: hole.height / 2))
        }
        return p
    }
}
