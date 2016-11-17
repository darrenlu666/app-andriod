package com.codyy.erpsportal.tutorship.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;

public class TutorialTestNumbersActivity extends AppCompatActivity {

    private final static String EXTRA_SINGLE_COUNT = "com.codyy.erpsportal.SINGLE_COUNT";

    private final static String EXTRA_MULTI_COUNT = "com.codyy.erpsportal.MULTI_COUNT";

    public final static String EXTRA_POSITION = "com.codyy.erpsportal.POSITION";

    private int mSingleChoiceQuestionCount;

    private int mMultiChoiceQuestionCount;

    private int mPosition;

    private TitleBar mTitleBar;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_test_numbers);
        mSingleChoiceQuestionCount = getIntent().getIntExtra(EXTRA_SINGLE_COUNT,0);
        mMultiChoiceQuestionCount = getIntent().getIntExtra(EXTRA_MULTI_COUNT, 0);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mTitleBar.setTitle(getString(R.string.test_number, mPosition + 1
                , mSingleChoiceQuestionCount + mMultiChoiceQuestionCount));
        final int singleTotal = getTypeCount(mSingleChoiceQuestionCount);
        final int multiTotal = getTypeCount(mMultiChoiceQuestionCount);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == singleTotal) {
                    return 5;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new NumberAdapter(singleTotal, multiTotal, mPosition));
    }

    private int getTypeCount(int questionCount) {
        return questionCount!= 0?(1 + questionCount): 0;
    }

    public void onPositionClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
        UIUtils.addExitTranAnim(this);
    }

    private class NumberAdapter extends Adapter{

        private int mCurrPos;

        private int mSingleTotal;

        private int mMultiTotal;

        public NumberAdapter(int singleTotal, int multiTotal, int currentPos) {
            mSingleTotal = singleTotal;
            mMultiTotal = multiTotal;
            mCurrPos = currentPos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_item_index_title, parent, false);
                return new TitleViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_item_index_content, parent, false);
                return new NumberViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder instanceof TitleViewHolder) {
                String title;
                if (mSingleTotal !=0 && position == 0) {
                    title = "单选题";
                } else {
                    title = "多选题";
                }
                ((TitleViewHolder) holder).setTitle(title);
            } else {
                int number;
                if (position >= mSingleTotal) {
                    number = position - mSingleTotal;
                } else {
                    number = position;
                }
                NumberViewHolder numberViewHolder = (NumberViewHolder) holder;
                numberViewHolder.setNumber(number);
                int readPosition;
                if (position > mSingleTotal){
                    readPosition = position - 2;
                } else {
                    readPosition = position - 1;
                }
                numberViewHolder.setHighlight(readPosition == mCurrPos);
                numberViewHolder.setRealPosition(readPosition);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mSingleTotal)
                return 0;
            else
                return 1;
        }

        @Override
        public int getItemCount() {
            return mSingleTotal + mMultiTotal;
        }

        class TitleViewHolder extends ViewHolder {

            private TextView titleTv;

            public TitleViewHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(R.id.tv_title_item_list);
            }

            public void setTitle(String title) {
                titleTv.setText(title);
            }
        }

        class NumberViewHolder extends ViewHolder {

            private Button mNumberBtn;

            public NumberViewHolder(View itemView) {
                super(itemView);
                mNumberBtn = (Button) itemView.findViewById(R.id.btn_item_content);
            }

            public void setNumber(int number) {
                mNumberBtn.setText(Integer.toString(number));
            }

            public void setHighlight(boolean highlight) {
                if (highlight) {
                    mNumberBtn.setBackgroundResource(R.color.main_color);
                } else {
                    mNumberBtn.setBackgroundResource(R.color.work_item_index_color);
                }
            }

            public void setRealPosition(final int realPosition) {
                mNumberBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPositionClick(realPosition);
                    }
                });
            }
        }
    }

    public static void start(Activity activity, int singleChoiceQuestionCount, int multiChoiceQuestionCount, int position) {
        Intent intent = new Intent(activity, TutorialTestNumbersActivity.class);
        intent.putExtra(EXTRA_SINGLE_COUNT, singleChoiceQuestionCount);
        intent.putExtra(EXTRA_MULTI_COUNT, multiChoiceQuestionCount);
        intent.putExtra(EXTRA_POSITION, position);
        activity.startActivityForResult(intent,1);
        UIUtils.addEnterAnim(activity);
    }
}
