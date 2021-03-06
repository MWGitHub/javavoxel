# javavoxel
3D game with voxel terrain and scripting support.
Uses Java as the main language for programming the game, jMonkeyEngine for rendering, gradle for building, and groovy for scripting.

## Features
* Use groovy to interact with the scripting API to handle what happens when an event in the game occurs.
* Editor for editing maps, creating and removing regions, and placing objects.
* High performance multithreaded rendering allows for the creation of large maps without loading from the disk after the initial map load. Larger maps will require more memory.
* Tutorial level included which has examples of script usage such as spawning enemies, handling damage, victory conditions, and more.
