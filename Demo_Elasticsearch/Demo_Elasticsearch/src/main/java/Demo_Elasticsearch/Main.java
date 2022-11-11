package Demo_Elasticsearch;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class Main {

	public static void main(String[] args) throws IOException {
		// initialize REST client
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
				new HttpHost("localhost", 9200, "http"),
				new HttpHost("localhost", 9201, "http")));

		// retrieves lastname and address of bank accounts 
		// whose address contains '244' AND 'Place' 
		// the same request in JSON format:
		//	GET /bank/_seach
		//	{
		//	  "query": {
		//	    "multi_match" : {
		//	      "query":      "244 Place",
		//	      "type":       "best_fields",
		//	      "fields":     [ "address" ],
		//	      "operator":   "and" 
		//	    }
		//	}
		SearchRequest req = new SearchRequest("bank");
		SearchSourceBuilder builder = new SearchSourceBuilder();
		QueryBuilder query = QueryBuilders.queryStringQuery("244 Place")
			.defaultOperator(Operator.AND)
			.defaultField("address");
		builder.query(query);
		req.source(builder);
		printResponse(client.search(req,  RequestOptions.DEFAULT));

		// close REST client
		client.close();
	}
	
	private static void printResponse(SearchResponse resp) {
		System.out.println("status: "+resp.status());
		for(SearchHit hit : resp.getHits()) {
			Map<String, Object> source = hit.getSourceAsMap();
			System.out.println("id: "+hit.getId()
			//+"\t score: "+hit.getScore()
			+"\t account: "+source.get("account_number")
			+"\t"+source.get("lastname")+", "+source.get("address"));
		}
		System.out.println("\n--------------------------------------------------------------------");
	}
}
