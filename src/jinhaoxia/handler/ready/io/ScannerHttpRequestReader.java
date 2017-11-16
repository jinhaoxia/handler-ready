package jinhaoxia.handler.ready.io;

import jinhaoxia.handler.ready.model.request.HttpRequest;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

public class ScannerHttpRequestReader implements HttpRequestReader {

    private Scanner in;

    public ScannerHttpRequestReader() {
        this(new Scanner(System.in));
    }

    public ScannerHttpRequestReader(Scanner in) {
        this.in = in;
    }

    @Override
    public List<HttpRequest> readAllHttpRequests() {
        Set<HttpRequest> tbl = new TreeSet<>();
        List<HttpRequest> requests = new ArrayList<>();
        while (in.hasNext()) {
            String method = in.next();
            String url = in.next();

            HttpRequest.methodLookup(method);
            try {
                HttpRequest req = new HttpRequest(HttpRequest.methodLookup(method), url);
                if(!tbl.contains(req))
                {
                    tbl.add(req);
                    requests.add(req);
                }
            } catch (URISyntaxException ex) {
                System.out.printf("URISyntaxException %s %s\n", method, url);
            }
        }

        return requests;
    }
}
