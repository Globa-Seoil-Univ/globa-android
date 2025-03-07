package team.y2k2.globa.api;

public enum HttpMethod {
    DELETE("DELETE"),
    GET("GET"),
    PATCH("PATCH"),
    POST("POST"),
    PUT("PUT");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
