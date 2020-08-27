package com.runone;

import com.runone.annotation.EsObject;
import com.runone.bean.SimpleNewsInfo;
import com.runone.utils.EsHelper;
import com.runone.vo.NewsInfoVo;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestEs {

    public RestHighLevelClient client;

    public EsHelper esHelper;

    @Before
    public void setUp() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.1.213", 9200, "http")));
        Assert.assertEquals("连接不能为空！", true, client != null);
        this.client = client;
        this.esHelper = new EsHelper();
    }

    @After
    public void closeResource() {
        try {
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCreateIndex() {
        Map data = new HashMap<String, Object>();
        Map<String, Object> name = new HashMap<String, Object>();
        name.put("type", "keyword");
        data.put("name", name);

        Map<String, Object> experience = new HashMap<String, Object>();
        experience.put("type", "text");
        data.put("experience", experience);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("properties", data);

        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        createIndexRequest.index("test01");
        createIndexRequest.mapping("doc", properties);

        createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));

        try {
            CreateIndexResponse response = client.indices().create(createIndexRequest);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testIndex() {
        // 测试创建index
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("user", "zhangsan");
        data.put("postDate", new Date());
        data.put("message", "test elasticsearch!");

        IndexRequest indexRequest = new IndexRequest("runone", "doc");
        indexRequest.source(data);

        try {
            IndexResponse indexResponse = client.index(indexRequest);
            String index = indexResponse.getIndex();
            String type = indexResponse.getType();
            String id = indexResponse.getId();
            long version = indexResponse.getVersion();
            System.out.println("index:\t " + index);
            System.out.println("type:\t " + type);
            System.out.println("id:\t " + id);
            System.out.println("version:\t " + version);
            DocWriteResponse.Result result = indexResponse.getResult();
            System.out.println("索引创建结果:\t" + result);
            Assert.assertEquals("索引创建结果验证", DocWriteResponse.Result.CREATED, result);

            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            int successful = shardInfo.getSuccessful();
            Assert.assertEquals("碎片等于成功创建的碎片数量", successful, shardInfo.getTotal());

            int failed = shardInfo.getFailed();
            Assert.assertEquals("失败的碎片的应该为0", 0, failed);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGet() {

        GetRequest request = new GetRequest("runone", "doc", "1");

        //可选参数设置
        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        String[] includes = new String[]{"message", "*Date"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);

        try {
            GetResponse getResponse = client.get(request);
            String index = getResponse.getIndex();
            String type = getResponse.getType();
            String id = getResponse.getId();
            Map<String, Object> resmap = getResponse.getSourceAsMap();
            String message = (String) resmap.get("message");
            Assert.assertEquals("从索引中获取消息", "test elasticsearch!", message);
            Object postDate = resmap.get("postDate");
            Assert.assertEquals("从索引中取出的日期不为空", true, postDate != null);

            long version = getResponse.getVersion();
            System.out.println("index:\t " + index);
            System.out.println("type:\t " + type);
            System.out.println("id:\t " + id);
            System.out.println("version:\t " + version);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testExist() {
        GetRequest getRequest = new GetRequest(
                "runone",
                "doc",
                "2");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        try {
            boolean exists = client.exists(getRequest);
            Assert.assertEquals("添加进去的索引应该存在!", true, exists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        DeleteRequest request = new DeleteRequest(
                "runone",
                "doc",
                "1");
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        request.setRefreshPolicy("wait_for");
//        request.version(1);
        try {
            DeleteResponse deleteResponse = client.delete(request);

            String index = deleteResponse.getIndex();
            String type = deleteResponse.getType();
            String id = deleteResponse.getId();
            long version = deleteResponse.getVersion();

            System.out.println("index:\t " + index);
            System.out.println("type:\t " + type);
            System.out.println("id:\t " + id);
            System.out.println("version:\t " + version);

            Assert.assertEquals("删除失败的个数应该为0", 0, deleteResponse.getShardInfo().getFailed());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "daily update");
        UpdateRequest request = new UpdateRequest("runone", "doc", "2")
                .doc(jsonMap);
        try {
            UpdateResponse update = client.update(request);
            DocWriteResponse.Result result = update.getResult();
            System.out.println("update result:\t" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //查询更新结果
        GetRequest getRequest = new GetRequest("runone", "doc", "2");

        //可选参数设置
        getRequest.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        String[] includes = new String[]{"message", "*Date"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);

        try {
            GetResponse getResponse = client.get(getRequest);
            Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
            String message = (String) sourceAsMap.get("message");
            Assert.assertEquals("查询更新后的数据", "daily update", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBulkApi() {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("runone01", "doc", "4")
                .source(XContentType.JSON, "field", "foo"));
        request.add(new IndexRequest("runone01", "doc", "5")
                .source(XContentType.JSON, "field", "bar"));
        request.add(new IndexRequest("runone01", "doc", "6")
                .source(XContentType.JSON, "field", "baz"));

        try {
            BulkResponse bulkResponse = client.bulk(request);
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                        || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    System.out.println(indexResponse.getResult());
                } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                    System.out.println(updateResponse.getResult());
                } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                    System.out.println(deleteResponse.getResult());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSearch() {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("message", "daily"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);


        try {
            SearchResponse search = client.search(searchRequest);
            SearchHits hits = search.getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> map = hit.getSourceAsMap();
                String msg = (String) map.get("message");
                String user = (String) map.get("user");
                System.out.println("msg:\t" + msg + ",user:\t" + user);

            }
            System.out.println(search.getClusters());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHighLight() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle =
                new HighlightBuilder.Field("content");
        highlightTitle.highlighterType("unified");
        highlightBuilder.field(highlightTitle);
//        HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
//        highlightBuilder.field(highlightUser);
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style=\"color:red\">");   //高亮设置
        highlightBuilder.postTags("</span>");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("content", "美国"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = null;
        try {
            search = client.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHits hits = search.getHits();
        for (SearchHit hit : hits.getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.size() == 0) {
                continue;
            }
            HighlightField highlight = highlightFields.get("message");
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
            System.out.println("fragmentString:\t" + fragmentString);
        }

    }

    @Test
    public void testEsHelperCreateIndex() {
        boolean res = this.esHelper.createIndex(SimpleNewsInfo.class);
        System.out.println("result:" + res);

    }

    @Test
    public void testIndexDoc() {
        SimpleNewsInfo simpleNewsInfo = new SimpleNewsInfo();
        simpleNewsInfo.setContent("hhhhh");
        simpleNewsInfo.setLabel("test");
        this.esHelper.indexDocument(SimpleNewsInfo.class, simpleNewsInfo);
    }

    @Test
    public void testIndexSearch() {
        this.esHelper.searchAll(SimpleNewsInfo.class);
    }

    @Test
    public void testGethightFields() {
        SimpleNewsInfo simpleNewsInfo = new SimpleNewsInfo();
        simpleNewsInfo.setContent("台湾");
        List<NewsInfoVo> newsInfoVos = this.esHelper.searchNewsInfo(simpleNewsInfo);
    }

    @Test
    public void testClearFile() {
        String packageDirName = "com/runone/bean";
        try {
            Enumeration<URL> resources = this.getClass().getClassLoader().getResources(packageDirName);
            while (resources.hasMoreElements()) {
                String path = resources.nextElement().getPath();
                File dir = new File(path);
                if (dir.isDirectory()) {
                    String[] list = dir.list();
                    for (int i = 0; i < list.length; i++) {
                        if (list[i].endsWith(".class")) {

                        }
                    }
                    System.out.println("扫描到文件夹！");
                } else {
                    System.out.println("未扫描到文件夹！");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Class<?> simpleNewsInfo = this.getClass().getClassLoader().loadClass("com.runone.bean.SimpleNewsInfo");
            EsObject esObject = simpleNewsInfo.getAnnotation(EsObject.class);
            String indexName = esObject.indexName();
            String type = esObject.type();

            System.out.println("indexName:" + indexName);
            System.out.println("type:" + type);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
