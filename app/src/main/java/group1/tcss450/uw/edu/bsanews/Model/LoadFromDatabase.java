package group1.tcss450.uw.edu.bsanews.Model;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import group1.tcss450.uw.edu.bsanews.ArticleDO;
import group1.tcss450.uw.edu.bsanews.AuthenticatorActivity;
import group1.tcss450.uw.edu.bsanews.NewsViewActivity;
import group1.tcss450.uw.edu.bsanews.R;

/**
 * provide service for loading news from database.
 * template provided by instructor.
 * @author Shao-Han Wang
 */
public class LoadFromDatabase extends AsyncTask<String, Void, List<ArticleDO>> {
    private final String SERVICE = "_load.php";

    /**
     * get the activity using this class for showing Toast Msg.
     */
    private AppCompatActivity mActivity;
    /**
     * the key for getting the username.
     */
    private static final String KEY_USERNAME = "USERNAME";
    /**
     * key for receiving News object.
     */
    private final static String NEWS_KEY = "news";
    private String mUsername;

    // Declare a DynamoDBMapper object
    private DynamoDBMapper dynamoDBMapper;


    /**
     * constructor, takes a activity as argument for showing toast,
     * and textView for showing result.
     * @param activity
     */
    public LoadFromDatabase(AppCompatActivity activity, String username){
        mActivity = activity;
        mUsername = username;

        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AuthenticatorActivity.credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AuthenticatorActivity.configuration)
                .build();
    }

    @Override
    protected List<ArticleDO> doInBackground(String... strings){
        List<ArticleDO> result = getArticlesFromDynamoDB();
        return result;
    }

    @Override
    protected void  onPostExecute(List<ArticleDO> result){
        if(result == null) {
            Toast.makeText(mActivity.getApplicationContext(), "unable ", Toast.LENGTH_LONG)
                    .show();
            return;
        }else{
            final News[] newses = convertArticlesToNews(result);
            final News[] tempNewses = newses;
            NewsListAdapter adapter = new NewsListAdapter(mActivity, newses);
            ListView mListView = (ListView) mActivity.findViewById(R.id.load_ListView);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Intent intent = new Intent(mActivity, NewsViewActivity.class);
                    intent.putExtra(KEY_USERNAME, mUsername);
                    intent.putExtra(NEWS_KEY, tempNewses[position]);
                    intent.putExtra("Activity", "load");
                    mActivity.startActivity(intent);

                }
            });
        }


    }

    private List<ArticleDO> getArticlesFromDynamoDB(){
        ArticleDO template = new ArticleDO();
        template.setUserId(IdentityManager.getDefaultIdentityManager().getCachedUserID());
        DynamoDBQueryExpression<ArticleDO> queryExpression;
        queryExpression = new DynamoDBQueryExpression<ArticleDO>().withHashKeyValues(template);
        List<ArticleDO> result = null;
        try {
            result = dynamoDBMapper.query(ArticleDO.class, queryExpression);
        }catch (Exception e){
            String response = "Unable to connect, Reason: "
                    + e.getMessage();
            Log.e("LoadFromDatabase.java getArticlesFromDynamoDB", response);
        }
        return result;
    }

    private News[] convertArticlesToNews(List<ArticleDO> articles){

        Iterator<ArticleDO> iterator = articles.iterator();
        News[] newses = new News[articles.size()];
        int i = 0;
        while(iterator.hasNext()){
            final ArticleDO articleDO = iterator.next();
            newses[i] = new News(articleDO);
            i++;
        }


        return newses;
    }


//    @Override
//    protected String doInBackground(String... strings) {
//        if (strings.length != 2) {
//            throw new IllegalArgumentException("two String arguments required.");
//        }
//        String response = "";
//        HttpURLConnection urlConnection = null;
//        String url = strings[0];
//        try {
//            URL urlObject = new URL(url + SERVICE);
//            urlConnection = (HttpURLConnection) urlObject.openConnection();
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
//            //my_name=username&my_pw=password
//            String data = URLEncoder.encode("my_name", "UTF-8")
//                    + "=" + URLEncoder.encode(strings[1], "UTF-8");
//            wr.write(data);
//            wr.flush();
//
//            InputStream content = urlConnection.getInputStream();
//
//            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
//            String s = "";
//            while ((s = buffer.readLine()) != null) {
//                response += s;
//            }
//        } catch (Exception e) {
//            response = "Unable to connect, Reason: "
//                    + e.getMessage();
//        } finally {
//            if (urlConnection != null)
//                urlConnection.disconnect();
//        }
//        return response;
//    }



//    @Override
//    protected void onPostExecute(String result) {
//        // Something wrong with the network or the URL.
//        if (result.startsWith("Unable to")) {
//            Toast.makeText(mActivity.getApplicationContext(), result, Toast.LENGTH_LONG)
//                    .show();
//
//            return;
//        }else {
//            News[] newses = new News[0];
//            try {
//                JSONObject resultObj = new JSONObject(result);
//                JSONArray value = resultObj.getJSONArray("value");
//                newses = new News[value.length()];
//                for (int i = 0; i < value.length(); i++){
//                    JSONObject oneNews = (JSONObject) value.get(i);
//                    Log.d("LoadFromDB one", oneNews.getString("url"));
//                    newses[i] = new News(oneNews);
//                }
//
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//
//            final News[] tempNewses = newses;
//            NewsListAdapter adapter = new NewsListAdapter(mActivity, newses);
//            ListView mListView = (ListView) mActivity.findViewById(R.id.load_ListView);
//            mListView.setAdapter(adapter);
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, final View view,
//                                        int position, long id) {
//                    Intent intent = new Intent(mActivity, NewsViewActivity.class);
//                    intent.putExtra(KEY_USERNAME, mUsername);
//                    intent.putExtra(NEWS_KEY, tempNewses[position]);
//                    intent.putExtra("Activity", "load");
//                    mActivity.startActivity(intent);
//
//                }
//            });
//        }
//
//    }




}
