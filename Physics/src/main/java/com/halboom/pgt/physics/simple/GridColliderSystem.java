package com.halboom.pgt.physics.simple;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.PhysicsUnits;
import com.halboom.pgt.physics.components.PhysicsStateComponent;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.GridColliderComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/31/13
 * Time: 2:30 PM
 * Checks collisions with the map grid.
 */
public class GridColliderSystem implements Subsystem {
    /**
     * Default stepping size for ray collisions.
     */
    private static final float DEFAULT_LINE_STEP = 0.05f;

    /**
     * Default friction of a tile.
     */
    private static final float DEFAULT_FRICTION = 1.0f;

    /**
     * Default collision group of a tile.
     */
    private static final long DEFAULT_COLLISION_GROUP = 1;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Collision resolver to use.
     */
    private CollisionResolver collisionResolver;

    /**
     * Tiles reference of the map.
     */
    private byte[][][] tiles;

    /**
     * Friction for the tile indices.
     */
    private float[] tileFriction = new float[]{0, DEFAULT_FRICTION};

    /**
     * Collision group for the tiles.
     */
    private long[] collisionGroups = new long[]{0, DEFAULT_COLLISION_GROUP};

    /**
     * Scale of a block.
     */
    private float scale = 1f;

    // Geometries that are frequently used in operations.
    /**
     * Used for operations that return a location and shared among operations.
     */
    private final Vector3f vector3 = new Vector3f(0, 0, 0);
    /**
     * Used for operations that return grid location.
     */
    private final Vector3Int vector3Int = new Vector3Int(0, 0, 0);

    /**
     * Step size to use when stepping through line points; the smaller the step the more
     * accurate at a trade off of performance.
     */
    private float lineStep = DEFAULT_LINE_STEP;

    /**
     * Callbacks for tile collisions.
     */
    private List<CollisionCallbacks> collisionCallbacks = new LinkedList<CollisionCallbacks>();

    /**
     * Initializes the grid and sets the resources to use.
     * @param entitySystem the entity system to use.
     * @param collisionResolver the collision resolver to use.
     */
    public GridColliderSystem(EntitySystem entitySystem, CollisionResolver collisionResolver) {
        this.entitySystem = entitySystem;
        this.collisionResolver = collisionResolver;
    }

    /**
     * Retrieves a position given an index in the tile grid.
     * @param x the X index from left to right.
     * @param y the Y index from bottom to top.
     * @param z the Z index from back to the camera.
     * @return a position for the center of a grid point; vector is reused, copy if needed.
     */
    public final Vector3f getPositionFromGrid(int x, int y, int z) {
        vector3.x = x * scale + scale / 2;
        vector3.y = y * scale + scale / 2;
        vector3.z = z * scale + scale / 2;

        return vector3;
    }

    /**
     * Retrieves the grid index from a position.
     * @param x the X position from left to right.
     * @param y the Y position from bottom to top.
     * @param z the Z position from back to front.
     * @return the grid indices of the position.
     */
    public final Vector3Int getGridFromPosition(float x, float y, float z) {
        vector3Int.x = (int) (x / scale);
        vector3Int.y = (int) (y / scale);
        vector3Int.z = (int) (z / scale);

        return vector3Int;
    }

    /**
     * Retrieves the closest collision with the grid from a ray.
     * @param origin the origin of the ray.
     * @param direction the direction of the ray.
     * @param maxDistance the maximum distance the ray should go.
     * @return the index of the collided grid, null if no collision and within bounds.
     */
    public final Vector3Int getClosestUsedGridFromRay(Vector3f origin, Vector3f direction, float maxDistance) {
        boolean hasCollided = false;

        Vector3Int currentIndex = getGridFromPosition(origin.x, origin.y, origin.z);
        boolean isInBounds = isIndexInBounds(currentIndex.x, currentIndex.y, currentIndex.z);
        Vector3Int lastBoundedPosition = new Vector3Int(0, 0, 0);

        // Current step to increment by.
        float step = 0f;
        while (!hasCollided && step < maxDistance) {
            // Update the position of the point on the ray.
            vector3.x = origin.x + direction.x * step;
            vector3.y = origin.y + direction.y * step;
            vector3.z = origin.z + direction.z * step;
            step += lineStep;

            // Get the grid position and check if in bounds and if collided.
            currentIndex = getGridFromPosition(vector3.x, vector3.y, vector3.z);
            isInBounds = isIndexInBounds(currentIndex.x, currentIndex.y, currentIndex.z);
            if (isInBounds) {
                lastBoundedPosition.x = currentIndex.x;
                lastBoundedPosition.y = currentIndex.y;
                lastBoundedPosition.z = currentIndex.z;
                if (tiles[currentIndex.x][currentIndex.y][currentIndex.z] != 0) {
                    hasCollided = true;
                }
            }
        }
        if (!isInBounds) {
            return null;
            //return lastBoundedPosition;
        } else if (!hasCollided) {
            return null;
        }

        return currentIndex;
    }

    /**
     * Retrieves the closest empty grid from the ray.
     * @param origin the start of the ray.
     * @param direction the direction of the ray.
     * @param maxDistance the maximum distance to check to.
     * @return the closest empty tile in the grid.
     */
    public final Vector3Int getClosestEmptyGridFromRay(Vector3f origin, Vector3f direction, float maxDistance) {
        getClosestUsedGridFromRay(origin, direction, maxDistance);

        // Update the position of the point on the ray.
        // This uses the shared vector3 position from the getClosestUsedGridFromRay function.
        vector3.x -= direction.x * lineStep;
        vector3.y -= direction.y * lineStep;
        vector3.z -= direction.z * lineStep;

        return getGridFromPosition(vector3.x, vector3.y, vector3.z);
    }

    /**
     * Retrieves the closest empty point on the grid from the ray.
     * @param origin the start of the ray.
     * @param direction the direction of the ray.
     * @param maxDistance the maximum distance to check to.
     * @return the closest empty point on the grid.
     */
    public final Vector3f getClosestEmptyPointFromRay(Vector3f origin, Vector3f direction, float maxDistance) {
        getClosestUsedGridFromRay(origin, direction, maxDistance);

        // Update the position of the point on the ray.
        // This uses the shared vector3 position from the getClosestUsedGridFromRay function.
        vector3.x -= direction.x * lineStep;
        vector3.y -= direction.y * lineStep;
        vector3.z -= direction.z * lineStep;

        return vector3;
    }

    /**
     * Checks if index is within the bounds of the tiles.
     * @param x the x index.
     * @param y the y index.
     * @param z the z index.
     * @return true if the tiles are in bounds, false if out.
     */
    public final boolean isIndexInBounds(int x, int y, int z) {
        boolean isOutsideMinimum = x < 0 || y < 0 || z < 0;
        boolean isOutsideMaximum = x > tiles.length - 1 || y > tiles[0].length - 1 || z > tiles[0][0].length - 1;

        return !(isOutsideMinimum || isOutsideMaximum);
    }

    /**
     * Retrieves the tile type at a specified index.
     * @param x the x index.
     * @param y the y index.
     * @param z the z index.
     * @return the tile type at the index location.
     */
    public final int getTileAtIndex(int x, int y, int z) {
        return tiles[x][y][z];
    }

    /**
     * Normalizes the index to be within the grid.
     * @param minStore the minimum index.
     * @param maxStore the maximum index.
     */
    public void normalizeIndices(Vector3Int minStore, Vector3Int maxStore) {
        if (minStore.x < 0) {
            minStore.x = 0;
        }
        if (maxStore.x >= tiles.length) {
            maxStore.x = tiles.length - 1;
        }
        if (minStore.y < 0) {
            minStore.y = 0;
        }
        if (maxStore.y >= tiles[0].length) {
            maxStore.y = tiles[0].length - 1;
        }
        if (minStore.z < 0) {
            minStore.z = 0;
        }
        if (maxStore.z >= tiles[0][0].length) {
            maxStore.z = tiles[0][0].length - 1;
        }
    }

    /**
     * Checks if a bounds is within the tile grid.
     * @param minX the min x.
     * @param minY the min y.
     * @param minZ the min z.
     * @param maxX the max x.
     * @param maxY the max y.
     * @param maxZ the max z.
     * @return true if the object is within the bounds, false otherwise.
     */
    private boolean isBoundsWithinGrid(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        if (tiles == null) {
            return false;
        }

        // Check if within bounds.
        boolean isOutsideX = (minX < 0 && maxX < 0) || (minX >= tiles.length && maxX >= tiles.length);
        boolean isOutsideY = (minY < 0 && maxY < 0) || (minY >= tiles[0].length && maxY >= tiles[0].length);
        boolean isOutsideZ = (minZ < 0 && maxZ < 0) || (minZ >= tiles[0][0].length && maxZ >= tiles[0][0].length);
        if (isOutsideX || isOutsideY || isOutsideZ) {
            return false;
        }
        return true;
    }

    /**
     * Retrieves the tile indices from the bounds and stores them in two vectors.
     * The indices should be tested for collisions as this allows for some tolerance.
     * @param bounds the bounds to calculate from.
     * @param minStore the minimum bounds store.
     * @param maxStore the maximum bounds store.
     * @return true if the object resides within the indices, false otherwise.
     */
    public final boolean getTileIndicesFromBounds(BoundingBox bounds, Vector3Int minStore, Vector3Int maxStore) {
        // Center of the object.
        Vector3f center = bounds.getCenter();
        // Get indices that are in the beginning in float format for easier out of bounds checking.
        float minX = (center.x - bounds.getXExtent()) / scale - PhysicsUnits.TOLERANCE;
        float minY = (center.y - bounds.getYExtent()) / scale - PhysicsUnits.TOLERANCE;
        float minZ = (center.z - bounds.getZExtent()) / scale - PhysicsUnits.TOLERANCE;
        // Get the end indices
        float maxX = (center.x + bounds.getXExtent()) / scale + PhysicsUnits.TOLERANCE;
        float maxY = (center.y + bounds.getYExtent()) / scale + PhysicsUnits.TOLERANCE;
        float maxZ = (center.z + bounds.getZExtent()) / scale + PhysicsUnits.TOLERANCE;

        if (isBoundsWithinGrid(minX, minY, minZ, maxX, maxY, maxZ)) {
            // Convert the index bounds.
            minStore.x = (int) minX;
            minStore.y = (int) minY;
            minStore.z = (int) minZ;
            // Get the end indices
            maxStore.x = (int) maxX;
            maxStore.y = (int) maxY;
            maxStore.z = (int) maxZ;
            normalizeIndices(minStore, maxStore);
            return true;
        }

        return false;
    }

    /**
     * Collides a root object with the grid.
     * The objects will be tested by the user defined cylinder collision shape otherwise by model bounds.
     * If the object collides with the floor or wall the object will lose acceleration in that direction.
     */
    public final void collideObjectsWithGrid() {
        Vector3Int beginIndex = new Vector3Int(0, 0, 0);
        Vector3Int endIndex = new Vector3Int(0, 0, 0);

        Vector3f movedAmount = new Vector3f();
        Vector3f center = new Vector3f();
        BoundsBox bounds = new BoundsBox();
        EntitySet set = entitySystem.getEntities(GridColliderComponent.class);
        for (Entity entity : set.getEntities()) {
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            // Only deal with entities that have bounds and movement.
            if (collisionComponent == null) {
                continue;
            }
            AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
            if (aabbComponent == null) {
                continue;
            }
            center.set(aabbComponent.centerX, aabbComponent.centerY, aabbComponent.centerZ);
            bounds.setXExtent(aabbComponent.worldExtentX);
            bounds.setYExtent(aabbComponent.worldExtentY);
            bounds.setZExtent(aabbComponent.worldExtentZ);
            bounds.setCenter(center);
            bounds.setTargets(collisionComponent.targets);

            BoundsBox addedBounds = bounds;
            // Add bounds with movement if applicable.
            MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
            if (movementComponent != null) {
                movedAmount.set(movementComponent.moveX, movementComponent.moveY, movementComponent.moveZ);
                addedBounds = bounds.addBounds(movedAmount.x, movedAmount.y, movedAmount.z);
            }

            // Do an easy terrain collision check if the collider is only a sensor.
            GridColliderComponent gridColliderComponent = entitySystem.getComponent(entity, GridColliderComponent.class);
            boolean isSensor = gridColliderComponent != null && gridColliderComponent.isSensor;
            if (isSensor) {
                // Check only that the entity collides with a tile.
                boolean inBounds = getTileIndicesFromBounds(addedBounds, beginIndex, endIndex);
                if (!inBounds) {
                    continue;
                }
                boolean hasCollided = false;
                // If any tile is collided with then exit the loop.
                for (int x = beginIndex.x; x <= endIndex.x; x++) {
                    for (int y = beginIndex.y; y <= endIndex.y; y++) {
                        for (int z = beginIndex.z; z <= endIndex.z; z++) {
                            byte tileIndex = tiles[x][y][z];
                            // Check if the tile is collidable.
                            if (tileIndex == 0) {
                                continue;
                            }
                            if ((collisionGroups[tileIndex] & gridColliderComponent.collisionGroup) == 0) {
                                continue;
                            }
                            // Stop right before the closest tile if enabled or just stop on the first collide.
                            if (gridColliderComponent.stopsOnCollide) {
                                hasCollided = true;
                                break;
                            } else {
                                CollisionInformation collisionInformation = new CollisionInformation();
                                collisionInformation.setCollider(entity);
                                for (CollisionCallbacks callback : collisionCallbacks) {
                                    callback.onTileCollide(collisionInformation);
                                }
                                break;
                            }
                        }
                    }
                }
                // Get the closest collision point but only for moving objects.
                if (movedAmount != null && gridColliderComponent.stopsOnCollide && hasCollided) {
                    Vector3f point = getClosestEmptyPointFromRay(center, movedAmount.normalize(), movedAmount.length());
                    CollisionInformation collisionInformation = new CollisionInformation();
                    collisionInformation.setCollider(entity);
                    collisionInformation.setCollisionPoint(point.x, point.y, point.z);
                    for (CollisionCallbacks callback : collisionCallbacks) {
                        callback.onTileCollide(collisionInformation);
                    }
                    // Stop the entity.
                    SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);
                    if (speedComponent != null) {
                        speedComponent.speedX = 0;
                        speedComponent.speedY = 0;
                        speedComponent.speedZ = 0;
                    }
                    // Move the entity to the closest point.
                    TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
                    if (transformComponent != null) {
                        transformComponent.positionX = point.x;
                        transformComponent.positionY = point.y;
                        transformComponent.positionZ = point.z;
                    }
                    if (movementComponent != null) {
                        movementComponent.moveX = 0;
                        movementComponent.moveY = 0;
                        movementComponent.moveZ = 0;
                    }
                }
            } else {
                if (movementComponent == null) {
                    continue;
                }
                boolean inBounds = getTileIndicesFromBounds(addedBounds, beginIndex, endIndex);
                // Only check entities in bounds.
                List<BoundsBox> boundsBoxes = new LinkedList<BoundsBox>();
                if (inBounds) {
                    // Create a list of bounding boxes for colliding tiles.
                    for (int x = beginIndex.x; x <= endIndex.x; x++) {
                        for (int y = beginIndex.y; y <= endIndex.y; y++) {
                            for (int z = beginIndex.z; z <= endIndex.z; z++) {
                                byte tileIndex = tiles[x][y][z];
                                if (tileIndex == 0) {
                                    continue;
                                }
                                if ((collisionGroups[tileIndex] & gridColliderComponent.collisionGroup) == 0) {
                                    continue;
                                }
                                BoundsBox tileBounds = new BoundsBox(new Vector3f(), scale / 2, scale / 2, scale / 2);
                                tileBounds.setCenter(new Vector3f(x * scale + scale / 2,
                                        y * scale + scale / 2,
                                        z * scale + scale / 2));
                                // Allow the tile to collide with everything as the grid collider has its own groups.
                                tileBounds.setGroups(Long.MAX_VALUE);
                                boundsBoxes.add(tileBounds);
                            }
                        }
                    }
                    CollisionInformation collisionInformation = collisionResolver.resolveCollisionUnfiltered(bounds, movedAmount, boundsBoxes);
                    collisionInformation.setCollider(entity);
                    Vector3f moveAmount = collisionInformation.getMoveAmount();
                    // Send a callback if the entity has collided with a tile.
                    // Check size to skip other checks if no callbacks.
                    boolean hasCollided = movementComponent.moveX != moveAmount.x
                            || movementComponent.moveY != moveAmount.y
                            || movementComponent.moveZ != moveAmount.z;
                    if (collisionCallbacks.size() > 0) {
                        if (hasCollided) {
                            for (CollisionCallbacks callback : collisionCallbacks) {
                                callback.onTileCollide(collisionInformation);
                            }
                        }
                    }

                    SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);
                    if (movementComponent.moveY != moveAmount.y) {
                        if (movementComponent.moveY < 0) {
                            PhysicsStateComponent physicsStateComponent = entitySystem.getComponent(entity, PhysicsStateComponent.class);
                            if (physicsStateComponent != null) {
                                physicsStateComponent.isGroundedGrid = true;
                            }
                            collisionComponent.isOnFloor = true;
                        }
                        // Apply friction if movement is restricted in the Y axis and reset the Y speed.
                        if (speedComponent != null) {
                            speedComponent.speedY = 0;
                            int indexY = (int) ((aabbComponent.centerY - aabbComponent.worldExtentY) / scale - PhysicsUnits.TOLERANCE);
                            if (indexY >= 0) {
                                int indexX = (int) (aabbComponent.centerX / scale - PhysicsUnits.TOLERANCE);
                                int indexZ = (int) (aabbComponent.centerZ / scale - PhysicsUnits.TOLERANCE);
                                if (indexX < tiles.length && indexZ < tiles[indexX][indexY].length) {
                                    int tileIndex = tiles[indexX][indexY][indexZ];
                                    float friction = 1.0f;
                                    if (tileIndex < tileFriction.length) {
                                        friction = tileFriction[tileIndex];
                                    }
                                    speedComponent.friction = friction;
                                }
                            }
                        }
                    }
                    // Remove speed when bumping into a tile.
                    if (speedComponent != null && hasCollided) {
                        if (gridColliderComponent != null && gridColliderComponent.stopsOnCollide) {
                            speedComponent.speedX = 0;
                            speedComponent.speedY = 0;
                            speedComponent.speedZ = 0;
                        } else {
                            if (movementComponent.moveX != moveAmount.x) {
                                speedComponent.speedX = 0;
                            }
                            if (movementComponent.moveZ != moveAmount.z) {
                                speedComponent.speedZ = 0;
                            }
                        }
                    }
                    movementComponent.moveX = moveAmount.x;
                    movementComponent.moveY = moveAmount.y;
                    movementComponent.moveZ = moveAmount.z;
                }
            }
        }
    }

    @Override
    public void update(float tpf) {
        collideObjectsWithGrid();
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * Sets the tiles of the grid collider system and fills the unused friction
     * and collision groups with empty ones.
     * @param tiles the tiles reference to use.
     */
    public void setTiles(byte[][][] tiles) {
        this.tiles = tiles;

        // Fill the unused tile indices with default values.
        int maxIndex = 0;
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                for (int z = 0; z < tiles[x][y].length; z++) {
                    if (tiles[x][y][z] > maxIndex) {
                        maxIndex = tiles[x][y][z];
                    }
                }
            }
        }
        // Fill the friction.
        if (tileFriction.length < maxIndex) {
            float[] newFriction = new float[maxIndex];
            newFriction[0] = 0;
            for (int i = 1; i < maxIndex; i++) {
                if (i < tileFriction.length) {
                    newFriction[i] = tileFriction[i];
                } else {
                    newFriction[i] = DEFAULT_FRICTION;
                }
            }
            tileFriction = newFriction;
        }
        // Fill the collision groups.
        if (collisionGroups.length < maxIndex) {
            long[] newCollisionGroups = new long[maxIndex];
            newCollisionGroups[0] = 0;
            for (int i = 1; i < maxIndex; i++) {
                if (i < collisionGroups.length) {
                    newCollisionGroups[i] = collisionGroups[i];
                } else {
                    newCollisionGroups[i] = DEFAULT_COLLISION_GROUP;
                }
                System.out.println(newCollisionGroups[i]);
            }
            collisionGroups = newCollisionGroups;
        }
    }

    /**
     * @param friction the friction for each tile index.
     */
    public void setTileFriction(float[] friction) {
        tileFriction = friction.clone();
    }

    /**
     * @param scale the scale of a tile.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * @param collisionGroups the collision groups of the tiles.
     */
    public void setCollisionGroups(long[] collisionGroups) {
        this.collisionGroups = collisionGroups;
    }

    /**
     * @param collisionCallbacks the collision callbacks to set.
     */
    public void addCollisionCallbacks(CollisionCallbacks collisionCallbacks) {
        if (!this.collisionCallbacks.contains(collisionCallbacks)) {
            this.collisionCallbacks.add(collisionCallbacks);
        }
    }

    @Override
    public void destroy() {
    }
}
