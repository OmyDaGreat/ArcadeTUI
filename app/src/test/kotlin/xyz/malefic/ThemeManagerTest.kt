package xyz.malefic

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertContains
import xyz.malefic.arcade.theme.ThemeManager

class ThemeManagerTest {
    
    @Test fun canInitializeThemeManager() {
        val themeManager = ThemeManager()
        assertNotNull(themeManager, "Theme manager should initialize")
        assertNotNull(themeManager.currentTheme, "Current theme should be set")
    }
    
    @Test fun hasDefaultThemes() {
        val themeManager = ThemeManager()
        val themes = themeManager.getDefaultThemes()
        
        assertTrue(themes.isNotEmpty(), "Should have default themes")
        assertEquals(5, themes.size, "Should have 5 default themes")
        
        val themeNames = themes.map { it.name }
        assertContains(themeNames, "Classic", "Should contain Classic theme")
        assertContains(themeNames, "Neon", "Should contain Neon theme")
        assertContains(themeNames, "Minimal", "Should contain Minimal theme")
        assertContains(themeNames, "Dark", "Should contain Dark theme")
        assertContains(themeNames, "Retro", "Should contain Retro theme")
    }
    
    @Test fun canSwitchThemes() {
        val themeManager = ThemeManager()
        val themes = themeManager.getAllThemes()
        val originalTheme = themeManager.currentTheme
        
        // Switch to a different theme
        val newTheme = themes.first { it.name != originalTheme.name }
        themeManager.setCurrentTheme(newTheme)
        
        assertEquals(newTheme.name, themeManager.currentTheme.name, "Theme should be switched")
        
        // Switch back
        themeManager.setCurrentTheme(originalTheme)
        assertEquals(originalTheme.name, themeManager.currentTheme.name, "Theme should be restored")
    }
    
    @Test fun allThemesHaveRequiredProperties() {
        val themeManager = ThemeManager()
        val themes = themeManager.getAllThemes()
        
        themes.forEach { theme ->
            assertNotNull(theme.name, "Theme should have a name")
            assertNotNull(theme.description, "Theme should have a description")
            assertNotNull(theme.colors, "Theme should have colors")
            assertNotNull(theme.colors.primary, "Theme should have primary color")
            assertNotNull(theme.colors.secondary, "Theme should have secondary color")
            assertNotNull(theme.colors.accent, "Theme should have accent color")
        }
    }
    
    @Test fun canConvertToLanternaColors() {
        val themeManager = ThemeManager()
        val theme = themeManager.currentTheme
        val lanternaColors = theme.toLanternaColors()
        
        assertNotNull(lanternaColors, "Should convert to Lanterna colors")
        assertNotNull(lanternaColors.primary, "Should have primary Lanterna color")
        assertNotNull(lanternaColors.secondary, "Should have secondary Lanterna color")
        assertNotNull(lanternaColors.accent, "Should have accent Lanterna color")
    }
}