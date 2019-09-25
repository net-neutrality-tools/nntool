export abstract class BasicRequestAPI {
  /**
   * The type of the specific request
   * needed to enable server deserialization
   */
  public readonly deserialize_type: string;
}
