package sn.autocloser.infrastructure.adapter.in.web.dto.response;

/**
 * DTO de réponse standard de l'API AutoCloser.
 */
public record ApiResponse(
        boolean success,
        String message,
        Object data
) {
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message, null);
    }

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}
