package com.codyy.erpsportal.exam.controllers.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.media.node.model.TreeNode;


public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<TreeItem> {
    private CheckBox nodeSelector;

    public SelectableItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final TreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_selectable_item, null, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setChecked(value.isSelected());
        node.setSelected(value.isSelected());
        tvTitle.setText(value.getClasslevelName());
        nodeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.setSelected(!node.isSelected());
                value.setSelected(node.isSelected());
                ((TreeItem) node.getParent().getValue()).setSelected(node.isSelected());
                getTreeView().selectNodeParent(node.getParent(), node.isSelected(), true);
            }
        });
        nodeSelector.setChecked(node.isSelected());

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

}
