package jinhaoxia.handler.ready.io;

import jinhaoxia.handler.ready.model.request.HttpRequest;

import java.util.List;
import java.util.Set;

public interface HttpRequestReader {
    List<HttpRequest> readAllHttpRequests();
}
