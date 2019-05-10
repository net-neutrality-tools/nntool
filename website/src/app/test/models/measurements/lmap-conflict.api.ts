export class LmapConflictAPI {

    /**
     * The names of Tasks overlapping with the execution of the Task that has produced this result.
     *
     */
    "schedule-name": string;

    /**
     * The name of an Action within the Schedule that might have impacted the execution of the Task
     * that has produced this result.
     *
     */
    "action-name": string;

    /**
     * The name of the Task executed by an Action within the Schedule that might have impacted
     * the execution of the Task that has produced this result.
     *
     */
    "task-name": string;

}

