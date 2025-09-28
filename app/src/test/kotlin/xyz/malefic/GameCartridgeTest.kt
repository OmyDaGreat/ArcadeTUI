package xyz.malefic

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import xyz.malefic.games.GalagaCartridge
import xyz.malefic.games.TetrisCartridge

class GameCartridgeTest {
    
    @Test fun tetrisCartridgePropertiesAreValid() {
        val tetris = TetrisCartridge()
        
        assertEquals("TETRIS", tetris.name)
        assertEquals("Classic falling blocks puzzle", tetris.description)
        assertNotNull(tetris.version, "Version should not be null")
        assertTrue(tetris.version.isNotEmpty(), "Version should not be empty")
    }
    
    @Test fun galagaCartridgePropertiesAreValid() {
        val galaga = GalagaCartridge()
        
        assertEquals("GALAGA", galaga.name)
        assertEquals("Classic space shooter", galaga.description)
        assertNotNull(galaga.version, "Version should not be null")
        assertTrue(galaga.version.isNotEmpty(), "Version should not be empty")
    }
    
    @Test fun cartridgesImplementRequiredInterface() {
        val tetris = TetrisCartridge()
        val galaga = GalagaCartridge()
        
        // Test that cartridges implement the interface properly
        assertTrue(tetris is xyz.malefic.arcade.GameCartridge, "Tetris should implement GameCartridge")
        assertTrue(galaga is xyz.malefic.arcade.GameCartridge, "Galaga should implement GameCartridge")
    }
    
    @Test fun cartridgeNamesAreUnique() {
        val tetris = TetrisCartridge()
        val galaga = GalagaCartridge()
        
        assertTrue(tetris.name != galaga.name, "Cartridge names should be unique")
    }
}