package jinhaoxia.handler.ready.model.request.comparator;

import jinhaoxia.handler.ready.model.request.HttpRequestLike;

import java.util.Comparator;

public class HttpRequestLikeComparator implements Comparator<HttpRequestLike> {
    public static final HttpRequestLikeComparator HTTP_REQUEST_LIKE_COMPARATOR = new HttpRequestLikeComparator();

    @Override
    public int compare(HttpRequestLike o1, HttpRequestLike o2) {

        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        // 顺序： authority, port, path, method, queryString

        int cmp = 0;

        cmp = ReversedListComparator.REVERSED_LIST_COMPARATOR.compare(o1.getAuthoritySegments(), o2.getAuthoritySegments());
        if (cmp != 0) return cmp;

        cmp = IntegerComparator.INTEGER_COMPARATOR.compare(o1.getPort(), o2.getPort());
        if (cmp != 0) return cmp;

        cmp = IntegerComparator.INTEGER_COMPARATOR.compare(o1.getMethod(), o2.getMethod());
        if (cmp != 0) return cmp;

        cmp = ListComparator.LIST_COMPARATOR.compare(o1.getPathSegments(), o2.getPathSegments());
        if (cmp != 0) return cmp;

        cmp = MapComparator.MAP_COMPARATOR.compare(o1.getQueryStringMapping(), o2.getQueryStringMapping());
        if (cmp != 0) return cmp;

        return 0;
    }
}
