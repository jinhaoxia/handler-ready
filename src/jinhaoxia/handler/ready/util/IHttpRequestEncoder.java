package jinhaoxia.handler.ready.util;

import jinhaoxia.handler.ready.model.request.HttpRequest;
import jinhaoxia.handler.ready.model.request.HttpRequestHandler;
import jinhaoxia.support.pattern.Transaction;

public interface IHttpRequestEncoder {

    Transaction encode(HttpRequest req);
    void prepare(Iterable<HttpRequest> requests);
    HttpRequestHandler decode(Transaction tran);
}
