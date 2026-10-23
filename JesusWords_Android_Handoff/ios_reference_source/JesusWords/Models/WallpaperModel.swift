import SwiftUI

struct Wallpaper: Identifiable, Equatable {
    let id: String
    let name: String
    let tamilName: String
    let subtitle: String
    let tamilSubtitle: String
    let imageName: String

    static func == (lhs: Wallpaper, rhs: Wallpaper) -> Bool {
        lhs.id == rhs.id
    }
}

class WallpaperManager: ObservableObject {
    static let shared = WallpaperManager()

    private let storageKey = "selectedWallpaperId"

    @Published var selectedWallpaperId: String {
        didSet {
            UserDefaults.standard.set(selectedWallpaperId, forKey: storageKey)
        }
    }

    var selectedWallpaper: Wallpaper {
        allWallpapers.first(where: { $0.id == selectedWallpaperId }) ?? allWallpapers[0]
    }

    let allWallpapers: [Wallpaper] = [
        Wallpaper(
            id: "wallpaper_gates_of_heaven",
            name: "Gates of Heaven",
            tamilName: "விண்ணக வாயில்கள்",
            subtitle: "Pearly gates of glory",
            tamilSubtitle: "மகிமையின் முத்து வாயில்கள்",
            imageName: "Wallpaper1"
        ),
        Wallpaper(
            id: "wallpaper_river_of_life",
            name: "River of Life",
            tamilName: "வாழ்வின் நதி",
            subtitle: "Flowing from the throne",
            tamilSubtitle: "அரியணையிலிருந்து பாயும் நதி",
            imageName: "Wallpaper2"
        ),
        Wallpaper(
            id: "wallpaper_good_shepherd",
            name: "The Good Shepherd",
            tamilName: "நல்ல ஆயன்",
            subtitle: "He leads me beside still waters",
            tamilSubtitle: "அமைதியான நீரருகே வழிநடத்துகிறார்",
            imageName: "Wallpaper3"
        ),
        Wallpaper(
            id: "wallpaper_holy_spirit",
            name: "Holy Spirit Glow",
            tamilName: "தூய ஆவியின் ஒளி",
            subtitle: "Descending in light",
            tamilSubtitle: "ஒளியில் இறங்கும் ஆவி",
            imageName: "Wallpaper4"
        ),
        Wallpaper(
            id: "wallpaper_resurrection",
            name: "Resurrection Glory",
            tamilName: "உயிர்த்தெழுதலின் மகிமை",
            subtitle: "New life at sunrise",
            tamilSubtitle: "விடியலில் புதிய வாழ்வு",
            imageName: "Wallpaper5"
        ),
        Wallpaper(
            id: "wallpaper_tree_of_life",
            name: "Tree of Life",
            tamilName: "வாழ்வின் மரம்",
            subtitle: "Heavenly golden tree with waterfalls",
            tamilSubtitle: "நீர்வீழ்ச்சிகளுடன் வானுலகப் பொன் மரம்",
            imageName: "WallpaperTreeOfLife"
        ),
        Wallpaper(
            id: "wallpaper_new_jerusalem",
            name: "New Jerusalem",
            tamilName: "புதிய எருசலேம்",
            subtitle: "Golden heavenly city above the clouds",
            tamilSubtitle: "மேகங்களுக்கு மேல் பொன்னான வானக நகரம்",
            imageName: "WallpaperNewJerusalem"
        ),
        Wallpaper(
            id: "wallpaper_still_waters",
            name: "Still Waters",
            tamilName: "அமைதியான நீர்",
            subtitle: "A peaceful lake at sunrise",
            tamilSubtitle: "விடியலில் அமைதியான ஏரி",
            imageName: "WallpaperStillWaters"
        ),
        Wallpaper(
            id: "wallpaper_star_of_bethlehem",
            name: "Star of Bethlehem",
            tamilName: "பெத்லகேமின் விண்மீன்",
            subtitle: "A radiant star over Bethlehem",
            tamilSubtitle: "பெத்லகேமின் மேல் ஒளிவீசும் விண்மீன்",
            imageName: "WallpaperStarOfBethlehem"
        ),
        Wallpaper(
            id: "wallpaper_rainbow_promise",
            name: "Rainbow Promise",
            tamilName: "வானவில் வாக்குறுதி",
            subtitle: "God's promise over the valley",
            tamilSubtitle: "பள்ளத்தாக்கின் மேல் இறைவனின் வாக்குறுதி",
            imageName: "WallpaperRainbowPromise"
        ),
        Wallpaper(
            id: "wallpaper_easter_dawn",
            name: "Easter Dawn",
            tamilName: "உயிர்த்தெழுதலின் விடியல்",
            subtitle: "The tomb at sunrise",
            tamilSubtitle: "விடியலில் வெற்றுக் கல்லறை",
            imageName: "WallpaperEasterDawn"
        ),
        Wallpaper(
            id: "wallpaper_crystal_paradise",
            name: "Crystal Paradise",
            tamilName: "படிகப் பரதீசு",
            subtitle: "Clear waters of paradise",
            tamilSubtitle: "பரதீசின் தெளிவான நீர்",
            imageName: "WallpaperCrystalParadise"
        ),
        Wallpaper(
            id: "wallpaper_pentecost_light",
            name: "Pentecost Light",
            tamilName: "பெந்தெகொஸ்தே ஒளி",
            subtitle: "Spirit descending in glory",
            tamilSubtitle: "மகிமையில் இறங்கும் தூய ஆவி",
            imageName: "WallpaperPentecostLight"
        ),
        Wallpaper(
            id: "wallpaper_sacred_heart_cathedral",
            name: "Sacred Heart Cathedral",
            tamilName: "திருஇருதயத் திருக்கோயில்",
            subtitle: "Sacred sanctuary of grace",
            tamilSubtitle: "அருளின் புனிதக் கோயில்",
            imageName: "WallpaperSacredHeartCathedral"
        ),
        Wallpaper(
            id: "wallpaper_crown_of_glory",
            name: "Crown of Glory",
            tamilName: "மகிமையின் கிரீடம்",
            subtitle: "Royal throne of heaven",
            tamilSubtitle: "விண்ணரசின் அரியணை",
            imageName: "WallpaperCrownOfGlory"
        ),
        Wallpaper(
            id: "wallpaper_nativity_star",
            name: "Nativity Star",
            tamilName: "பிறப்பின் விண்மீன்",
            subtitle: "The star that led the wise",
            tamilSubtitle: "ஞானிகளை வழிநடத்திய விண்மீன்",
            imageName: "WallpaperNativityStar"
        ),
        Wallpaper(
            id: "wallpaper_rose_of_sharon",
            name: "Rose of Sharon",
            tamilName: "சாரோனின் ரோஜா",
            subtitle: "Beauty of the Beloved",
            tamilSubtitle: "அன்பருடைய அழகு",
            imageName: "WallpaperRoseOfSharon"
        ),
        Wallpaper(
            id: "wallpaper_garden_of_grace",
            name: "Garden of Grace",
            tamilName: "அருளின் தோட்டம்",
            subtitle: "Living waters flow here",
            tamilSubtitle: "உயிர்தரும் நீர் பாயும் இடம்",
            imageName: "WallpaperGardenOfGrace"
        ),
        Wallpaper(
            id: "wallpaper_little_blessings",
            name: "Little Blessings",
            tamilName: "சிறு ஆசிகள்",
            subtitle: "Joy carried on heart balloons",
            tamilSubtitle: "இதயத்தில் பறக்கும் மகிழ்ச்சி",
            imageName: "WallpaperLittleBlessings"
        ),
        Wallpaper(
            id: "wallpaper_lavender_grace",
            name: "Lavender Grace",
            tamilName: "இளம் ஊதா அருள்",
            subtitle: "Gentle paths of grace",
            tamilSubtitle: "அருளின் மெல்லிய பாதை",
            imageName: "WallpaperLavenderGrace"
        ),
        Wallpaper(
            id: "wallpaper_friends_on_high",
            name: "Friends on High",
            tamilName: "விண்ணக நண்பர்கள்",
            subtitle: "Rising together in joy",
            tamilSubtitle: "மகிழ்ச்சியில் ஒன்றாய் உயர்வோம்",
            imageName: "WallpaperFriendsOnHigh"
        ),
        Wallpaper(
            id: "wallpaper_smiling_skies",
            name: "Smiling Skies",
            tamilName: "புன்னகைக்கும் வானம்",
            subtitle: "A world full of joy",
            tamilSubtitle: "மகிழ்ச்சி நிறைந்த உலகம்",
            imageName: "WallpaperSmilingSkies"
        ),
        Wallpaper(
            id: "wallpaper_stacked_joy",
            name: "Stacked Joy",
            tamilName: "அடுக்கிய மகிழ்ச்சி",
            subtitle: "Blessings piled high",
            tamilSubtitle: "அடுக்கடுக்கான ஆசிகள்",
            imageName: "WallpaperStackedJoy"
        )
    ]

    init() {
        self.selectedWallpaperId = UserDefaults.standard.string(forKey: storageKey) ?? "wallpaper_gates_of_heaven"
    }
}
