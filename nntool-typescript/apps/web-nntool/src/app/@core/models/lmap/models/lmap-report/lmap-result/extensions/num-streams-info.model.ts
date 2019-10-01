export class NumStreamsInfo {
  /**
   * The requested number of streams for the download measurement.
   */
  public requested_num_streams_download: number;

  /**
   * The requested number of streams for the upload measurement.
   */
  public requested_num_streams_upload: number;

  /**
   * The actual number of streams used by the download measurement.
   */
  public actual_num_streams_download: number;

  /**
   * The actual number of streams used by the upload measurement.
   */
  public actual_num_streams_upload: number;
}
