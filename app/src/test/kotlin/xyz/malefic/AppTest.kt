/*
 * ArcadeTUI Test Suite
 */
package xyz.malefic

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import xyz.malefic.arcade.ArcadeSystem
import xyz.malefic.games.GalagaCartridge
import xyz.malefic.games.TetrisCartridge

class ArcadeTuiTest {
    
    @Test fun canInitializeArcadeSystem() {
        val arcade = ArcadeSystem()
        assertNotNull(arcade, "ArcadeSystem should initialize successfully")
        arcade.cleanup()
    }
    
    @Test fun canLoadGameCartridges() = runBlocking {
        val arcade = ArcadeSystem()
        
        // Add game cartridges
        arcade.addCartridge(TetrisCartridge())
        arcade.addCartridge(GalagaCartridge())
        
        // Verify cartridges are loaded
        assertTrue(arcade.getCartridges().isNotEmpty(), "Cartridges should be loaded")
        assertEquals(2, arcade.getCartridges().size, "Should have 2 cartridges")
        
        arcade.cleanup()
    }
    
    @Test fun arcadeSystemHasThemeManager() {
        val arcade = ArcadeSystem()
        val themeManager = arcade.getThemeManager()
        
        assertNotNull(themeManager, "Theme manager should be available")
        assertNotNull(themeManager.currentTheme, "Current theme should be set")
        
        arcade.cleanup()
    }
}
