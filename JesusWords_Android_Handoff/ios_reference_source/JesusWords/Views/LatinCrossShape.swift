import SwiftUI

/// A Latin cross (Christian cross) shape where the bottom arm is longer than the top.
/// The natural aspect ratio is about 0.55:1 (width:height).
struct LatinCrossShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()

        let w = rect.width
        let h = rect.height

        // Bar thickness (same for vertical and horizontal)
        let barW = w * 0.35
        // Crossbar starts at 22% from top — gives a longer bottom arm
        let crossbarTop = h * 0.22
        let crossbarBottom = crossbarTop + barW

        // Vertical bar (centered horizontally)
        let vLeft = (w - barW) / 2
        let vRight = (w + barW) / 2

        // Draw clockwise from top-left of vertical bar
        path.move(to: CGPoint(x: vLeft, y: 0))
        path.addLine(to: CGPoint(x: vRight, y: 0))
        path.addLine(to: CGPoint(x: vRight, y: crossbarTop))
        path.addLine(to: CGPoint(x: w, y: crossbarTop))
        path.addLine(to: CGPoint(x: w, y: crossbarBottom))
        path.addLine(to: CGPoint(x: vRight, y: crossbarBottom))
        path.addLine(to: CGPoint(x: vRight, y: h))
        path.addLine(to: CGPoint(x: vLeft, y: h))
        path.addLine(to: CGPoint(x: vLeft, y: crossbarBottom))
        path.addLine(to: CGPoint(x: 0, y: crossbarBottom))
        path.addLine(to: CGPoint(x: 0, y: crossbarTop))
        path.addLine(to: CGPoint(x: vLeft, y: crossbarTop))
        path.closeSubpath()

        return path
    }
}

/// A ready-to-use Latin cross icon view. Drop-in replacement for Image(systemName: "cross.fill").
struct LatinCrossIcon: View {
    var size: CGFloat = 36

    var body: some View {
        LatinCrossShape()
            .fill()
            .aspectRatio(0.55, contentMode: .fit)
            .frame(height: size)
    }
}

/// Helper that renders a system icon OR a Latin cross if the icon name is "cross.fill".
/// Use this in overlay / theme rendering where icons come from string names.
struct ThemeIcon: View {
    let systemName: String

    @ViewBuilder
    var body: some View {
        if systemName == "cross.fill" {
            LatinCrossShape()
                .fill()
                .aspectRatio(0.55, contentMode: .fit)
        } else {
            Image(systemName: systemName)
                .resizable()
                .scaledToFit()
        }
    }
}
