package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDesktopLauncherTest {
    @Test
    void randomModeDoesNotDependOnALevelFileBeingPresent() {
        assertNull(GameDesktopLauncher.chooseLevelDescriptionFile(new String[0]));
        assertNull(GameDesktopLauncher.chooseLevelDescriptionFile(new String[]{"--random"}));
    }

    @Test
    void acceptsExplicitLevelFiles() {
        assertEquals("level.dat", GameDesktopLauncher.chooseLevelDescriptionFile(new String[]{"--level", "level.dat"}));
        assertEquals("custom.dat", GameDesktopLauncher.chooseLevelDescriptionFile(new String[]{"custom.dat"}));
    }

    @Test
    void rejectsInvalidArguments() {
        for (String[] args : new String[][]{{"--level"}, {"--unknown"}, {"--random", "level.dat"}, {"a", "b"}}) {
            assertThrows(IllegalArgumentException.class, () -> GameDesktopLauncher.chooseLevelDescriptionFile(args));
        }
    }
}
