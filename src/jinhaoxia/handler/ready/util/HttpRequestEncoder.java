package jinhaoxia.handler.ready.util;

import com.google.common.collect.Lists;
import jinhaoxia.handler.ready.model.request.HttpRequest;
import jinhaoxia.handler.ready.model.request.HttpRequestHandler;
import jinhaoxia.support.pattern.Group;
import jinhaoxia.support.pattern.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestEncoder implements IHttpRequestEncoder {

    private class Meta {

        public static final int PART_PROT = 1;
        public static final int PART_AUTH = 2;
        public static final int PART_PATH = 3;
        public static final int PART_QSMD = 4;

        Comparable part;
        Comparable key;
        Comparable value;

        public Meta(Comparable part, Comparable key, Comparable value) {
            this.part = part;
            this.key = key;
            this.value = value;
        }
    }

    private Map<Meta, Integer> dict = new TreeMap<>(new Comparator<Meta>() {
        @Override
        public int compare(Meta o1, Meta o2) {
            int cmp;
            cmp = o1.part.compareTo(o2.part);
            if (cmp != 0) return cmp;
            cmp = o1.key.compareTo(o2.key);
            if (cmp != 0) return cmp;
            if (o1.value == o2.value) return 0;
            if (o1.value == null) return -1;
            if (o2.value == null) return 1;
            return o1.value.compareTo(o2.value);
        }
    });
    private Map<Integer, Meta> reverseDict = new TreeMap<>();

    private static final int KEY_FOR_METHOD = 1;
    private static final int KEY_FOR_SCHEME = 2;
    private static final int KEY_FOR_PORT = 3;

    private SortedSet<String> qsKeys = new TreeSet<>();

    @Override
    public Transaction encode(HttpRequest req) {
        int[] info = new int[]{
                dict.get(new Meta(Meta.PART_PROT, KEY_FOR_METHOD, req.getMethod())),
                dict.get(new Meta(Meta.PART_PROT, KEY_FOR_SCHEME, req.getScheme())),
                dict.get(new Meta(Meta.PART_PROT, KEY_FOR_PORT, req.getPort()))
        };
        Arrays.sort(info);

        int[] qs = new int[qsKeys.size()];
        {
            int i = 0;
            for (String key : qsKeys) {
                qs[i++] = dict.get(new Meta(Meta.PART_QSMD, key, req.getQueryStringMapping().get(key)));
            }
            Arrays.sort(qs);
        }

        int[] auth = new int[req.getAuthoritySegments().size() * 2];
        {
            int c = 0;
            int l = req.getAuthoritySegments().size();
            for (int i = 0; i < l; i++) {
                int keyForExist = (l - i - 1) * 2 + 1;
                int keyForValue = (l - i - 1) * 2 + 2;
                auth[c++] = dict.get(new Meta(Meta.PART_AUTH, keyForExist, 0));
                auth[c++] = dict.get(new Meta(Meta.PART_AUTH, keyForValue, req.getAuthoritySegments().get(i)));
            }
        }

        int[] path = new int[req.getPathSegments().size() * 2];
        {
            int c = 0;
            for (int i = 0; i < req.getPathSegments().size(); i++) {
                int keyForExist = 2 * i + 1;
                int keyForValue = 2 * i + 2;
                path[c++] = dict.get(new Meta(Meta.PART_PATH, keyForExist, 0));
                path[c++] = dict.get(new Meta(Meta.PART_PATH, keyForValue, req.getPathSegments().get(i)));
            }
        }

        Transaction tran = new Transaction();
        tran.add(new Group(Group.TYPE_ITEM_SET, info));
        tran.add(new Group(Group.TYPE_SEQUENCE, auth));
        tran.add(new Group(Group.TYPE_SEQUENCE, path));
        tran.add(new Group(Group.TYPE_ITEM_SET, qs));

        return tran;
    }

    @Override
    public void prepare(Iterable<HttpRequest> reqs) {
        qsKeys.clear();
        dict.clear();
        reverseDict.clear();

        int authWidth = 0;
        int pathWidth = 0;

        for (HttpRequest req : reqs) {
            this.qsKeys.addAll(req.getQueryStringMapping().keySet());
            if (req.getAuthoritySegments().size() > authWidth)
                authWidth = req.getAuthoritySegments().size();
            if (req.getPathSegments().size() > pathWidth)
                pathWidth = req.getPathSegments().size();
        }


        for (HttpRequest req : reqs) {
            // METHOD + SCHEME + PORT
            {

                Meta meta = new Meta(Meta.PART_PROT, KEY_FOR_METHOD, req.getMethod());
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());

                meta = new Meta(Meta.PART_PROT, KEY_FOR_SCHEME, req.getScheme());
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());

                meta = new Meta(Meta.PART_PROT, KEY_FOR_PORT, req.getPort());
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());
            }

            // Authority
            {
                int l = req.getAuthoritySegments().size();
                for (int i = 0; i < l; i++) {
                    int keyForExist = (l - i - 1) * 2 + 1;
                    int keyForValue = (l - i - 1) * 2 + 2;
                    Meta meta = new Meta(Meta.PART_AUTH, keyForExist, 0);
                    if (!dict.containsKey(meta)) dict.put(meta, dict.size());
                    meta = new Meta(Meta.PART_AUTH, keyForValue, req.getAuthoritySegments().get(i));
                    if (!dict.containsKey(meta)) dict.put(meta, dict.size());
                }
            }

            // Path
            for (int i = 0; i < req.getPathSegments().size(); i++) {
                int keyForExist = 2 * i + 1;
                int keyForValue = 2 * i + 2;
                Meta meta = new Meta(Meta.PART_PATH, keyForExist, 0);
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());
                meta = new Meta(Meta.PART_PATH, keyForValue, req.getPathSegments().get(i));
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());
            }

            // QueryString
            for (String key : qsKeys) {
                String rawValue = req.getQueryStringMapping().get(key);
                Meta meta = new Meta(Meta.PART_QSMD, key, rawValue);
                if (!dict.containsKey(meta)) dict.put(meta, dict.size());
            }
        }

        dict.keySet().stream().forEach(key -> reverseDict.put(dict.get(key), key));
    }

    @Override
    public HttpRequestHandler decode(Transaction tran) {
        Integer method = null;
        String scheme = null;
        Integer port = null;

        Map<Integer, String> authMap = new TreeMap<>();
        Map<Integer, String> pathMap = new TreeMap<>();

        List<String> authoritySegments = new ArrayList<>();
        List<String> pathSegments;
        SortedMap<String, String> queryStringMapping = new TreeMap<>();

        Group infoGroup = tran.headGroup();
        Group authGroup = tran.tailTransaction().headGroup();
        Group pathGroup = tran.tailTransaction().tailTransaction().headGroup();
        Group qsGroup = tran.tailTransaction().tailTransaction().tailTransaction().headGroup();

        for (int x : infoGroup.array) {
            Meta meta = reverseDict.get(x);

            if (meta.key.compareTo(KEY_FOR_METHOD) == 0)
                method = (Integer) meta.value;
            else if (meta.key.compareTo(KEY_FOR_SCHEME) == 0)
                scheme = (String) meta.value;
            else if (meta.key.compareTo(KEY_FOR_PORT) == 0)
                port = (Integer) meta.value;
            else
                assert false;
        }

        for (int x : authGroup.array) {
            Meta meta = reverseDict.get(x);
            int key = (int) meta.key;
            if (key % 2 == 1) {
                if (!pathMap.containsKey((key + 1) / 2)) {
                    authMap.put((key + 1) / 2, null);
                }
            } else {
                authMap.put(key / 2, (String) meta.value);
            }
        }
        authoritySegments = authMap.keySet().stream().map(key -> authMap.get(key)).collect(Collectors.toList());
        authoritySegments = Lists.reverse(authoritySegments);

        for (int x : pathGroup.array) {
            Meta meta = reverseDict.get(x);
            int key = (int) meta.key;
            if (key % 2 == 1) {
                if (!pathMap.containsKey((key + 1) / 2)) {
                    pathMap.put((key + 1) / 2, null);
                }
            } else {
                pathMap.put(key / 2, (String) meta.value);
            }
        }
        pathSegments = pathMap.keySet().stream().map(key -> pathMap.get(key)).collect(Collectors.toList());

        for (int x : qsGroup.array) {
            Meta meta = reverseDict.get(x);
            String key = (String) meta.key;
            String rawValue = (String) meta.key;
            queryStringMapping.put(key, rawValue);
        }

        return new HttpRequestHandler(method, scheme, port, authoritySegments, pathSegments, queryStringMapping);
    }


}
