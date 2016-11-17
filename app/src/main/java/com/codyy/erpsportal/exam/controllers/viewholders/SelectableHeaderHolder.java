package com.codyy.erpsportal.exam.controllers.viewholders;

import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.media.node.model.TreeNode;


public class SelectableHeaderHolder extends TreeNode.BaseNodeViewHolder<TreeItem> {
    private CheckBox nodeSelector;
    private ImageView ivArrow;
    private static int mSelectCount;

    public SelectableHeaderHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_selectable_header, null, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        ivArrow.setVisibility(View.GONE);
        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setVisibility(View.VISIBLE);
        nodeSelector.setChecked(value.isSelected());
        node.setSelected(value.isSelected());
        tvTitle.setText(value.getClasslevelName());
        nodeSelector.setOnClickListener(new View.OnClickListener() {
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
        nodeSelector.setChecked(node.isSelected());
        nodeSelector.setVisibility(node.isSelectable() ? View.VISIBLE : View.INVISIBLE);
        return view;
    }

    private float mAngle;

    @Override
    public void toggle(boolean active) {
        ivArrow.animate().rotation(mAngle += 180f).setDuration(300L).setInterpolator(new FastOutLinearInInterpolator()).start();
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}
