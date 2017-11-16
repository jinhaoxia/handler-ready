package jinhaoxia.handler.ready.model.request;

import com.google.common.collect.Sets;
import jinhaoxia.handler.ready.model.request.comparator.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpRequestHandler implements HttpRequestLike {

    private Integer method;
    private Integer port;
    private String scheme;
    private List<String> authoritySegments;
    private List<String> pathSegments;
    private SortedMap<String, String> queryStringMapping;

    private String representation;


    public HttpRequestHandler(Integer method, String scheme, Integer port, List<String> authoritySegments, List<String> pathSegments, SortedMap<String, String> queryStringMapping) {
        this.method = method;
        this.scheme = scheme;
        this.port = port;
        this.authoritySegments = authoritySegments;
        this.pathSegments = pathSegments;
        this.queryStringMapping = queryStringMapping;

        // representation
        {
            StringBuffer sb = new StringBuffer();

            if (method != null) {
                sb.append(HttpRequest.METHOD_DICT[method]);
                sb.append(" ");
            } else {
                sb.append("* ");
            }

            if (scheme != null) {
                sb.append(scheme);
                sb.append("://");
            } else {
                sb.append("*://");
            }

            if (authoritySegments != null && authoritySegments.size() > 0) {
                sb.append(String.join(".", authoritySegments.stream().map(path -> path != null ? path : "*").collect(Collectors.toList())));
            }

            if (port != null && port != -1) {
                sb.append(":");
                sb.append(port);
            }

            sb.append("/");

            if (pathSegments != null && pathSegments.size() > 0) {
                sb.append(String.join("/", pathSegments.stream().map(path -> path != null ? path : "*").collect(Collectors.toList())));
            }

            if (queryStringMapping != null && queryStringMapping.size() > 0) {
                sb.append("?");

                SortedSet<String> keys = new TreeSet<>(queryStringMapping.keySet());
                sb.append(String.join("&", keys.stream().map(key -> String.format("%s=%s", key, queryStringMapping.get(key) != null ? queryStringMapping.get(key) : "*")).collect(Collectors.toList())));
            }


            this.representation = sb.toString();
        }
    }

    public String getRepresentation() {
        return representation;
    }

    public String getScheme() {
        return scheme;
    }

    public Integer getMethod() {
        return method;
    }

    public Integer getPort() {
        return port;
    }

    public List<String> getAuthoritySegments() {
        return Collections.unmodifiableList(authoritySegments);
    }

    public List<String> getPathSegments() {
        return Collections.unmodifiableList(pathSegments);
    }

    public SortedMap<String, String> getQueryStringMapping() {
        return Collections.unmodifiableSortedMap(queryStringMapping);
    }

    @Override
    public String toString() {
        return this.representation;
    }

    public boolean hit(HttpRequest o) {
        return  (this.port == null || this.port.equals(o.getPort()))
                &&
                (this.method == null || this.method.equals(o.getMethod()))
                &&
                (
                    this.authoritySegments.size() == o.getAuthoritySegments().size()
                    &&
                    IntStream.range(0, this.authoritySegments.size()).allMatch(i -> this.authoritySegments.get(i) == null || this.authoritySegments.get(i).equals(o.getAuthoritySegments().get(i)))
                )
                &&
                (
                    this.pathSegments.size() == o.getPathSegments().size()
                    &&
                    IntStream.range(0, this.pathSegments.size()).allMatch(i -> this.pathSegments.get(i) == null || this.pathSegments.get(i).equals(o.getPathSegments().get(i)))
                )
                &&
                (
                    Sets.symmetricDifference(this.queryStringMapping.keySet(), o.getQueryStringMapping().keySet()).size() == 0
                    &&
                    this.queryStringMapping.keySet().stream().allMatch(k -> this.queryStringMapping.get(k) == null || this.queryStringMapping.get(k).equals(o.getQueryStringMapping().get(k)))
                );
    }

    @Override
    public int compareTo(HttpRequestLike o) {
        return HttpRequestLikeComparator.HTTP_REQUEST_LIKE_COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HttpRequestHandler && this.compareTo((HttpRequestHandler) obj) == 0;
    }
}
