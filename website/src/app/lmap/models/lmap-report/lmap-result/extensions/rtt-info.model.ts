import {Rtt} from './rtt.model';

export class RttInfo {

    /**
     * List of all measured RTTs.
     */
    rtts: Rtt[];

    /**
     * The number of RTT packets to send, as instructed by the server.
     */
    requested_num_packets: number;

    /**
     * The actual number of sent RTT packets.
     */
    num_sent: number;

    /**
     * The actual number of received RTT packets.
     */
    num_received: number;

    /**
     * The actual number of failed RTT packets.
     */
    num_error: number;

    /**
     * The actual number of missing RTT packets.
     */

    num_missing: number;

    /**
     * The actual size of RTT packets.
     */
    packet_size: number;
}
