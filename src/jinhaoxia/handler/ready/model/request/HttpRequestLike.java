package jinhaoxia.handler.ready.model.request;

import java.util.List;
import java.util.SortedMap;

public interface HttpRequestLike extends Comparable<HttpRequestLike> {
    Integer getMethod();
    Integer getPort();
    List<String> getAuthoritySegments();
    List<String> getPathSegments();
    SortedMap<String, String> getQueryStringMapping();
    String getScheme();
}
