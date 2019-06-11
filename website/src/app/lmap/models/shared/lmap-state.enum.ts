/**
 * The status of a measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
export enum LmapState {
    ENABLED = 'ENABLED', // The value 'enabled' indicates that the Action/Schedule is currently enabled.
    DISABLED = 'DISABLED', // The value 'disabled' indicates that the Action/Schedule is currently disabled.
    RUNNING = 'RUNNING', // The value 'running' indicates that the Action/Schedule is currently running.
    SUPPRESSED = 'SUPPRESSED' // The value 'suppressed' indicates that the Action/Schedule is currently suppressed.
}

