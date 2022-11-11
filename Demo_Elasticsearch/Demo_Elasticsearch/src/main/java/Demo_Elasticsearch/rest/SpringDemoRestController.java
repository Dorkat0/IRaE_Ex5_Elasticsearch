package Demo_Elasticsearch.rest;

import org.apache.http.HttpHost;
import org.apache.lucene.queryparser.surround.query.DistanceQuery;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController
public class SpringDemoRestController {


    // initialize REST client

    @GetMapping("/search")
    public ResponseEntity<String> searchQuery(@RequestParam(value = "query", defaultValue = "World") String query , @RequestParam(value = "type", defaultValue = "0") String type ) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));
        SearchRequest req = new SearchRequest("ex5");


                switch (type) {
                    case "0":
                        SearchSourceBuilder builder = new SearchSourceBuilder();
                        builder.query(QueryBuilders.queryStringQuery(query).defaultField("text")
                                .analyzeWildcard(true));
                        builder.size(0);
                        builder.size(100);

                        req.source(builder);
                        break;
                    case "1":
                        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                        boolQueryBuilder.must(
                                QueryBuilders.queryStringQuery(query).defaultField("text")
                                        .analyzeWildcard(true).defaultOperator(Operator.AND));

                        sourceBuilder.query(boolQueryBuilder);
                        sourceBuilder.from(0);
                        sourceBuilder.size(100);
                        req.source(sourceBuilder);
                        break;
                    case "2":
                        SearchSourceBuilder builder1 = new SearchSourceBuilder()
                                .postFilter( QueryBuilders.queryStringQuery(query).defaultField("text").defaultOperator(Operator.AND));

                        SearchRequest searchRequest = new SearchRequest();
                        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
                        searchRequest.source(builder1);
                        builder1.from(0);
                        builder1.size(100);
                        req.source(builder1);
                        break;
                    default:
                        break;
        }



        var result = client.search(req, RequestOptions.DEFAULT).toString();
        client.close();
        return ResponseEntity.ok(result);
        // close REST client




    }


}
