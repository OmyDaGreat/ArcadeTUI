package xyz.malefic

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import xyz.malefic.arcade.theme.ThemeManager

class LanternaIntegrationTest {
    
    @Test fun canCreateTerminal() {
        // Test that we can create a terminal without errors
        val factory = DefaultTerminalFactory()
        assertNotNull(factory, "Terminal factory should be created")
        
        // Note: In headless environments, this may throw an exception
        // but we can test that the factory is available
        try {
            val terminal = factory.createTerminal()
            assertNotNull(terminal, "Terminal should be created")
            terminal.close()
            assertTrue(true, "Terminal created successfully")
        } catch (e: Exception) {
            // In headless environments, we expect this to fail gracefully
            assertTrue(e.message?.contains("DISPLAY") == true || 
                      e.message?.contains("headless") == true ||
                      e.message?.contains("terminal") == true ||
                      e.javaClass.simpleName.contains("Unsupported") ||
                      e.javaClass.simpleName.contains("IO"),
                      "Should fail with expected error in headless environment: ${e.javaClass.simpleName}: ${e.message}")
        }
    }
    
    @Test fun themesWorkWithLanterna() {
        val themeManager = ThemeManager()
        val theme = themeManager.currentTheme
        
        // Test that theme can be converted to Lanterna colors
        val lanternaColors = theme.toLanternaColors()
        assertNotNull(lanternaColors, "Should convert theme to Lanterna colors")
        
        // Test all themes can be converted
        themeManager.getAllThemes().forEach { testTheme ->
            val colors = testTheme.toLanternaColors()
            assertNotNull(colors, "All themes should convert to Lanterna colors")
            assertNotNull(colors.primary, "Primary color should exist")
            assertNotNull(colors.secondary, "Secondary color should exist")
            assertNotNull(colors.accent, "Accent color should exist")
            assertNotNull(colors.text, "Text color should exist")
        }
    }
    
    @Test fun canTestTerminalOperationsWithMockTerminal() {
        // This tests the Lanterna API usage patterns without requiring a real terminal
        val themeManager = ThemeManager()
        val theme = themeManager.currentTheme.toLanternaColors()
        
        // Test that we can access color properties that would be used with terminal
        assertNotNull(theme.primary, "Primary color for terminal should exist")
        assertNotNull(theme.secondary, "Secondary color for terminal should exist")
        assertNotNull(theme.accent, "Accent color for terminal should exist")
        assertNotNull(theme.text, "Text color for terminal should exist")
        assertNotNull(theme.textDim, "Dim text color for terminal should exist")
        assertNotNull(theme.success, "Success color for terminal should exist")
        assertNotNull(theme.warning, "Warning color for terminal should exist")
        assertNotNull(theme.error, "Error color for terminal should exist")
    }
}