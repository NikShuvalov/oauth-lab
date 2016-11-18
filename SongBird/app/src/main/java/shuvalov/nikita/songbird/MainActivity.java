package shuvalov.nikita.songbird;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    Button mSearchButton;
    EditText mUserInput;

    BearerTokenResponse mBearerTokenHolder;
    BearerTokenTask mBTtask;

    ArrayList<TweetInfo> mTweetInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mSearchButton = (Button) findViewById(R.id.enter_search);
        mUserInput = (EditText) findViewById(R.id.user_prompt);

        mTweetInfo = new ArrayList<>();


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        String concatentatedKeySecret = AppData.CONSUMER_KEY+":"+AppData.CONSUMER_SECRET;
//        String concatentatedKeySecret = AppData.ACCESS_TOKEN+":"+AppData.ACCESS_TOKEN_SECRET;

        String encodedConsumerKey = new String(Base64.encodeBase64(concatentatedKeySecret.getBytes()));

        if (info!=null && info.isConnected()){
            mBTtask= new BearerTokenTask();
            mBTtask.execute(encodedConsumerKey);
        }else{
            Toast.makeText(this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBTtask!= null && mBTtask.getStatus() == AsyncTask.Status.FINISHED){
                    String query = String.format("?screen_name=%s&count=20",mUserInput.getText().toString());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .addHeader("Authorization", "Bearer "+ mBearerTokenHolder.getAccess_token())
                            .url(AppData.USER_TIMELINE_SCRUB_URL+query)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException();
                            }
                            Gson tweetGson = new Gson();
                            try {
                                JSONArray listOfTweets = new JSONArray(response.body().string());
                                mTweetInfo.clear();
                                for (int i =0; i<listOfTweets.length();i++){
                                    mTweetInfo.add(tweetGson.fromJson(String.valueOf(listOfTweets.getJSONObject(i)),TweetInfo.class));
                                }
                                Log.d("TWEET", mTweetInfo.get(0).getText()+mTweetInfo.get(0).getCreated_at());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }else{
                    Toast.makeText(MainActivity.this, "Hold your horses", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class BearerTokenTask extends AsyncTask<String, Void, BearerTokenResponse>{

        @Override
        protected BearerTokenResponse doInBackground(String... strings) {
            Log.d("MALLET", "Working");
            OkHttpClient okHttpClient = new OkHttpClient();
            Headers header = new Headers.Builder()
                    .add("Authorization"," Basic " +strings[0])
                    .add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .build();
            FormBody body = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .build();
            Request request = new Request.Builder()
                    .post(body)
                    .headers(header)
                    .url("https://api.twitter.com/oauth2/token")
                    .build();
            try {
                Gson gson = new Gson();
                Response response = okHttpClient.newCall(request).execute();
                return gson.fromJson(response.body().string(),BearerTokenResponse.class);

            } catch (Exception e) {
                Log.d("JSON", "Something went wrong");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BearerTokenResponse s) {
            super.onPostExecute(s);
            Log.d("ACCESS TOKEN", s.getAccess_token()+ "What");
            mBearerTokenHolder = s;
        }
    }

}
