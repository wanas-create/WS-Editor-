package com.example.ui.screens

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFamily
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCardGradient
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import java.util.Locale
import java.util.TimeZone

enum class ProPlanId {
    MONTHLY,
    YEARLY,
    LIFETIME
}

data class ProPlanOption(
    val id: ProPlanId,
    val title: String,
    val primaryPriceText: String,
    val periodText: String,
    val secondarySubtext: String,
    val usdReferencePrice: String,
    val badgeText: String? = null,
    val isBestValue: Boolean = false
)

object ProPricingResolver {
    /**
     * Detects the user's country code (e.g. "PK" for Pakistan, "US" for United States)
     * using TelephonyManager (SIM/network ISO), system Locale, and TimeZone fallback.
     */
    fun detectUserCountryCode(context: Context?): String {
        runCatching {
            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val simIso = tm?.simCountryIso?.uppercase(Locale.ROOT)?.trim()
            if (!simIso.isNullOrBlank() && simIso.length == 2) {
                return simIso
            }
            val netIso = tm?.networkCountryIso?.uppercase(Locale.ROOT)?.trim()
            if (!netIso.isNullOrBlank() && netIso.length == 2) {
                return netIso
            }
        }

        val localeCountry = Locale.getDefault().country.uppercase(Locale.ROOT).trim()
        if (localeCountry == "PK") {
            return "PK"
        }

        val tzId = TimeZone.getDefault().id
        if (tzId.equals("Asia/Karachi", ignoreCase = true)) {
            return "PK"
        }

        return localeCountry.ifEmpty { "US" }
    }

    fun isPakistan(countryCode: String): Boolean =
        countryCode.equals("PK", ignoreCase = true) ||
            countryCode.equals("PAK", ignoreCase = true)

    /**
     * Returns the 3 Pro subscription plans using USD as the primary currency:
     * - If country is Pakistan ("PK"): shows Rs 100 (Monthly) / Rs 1100 (Yearly) + USD reference,
     *   and $19.99 one time for Lifetime.
     * - If any other country: shows $1.99 / month (with Pakistan ~ Rs 100 local note),
     *   $9.99 / year (BEST VALUE), and $19.99 one time.
     */
    fun getPlansForCountry(countryCode: String): List<ProPlanOption> {
        val isPk = isPakistan(countryCode)
        return if (isPk) {
            listOf(
                ProPlanOption(
                    id = ProPlanId.MONTHLY,
                    title = "Monthly Pro",
                    primaryPriceText = "Rs 100",
                    periodText = "/ month",
                    secondarySubtext = "International USD: $1.99 / month (~ Rs 100 in local)",
                    usdReferencePrice = "$1.99 / month",
                    badgeText = "PAKISTAN LOCAL"
                ),
                ProPlanOption(
                    id = ProPlanId.YEARLY,
                    title = "Yearly Pro",
                    primaryPriceText = "Rs 1100",
                    periodText = "/ year",
                    secondarySubtext = "International USD: $9.99 / year • Save 58%",
                    usdReferencePrice = "$9.99 / year",
                    badgeText = "BEST VALUE",
                    isBestValue = true
                ),
                ProPlanOption(
                    id = ProPlanId.LIFETIME,
                    title = "Lifetime Master",
                    primaryPriceText = "$19.99",
                    periodText = "one time",
                    secondarySubtext = "One-time payment • Own WS-Editor Pro forever (USD Primary)",
                    usdReferencePrice = "$19.99 one time",
                    badgeText = "ONE TIME"
                )
            )
        } else {
            listOf(
                ProPlanOption(
                    id = ProPlanId.MONTHLY,
                    title = "Monthly Pro",
                    primaryPriceText = "$1.99",
                    periodText = "/ month",
                    secondarySubtext = "For Pakistan: ~ Rs 100 in local • Billed monthly in USD",
                    usdReferencePrice = "$1.99 / month",
                    badgeText = "FLEXIBLE"
                ),
                ProPlanOption(
                    id = ProPlanId.YEARLY,
                    title = "Yearly Pro",
                    primaryPriceText = "$9.99",
                    periodText = "/ year",
                    secondarySubtext = "Save 58% vs Monthly • Full 4K 60FPS & Wanas AI access",
                    usdReferencePrice = "$9.99 / year",
                    badgeText = "BEST VALUE",
                    isBestValue = true
                ),
                ProPlanOption(
                    id = ProPlanId.LIFETIME,
                    title = "Lifetime Master",
                    primaryPriceText = "$19.99",
                    periodText = "one time",
                    secondarySubtext = "Pay once, unlock all WS-Editor Pro features forever",
                    usdReferencePrice = "$19.99 one time",
                    badgeText = "LIFETIME"
                )
            )
        }
    }
}

@Composable
fun ProPaywallScreen(
    isProUnlocked: Boolean,
    activePlanSummary: String?,
    onUnlockPlan: (ProPlanOption, String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val detectedCountry = remember { ProPricingResolver.detectUserCountryCode(context) }
    var activeCountryCode by remember { mutableStateOf(detectedCountry) }

    val plans = remember(activeCountryCode) {
        ProPricingResolver.getPlansForCountry(activeCountryCode)
    }

    var selectedPlanId by remember { mutableStateOf(ProPlanId.YEARLY) }
    val selectedPlan = plans.firstOrNull { it.id == selectedPlanId } ?: plans[1]
    val isPakistanView = ProPricingResolver.isPakistan(activeCountryCode)

    val scrollState = rememberScrollState()

    val proPerks: List<Pair<ImageVector, String>> = listOf(
        Icons.Default.HighQuality to "Unlimited 4K 60FPS UHD Exports without Watermark",
        Icons.Default.FontDownload to "Unlimited Custom Font Uploads (.TTF & .OTF) on Video",
        Icons.Default.AutoAwesome to "Early Access to Wanas AI (Text to Video, Auto Captions & Voice)",
        Icons.Default.Star to "All Cinema LUT Filters, Speed Curves (0.2x–8x) & 4-Track Pro Timeline"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("pro_paywall_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar with Region Auto-Detection Indicator & Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WsPrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "WS-Editor Pro",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "WS-EDITOR PRO",
                        fontFamily = BebasNeueFamily,
                        fontSize = 24.sp,
                        letterSpacing = 1.5.sp,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "Primary Currency: USD ($) • Region: $activeCountryCode",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsCyan
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Interactive Region Preview Toggle so user can test both International USD and Pakistan Rs 100 / Rs 1100
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsCyan.copy(alpha = 0.6f), RoundedCornerShape(50))
                        .clickable {
                            activeCountryCode = if (isPakistanView) "US" else "PK"
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_toggle_pricing_region")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Pricing Region",
                            tint = WsCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPakistanView) "PK (Rs / USD)" else "Global (USD $)",
                            fontFamily = RobotoMonoFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsCyan
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WsSurfaceElevated)
                        .testTag("btn_close_paywall")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Paywall",
                        tint = WsTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Hero Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(WsCardGradient)
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "VN PRO STUDIO UNLOCK",
                            fontFamily = RobotoMonoFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsCyan
                        )
                    }
                    if (isProUnlocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrackGreen)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ACTIVE: ${activePlanSummary ?: "PRO"}",
                                fontFamily = RobotoMonoFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Text(
                    text = "Unleash 4K 60FPS & Unlimited Custom Fonts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    proPerks.forEach { (icon, text) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = WsCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Region Pricing Notice Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(WsSurface)
                .border(1.dp, WsBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPakistanView) {
                    "🇵🇰 Pakistan Local Pricing Active (Rs 100 / Rs 1100)"
                } else {
                    "🌐 International USD Pricing ($1.99 / $9.99 / $19.99)"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = WsTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (isPakistanView) "Switch to USD" else "Pakistan? Tap PK",
                fontFamily = RobotoMonoFamily,
                fontSize = 10.sp,
                color = WsCyan,
                modifier = Modifier.clickable {
                    activeCountryCode = if (isPakistanView) "US" else "PK"
                }
            )
        }

        // 3 Pricing Cards: Monthly, Yearly (BEST VALUE), Lifetime
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            plans.forEach { plan ->
                val isSelected = plan.id == selectedPlan.id
                ProPlanCard(
                    plan = plan,
                    isSelected = isSelected,
                    onClick = { selectedPlanId = plan.id }
                )
            }
        }

        // Primary CTA Button
        Button(
            onClick = { onUnlockPlan(selectedPlan, activeCountryCode) },
            colors = ButtonDefaults.buttonColors(
                containerColor = WsCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = WsCyan)
                .testTag("btn_subscribe_pro_plan")
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CONTINUE WITH ${selectedPlan.title.uppercase()} • ${selectedPlan.primaryPriceText} ${selectedPlan.periodText}",
                fontFamily = RobotoMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        // Transparent Pricing Breakdown Footer
        Text(
            text = "Primary billing currency: USD ($1.99/mo • $9.99/yr • $19.99 lifetime). " +
                "For Pakistan users, local pricing (~ Rs 100/mo • Rs 1100/yr) is automatically displayed. Cancel anytime.",
            style = MaterialTheme.typography.labelSmall,
            color = WsTextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun ProPlanCard(
    plan: ProPlanOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> WsCyan
        plan.isBestValue -> WsPurple
        else -> WsBorder
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected) WsCyan.copy(alpha = 0.14f)
                else WsSurface
            )
            .border(
                width = if (isSelected || plan.isBestValue) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("pro_plan_card_${plan.id.name.lowercase()}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = WsTextPrimary
                    )

                    plan.badgeText?.let { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (plan.isBestValue) WsPrimaryGradient
                                    else androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(WsSurfaceElevated, WsSurfaceElevated)
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (plan.isBestValue) WsCyan else TrackOrange,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = badge,
                                fontFamily = RobotoMonoFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (plan.isBestValue) Color.White else TrackOrange
                            )
                        }
                    }
                }

                // Price Display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = plan.primaryPriceText,
                        fontFamily = BebasNeueFamily,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) WsCyan else WsTextPrimary
                    )
                    Text(
                        text = plan.periodText,
                        fontFamily = RobotoMonoFamily,
                        fontSize = 11.sp,
                        color = WsTextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Text(
                text = plan.secondarySubtext,
                style = MaterialTheme.typography.bodyMedium,
                color = WsTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
