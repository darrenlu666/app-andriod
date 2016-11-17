package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.Comment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 2015/4/21.
 */
public class CommentAdapter extends BaseAdapter {
    private List<Comment> comments;
    private Context context;
    private String type;
    private String uuid;
    private boolean isShow;
    private String state;

    public CommentAdapter(Context context, List<Comment> comments, String type, String uuid, boolean isShow, String state) {
        this.context = context;
        this.comments = comments;
        this.type = type;
        this.uuid = uuid;
        this.isShow = isShow;
        this.state = state;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Comment comment = comments.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.evaluation_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.heardimage = (SimpleDraweeView) convertView.findViewById(R.id.evaluation_list_item_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.evaluation_list_item_text_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.evaluation_list_item_text_time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.evaluation_list_item_text_cont);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.evaluation_list_item_tatingbar);
            viewHolder.score = (TextView) convertView.findViewById(R.id.evaluation_list_item_myscore);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(comment.getRealName());
        viewHolder.time.setText(comment.getFormattedTime());
        viewHolder.content.setText(comment.getCommentContent());
        viewHolder.ratingBar.setNumStars(5);
        viewHolder.ratingBar.setStepSize(0.5f);
        if (comment.getShowScore() >= 0) {
            if ((uuid.equals(comment.getBaseUserId()) || isShow) && !"PROGRESS".equals(state)) {
                if (type.equals("star")) {
                    viewHolder.score.setVisibility(View.GONE);
                    viewHolder.ratingBar.setVisibility(View.VISIBLE);
                    double a = Math.rint(comment.getShowScore()) / 2;
                    viewHolder.ratingBar.setRating((float) a);
                } else {
                    viewHolder.score.setVisibility(View.VISIBLE);
                    viewHolder.ratingBar.setVisibility(View.GONE);
                    viewHolder.score.setText(comment.getShowScore() + "分");
                }
            } else {
                viewHolder.score.setVisibility(View.GONE);
                viewHolder.ratingBar.setVisibility(View.GONE);
            }
        } else {
            viewHolder.score.setVisibility(View.GONE);
            viewHolder.ratingBar.setVisibility(View.GONE);
        }
        ImageFetcher.getInstance(context).fetchSmall(viewHolder.heardimage, comment.getHeadPic());
//        viewHolder.heardimage.setImageURI(Uri.parse(comment.getHeadPic()));
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView heardimage;
        TextView name;
        TextView time;
        RatingBar ratingBar;
        TextView score;
        TextView content;
    }
}
