package shuvalov.nikita.songbird;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NikitaShuvalov on 11/18/16.
 */

public class TweetRecyclerAdapater extends RecyclerView.Adapter<TweetHolder> {
    private ArrayList<TweetInfo> mTweets;

    public TweetRecyclerAdapater(ArrayList<TweetInfo> tweets) {
        mTweets = tweets;
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.tweetholder_form,parent, false);
        return new TweetHolder(view);

    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        TweetInfo tweet = mTweets.get(position);
        holder.mBody.setText(tweet.getText());
        holder.mDate.setText(tweet.getCreated_at());
    }


    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
 class TweetHolder extends RecyclerView.ViewHolder{
    TextView mBody,mDate;

    TweetHolder(View itemView) {
        super(itemView);
        mBody = (TextView) itemView.findViewById(R.id.tweet_body);
        mDate = (TextView) itemView.findViewById(R.id.time_view);
    }



}
