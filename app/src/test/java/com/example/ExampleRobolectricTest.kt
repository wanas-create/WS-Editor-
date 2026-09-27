package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.WsEditorDatabase
import com.example.data.WsEditorRepository
import com.example.ui.screens.ProPlanId
import com.example.ui.screens.ProPricingResolver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name and verify custom font asset import`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WS-Editor", appName)

        val db = WsEditorDatabase.getInstance(context)
        val repo = WsEditorRepository(context, db.projectDao(), db.customFontDao())
        val importResult = repo.importSampleFontFromAssets(
            "CyberDisplay-Bold.ttf",
            "CyberDisplay Bold"
        )
        assertTrue("Expected custom font import to succeed", importResult.isSuccess)
        val importedFont = importResult.getOrThrow()
        assertEquals("CyberDisplay Bold", importedFont.displayName)
        assertTrue(importedFont.fileSizeKb > 0)
    }

    @Test
    fun `verify ProPricingResolver international USD and Pakistan local pricing`() {
        val usPlans = ProPricingResolver.getPlansForCountry("US")
        val usMonthly = usPlans.first { it.id == ProPlanId.MONTHLY }
        val usYearly = usPlans.first { it.id == ProPlanId.YEARLY }
        val usLifetime = usPlans.first { it.id == ProPlanId.LIFETIME }

        assertEquals("$1.99", usMonthly.primaryPriceText)
        assertEquals("$9.99", usYearly.primaryPriceText)
        assertEquals("BEST VALUE", usYearly.badgeText)
        assertEquals("$19.99", usLifetime.primaryPriceText)

        val pkPlans = ProPricingResolver.getPlansForCountry("PK")
        val pkMonthly = pkPlans.first { it.id == ProPlanId.MONTHLY }
        val pkYearly = pkPlans.first { it.id == ProPlanId.YEARLY }
        val pkLifetime = pkPlans.first { it.id == ProPlanId.LIFETIME }

        assertEquals("Rs 100", pkMonthly.primaryPriceText)
        assertEquals("Rs 1100", pkYearly.primaryPriceText)
        assertEquals("BEST VALUE", pkYearly.badgeText)
        assertEquals("$19.99", pkLifetime.primaryPriceText)
    }
}
