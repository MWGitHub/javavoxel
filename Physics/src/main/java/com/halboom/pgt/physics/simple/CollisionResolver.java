package com.halboom.pgt.physics.simple;

import com.halboom.pgt.physics.PhysicsUnits;
import com.halboom.pgt.physics.filters.Filter;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/6/13
 * Time: 11:22 AM
 * Resolves collisions between AABB objects and other types of geometries.
 * Collision resolution requires a movement amount.
 */
public class CollisionResolver {
    /**
     * Callbacks for the collisions.
     */
    private List<Filter> filters = new LinkedList<Filter>();

    /**
     * Initializes the resolver.
     */
    public CollisionResolver() {
    }

    /**
     * Collides a ray with bounds.
     * @param ray the ray to use for collision testing.
     * @param collidee the collidee to test with.
     * @return true if an intersection has occured.
     */
    public boolean intersectsWithBounds(Ray ray, Bounds collidee) {
        return collidee.getBounds().intersects(ray);
    }

    /**
     * Collides a bound with another bound.
     * Use this collision when determining what to do with object to object collisions.
     * @param bounds the bounds to collide with.
     * @param collidee the collidee to test with.
     * @return the collision information or null if none exists.
     */
    public CollisionInformation collideBounds(Bounds bounds, Bounds collidee) {
        if (!bounds.equals(collidee) && (bounds.getTargets() & collidee.getGroups()) != 0) {
            boolean isFilterPassed = true;
            for (Filter filter : filters) {
                if (!filter.filterBounds(bounds, collidee)) {
                    isFilterPassed = false;
                    break;
                }
            }
            if (isFilterPassed && bounds.getBounds().intersects(collidee.getBounds())) {
                CollisionInformation collisionInformation = new CollisionInformation();
                collisionInformation.setCollider(bounds.getEntity());
                collisionInformation.setCollidee(collidee.getEntity());
                return collisionInformation;
            }
        }

        return null;
    }

    /**
     * Resolves collisions between an AABB with another AABB object and returns information on how to
     * move the collided object.
     * @param bounds the collider bounds in the original location.
     * @param moveAmount the amount the collider will move if no collision occurs.
     * @param colliders the colliders.
     * @param filters the filters to use.
     * @return information on how to resolve the collision.
     */
    public CollisionInformation resolveCollision(BoundsBox bounds, Vector3f moveAmount, List<BoundsBox> colliders, List<Filter> filters) {
        BoundsBox modifiedBounds = new BoundsBox(bounds);
        Vector3f centerStart = modifiedBounds.getCenter();
        Vector3f finalMovedAmount = new Vector3f(moveAmount);

        // Use custom collision filters when necessary.
        List<BoundsBox> validColliders = colliders;
        if (filters != null && filters.size() > 0) {
            validColliders = new LinkedList<BoundsBox>();
            for (BoundsBox collidee : colliders) {
                boolean isValid = true;
                for (Filter filter : filters) {
                    isValid = filter.filterBounds(bounds, collidee);
                    if (!isValid) {
                        break;
                    }
                }
                if (isValid) {
                    validColliders.add(collidee);
                }
            }
        }

        // Resolve the Y axis
        float move = moveAmount.y;
        // Get the collision depths.
        for (BoundsBox collider : validColliders) {
            if (!collider.equals(bounds) && (bounds.getTargets() & collider.getGroups()) != 0) {
                move = getYPreCollision(collider, modifiedBounds, move);
            }
        }
        finalMovedAmount.y = move;
        centerStart.y += move;
        modifiedBounds.setCenter(centerStart);

        // Resolve the X axis
        move = moveAmount.x;
        // Get the collision depths.
        for (BoundsBox collider : validColliders) {
            if (!collider.equals(bounds) && (bounds.getTargets() & collider.getGroups()) != 0) {
                move = getXPreCollision(collider, modifiedBounds, move);
            }
        }
        finalMovedAmount.x = move;
        centerStart.x += move;
        modifiedBounds.setCenter(centerStart);

        // Resolve the Z axis
        move = moveAmount.z;
        // Get the collision depths.
        for (BoundsBox collider : validColliders) {
            if (!collider.equals(bounds) && (bounds.getTargets() & collider.getGroups()) != 0) {
                move = getZPreCollision(collider, modifiedBounds, move);
            }
        }
        finalMovedAmount.z = move;
        centerStart.z += move;
        modifiedBounds.setCenter(centerStart);

        CollisionInformation collisionInformation = new CollisionInformation();
        collisionInformation.setMoveAmount(finalMovedAmount);
        return collisionInformation;
    }

    /**
     * Resolves collisions between an AABB with another AABB object and returns information on how to
     * move the collided object.
     * No filters are used for the collisions.
     * @param bounds the collider bounds in the original location.
     * @param moveAmount the amount the collider will move if no collision occurs.
     * @param colliders the colliders.
     * @return information on how to resolve the collision.
     */
    public CollisionInformation resolveCollisionUnfiltered(BoundsBox bounds, Vector3f moveAmount, List<BoundsBox> colliders) {
        return resolveCollision(bounds, moveAmount, colliders, null);
    }

    /**
     * Resolves collisions between an AABB with another AABB object and returns information on how to
     * move the collided object.
     * Default filters are used.
     * @param bounds the collider bounds in the original location.
     * @param moveAmount the amount the collider will move if no collision occurs.
     * @param colliders the colliders.
     * @return information on how to resolve the collision.
     */
    public CollisionInformation resolveCollision(BoundsBox bounds, Vector3f moveAmount, List<BoundsBox> colliders) {
        return resolveCollision(bounds, moveAmount, colliders, filters);
    }

    /**
     * Checks if an object is colliding along one axis.
     * @param min1 the minimum of the first bound.
     * @param max1 the maximum of the first bound.
     * @param min2 the minimum of the second bound.
     * @param max2 the maximum of the second bound.
     * @return true if the bounds are colliding along the axis.
     */
    private boolean isCollidingOnAxis(float min1, float max1, float min2, float max2) {
        return max2 - PhysicsUnits.TOLERANCE > min1 && min2 + PhysicsUnits.TOLERANCE < max1;
    }

    /**
     * Checks if the distance is smaller than the current distance and returns the smaller distance.
     * @param min1 the minimum of the first bound.
     * @param max1 the maximum of the first bound.
     * @param min2 the minimum of the second bound.
     * @param max2 the maximum of the second bound.
     * @param currentDistance the current minimum distance.
     * @return the smaller distance between the calculated and current.
     */
    private float getMinimumDistance(float min1, float max1, float min2, float max2, float currentDistance) {
        float distance;
        float minimum = currentDistance;
        if (currentDistance > 0 && max2 - PhysicsUnits.TOLERANCE <= min1) {
            distance = min1 - max2;
            if (distance < currentDistance) {
                minimum = distance;
            }
        }
        if (currentDistance < 0 && min2 + PhysicsUnits.TOLERANCE >= max1) {
            distance = max1 - min2;
            if (distance > currentDistance) {
                minimum = distance;
            }
        }
        return minimum;
    }

    /**
     * Calculate the Y offset and return it if the bounds overlap and if the overlap is smaller.
     * @param bounds the bounds to check with.
     * @param checkedBounds the bounds to check against.
     * @param minimumX the minimum to replace if smaller.
     * @return the minimum in the x axis.
     */
    private float getXPreCollision(BoundsBox bounds, BoundsBox checkedBounds, float minimumX) {
        Vector3f min = bounds.getMin(null);
        Vector3f max = bounds.getMax(null);

        Vector3f checkedMin = checkedBounds.getMin(null);
        Vector3f checkedMax = checkedBounds.getMax(null);

        // Check if overlapping.
        boolean isYColliding = isCollidingOnAxis(min.y, max.y, checkedMin.y, checkedMax.y);
        boolean isZColliding = isCollidingOnAxis(min.z, max.z, checkedMin.z, checkedMax.z);
        float currentMinX = minimumX;
        if (isYColliding && isZColliding) {
            currentMinX = getMinimumDistance(min.x, max.x, checkedMin.x, checkedMax.x, minimumX);
        }
        return currentMinX;
    }

    /**
     * Calculate the Y offset and return it if the bounds overlap and if the overlap is smaller.
     * @param bounds the bounds to check with.
     * @param checkedBounds the bounds to check against.
     * @param minimumY the minimum to replace if smaller.
     * @return the minimum in the y axis.
     */
    private float getYPreCollision(BoundsBox bounds, BoundsBox checkedBounds, float minimumY) {
        Vector3f min = bounds.getMin(null);
        Vector3f max = bounds.getMax(null);

        Vector3f checkedMin = checkedBounds.getMin(null);
        Vector3f checkedMax = checkedBounds.getMax(null);
        // Check if overlapping.
        boolean isXColliding = isCollidingOnAxis(min.x, max.x, checkedMin.x, checkedMax.x);
        boolean isZColliding = isCollidingOnAxis(min.z, max.z, checkedMin.z, checkedMax.z);
        float currentMinY = minimumY;
        if (isXColliding && isZColliding) {
            currentMinY = getMinimumDistance(min.y, max.y, checkedMin.y, checkedMax.y, minimumY);
        }
        return currentMinY;
    }

    /**
     * Calculate the Y offset and return it if the bounds overlap and if the overlap is smaller.
     * @param bounds the bounds to check with.
     * @param checkedBounds the bounds to check against.
     * @param minimumZ the minimum to replace if smaller.
     * @return the minimum in the y axis.
     */
    private float getZPreCollision(BoundsBox bounds, BoundsBox checkedBounds, float minimumZ) {
        Vector3f min = bounds.getMin(null);
        Vector3f max = bounds.getMax(null);

        Vector3f checkedMin = checkedBounds.getMin(null);
        Vector3f checkedMax = checkedBounds.getMax(null);
        // Check if overlapping.
        boolean isXColliding = isCollidingOnAxis(min.x, max.x, checkedMin.x, checkedMax.x);
        boolean isYColliding = isCollidingOnAxis(min.y, max.y, checkedMin.y, checkedMax.y);
        float currentMinZ = minimumZ;
        if (isXColliding && isYColliding) {
            currentMinZ = getMinimumDistance(min.z, max.z, checkedMin.z, checkedMax.z, minimumZ);
        }

        return currentMinZ;
    }

    /**
     * @param filter the filter to set.
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
