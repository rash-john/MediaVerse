package com.johny.mediaverse.utils

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


/**
 * Converts a millisecond timestamp (Long) into a locale-sensitive, long-format date string.
 *
 * It uses the modern java. Time API (JSR 310) which is safe, thread-safe, and locale-aware
 * (available via API desugaring on all Android versions).
 *
 * @return The formatted date string (e.g., "May 12, 2018") or null if an error occurs.
 */
fun Long.toDateString(): String? {
    return try {
        val instant = Instant.ofEpochMilli(this)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.getDefault())
        zonedDateTime.format(formatter)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

enum class DateFormat(val format: String) {
    YYYY("yyyy"),
    MMM_DD_YYYY_STRING_HH_MM_A("MMM dd, yyyy 'at' hh.mm a"),
    YYYY_MM_DD("yyyy-MM-dd")
}

/*
*Pattern explanation:
// MMM   = Short Month (May)
// dd    = Day (12)
// yyyy  = Year (2018)
// 'at'  = Literal text " at "
// hh    = Hour in 12-hour format (11)
// .     = The dot separator you requested
// mm    = Minute (00)
// a     = AM/PM marker
* @return format of time mils to May 12, 2018, at 11.00 AM
* */
fun Long.toDateFormatString(dateFormat: DateFormat): String {
    return try {
        val instant = Instant.ofEpochMilli(this)
        val zoneDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern(dateFormat.format, Locale.US)
        zoneDateTime.format(formatter)
    } catch (_: Exception) {
        ""
    }
}

fun String.toFormattedDate(dateFormat: DateFormat): String {
    return try {
        val parsedDate = LocalDate.parse(this)
        val formatter = DateTimeFormatter.ofPattern(dateFormat.format, Locale.getDefault())
        parsedDate.format(formatter)
    } catch (ex: Exception) {
        ex.printStackTrace()
        ""
    }
}

fun Int.toSecondToMinute(): String {
    val minutes = this / 60
    return String.format(Locale.getDefault(), "%02d", minutes)
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = remember {
        listOf(
            Color.LightGray.copy(alpha = 0.9f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.9f),
        )
    }

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "ShimmerAnimation"
    )

    // Read the animated value inside drawBehind so each frame only invalidates
    // the draw phase instead of recomposing every shimmering item.
    drawBehind {
        val translate = translateAnim.value
        drawRect(
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(x = translate - 200, y = translate),
                end = Offset(x = translate + 200, y = translate + 500)
            )
        )
    }
}

fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

fun String.parseHtml(
    linkColor: Color = Color.Blue,
    onLinkClick: ((String) -> Unit)? = null
): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val text = spanned.toString()

    return buildAnnotatedString {
        append(text)

        val spans = spanned.getSpans(0, spanned.length, Any::class.java)

        spans.forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )

                        Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            start,
                            end
                        )

                        Typeface.BOLD_ITALIC -> addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            ), start, end
                        )
                    }
                }

                is UnderlineSpan -> {
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                }

                is ForegroundColorSpan -> {
                    addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
                }

                is URLSpan -> {
                    val link = LinkAnnotation.Url(
                        url = span.url,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        ),
                        linkInteractionListener = { _ ->
                            if (onLinkClick != null) {
                                onLinkClick(span.url)
                            }
                        }
                    )
                    addLink(link, start, end)
                }
            }
        }
    }
}


fun Context.checkInternet(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = manager.activeNetwork ?: return false
    val activeNetwork = manager.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun Context.openConnectivitySettings() {
    try {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        this.startActivity(intent)
    } catch (_: Exception) {
    }
}

fun String.toMediaType(): MediaTypeEnum {
    return when {
        this.equals("movie", ignoreCase = true) -> {
            MediaTypeEnum.MOVIE
        }

        this.equals("tv", ignoreCase = true) -> {
            MediaTypeEnum.TV
        }

        this.equals("person", ignoreCase = true) -> {
            MediaTypeEnum.PERSON
        }

        else -> {
            MediaTypeEnum.NONE
        }
    }
}