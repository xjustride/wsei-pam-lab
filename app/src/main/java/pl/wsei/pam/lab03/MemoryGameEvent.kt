// File: app/src/main/java/pl/wsei/pam/lab03/MemoryGameEvent.kt
package pl.wsei.pam.lab03

data class MemoryGameEvent(
    val tiles: List<Tile>,
    val state: GameStates
)