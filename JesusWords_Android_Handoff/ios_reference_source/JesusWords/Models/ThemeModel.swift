import SwiftUI

struct AppTheme: Identifiable, Equatable {
    let id: String
    let name: String
    let subtitle: String
    let gradientColors: [Color]
    let accentColor: Color
    let fontDesign: Font.Design
    let decorativeIcon: String
    let overlayElements: [ThemeOverlay]
    var backgroundImageName: String? = nil

    static func == (lhs: AppTheme, rhs: AppTheme) -> Bool {
        lhs.id == rhs.id
    }
}

struct ThemeOverlay: Equatable {
    let systemIcon: String
    let size: CGFloat
    let opacity: Double
    let xOffset: CGFloat
    let yOffset: CGFloat
    let rotation: Double
}

class ThemeManager: ObservableObject {
    static let shared = ThemeManager()

    @Published var selectedThemeId: String {
        didSet {
            UserDefaults.standard.set(selectedThemeId, forKey: "selectedThemeId")
        }
    }

    var selectedTheme: AppTheme {
        allThemes.first(where: { $0.id == selectedThemeId }) ?? allThemes[0]
    }

    let allThemes: [AppTheme] = [
        // 1. Sunrise Chapel - Warm golden morning light
        AppTheme(
            id: "sunrise_chapel",
            name: "Sunrise Chapel",
            subtitle: "Golden morning light",
            gradientColors: [
                Color(red: 1.0, green: 0.85, blue: 0.40),
                Color(red: 0.95, green: 0.55, blue: 0.25),
                Color(red: 0.70, green: 0.25, blue: 0.30),
                Color(red: 0.40, green: 0.15, blue: 0.30)
            ],
            accentColor: Color(red: 1.0, green: 0.90, blue: 0.60),
            fontDesign: .serif,
            decorativeIcon: "sun.max.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "cross.fill", size: 180, opacity: 0.06, xOffset: 0.8, yOffset: 0.3, rotation: -10),
                ThemeOverlay(systemIcon: "sparkle", size: 30, opacity: 0.12, xOffset: 0.15, yOffset: 0.15, rotation: 0),
                ThemeOverlay(systemIcon: "sparkle", size: 20, opacity: 0.10, xOffset: 0.85, yOffset: 0.12, rotation: 45)
            ]
        ),

        // 2. Mountain Sermon - Majestic blue-purple peaks
        AppTheme(
            id: "mountain_sermon",
            name: "Mountain Sermon",
            subtitle: "Majestic mountain peaks",
            gradientColors: [
                Color(red: 0.55, green: 0.75, blue: 0.95),
                Color(red: 0.35, green: 0.45, blue: 0.80),
                Color(red: 0.20, green: 0.20, blue: 0.55),
                Color(red: 0.10, green: 0.10, blue: 0.35)
            ],
            accentColor: Color(red: 0.70, green: 0.85, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "mountain.2.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "mountain.2.fill", size: 250, opacity: 0.07, xOffset: 0.5, yOffset: 0.85, rotation: 0),
                ThemeOverlay(systemIcon: "star.fill", size: 15, opacity: 0.15, xOffset: 0.20, yOffset: 0.10, rotation: 0),
                ThemeOverlay(systemIcon: "star.fill", size: 10, opacity: 0.12, xOffset: 0.70, yOffset: 0.08, rotation: 0)
            ]
        ),

        // 3. Garden of Gethsemane - Rich olive greens
        AppTheme(
            id: "garden_gethsemane",
            name: "Garden of Gethsemane",
            subtitle: "Peaceful olive garden",
            gradientColors: [
                Color(red: 0.65, green: 0.85, blue: 0.55),
                Color(red: 0.35, green: 0.65, blue: 0.40),
                Color(red: 0.18, green: 0.42, blue: 0.30),
                Color(red: 0.08, green: 0.22, blue: 0.18)
            ],
            accentColor: Color(red: 0.80, green: 0.95, blue: 0.70),
            fontDesign: .serif,
            decorativeIcon: "leaf.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "leaf.fill", size: 160, opacity: 0.06, xOffset: 0.85, yOffset: 0.25, rotation: -30),
                ThemeOverlay(systemIcon: "leaf.fill", size: 100, opacity: 0.04, xOffset: 0.10, yOffset: 0.70, rotation: 20),
                ThemeOverlay(systemIcon: "sparkle", size: 25, opacity: 0.10, xOffset: 0.30, yOffset: 0.15, rotation: 0)
            ]
        ),

        // 4. Sea of Galilee - Calm turquoise waters
        AppTheme(
            id: "sea_galilee",
            name: "Sea of Galilee",
            subtitle: "Calm turquoise waters",
            gradientColors: [
                Color(red: 0.55, green: 0.90, blue: 0.92),
                Color(red: 0.25, green: 0.70, blue: 0.80),
                Color(red: 0.12, green: 0.45, blue: 0.65),
                Color(red: 0.05, green: 0.22, blue: 0.42)
            ],
            accentColor: Color(red: 0.70, green: 0.95, blue: 0.98),
            fontDesign: .serif,
            decorativeIcon: "water.waves",
            overlayElements: [
                ThemeOverlay(systemIcon: "water.waves", size: 200, opacity: 0.06, xOffset: 0.5, yOffset: 0.80, rotation: 0),
                ThemeOverlay(systemIcon: "wind", size: 60, opacity: 0.05, xOffset: 0.80, yOffset: 0.15, rotation: -10),
                ThemeOverlay(systemIcon: "bird.fill", size: 25, opacity: 0.08, xOffset: 0.25, yOffset: 0.12, rotation: 0)
            ]
        ),

        // 5. Starry Bethlehem - Deep night sky
        AppTheme(
            id: "starry_bethlehem",
            name: "Starry Bethlehem",
            subtitle: "Deep celestial night",
            gradientColors: [
                Color(red: 0.10, green: 0.12, blue: 0.30),
                Color(red: 0.08, green: 0.08, blue: 0.25),
                Color(red: 0.15, green: 0.12, blue: 0.35),
                Color(red: 0.05, green: 0.05, blue: 0.18)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.70),
            fontDesign: .serif,
            decorativeIcon: "star.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "star.fill", size: 50, opacity: 0.20, xOffset: 0.50, yOffset: 0.08, rotation: 0),
                ThemeOverlay(systemIcon: "star.fill", size: 12, opacity: 0.25, xOffset: 0.15, yOffset: 0.20, rotation: 15),
                ThemeOverlay(systemIcon: "star.fill", size: 8, opacity: 0.20, xOffset: 0.80, yOffset: 0.15, rotation: 0),
                ThemeOverlay(systemIcon: "star.fill", size: 10, opacity: 0.18, xOffset: 0.65, yOffset: 0.25, rotation: 30)
            ]
        ),

        // 6. Desert Wilderness - Warm sandy tones
        AppTheme(
            id: "desert_wilderness",
            name: "Desert Wilderness",
            subtitle: "Warm sandy wilderness",
            gradientColors: [
                Color(red: 0.95, green: 0.82, blue: 0.60),
                Color(red: 0.88, green: 0.65, blue: 0.40),
                Color(red: 0.72, green: 0.45, blue: 0.28),
                Color(red: 0.45, green: 0.25, blue: 0.18)
            ],
            accentColor: Color(red: 1.0, green: 0.92, blue: 0.75),
            fontDesign: .serif,
            decorativeIcon: "sun.haze.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "sun.max.fill", size: 120, opacity: 0.08, xOffset: 0.80, yOffset: 0.10, rotation: 0),
                ThemeOverlay(systemIcon: "wind", size: 80, opacity: 0.05, xOffset: 0.30, yOffset: 0.60, rotation: 5)
            ]
        ),

        // 7. Lily Fields - Soft white and pink
        AppTheme(
            id: "lily_fields",
            name: "Lily of the Fields",
            subtitle: "Soft floral serenity",
            gradientColors: [
                Color(red: 1.0, green: 0.95, blue: 0.95),
                Color(red: 0.95, green: 0.80, blue: 0.85),
                Color(red: 0.80, green: 0.55, blue: 0.65),
                Color(red: 0.55, green: 0.30, blue: 0.45)
            ],
            accentColor: Color(red: 1.0, green: 0.90, blue: 0.92),
            fontDesign: .rounded,
            decorativeIcon: "camera.macro",
            overlayElements: [
                ThemeOverlay(systemIcon: "camera.macro", size: 140, opacity: 0.06, xOffset: 0.85, yOffset: 0.30, rotation: -20),
                ThemeOverlay(systemIcon: "camera.macro", size: 80, opacity: 0.04, xOffset: 0.10, yOffset: 0.65, rotation: 15),
                ThemeOverlay(systemIcon: "sparkle", size: 18, opacity: 0.12, xOffset: 0.40, yOffset: 0.12, rotation: 0)
            ]
        ),

        // 8. Cedar of Lebanon - Deep forest greens
        AppTheme(
            id: "cedar_lebanon",
            name: "Cedar of Lebanon",
            subtitle: "Ancient forest majesty",
            gradientColors: [
                Color(red: 0.35, green: 0.55, blue: 0.35),
                Color(red: 0.20, green: 0.40, blue: 0.25),
                Color(red: 0.12, green: 0.28, blue: 0.18),
                Color(red: 0.05, green: 0.15, blue: 0.10)
            ],
            accentColor: Color(red: 0.65, green: 0.85, blue: 0.60),
            fontDesign: .serif,
            decorativeIcon: "tree.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "tree.fill", size: 200, opacity: 0.06, xOffset: 0.80, yOffset: 0.75, rotation: 0),
                ThemeOverlay(systemIcon: "tree.fill", size: 150, opacity: 0.04, xOffset: 0.20, yOffset: 0.80, rotation: 0),
                ThemeOverlay(systemIcon: "leaf.fill", size: 30, opacity: 0.08, xOffset: 0.15, yOffset: 0.20, rotation: -25)
            ]
        ),

        // 9. Burning Bush - Fiery reds and oranges
        AppTheme(
            id: "burning_bush",
            name: "Burning Bush",
            subtitle: "Holy fire and flame",
            gradientColors: [
                Color(red: 1.0, green: 0.75, blue: 0.30),
                Color(red: 0.95, green: 0.45, blue: 0.20),
                Color(red: 0.80, green: 0.20, blue: 0.15),
                Color(red: 0.45, green: 0.10, blue: 0.10)
            ],
            accentColor: Color(red: 1.0, green: 0.85, blue: 0.50),
            fontDesign: .serif,
            decorativeIcon: "flame.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "flame.fill", size: 160, opacity: 0.08, xOffset: 0.80, yOffset: 0.30, rotation: -5),
                ThemeOverlay(systemIcon: "flame.fill", size: 100, opacity: 0.05, xOffset: 0.15, yOffset: 0.65, rotation: 10),
                ThemeOverlay(systemIcon: "sparkle", size: 20, opacity: 0.15, xOffset: 0.50, yOffset: 0.10, rotation: 0)
            ]
        ),

        // 10. Heavenly Clouds - Ethereal light
        AppTheme(
            id: "heavenly_clouds",
            name: "Heavenly Clouds",
            subtitle: "Ethereal divine light",
            gradientColors: [
                Color(red: 0.90, green: 0.92, blue: 1.0),
                Color(red: 0.75, green: 0.82, blue: 0.95),
                Color(red: 0.55, green: 0.65, blue: 0.88),
                Color(red: 0.35, green: 0.42, blue: 0.72)
            ],
            accentColor: Color(red: 0.95, green: 0.95, blue: 1.0),
            fontDesign: .rounded,
            decorativeIcon: "cloud.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "cloud.fill", size: 180, opacity: 0.08, xOffset: 0.75, yOffset: 0.15, rotation: 0),
                ThemeOverlay(systemIcon: "cloud.fill", size: 120, opacity: 0.05, xOffset: 0.20, yOffset: 0.25, rotation: 0),
                ThemeOverlay(systemIcon: "sun.max.fill", size: 80, opacity: 0.06, xOffset: 0.50, yOffset: 0.05, rotation: 0)
            ]
        ),

        // 11. Royal Purple - Regal majesty
        AppTheme(
            id: "royal_purple",
            name: "Royal Purple",
            subtitle: "Regal crown of glory",
            gradientColors: [
                Color(red: 0.75, green: 0.60, blue: 0.95),
                Color(red: 0.55, green: 0.35, blue: 0.80),
                Color(red: 0.35, green: 0.18, blue: 0.60),
                Color(red: 0.18, green: 0.08, blue: 0.38)
            ],
            accentColor: Color(red: 0.88, green: 0.78, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "crown.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "crown.fill", size: 140, opacity: 0.07, xOffset: 0.50, yOffset: 0.15, rotation: 0),
                ThemeOverlay(systemIcon: "sparkle", size: 25, opacity: 0.15, xOffset: 0.20, yOffset: 0.30, rotation: 0),
                ThemeOverlay(systemIcon: "sparkle", size: 18, opacity: 0.12, xOffset: 0.80, yOffset: 0.25, rotation: 30)
            ]
        ),

        // 12. Living Water - Crystal blue
        AppTheme(
            id: "living_water",
            name: "Living Water",
            subtitle: "Rivers of living water",
            gradientColors: [
                Color(red: 0.40, green: 0.80, blue: 0.90),
                Color(red: 0.20, green: 0.60, blue: 0.78),
                Color(red: 0.10, green: 0.40, blue: 0.62),
                Color(red: 0.05, green: 0.20, blue: 0.40)
            ],
            accentColor: Color(red: 0.60, green: 0.92, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "drop.fill",
            overlayElements: [
                ThemeOverlay(systemIcon: "drop.fill", size: 100, opacity: 0.07, xOffset: 0.80, yOffset: 0.20, rotation: 10),
                ThemeOverlay(systemIcon: "drop.fill", size: 60, opacity: 0.05, xOffset: 0.15, yOffset: 0.55, rotation: -15),
                ThemeOverlay(systemIcon: "water.waves", size: 200, opacity: 0.05, xOffset: 0.50, yOffset: 0.85, rotation: 0)
            ]
        ),

        // 13. Gates of Heaven - Golden gates with doves
        AppTheme(
            id: "gates_of_heaven",
            name: "Gates of Heaven",
            subtitle: "Pearly gates of glory",
            gradientColors: [
                Color(red: 1.0, green: 0.95, blue: 0.80),
                Color(red: 0.95, green: 0.85, blue: 0.55),
                Color(red: 0.80, green: 0.70, blue: 0.40),
                Color(red: 0.55, green: 0.45, blue: 0.25)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.70),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "ThemeGatesOfHeaven"
        ),

        // 14. River of Life - Heavenly waters & gates
        AppTheme(
            id: "river_of_life",
            name: "River of Life",
            subtitle: "Flowing from the throne",
            gradientColors: [
                Color(red: 0.90, green: 0.95, blue: 1.0),
                Color(red: 0.70, green: 0.85, blue: 0.95),
                Color(red: 0.50, green: 0.70, blue: 0.90),
                Color(red: 0.30, green: 0.50, blue: 0.75)
            ],
            accentColor: Color(red: 0.95, green: 0.98, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "Wallpaper2"
        ),

        // 15. The Good Shepherd - Paradise waters
        AppTheme(
            id: "good_shepherd",
            name: "The Good Shepherd",
            subtitle: "He leads me beside still waters",
            gradientColors: [
                Color(red: 0.55, green: 0.90, blue: 0.92),
                Color(red: 0.35, green: 0.75, blue: 0.85),
                Color(red: 0.20, green: 0.55, blue: 0.70),
                Color(red: 0.10, green: 0.35, blue: 0.50)
            ],
            accentColor: Color(red: 0.75, green: 0.95, blue: 0.98),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "Wallpaper3"
        ),

        // 16. Holy Spirit Glow - Crystal waters & lilies
        AppTheme(
            id: "holy_spirit_glow",
            name: "Holy Spirit Glow",
            subtitle: "Living waters flowing",
            gradientColors: [
                Color(red: 0.95, green: 0.95, blue: 0.85),
                Color(red: 0.80, green: 0.90, blue: 0.80),
                Color(red: 0.55, green: 0.80, blue: 0.75),
                Color(red: 0.30, green: 0.55, blue: 0.65)
            ],
            accentColor: Color(red: 1.0, green: 0.98, blue: 0.90),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "Wallpaper4"
        ),

        // 17. Resurrection Glory - Pastoral sunrise
        AppTheme(
            id: "resurrection_glory",
            name: "Resurrection Glory",
            subtitle: "New life at sunrise",
            gradientColors: [
                Color(red: 1.0, green: 0.90, blue: 0.65),
                Color(red: 0.95, green: 0.75, blue: 0.50),
                Color(red: 0.75, green: 0.60, blue: 0.45),
                Color(red: 0.45, green: 0.45, blue: 0.40)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.75),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "Wallpaper5"
        ),

        // 18. Tree of Life - Golden tree with waterfalls
        AppTheme(
            id: "tree_of_life",
            name: "Tree of Life",
            subtitle: "Heavenly tree of eternal life",
            gradientColors: [
                Color(red: 1.0, green: 0.92, blue: 0.65),
                Color(red: 0.90, green: 0.75, blue: 0.45),
                Color(red: 0.55, green: 0.70, blue: 0.65),
                Color(red: 0.25, green: 0.45, blue: 0.55)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.70),
            fontDesign: .serif,
            decorativeIcon: "tree.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperTreeOfLife"
        ),

        // 19. New Jerusalem - Heavenly golden city
        AppTheme(
            id: "new_jerusalem",
            name: "New Jerusalem",
            subtitle: "The holy city above the clouds",
            gradientColors: [
                Color(red: 1.0, green: 0.95, blue: 0.80),
                Color(red: 0.95, green: 0.85, blue: 0.55),
                Color(red: 0.85, green: 0.75, blue: 0.45),
                Color(red: 0.50, green: 0.55, blue: 0.65)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.75),
            fontDesign: .serif,
            decorativeIcon: "building.columns.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperNewJerusalem"
        ),

        // 20. Still Waters - Peaceful lake sunrise
        AppTheme(
            id: "still_waters",
            name: "Still Waters",
            subtitle: "He restores my soul",
            gradientColors: [
                Color(red: 0.65, green: 0.80, blue: 0.95),
                Color(red: 0.95, green: 0.80, blue: 0.60),
                Color(red: 0.80, green: 0.75, blue: 0.65),
                Color(red: 0.40, green: 0.55, blue: 0.70)
            ],
            accentColor: Color(red: 0.95, green: 0.90, blue: 0.80),
            fontDesign: .serif,
            decorativeIcon: "water.waves",
            overlayElements: [],
            backgroundImageName: "WallpaperStillWaters"
        ),

        // 21. Star of Bethlehem - Night sky over Bethlehem
        AppTheme(
            id: "star_of_bethlehem",
            name: "Star of Bethlehem",
            subtitle: "A light shining in the darkness",
            gradientColors: [
                Color(red: 0.10, green: 0.15, blue: 0.40),
                Color(red: 0.08, green: 0.12, blue: 0.30),
                Color(red: 0.15, green: 0.15, blue: 0.35),
                Color(red: 0.05, green: 0.08, blue: 0.20)
            ],
            accentColor: Color(red: 1.0, green: 0.90, blue: 0.60),
            fontDesign: .serif,
            decorativeIcon: "star.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperStarOfBethlehem"
        ),

        // 22. Rainbow Promise - Covenant rainbow over valley
        AppTheme(
            id: "rainbow_promise",
            name: "Rainbow Promise",
            subtitle: "God's covenant of mercy",
            gradientColors: [
                Color(red: 0.70, green: 0.85, blue: 0.95),
                Color(red: 0.95, green: 0.80, blue: 0.85),
                Color(red: 0.90, green: 0.85, blue: 0.60),
                Color(red: 0.45, green: 0.70, blue: 0.75)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.85),
            fontDesign: .serif,
            decorativeIcon: "rainbow",
            overlayElements: [],
            backgroundImageName: "WallpaperRainbowPromise"
        ),

        // 23. Easter Dawn - Empty tomb at pink sunrise
        AppTheme(
            id: "easter_dawn",
            name: "Easter Dawn",
            subtitle: "He is risen",
            gradientColors: [
                Color(red: 1.0, green: 0.80, blue: 0.75),
                Color(red: 0.95, green: 0.70, blue: 0.65),
                Color(red: 0.85, green: 0.60, blue: 0.60),
                Color(red: 0.55, green: 0.35, blue: 0.40)
            ],
            accentColor: Color(red: 1.0, green: 0.92, blue: 0.85),
            fontDesign: .serif,
            decorativeIcon: "sunrise.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperEasterDawn"
        ),

        // 24. Crystal Paradise - Clear turquoise paradise waters
        AppTheme(
            id: "crystal_paradise",
            name: "Crystal Paradise",
            subtitle: "Clear waters of paradise",
            gradientColors: [
                Color(red: 0.85, green: 0.95, blue: 0.95),
                Color(red: 0.55, green: 0.90, blue: 0.90),
                Color(red: 0.30, green: 0.70, blue: 0.80),
                Color(red: 0.15, green: 0.45, blue: 0.60)
            ],
            accentColor: Color(red: 0.90, green: 0.98, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "water.waves",
            overlayElements: [],
            backgroundImageName: "WallpaperCrystalParadise"
        ),

        // 25. Pentecost Light - Dove in pink/purple clouds
        AppTheme(
            id: "pentecost_light",
            name: "Pentecost Light",
            subtitle: "The Spirit descends",
            gradientColors: [
                Color(red: 1.0, green: 0.90, blue: 0.95),
                Color(red: 0.95, green: 0.80, blue: 0.95),
                Color(red: 0.75, green: 0.65, blue: 0.90),
                Color(red: 0.45, green: 0.35, blue: 0.65)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "sparkles",
            overlayElements: [],
            backgroundImageName: "WallpaperPentecostLight"
        ),

        // 26. Sacred Heart Cathedral - Red stained glass sanctuary
        AppTheme(
            id: "sacred_heart_cathedral",
            name: "Sacred Heart Cathedral",
            subtitle: "Sacred sanctuary of grace",
            gradientColors: [
                Color(red: 1.0, green: 0.85, blue: 0.60),
                Color(red: 0.95, green: 0.55, blue: 0.45),
                Color(red: 0.75, green: 0.20, blue: 0.25),
                Color(red: 0.45, green: 0.10, blue: 0.15)
            ],
            accentColor: Color(red: 1.0, green: 0.90, blue: 0.75),
            fontDesign: .serif,
            decorativeIcon: "building.columns.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperSacredHeartCathedral"
        ),

        // 27. Crown of Glory - Purple heavenly throne
        AppTheme(
            id: "crown_of_glory",
            name: "Crown of Glory",
            subtitle: "Royal throne of heaven",
            gradientColors: [
                Color(red: 1.0, green: 0.90, blue: 0.95),
                Color(red: 0.90, green: 0.80, blue: 0.95),
                Color(red: 0.70, green: 0.60, blue: 0.90),
                Color(red: 0.40, green: 0.30, blue: 0.70)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 1.0),
            fontDesign: .serif,
            decorativeIcon: "crown.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperCrownOfGlory"
        ),

        // 28. Nativity Star - Deep blue night with golden star
        AppTheme(
            id: "nativity_star",
            name: "Nativity Star",
            subtitle: "The star that led the wise",
            gradientColors: [
                Color(red: 0.60, green: 0.75, blue: 0.95),
                Color(red: 0.25, green: 0.40, blue: 0.75),
                Color(red: 0.10, green: 0.20, blue: 0.50),
                Color(red: 0.05, green: 0.10, blue: 0.30)
            ],
            accentColor: Color(red: 1.0, green: 0.92, blue: 0.65),
            fontDesign: .serif,
            decorativeIcon: "star.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperNativityStar"
        ),

        // 29. Rose of Sharon - Pink roses archway
        AppTheme(
            id: "rose_of_sharon",
            name: "Rose of Sharon",
            subtitle: "Beauty of the Beloved",
            gradientColors: [
                Color(red: 1.0, green: 0.90, blue: 0.85),
                Color(red: 1.0, green: 0.75, blue: 0.75),
                Color(red: 0.95, green: 0.60, blue: 0.65),
                Color(red: 0.70, green: 0.40, blue: 0.50)
            ],
            accentColor: Color(red: 1.0, green: 0.95, blue: 0.90),
            fontDesign: .serif,
            decorativeIcon: "camera.macro",
            overlayElements: [],
            backgroundImageName: "WallpaperRoseOfSharon"
        ),

        // 30. Garden of Grace - Green garden with cross and fountain
        AppTheme(
            id: "garden_of_grace",
            name: "Garden of Grace",
            subtitle: "Living waters flow here",
            gradientColors: [
                Color(red: 0.90, green: 0.95, blue: 0.80),
                Color(red: 0.70, green: 0.85, blue: 0.65),
                Color(red: 0.40, green: 0.70, blue: 0.50),
                Color(red: 0.20, green: 0.45, blue: 0.35)
            ],
            accentColor: Color(red: 0.95, green: 1.0, blue: 0.90),
            fontDesign: .serif,
            decorativeIcon: "leaf.fill",
            overlayElements: [],
            backgroundImageName: "WallpaperGardenOfGrace"
        )
    ]

    init() {
        self.selectedThemeId = UserDefaults.standard.string(forKey: "selectedThemeId") ?? "sunrise_chapel"
    }
}
