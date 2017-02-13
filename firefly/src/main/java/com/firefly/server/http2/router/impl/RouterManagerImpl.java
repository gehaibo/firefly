package com.firefly.server.http2.router.impl;

import com.firefly.server.http2.SimpleRequest;
import com.firefly.server.http2.router.Matcher;
import com.firefly.server.http2.router.Router;
import com.firefly.server.http2.router.RouterManager;
import com.firefly.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Pengtao Qiu
 */
public class RouterManagerImpl implements RouterManager {

    private AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Matcher.MatchType, List<Matcher>> matcherMap;
    private final Matcher precisePathMather;
    private final Matcher patternPathMatcher;
    private final Matcher regexPathMatcher;
    private final Matcher parameterPathMatcher;
    private final Matcher httpMethodMatcher;
    private final Matcher contentTypePreciseMatcher;
    private final Matcher contentTypePatternMatcher;
    private final Matcher acceptHeaderPreciseMatcher;
    private final Matcher acceptHeaderPatternMatcher;

    public RouterManagerImpl() {
        matcherMap = new HashMap<>();
        precisePathMather = new PrecisePathMatcher();
        patternPathMatcher = new PatternPathMatcher();
        parameterPathMatcher = new ParameterPathMatcher();
        regexPathMatcher = new RegexPathMatcher();
        matcherMap.put(Matcher.MatchType.PATH, new ArrayList<>(Arrays.asList(precisePathMather, patternPathMatcher, parameterPathMatcher, regexPathMatcher)));

        httpMethodMatcher = new HTTPMethodMatcher();
        matcherMap.put(Matcher.MatchType.METHOD, new ArrayList<>(Arrays.asList(httpMethodMatcher)));

        contentTypePreciseMatcher = new ContentTypePreciseMatcher();
        contentTypePatternMatcher = new ContentTypePatternMatcher();
        matcherMap.put(Matcher.MatchType.CONTENT_TYPE, new ArrayList<>(Arrays.asList(contentTypePreciseMatcher, contentTypePatternMatcher)));

        acceptHeaderPreciseMatcher = new AcceptHeaderPreciseMatcher();
        acceptHeaderPatternMatcher = new AcceptHeaderPatternMatcher();
        matcherMap.put(Matcher.MatchType.ACCEPT, new ArrayList<>(Arrays.asList(acceptHeaderPreciseMatcher, acceptHeaderPatternMatcher)));
    }

    public static class RouterMatchResult implements Comparable<RouterMatchResult> {

        private final Router router;
        private final Map<String, String> parameters;
        private final Set<Matcher.MatchType> matchTypes;

        public RouterMatchResult(Router router, Map<String, String> parameters, Set<Matcher.MatchType> matchTypes) {
            this.router = router;
            this.parameters = parameters;
            this.matchTypes = matchTypes;
        }

        public Router getRouter() {
            return router;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public Set<Matcher.MatchType> getMatchTypes() {
            return matchTypes;
        }

        @Override
        public int compareTo(RouterMatchResult o) {
            return router.compareTo(o.getRouter());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouterMatchResult that = (RouterMatchResult) o;
            return Objects.equals(router, that.router);
        }

        @Override
        public int hashCode() {
            return Objects.hash(router);
        }
    }

    public Matcher getHttpMethodMatcher() {
        return httpMethodMatcher;
    }

    public Matcher getPrecisePathMather() {
        return precisePathMather;
    }

    public Matcher getPatternPathMatcher() {
        return patternPathMatcher;
    }

    public Matcher getRegexPathMatcher() {
        return regexPathMatcher;
    }

    public Matcher getParameterPathMatcher() {
        return parameterPathMatcher;
    }

    public Matcher getContentTypePreciseMatcher() {
        return contentTypePreciseMatcher;
    }

    public Matcher getAcceptHeaderPreciseMatcher() {
        return acceptHeaderPreciseMatcher;
    }

    public Matcher getContentTypePatternMatcher() {
        return contentTypePatternMatcher;
    }

    public Matcher getAcceptHeaderPatternMatcher() {
        return acceptHeaderPatternMatcher;
    }

    public SortedSet<RouterMatchResult> findRouter(String method, String path, String contentType, String accept) {
        Map<Router, Set<Matcher.MatchType>> routerMatchTypes = new HashMap<>();
        Map<Router, Map<String, String>> routerParameters = new HashMap<>();
        findRouter(method, Matcher.MatchType.METHOD, routerMatchTypes, routerParameters);
        findRouter(path, Matcher.MatchType.PATH, routerMatchTypes, routerParameters);
        findRouter(contentType, Matcher.MatchType.CONTENT_TYPE, routerMatchTypes, routerParameters);
        findRouter(accept, Matcher.MatchType.ACCEPT, routerMatchTypes, routerParameters);
        Map<Router, Set<Matcher.MatchType>> filtered = filterUnMatched(routerMatchTypes);

        if (filtered.isEmpty()) {
            return Collections.emptySortedSet();
        } else {
            SortedSet<RouterMatchResult> ret = new TreeSet<>();
            filtered.entrySet()
                    .forEach(e -> {
                        Router router = e.getKey();
                        Map<String, String> routerParam = routerParameters.get(router);
                        ret.add(new RouterMatchResult(router, routerParam, e.getValue()));
                    });
            return ret;
        }
    }

    private Map<Router, Set<Matcher.MatchType>> filterUnMatched(Map<Router, Set<Matcher.MatchType>> routerMatchTypes) {
        Map<Router, Set<Matcher.MatchType>> ret = new HashMap<>();
        routerMatchTypes.entrySet()
                        .stream()
                        .filter(e -> e.getKey().getMatchTypes().equals(e.getValue()))
                        .forEach(e -> ret.computeIfAbsent(e.getKey(), k -> new HashSet<>()).addAll(e.getValue()));
        return ret;
    }

    private List<Matcher.MatchResult> findRouter(String value, Matcher.MatchType matchType,
                                                 Map<Router, Set<Matcher.MatchType>> routerMatchTypes,
                                                 Map<Router, Map<String, String>> routerParameters) {
        List<Matcher.MatchResult> r = findRouter(value, matchType);
        r.forEach(result -> result.getRouters().forEach(router -> {
            routerMatchTypes.computeIfAbsent(router, k -> new HashSet<>()).add(result.getMatchType());
            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                routerParameters.computeIfAbsent(router, k -> new HashMap<>())
                                .putAll(result.getParameters().get(router));
            }
        }));
        return r;
    }

    private List<Matcher.MatchResult> findRouter(String value, Matcher.MatchType matchType) {
        if (!StringUtils.hasText(value) || matchType == null) {
            return Collections.emptyList();
        }

        List<Matcher.MatchResult> ret = matcherMap.get(matchType)
                                                  .stream()
                                                  .map(m -> m.match(value))
                                                  .filter(Objects::nonNull)
                                                  .collect(Collectors.toList());
        if (ret == null) {
            return Collections.emptyList();
        } else {
            return ret;
        }
    }

    @Override
    public Router register() {
        return new RouterImpl(idGenerator.getAndIncrement(), this);
    }

    @Override
    public void accept(SimpleRequest request) {

    }
}