export enum LmapExecutionMode {
  SEQUENTIAL = 'SEQUENTIAL', // The Actions of the Schedule are executed sequentially.
  PARALLEL = 'PARALLEL', // The Actions of the Schedule are executed concurrently.
  PIPELINED = 'PIPELINED' // The Actions of the Schedule are executed in a pipelined mode.
  // Output created by an Action is passed as input to the subsequent Action.
}
