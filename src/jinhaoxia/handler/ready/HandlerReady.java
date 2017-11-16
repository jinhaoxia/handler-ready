package jinhaoxia.handler.ready;

import jinhaoxia.handler.ready.io.ScannerHttpRequestReader;
import jinhaoxia.handler.ready.model.request.HttpRequest;
import jinhaoxia.handler.ready.model.request.HttpRequestHandler;
import jinhaoxia.handler.ready.model.request.Support;
import jinhaoxia.handler.ready.util.HttpRequestItemSetEncoder;
import jinhaoxia.handler.ready.util.IHttpRequestEncoder;
import jinhaoxia.support.pattern.AlgoGISM;
import jinhaoxia.support.pattern.Database;
import jinhaoxia.support.pattern.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public final class HandlerReady {
    private HandlerReady() {
    }

    public static List<HttpRequestHandler> learn(Collection<HttpRequest> requests) {
        return learn(requests, 0.01);
    }

    public static List<HttpRequestHandler> learn(Collection<HttpRequest> requests, double minSupport) {
        // encoding requests to itemset database
        IHttpRequestEncoder encoder = new HttpRequestItemSetEncoder();
        encoder.prepare(requests);
        List<Transaction> transactions = requests.stream().map(req -> encoder.encode(req)).collect(Collectors.toList());
        Database database = new Database(transactions);

        // launching FPClose to mine the frequent patterns
        AlgoGISM gism = new AlgoGISM();
        gism.runAlgorithm(database, minSupport, null);

        // receiving the mined frequent patterns & converting them to handlers.
        List<HttpRequestHandler> handlers = gism.getPatterns().stream().map(tran -> encoder.decode(tran)).collect(Collectors.toList());

        // sorting and returning handlers.
        Collections.sort(handlers);
        return handlers;
    }

    /**
     *
     * @param handlers
     * @param requests
     * @return Handlers with no support will be ignored in this phase.
     */
    public static List<Support> handlers2supports(Collection<HttpRequestHandler> handlers, Collection<HttpRequest> requests) {

        List<Support> supports = handlers.stream().map(handler -> new Support(handler)).collect(Collectors.toList());
        List<HttpRequest> homeless = new ArrayList<>();

        // assign requests to different handlers
        for (HttpRequest req : requests) {
            boolean assigned = false;
            for (Support s : supports) {
                if (s.getHandler().hit(req)) {
                    s.getRequests().add(req);
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                homeless.add(req);
            }
        }

        // assign handlers, which are not be assigned to any handlers, to a special handler null.
        Support support4homeless = new Support(null);
        support4homeless.getRequests().addAll(homeless);
        supports.add(support4homeless);

        supports  = supports.stream().filter(s -> s.getRequests().size() > 0).collect(Collectors.toList());

        return supports;
    }

    public static List<List<HttpRequest>> sample(Collection<Support> supports) {
        List<List<HttpRequest>> requests = new ArrayList<>();

        supports.forEach(Support::reOrderRequests);

        for (int level = 0; ; level++) {
            List<HttpRequest> reqs = new ArrayList<>();
            for (Support s : supports) {
                if (s.getRequests().size() > level) {
                    reqs.add(s.getRequests().get(level));
                }
            }
            if (reqs.size() != 0) requests.add(reqs);
            else break;
        }
        return requests;
    }

}
