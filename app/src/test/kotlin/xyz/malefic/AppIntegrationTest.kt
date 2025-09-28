package xyz.malefic

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import xyz.malefic.arcade.ArcadeSystem
import xyz.malefic.games.GalagaCartridge
import xyz.malefic.games.TetrisCartridge

class AppIntegrationTest {
    
    @Test fun canStartArcadeSystem() = runBlocking {
        val arcade = ArcadeSystem()
        
        try {
            // Load game cartridges like the main app does
            arcade.addCartridge(TetrisCartridge())
            arcade.addCartridge(GalagaCartridge())
            
            // Verify system is properly initialized
            assertTrue(arcade.getCartridges().isNotEmpty(), "Games should be loaded")
            assertNotNull(arcade.getThemeManager(), "Theme manager should be available")
            assertNotNull(arcade.getScoreManager(), "Score manager should be available")
            
            // Verify theme system is working
            val currentTheme = arcade.getThemeManager().currentTheme
            assertNotNull(currentTheme, "Current theme should be set")
            assertNotNull(currentTheme.toLanternaColors(), "Theme should convert to Lanterna colors")
            
            assertTrue(true, "Arcade system initialized successfully")
        } finally {
            arcade.cleanup()
        }
    }
    
    @Test fun themeSwitchingWorksInIntegratedSystem() = runBlocking {
        val arcade = ArcadeSystem()
        
        try {
            val themeManager = arcade.getThemeManager()
            val originalTheme = themeManager.currentTheme
            val allThemes = themeManager.getAllThemes()
            
            // Test theme switching in the integrated system
            if (allThemes.size > 1) {
                val newTheme = allThemes.first { it.name != originalTheme.name }
                themeManager.setCurrentTheme(newTheme)
                
                assertTrue(themeManager.currentTheme.name == newTheme.name, "Theme should be switched")
                
                // Switch back
                themeManager.setCurrentTheme(originalTheme)
                assertTrue(themeManager.currentTheme.name == originalTheme.name, "Theme should be restored")
            }
        } finally {
            arcade.cleanup()
        }
    }
}