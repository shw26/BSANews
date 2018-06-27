package group1.tcss450.uw.edu.bsanews.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import group1.tcss450.uw.edu.bsanews.ArticleDO;

/**
 * News object
 * Created by jnbui on 2/22/2017.
 */

public class News implements Serializable {
    /**
     * Name of news from Json.
     */
    private String mName;
    /**
     * Url of news JsonObject.
     */
    private String mUrl;
    /**
     * Description of news.
     */
    private String mDescription;
    /**
     * Image URL.
     */
    private String mImageUrl;

    /**
     * takes in a jsonobject.
     * @param json
     * @throws JSONException
     */
    public News(JSONObject json)  throws JSONException{
        create(json);
//        getNews(json);
    }

    /**
     * create News Obj by ArticleDO.
     * @param articleDO
     */
    public News(ArticleDO articleDO){
        createByArticleDO(articleDO);
    }

    /**
     * helper function to create News Obj by ArticleDO.
     * @param articleDO
     */
    private void createByArticleDO(ArticleDO articleDO){
        mName = articleDO.getTitle();
        mUrl = articleDO.getUrl();
        mDescription = articleDO.getDescription();
        mImageUrl = articleDO.getUrlToImage();
    }

    /**
     * parse the jsonobject.
     * include name, url, description, image url.
     * @param json
     * @throws JSONException
     */
    private void create(JSONObject json) throws JSONException{
            mName = json.getString("title");
            mUrl = json.getString("url");
            mDescription = json.getString("description");
            mImageUrl = json.getString("urlToImage");
//            if(json.has("image")) {
//                JSONObject temp = json.getJSONObject("image");
//                temp = temp.getJSONObject("thumbnail");
//                mImageUrl = temp.getString("contentUrl");
//            }

    }

    /**
     * @return name of news.
     */
    public String getName(){return mName;}

    /**
     * @return URl of news.
     */
    public String getUrl() {return mUrl;}

    /**
     * @return description of news
     */
    public String getDescription() {return mDescription;}

    /**
     * @return image URL
     */
    public String getImageUrl() {return mImageUrl;}
}
