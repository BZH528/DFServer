package com.runone.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.runone.annotation.EsObject;
import com.runone.bean.SimpleNewsInfo;
import com.runone.vo.NewsInfoVo;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class EsHelper {

    public RestHighLevelClient client;

    public EsHelper() {
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.1.213", 9200, "http")));
    }

    /**
     * 通过类反射创建索引
     *
     * @param clazz
     * @return
     */
    public boolean createIndex(Class<?> clazz) {

        //判断是否有Es对象注解
        if (!clazz.isAnnotationPresent(EsObject.class)) {
            return false;
        }

        //获得类上的注解
        EsObject esObject = clazz.getAnnotation(EsObject.class);
        String indexName = esObject.indexName();
        String type = esObject.type();
        System.out.println("indexName:" + indexName + "\t" + "type:" + type);
        int replicas = esObject.replicas();
        int shards = esObject.shards();

        Settings.Builder builder = Settings.builder();
        builder.put("index.number_of_shards", shards);
        builder.put("index.number_of_replicas", replicas);

        //获得注解的字段
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Object> data = new HashMap<String, Object>();
        for (Field declaredField : declaredFields) {
            com.runone.annotation.Field field = declaredField.getAnnotation(com.runone.annotation.Field.class);
            if (field == null) {
                continue;
            }
            String fieldType = field.type();
            String name = declaredField.getName();
            Map<String, Object> typeMap = new HashMap<String, Object>();
            typeMap.put("type", fieldType);
            data.put(name, typeMap);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("properties", data);

        System.out.println(JSONObject.toJSONString(properties));

        //组装创建索引请求。
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        createIndexRequest.settings(builder);
        createIndexRequest.index(indexName);
        createIndexRequest.mapping(type, properties);

        try {
            CreateIndexResponse response = client.indices().create(createIndexRequest);
            System.out.println(JSONObject.toJSONString(response));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向索引中添加值。
     *
     * @param clazz
     * @param object
     * @return
     */
    public boolean indexDocument(Class clazz, Object object) {

        Class<?> obj = object.getClass();
        if (!clazz.isAnnotationPresent(EsObject.class)) {
            return false;
        }

        EsObject esObject = (EsObject) clazz.getAnnotation(EsObject.class);
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Object> data = new HashMap<String, Object>();

        for (Field declaredField : declaredFields) {
            com.runone.annotation.Field field = declaredField.getAnnotation(com.runone.annotation.Field.class);
            if (field == null) {
                continue;
            }
            String name = declaredField.getName();
            String subname = name.substring(1);
            char firstChar = Character.toUpperCase(name.charAt(0));
            try {
                Method method = obj.getMethod("get" + firstChar + subname);
                if (method != null) {
                    //反射调用对象方法。
                    Object value = method.invoke(object);
                    data.put(name, value);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(esObject.indexName());
        indexRequest.type(esObject.type());
        indexRequest.source(data);

        try {
            IndexResponse indexResponse = client.index(indexRequest);
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public RestHighLevelClient getClient() {
        return this.client;
    }

    //TODO 搜索
    public <T> List<T> searchAll(Class<T> clazz) {
        EsObject esObject = clazz.getAnnotation(EsObject.class);
        String indexName = esObject.indexName();
        String type = esObject.type();

        List<T> result = new ArrayList<T>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("content", "美国");
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
//        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("content", "美国")).must(QueryBuilders.matchPhraseQuery("summary", "交通"));
        searchSourceBuilder.query(matchAllQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse search = this.client.search(searchRequest);
            SearchHits hits = search.getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> map = hit.getSourceAsMap();
                JSON object = (JSON) JSONObject.toJSON(map);
                T info = JSONObject.toJavaObject(object, clazz);
                result.add(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private SearchSourceBuilder decoradeSearchBuilder(SimpleNewsInfo newsInfo) {

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(true);

        //不显示匹配标签
        highlightBuilder.preTags("");
        highlightBuilder.postTags("");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        JSON o = (JSON) JSONObject.toJSON(newsInfo);
        Map map = JSONObject.toJavaObject(o, Map.class);
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String next = String.valueOf(iterator.next());
            String value = String.valueOf(map.get(next));
            if (value == null || value.equals("") || value.equals("null")) {
                continue;
            }
            highlightBuilder.field(next);
            searchSourceBuilder.query(QueryBuilders.matchPhraseQuery(next, value));
        }

        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        return searchSourceBuilder;
    }

    public List<NewsInfoVo> searchNewsInfo(SimpleNewsInfo newsInfo) {

        EsObject esObject = newsInfo.getClass().getAnnotation(EsObject.class);
        String indexName = esObject.indexName();
        String type = esObject.type();

        List<NewsInfoVo> infos = new ArrayList<NewsInfoVo>();
        SearchSourceBuilder searchSourceBuilder = this.decoradeSearchBuilder(newsInfo);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = null;
        try {
            search = client.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHits hits = search.getHits();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            JSON o = (JSON) JSON.toJSON(map);
            NewsInfoVo newsInfoVo = JSON.toJavaObject(o, NewsInfoVo.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.size() == 0) {
                continue;
            }
            Set<String> keys = highlightFields.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                HighlightField highlightField = highlightFields.get(next);
                Text[] fragments = highlightField.getFragments();
                List<String> list = this.transtoList(fragments);
                newsInfoVo.setMatch_Sentence(list);
            }
            infos.add(newsInfoVo);
        }
        return infos;
    }

    private List<String> transtoList(Text[] fragments) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < fragments.length; i++) {
            list.add(fragments[i].toString());
        }
        return list;
    }

    //根据索引名称删除索引
    public boolean deleteIndex(String indexName) {
        GetIndexRequest existRequest = new GetIndexRequest();
        existRequest.indices(indexName);
        try {
            boolean exists = this.client.indices().exists(existRequest);
            if (exists) {

            }
            DeleteIndexRequest request = new DeleteIndexRequest();
            request.indices(indexName);
            try {
                this.client.indices().delete(request);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isExistIndex(String indexName) {
        GetIndexRequest existRequest = new GetIndexRequest();
        existRequest.indices(indexName);
        try {
            return client.indices().exists(existRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
