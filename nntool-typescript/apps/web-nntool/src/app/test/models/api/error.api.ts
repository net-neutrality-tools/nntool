export class ErrorAPI {

    /**
     * Date and time at which the error occurred.
     */
    time: Date;

    /**
     * URI path/resource that caused the error.
     */
    path: string;

    /**
     * Status code for the error. Example: 400, 404, 500, ...
     */
    status: number;

    /**
     * String representation of the status. Example: "Internal Server Error, "Not Found", ...
     */
    error: string;

    /**
     * The error or exception message. Example: "java.lang.RuntimeException".
     */
    message: string;

    /**
     * Exception class name.
     */
    exception: string;

    /**
     * Exception stack trace.
     */
    trace: string;

}

