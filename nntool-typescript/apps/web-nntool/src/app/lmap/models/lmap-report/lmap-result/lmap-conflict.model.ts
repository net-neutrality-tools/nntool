export class LmapConflict {
  /**
   * The names of Tasks overlapping with the execution of the Task that has produced this result.
   *
   */
  public 'schedule-name'?: string;

  /**
   * The name of an Action within the Schedule that might have impacted the execution of the Task
   * that has produced this result.
   *
   */
  public 'action-name'?: string;

  /**
   * The name of the Task executed by an Action within the Schedule that might have impacted
   * the execution of the Task that has produced this result.
   *
   */
  public 'task-name'?: string;
}
