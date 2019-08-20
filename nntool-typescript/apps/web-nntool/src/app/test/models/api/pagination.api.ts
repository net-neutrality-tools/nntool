
export class PaginationAPI<T> {

    /**
     * Paginated list of objects.
     */
    content: T[];

    /**
     * Current page number (>= 0).
     */
    page_number: number;

    /**
     * Current page size (> 0).
     */
    page_size: number;

    /**
     * Total amount of pages (>= 0).
     */
    total_pages: number;

    /**
     * Total amount of objects (>= 0).
     */
    total_elements: number;


}

