package com.codyy.erpsportal.exam.controllers.viewholders;

import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.media.node.model.TreeNode;


public class SelectableHeaderTitleHolder extends TreeNode.BaseNodeViewHolder<TreeItem> {
    private ImageView ivArrow;

    public SelectableHeaderTitleHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_selectable_title_header, null, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        final CheckBox mNodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        mNodeSelector.setVisibility(View.VISIBLE);
        //mNodeSelector.setChecked(value.isSelected());
        //node.setSelected(value.isSelected());
        mNodeSelector.setSelected(false);
        tvTitle.setText(value.getClasslevelName());
        mNodeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.setSelected(!node.isSelected());
                value.setSelected(node.isSelected());
                for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, node.isSelected());
                    ((TreeItem) n.getValue()).setSelected(node.isSelected());
                }
            }
        });
        return view;
    }

    private float mAngle;

    @Override
    public void toggle(boolean active) {
        ivArrow.animate().rotation(mAngle += 180f).setDuration(300L).setInterpolator(new FastOutLinearInInterpolator()).start();
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
       /* mNodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        mNodeSelector.setChecked(mNode.isSelected());*/
    }
}
