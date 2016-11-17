package com.codyy.erpsportal.exam.controllers.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.commons.utils.CharUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eachann on 2016/1/15.
 */
@SuppressWarnings("ALL")
public class SwitchTopicDialog extends DialogFragment {
    public static final String TAG = SwitchTopicDialog.class.getSimpleName();
    private List<ItemInfoClass> mData;
    public static final String ARG_KEY = "ITEM";
    private static final float DIALOG_HEIGHT = 380f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = switchList(getArguments().<ItemInfoClass>getParcelableArrayList(ARG_KEY));
    }

    private int getActivityWindowHeight() {
        return getActivity().getWindow().getDecorView().getHeight();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_item_index_dialog, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        TextView textView = (TextView) view.findViewById(R.id.tv_close);
        Dialog dialog = new Dialog(getActivity(), R.style.input_dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = getActivityWindowHeight() / 2;
        window.setAttributes(lp);
        RecyclerView.Adapter mAdapter = new ItemIndexListRecyAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return CharUtils.strIsEnglish(mData.get(position).getWorkItemType().replace("_", "")) ? 1 : gridLayoutManager.getSpanCount();
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(true);
        return dialog;
    }

    public class ItemIndexListRecyAdapter extends ItemIndexListRecyBaseAdapter<ItemInfoClass> {

        public ItemIndexListRecyAdapter(Context context, List<ItemInfoClass> list) {
            super(context, list);
        }

        @Override
        public int getItemType(int position) {
            return CharUtils.strIsEnglish(mData.get(position).getWorkItemType().replace("_", "")) ? TYPE_CONTENT : TYPE_TITLE;
        }

        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.item_work_item_index_content;
        }

        @Override
        protected RecyclerView.ViewHolder createViewHolder(View view, int viewType) {
            return viewType == TYPE_TITLE ? new ItemTitleViewHolder(view) : new ItemIndexViewHolder(view);
        }
    }

    public static class ItemTitleViewHolder extends RecyclerViewHolder<ItemInfoClass> {
        private TextView textView;

        public ItemTitleViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            textView = (TextView) view.findViewById(R.id.tv_title_item_list);
        }

        @Override
        public void setDataToView(ItemInfoClass data) {
            textView.setText(data.getWorkItemType());
        }
    }

    public static class ItemIndexViewHolder extends RecyclerViewHolder<ItemInfoClass> {
        private Button button;

        public ItemIndexViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            button = (Button) view.findViewById(R.id.btn_item_content);
        }

        @Override
        public void setDataToView(final ItemInfoClass data) {
            button.setText(String.valueOf(data.getWorkItemIndex()));
            button.setBackgroundColor(data.getColor() != 0 ? data.getColor() : Color.rgb(236, 236, 236));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(data.getWorkItemIndex());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private static OnItemClickListener mOnItemClickListener;


    @Nullable
    private ArrayList<ItemInfoClass> switchList(ArrayList<ItemInfoClass> list) {
        ArrayList<ItemInfoClass> newList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ItemInfoClass itemInfoClass = list.get(i);
                if (i == 0) {
                    ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                    newList.add(titleItemInfo);
                    newList.add(itemInfoClass);
                } else {
                    if (!itemInfoClass.getWorkItemType().equals(list.get(i - 1).getWorkItemType())) {
                        ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                        newList.add(titleItemInfo);
                        newList.add(itemInfoClass);
                    } else
                        newList.add(itemInfoClass);
                }
            }
            return newList;
        }
        return null;
    }

    private ItemInfoClass changeItemType(String workItemType) {
        ItemInfoClass titleItemInfo = new ItemInfoClass();
        switch (workItemType) {
            case TaskFragment.TYPE_SINGLE_CHOICE:
                titleItemInfo.setWorkItemType(getString(R.string.type_single_choice));
                break;
            case TaskFragment.TYPE_MULTI_CHOICE:
                titleItemInfo.setWorkItemType(getString(R.string.type_multi_choice));
                break;
            case TaskFragment.TYPE_JUDGEMENT:
                titleItemInfo.setWorkItemType(getString(R.string.type_judgement));
                break;
            case TaskFragment.TYPE_FILL_IN_BLANK:
                titleItemInfo.setWorkItemType(getString(R.string.type_fill_in_blank));
                break;
            case TaskFragment.TYPE_ASK_ANSWER:
                titleItemInfo.setWorkItemType(getString(R.string.type_ask_answer));
                break;
            case TaskFragment.TYPE_COMPUTING:
                titleItemInfo.setWorkItemType(getString(R.string.type_computing));
                break;
            case TaskFragment.TYPE_TEXT:
                titleItemInfo.setWorkItemType(getString(R.string.type_text));
                break;
            case TaskFragment.TYPE_FILE:
                titleItemInfo.setWorkItemType(getString(R.string.type_file));
                break;
        }
        return titleItemInfo;
    }
}
