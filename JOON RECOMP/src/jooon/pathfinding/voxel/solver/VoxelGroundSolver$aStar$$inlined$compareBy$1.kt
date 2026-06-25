package jooon.pathfinding.voxel.solver

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as VoxelGroundSolver.Entry).f, (b as VoxelGroundSolver.Entry).f)
}