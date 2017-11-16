package jinhaoxia.handler.ready.model.request;

import com.google.common.collect.Lists;
import jinhaoxia.handler.ready.model.request.comparator.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class HttpRequest implements HttpRequestLike {

    public static final int METHOD_OPTIONS = 2;
    public static final int METHOD_GET = 3;
    public static final int METHOD_HEAD = 4;
    public static final int METHOD_POST = 5;
    public static final int METHOD_PUT = 6;
    public static final int METHOD_DELETE = 7;
    public static final int METHOD_CONNECT = 8;

    public static Integer methodLookup(String strMethod) {
        if (strMethod == null || strMethod.trim().length() == 0) return null;

        switch (strMethod) {
            case "OPTIONS":
                return METHOD_OPTIONS;
            case "GET":
                return METHOD_GET;
            case "HEAD":
                return METHOD_HEAD;
            case "POST":
                return METHOD_POST;
            case "PUT":
                return METHOD_PUT;
            case "DELETE":
                return METHOD_DELETE;
            case "CONNECT":
                return METHOD_CONNECT;
        }

        return null;
    }

    public static final String[] METHOD_DICT = new String[]{null, null, "OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT",};

    private String originalUrl;

    private Integer method;
    private String scheme;
    private Integer port;
    private List<String> authoritySegments;
    private List<String> pathSegments;
    private SortedMap<String, String> queryStringMapping;


    public HttpRequest(String method, String originalUrl) throws URISyntaxException {
        this(methodLookup(method), originalUrl);
    }

    public HttpRequest(Integer method, String originalUrl) throws URISyntaxException {
        this.method = method;
        this.originalUrl = originalUrl;

        //  转换过程
        URI uri = new URI(originalUrl);
        this.scheme = uri.getScheme();
        this.port = uri.getPort();

        //  域名分割
        {
            String authorities = uri.getHost();
            if (authorities != null) {
                authoritySegments = Lists.newArrayList(authorities.split("[.]"));
            } else {
                authoritySegments = Collections.emptyList();
            }
        }

        //  路径分割
        {
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            this.pathSegments = Lists.newArrayList(path.split("/"));
            if (this.pathSegments.get(pathSegments.size() - 1).length() == 0)
                this.pathSegments.remove(pathSegments.size() - 1);
        }

        //  QueryString 处理
        {
            this.queryStringMapping = new TreeMap<>();
            List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
            for (NameValuePair param : params) {
                this.queryStringMapping.put(param.getName(), param.getValue());
            }
        }
    }


    public String getOriginalUrl() {
        return originalUrl;
    }

    public Integer getMethod() {
        return method;
    }

    public String getMethodString() {
        return METHOD_DICT[getMethod()];
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
    public int hashCode() {
        return this.method.hashCode() ^ this.port.hashCode() ^ this.authoritySegments.hashCode() ^ this.pathSegments.hashCode() ^ this.queryStringMapping.hashCode();
    }

    public boolean equals(HttpRequest o) {
        return compareTo(o) == 0;
    }

    @Override
    public int compareTo(HttpRequestLike o) {
        return HttpRequestLikeComparator.HTTP_REQUEST_LIKE_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return METHOD_DICT[this.method] + " " + this.originalUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HttpRequest && compareTo((HttpRequest) obj) == 0;
    }

    public String getScheme() {
        return this.scheme;
    }
}

