package jooon.pathfinding.voxel.world

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((b as VoxelBlockCache.StandSurface).feetY, (a as VoxelBlockCache.StandSurface).feetY)
}