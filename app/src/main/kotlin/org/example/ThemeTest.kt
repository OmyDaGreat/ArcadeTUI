package org.example

import org.example.arcade.theme.ThemeManager
import kotlinx.coroutines.runBlocking

/**
 * Simple test for theme management functionality
 */
fun main(args: Array<String>) {
    println("🧪 ArcadeTUI Theme Manager Test")
    println("===============================")
    
    val themeManager = ThemeManager()
    
    // Test 1: List all default themes
    println("\n📋 Default themes:")
    themeManager.getDefaultThemes().forEach { theme ->
        println("- ${theme.name}: ${theme.description}")
        println("  Primary: ${theme.colors.primary}, Secondary: ${theme.colors.secondary}")
    }
    
    // Test 2: Theme switching
    println("\n🔄 Testing theme switching:")
    val themes = themeManager.getAllThemes()
    themes.forEach { theme ->
        themeManager.setCurrentTheme(theme)
        println("Switched to: ${themeManager.currentTheme.name}")
    }
    
    // Test 3: Custom theme persistence
    println("\n💾 Testing custom theme storage:")
    val customThemes = themeManager.getCustomThemes()
    println("Found ${customThemes.size} custom themes")
    
    // Test 4: Theme URL downloading (mock test)
    if (args.isNotEmpty() && args[0] == "--test-url") {
        println("\n🌐 Testing URL download (this would normally download from a URL):")
        println("URL download functionality is implemented but requires actual HTTP requests")
        println("In a real scenario, you would call:")
        println("themeManager.addThemeFromUrl(\"https://example.com/theme.yaml\")")
    }
    
    println("\n✅ Theme system tests completed!")
    println("Current active theme: ${themeManager.currentTheme.name}")
}