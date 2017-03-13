package com.codyy.erpsportal.commons.controllers.fragments.NetTeach;

import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.commons.controllers.fragments.BaseCommentDelegate;
import com.codyy.erpsportal.commons.controllers.fragments.BaseCommentFragment;
import com.codyy.erpsportal.commons.controllers.fragments.SimpleRequestDelegate;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.Comment;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.codyy.erpsportal.commons.models.entities.comment.EvaluationComment;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.erpsportal.groups.utils.SnackToastUtils;
import com.codyy.url.URLConfig;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 * 评课的评论Fragment .
 * Created by poe on 17-3-13.
 */
public class EvaluationCommentFragment extends BaseCommentFragment<EvaluationComment> {
    private final String TAG = "EvaluationCommentFragment";
    private IBaseCommentInterface<EvaluationComment> mIBaseCommentInterface;
    private AssessmentDetails mAssessmentDetails;

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mIBaseCommentInterface = (IBaseCommentInterface<EvaluationComment>) getActivity();
    }

    @Override
    public BlogComposeView getComposeView() {
        return mIBaseCommentInterface.getBlogComposeView();
    }

    @Override
    public BaseCommentDelegate getBaseCommentDelegate() {
        return new BaseCommentDelegate() {
            @Override
            public SimpleRequestDelegate getParentDelegate() {
                return new SimpleRequestDelegate() {
                    @Override
                    public String obtainAPI() {
                        return URLConfig.GET_COMMENT;
                    }

                    @Override
                    public HashMap<String, String> getParams() {
                        int start = getFirstCommentCount(mData);
                        HashMap<String, String> data = new HashMap<>();
                        data.put("uuid", mUserInfo.getUuid());
                        data.put("evaluationId", mAssessmentDetails.getEvaluationId());
                        data.put("start", String.valueOf(start));
                        data.put("end", String.valueOf((start != 0 ? (start + sPageCount - 1) : 3)));
                        return data;
                    }

                    @Override
                    public void parseData(JSONObject response, List dataSource) {
                         Comment.getComment(response, dataSource);
                    }

                    @Override
                    public BaseRecyclerViewHolder getViewHolder(ViewGroup parent) {
                        return null;
                    }

                    @Override
                    public void OnItemClicked(View v, int position, Object data) {
                        if (position > 1) {
                            /*Intent intent = new Intent(getActivity(), EvaluationAllActivity.class);
                            intent.putParcelableArrayListExtra("mComments", mData);
                            intent.putExtra("userInfo", mUserInfo);
                            intent.putExtra("assessmentDetails", mAssessmentDetails);
                            startActivity(intent);
                            UIUtils.addEnterAnim(getActivity());*/
                            SnackToastUtils.toastLong(mRootView,"item clicked !"+position);
                        }
                    }

                    @Override
                    public int getTotal() {
                        return getFirstCommentCount(mData);
                    }
                };
            }

            @Override
            public SimpleRequestDelegate getChildrenDelegate() {
                return new SimpleRequestDelegate() {
                    @Override
                    public String obtainAPI() {
                        return null;
                    }

                    @Override
                    public HashMap<String, String> getParams() {
                        return null;
                    }

                    @Override
                    public void parseData(JSONObject response, List dataSource) {

                    }

                    @Override
                    public BaseRecyclerViewHolder getViewHolder(ViewGroup parent) {
                        return null;
                    }

                    @Override
                    public void OnItemClicked(View v, int position, Object data) {

                    }

                    @Override
                    public int getTotal() {
                        return 0;
                    }
                };
            }

            @Override
            public SimpleRequestDelegate sendCommentDelegate() {
                return null;
            }

            @Override
            public SimpleRequestDelegate sendReplyDelegate() {
                return null;
            }
        };
    }

    /** 接口回调　**/
    public interface IBaseCommentInterface<T> {
        /** 发送文本输入框 **/
        BlogComposeView getBlogComposeView();
    }
}
