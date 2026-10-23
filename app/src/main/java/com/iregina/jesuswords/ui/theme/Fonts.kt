package com.iregina.jesuswords.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppTheme

/** Closest match to iOS "New York" (Font.Design.serif). Latin subset, embedded. */
val NotoSerif = FontFamily(
    Font(R.font.noto_serif_regular, FontWeight.Normal),
    Font(R.font.noto_serif_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.noto_serif_medium, FontWeight.Medium),
    Font(R.font.noto_serif_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.noto_serif_semibold, FontWeight.SemiBold),
    Font(R.font.noto_serif_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.noto_serif_bold, FontWeight.Bold),
    Font(R.font.noto_serif_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

/** Stand-in for iOS SF Rounded (Font.Design.rounded). */
val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

/** Tamil text. Has no italic cut, so italics are synthesised. */
val NotoSerifTamil = FontFamily(
    Font(R.font.noto_serif_tamil_regular, FontWeight.Normal),
    Font(R.font.noto_serif_tamil_medium, FontWeight.Medium),
    Font(R.font.noto_serif_tamil_semibold, FontWeight.SemiBold),
    Font(R.font.noto_serif_tamil_bold, FontWeight.Bold),
)

/** Equivalent of `.font(.system(..., design: theme.fontDesign))` for the current language. */
fun themeFontFamily(theme: AppTheme?, language: AppLanguage): FontFamily = when {
    language == AppLanguage.TAMIL -> NotoSerifTamil
    theme?.isRounded == true -> Nunito
    else -> NotoSerif
}

/** Equivalent of `.font(.system(..., design: .serif))` regardless of theme. */
fun serifFontFamily(language: AppLanguage): FontFamily =
    if (language == AppLanguage.TAMIL) NotoSerifTamil else NotoSerif
