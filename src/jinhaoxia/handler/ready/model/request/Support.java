package jinhaoxia.handler.ready.model.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public
class Support {
    private HttpRequestHandler handler;
    private List<HttpRequest> requests;

    public Support(HttpRequestHandler handler) {
        this.handler = handler;
        this.requests = new ArrayList<>();
    }

    public HttpRequestHandler getHandler() {
        return handler;
    }

    public List<HttpRequest> getRequests() {
        return requests;
    }

    public void reOrderRequests() {
        Collections.shuffle(requests, new Random(0));
    }
}
