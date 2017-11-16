package jinhaoxia.handler.ready.util;

import com.google.common.collect.Lists;
import jinhaoxia.handler.ready.model.request.HttpRequest;
import jinhaoxia.handler.ready.model.request.HttpRequestHandler;
import jinhaoxia.support.pattern.Group;
import jinhaoxia.support.pattern.Transaction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 把所有信息都整合成 一个 Item Set
 */
public class HttpRequestItemSetEncoder implements IHttpRequestEncoder {

    //                           name                   type        key         value
    private static final Integer TYPE_METHOD = 1;//int     //0         Integer
    private static final Integer TYPE_SCHEME = 2;//int     //0         String
    private static final Integer TYPE_PORT = 3;//int     //0         Integer
    private static final Integer TYPE_AUTHORITY = 4;//int     //Integer   String
    private static final Integer TYPE_PATH = 5;//int     //Integer   String
    private static final Integer TYPE_QUERY_STRING = 6;//int     //String    String

    private static class Meta {
        public Comparable type;
        public Comparable key;
        public Comparable value;

        public Meta(Comparable type, Comparable key, Comparable value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }
    }

    private static final Comparator<Meta> META_COMPARATOR = new Comparator<Meta>() {
        @Override
        public int compare(Meta o1, Meta o2) {
            int cmp;

            cmp = o1.type.compareTo(o2.type);
            if (cmp != 0) return cmp;

            cmp = o1.key.compareTo(o2.key);
            if (cmp != 0) return cmp;

            if (o1.value == o2.value) return 0;
            if (o1.value == null) return -1;
            if (o2.value == null) return 1;

            return o1.value.compareTo(o2.value);
        }
    };

    private SortedMap<Meta, Integer> dict = new TreeMap<>(META_COMPARATOR);
    private SortedMap<Integer, Meta> reversedDict = new TreeMap<>();


    public HttpRequestItemSetEncoder() {
    }

    @Override
    public Transaction encode(HttpRequest req) {
        Group grp = new Group();

        int size = 3 * 2;
        size += req.getAuthoritySegments().size() * 2;
        size += req.getPathSegments().size() * 2;
        size += req.getQueryStringMapping().size() * 2;

        grp.type = Group.TYPE_ITEM_SET;
        grp.array = new int[size];

        // 处理基本信息
        {
            grp.array[0] = lookupOrUpdate(new Meta(TYPE_METHOD, 0, null));
            grp.array[1] = lookupOrUpdate(new Meta(TYPE_METHOD, 0, req.getMethod()));
            grp.array[2] = lookupOrUpdate(new Meta(TYPE_SCHEME, 0, null));
            grp.array[3] = lookupOrUpdate(new Meta(TYPE_SCHEME, 0, req.getScheme()));
            grp.array[4] = lookupOrUpdate(new Meta(TYPE_PORT, 0, null));
            grp.array[5] = lookupOrUpdate(new Meta(TYPE_PORT, 0, req.getPort()));
        }

        int c = 6;
        // 处理域名信息
        {
            int n = req.getAuthoritySegments().size();
            for (int i = 0; i < n; i++) {
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_AUTHORITY, n - i, null));
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_AUTHORITY, n - i, req.getAuthoritySegments().get(i)));
            }
        }
        // 处理路径信息
        {
            int n = req.getPathSegments().size();
            for (int i = 0; i < n; i++) {
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_PATH, i + 1, null));
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_PATH, i + 1, req.getPathSegments().get(i)));
            }
        }
        // 处理查询部分
        {
            for (String key : req.getQueryStringMapping().keySet()) {
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_QUERY_STRING, key, null));
                grp.array[c++] = lookupOrUpdate(new Meta(TYPE_QUERY_STRING, key, req.getQueryStringMapping().get(key)));
            }
        }

        Arrays.sort(grp.array);

        Transaction tran = new Transaction();
        tran.add(grp);
        return tran;
    }

    private Integer lookupOrUpdate(Meta meta) {
        if (!dict.containsKey(meta)) {
            dict.put(meta, dict.size() + 1);
            reversedDict.put(dict.size(), meta);
        }
        return dict.get(meta);
    }


    @Override
    public void prepare(Iterable<HttpRequest> requests) {
        dict.clear();
        reversedDict.clear();

        for (HttpRequest req : requests) {
            // 处理简单信息
            {
                lookupOrUpdate(new Meta(TYPE_METHOD, 0, null));
                lookupOrUpdate(new Meta(TYPE_METHOD, 0, req.getMethod()));
                lookupOrUpdate(new Meta(TYPE_SCHEME, 0, null));
                lookupOrUpdate(new Meta(TYPE_SCHEME, 0, req.getScheme()));
                lookupOrUpdate(new Meta(TYPE_PORT, 0, null));
                lookupOrUpdate(new Meta(TYPE_PORT, 0, req.getPort()));
            }
            // 处理域名部分
            {
                int n = req.getAuthoritySegments().size();
                for (int i = 0; i < n; i++) {
                    lookupOrUpdate(new Meta(TYPE_AUTHORITY, n - i, null));
                    lookupOrUpdate(new Meta(TYPE_AUTHORITY, n - i, req.getAuthoritySegments().get(i)));
                }
            }
            // 处理路径部分
            {
                int n = req.getPathSegments().size();
                for (int i = 0; i < n; i++) {
                    lookupOrUpdate(new Meta(TYPE_PATH, i + 1, null));
                    lookupOrUpdate(new Meta(TYPE_PATH, i + 1, req.getPathSegments().get(i)));
                }
            }
            // 处理查询部分
            {
                for (String key : req.getQueryStringMapping().keySet()) {
                    lookupOrUpdate(new Meta(TYPE_QUERY_STRING, key, null));
                    lookupOrUpdate(new Meta(TYPE_QUERY_STRING, key, req.getQueryStringMapping().get(key)));
                }
            }
        }
    }

    @Override
    public HttpRequestHandler decode(Transaction tran) {
        Integer method = null;
        String scheme = null;
        Integer port = null;

        SortedMap<Integer, String> auth = new TreeMap<>();
        SortedMap<Integer, String> path = new TreeMap<>();
        SortedMap<String, String> qs = new TreeMap<>();


        for (int x : tran.headGroup().array) {
            Meta meta = reversedDict.get(x);
            if (meta.type == TYPE_METHOD) {
                if (method == null) method = (Integer) meta.value;
            } else if (meta.type == TYPE_SCHEME) {
                if (scheme == null) scheme = (String) meta.value;
            } else if (meta.type == TYPE_PORT) {
                if (port == null) port = (Integer) meta.value;
            } else if (meta.type == TYPE_AUTHORITY) {
                if (auth.get(meta.key) == null) auth.put((Integer) meta.key, (String) meta.value);
            } else if (meta.type == TYPE_PATH) {
                if (path.get(meta.key) == null) path.put((Integer) meta.key, (String) meta.value);
            } else if (meta.type == TYPE_QUERY_STRING) {
                if (qs.get(meta.key) == null) qs.put((String) meta.key, (String) meta.value);
            } else {
                assert false;
                return null;
            }
        }

        List<String> authSegments = Lists.reverse(auth.keySet().stream().map(auth::get).collect(Collectors.toList()));
        List<String> pathSegments = path.keySet().stream().map(path::get).collect(Collectors.toList());

        return new HttpRequestHandler(method, scheme, port, authSegments, pathSegments, qs);
    }
}
