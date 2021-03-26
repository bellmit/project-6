package com.miguan.recommend.common.es;

import com.alibaba.fastjson.JSON;
import com.miguan.recommend.common.config.EsConfig;
import com.miguan.recommend.common.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 */
@Slf4j
@Component
public class EsDao {
    @Resource
    private EsConfig esConfig;

    public static final String INDEX_NAME = "embedding";

    public static final String CREATE_INDEX = "{\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"userId\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"url\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"index\": true,\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      }\n" +
            "    }\n" +
            "  }";

    public static RestHighLevelClient client = null;


    /**
     * 预创建 index.虽然 es 在插入数据时会自动根据字段类型来创建字段定义，但是自动创建并不总是和需要相符的，比如想让某个字段不分词，
     * 或者使用其他的分词器。所以在代码中先判断 index(es7 中已经废弃了 mapping，也就是一个 index 相当于一个表)是否存在，如果不存在就创建 index.
     */
    @PostConstruct
    public void init() {
        try {
            if (client != null) {
                client.close();
            }
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(esConfig.getHost(), esConfig.getPort(), esConfig.getScheme())));
            if (this.indexExist(INDEX_NAME)) {
//                return;
            }
            if (!this.indexExist(esConfig.getVideo_title())) {
                log.debug("视频标题索引不存在,开始创建索引");
                CreateIndexRequest request = new CreateIndexRequest(esConfig.getVideo_title());
                client.indices().create(request, RequestOptions.DEFAULT);
             }

            //初始化索引
//            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
//            request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
//            request.mapping(CREATE_INDEX, XContentType.JSON);
//            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
//            if (!res.isAcknowledged()) {
//                throw new RuntimeException("初始化失败");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Description: 判断某个index是否存在
     *
     * @param index index名
     * @return boolean
     */
    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * Description: 插入/更新一条记录
     *
     * @param index  index
     * @param entity 对象
     */
    public void insertOrUpdateOne(String index, EsEntity entity) {
        IndexRequest request = new IndexRequest(index);
        request.id(entity.getId());
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 批量插入数据
     *
     * @param index index
     * @param list  带插入列表
     */
    public void insertBatch(String index, List<EsEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 批量删除
     *
     * @param index  index
     * @param idList 待删除列表
     */
    public <T> void deleteBatch(String index, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(index, item.toString())));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 搜索
     *
     * @param index   index
     * @param builder 查询参数
     * @param c       结果类对象
     * @return java.util.ArrayList
     */
    public <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 删除index
     *
     * @param index index
     * @return void
     */
    public void deleteIndex(String index) {
        try {
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: delete by query
     *
     * @param index   index
     * @param builder builder
     */
    public void deleteByQuery(String index, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据用户向量数据，在es中用余弦相似度函数查询出相似度最高的近800个视频
     *
     * @param vector
     * @return
     */
    @Cacheable(cacheNames = "embedding_video", cacheManager = "getCacheManager")
    public String findVideoByVideoEmbeddingVector(String vector) {
        String queryBody = "";
        String url = "http://%s:%s/video_embedding/_search";
        try {
            queryBody = "{\n" +
                    "  \"from\" : 0, \n" +
                    "  \"size\" : 800,\n" +
                    "  \"query\": {\n" +
                    "    \"script_score\": {\n" +
                    "      \"query\": {\n" +
                    "        \"match_all\": {}\n" +
                    "      },\n" +
                    "      \"script\": {\n" +
                    "        \"source\": \"1.0-cosineSimilarity(params.queryVector, doc['vector'])\",\n" +
                    "        \"params\": {\n" +
                    "          \"queryVector\": %s\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            queryBody = String.format(queryBody, vector);
            url = String.format(url, esConfig.getHost(), esConfig.getPort());
            return HttpUtil.doPost(url, queryBody);
        } catch (Exception e) {
            log.error("请求ES异常：URL->" + url + "请求queryBody:" + queryBody);
            return "";
        }
    }

    /**
     * ES新增或更新图像特征向量
     * @param videoId
     * @param title
     * @param imgUrl
     * @param imgVector
     */
    public void saveEsImgVector(Integer videoId, String title, String imgUrl, String imgVector) {
        String url = "";
        String json = "";
        try {
            log.info("ES新增或更新图像特征向量");
            StringBuffer queryBody = new StringBuffer();
            queryBody.append("{");
            queryBody.append("  \"video_id\" : %d,");
            queryBody.append("  \"title\": \"%s\",");
            queryBody.append("  \"bsy_img_url\": \"%s\",");
            queryBody.append("  \"img_vector\" : %s");
            queryBody.append("}");
            url = String.format("http://%s:%s/img_vector/_doc/%d", esConfig.getHost(), esConfig.getPort(), videoId);
            json = String.format(queryBody.toString(), videoId, title, imgUrl, imgVector);
            HttpUtil.doPost(url, json);
        } catch (Exception e) {
            log.error("ES新增或更新图像特征向量异常", e);
            log.error("ES新增或更新图像特征向量url:{}，参数:{}", url, json);
        }
    }
}
