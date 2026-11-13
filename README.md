# Skyblocker Shortcut Case Fix

A small Fabric client addon for [Skyblocker](https://github.com/SkyblockerMod/Skyblocker) that makes command shortcuts case-insensitive.

## Features

- Shortcuts are registered in lowercase: a shortcut saved as `/M7` is registered as `/m7`
- Typing `/M7`, `/m7` or any other casing runs the shortcut
- Tab completion matches regardless of case: `/M7` completes to `/m7`
- Commands typed in a different case are no longer highlighted red in the chat box
- Case-duplicate shortcuts (e.g. `/M7` and `/m7`) are removed from `shortcuts.json` on startup, keeping the lowercase one
- The shortcuts config screen won't save a new shortcut that only differs by case from an existing one

Only the command name is lowercased; arguments keep their original case, so `/msg SomePlayer` still works.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3+
- Fabric API
- Skyblocker
- Java 25