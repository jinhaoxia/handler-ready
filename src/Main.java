import jinhaoxia.handler.ready.HandlerReady;
import jinhaoxia.handler.ready.io.ScannerHttpRequestReader;
import jinhaoxia.handler.ready.model.request.HttpRequest;
import jinhaoxia.handler.ready.model.request.HttpRequestHandler;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

/*

GET http://example.com/foo/bar/1
GET http://example.com/foo/bar/2
GET http://example.com/foo/bar/3
GET http://example.com/foo/bar/4
GET http://example.com/foo/bar/5

 */

        Scanner in = new Scanner(System.in);
        ScannerHttpRequestReader reader = new ScannerHttpRequestReader(in);

        List<HttpRequest> requests = reader.readAllHttpRequests();

        List<HttpRequestHandler> handlers = HandlerReady.learn(requests, 0.5);

        handlers.forEach(System.out::println);
    }
}
