export class WebSocketInfo {

    /**
     * Size of a transmitted frame over the WebSocket protocol.
     */
    frameSize: number;

    /**
     * Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.
     */
    frameCount: number;

    /**
     * Number of frames sent over the WebSocket protocol during measurement including slow-start phase.
     */
    frame_count_including_slow_start: number;

    /**
     * The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.
     */
    overhead: number;

    /**
     * The overhead sent during the communication via the WebSocket protocol including slow-start phase.
     */
    overhead_per_frame_including_slow_start: number;

    /**
     * The overhead a single frame produces on average.
     */
    overhead_per_frame: number;
}
