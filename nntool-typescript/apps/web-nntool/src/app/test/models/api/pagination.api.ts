export class PaginationAPI<T> {
  /**
   * Paginated list of objects.
   */
  public content: T[];

  /**
   * Current page number (>= 0).
   */
  public page_number: number;

  /**
   * Current page size (> 0).
   */
  public page_size: number;

  /**
   * Total amount of pages (>= 0).
   */
  public total_pages: number;

  /**
   * Total amount of objects (>= 0).
   */
  public total_elements: number;
}
