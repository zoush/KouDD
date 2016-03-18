package activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import type.Type;
import utils.DensityUtil;
import widgh.FollowListView;
import yd.koudd.R;

/**
 * @author timor_du ExpandableListView适配器
 */
public class FollowListViewAdapter extends BaseExpandableListAdapter {
    public static final int ItemHeight = 48;// 每项的高度
    private static final int GROUP_TEXTVIEW = 999;

    private FollowListView toolbarGrid;

    private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

    private Context parentContext;

    private LayoutInflater layoutInflater;

    private SimpleAdapter simperAdapter;

    private int groupposition;

    static public class TreeNode {
        Object parent;
        List<Map<String, Object>> childs = new ArrayList<Map<String, Object>>();
        Object id;

        public void get(String string) {
            // TODO Auto-generated method stub

        }
    }

    public FollowListViewAdapter(Context view) {
        parentContext = view;
    }

    /**
     * 获取treeNodes
     *
     * @return
     */
    public List<TreeNode> GetTreeNode() {
        System.out.println("treeNodes:" + treeNodes);
        return treeNodes;
    }

    /**
     * 更新treeNodes中的数据
     */
    public void UpdateTreeNode(List<TreeNode> nodes) {
        treeNodes = nodes;
    }

    /**
     * 清除treeNodes中的数据
     */
    public void RemoveAll() {
        treeNodes.clear();
    }

    /**
     * 获取组员名
     */
    public Object getChild(int groupPosition, int childPosition) {
        System.out.println("组员名：" + treeNodes.get(groupPosition).childs.get(childPosition));
        return treeNodes.get(groupPosition).childs.get(childPosition);
    }

    /**
     * 返回值必须为1，否则会重复数据
     */
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * 新建TextView
     *
     * @param context
     * @return
     */
    static public LinearLayout getGroupLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 50)));
        layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        TextView textView = new TextView(context);
        textView.setLayoutParams(new AbsListView.LayoutParams(DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context, 50)));
        textView.setId(GROUP_TEXTVIEW);
        Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_right_arrow);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        textView.setCompoundDrawables(null, null, drawable, null);

        textView.setBackgroundColor(context.getResources().getColor(R.color.white));
        textView.setTextColor(context.getResources().getColor(R.color.rule_color));
        textView.setPadding(40, 0, 40, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        layout.addView(textView);
        return layout;
    }

    /**
     * 自定义组员
     */
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.follow_view, null);
        toolbarGrid = (FollowListView) convertView.findViewById(R.id.GridView_toolbar);
        toolbarGrid.setAdapter(getMenuAdapter(groupPosition));// 设置菜单Adapter
        toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                System.out.println("map:" + map);
                System.out.println("我点击的是：" + parent.getItemAtPosition(position));
                //Toast.makeText(parentContext, (String) map.get("itemText"), 0).show();

                System.out.println("id:--->" + (String) map.get("itemText"));


                FollowListViewAdapter.TreeNode node = treeNodes.get(groupPosition);
                System.out.println("parentName:--->" + (String) map.get("name"));
                System.out.println("parentId:--->" + (String) map.get("class_id"));
                Intent intent = new Intent(parentContext, TypeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("merchant_class_id", (String)node.id);
                bundle.putString("merchant_sort_id", (String) map.get("itemId"));
                bundle.putString("typename", (String) map.get("itemText"));
                intent.putExtras(bundle);
                parentContext.startActivity(intent);

            }
        });
        return convertView;
    }

    /**
     * 自定义组
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LinearLayout layout = getGroupLayout(this.parentContext);
        TextView textView = (TextView) layout.findViewById(GROUP_TEXTVIEW);
        textView.setText(getGroup(groupPosition).toString());
        return layout;
    }

    /**
     * 获取组员id
     */
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 获取组名
     */
    public Object getGroup(int groupPosition) {
        return treeNodes.get(groupPosition).parent;
    }

    /**
     * 统计组的长度
     */
    public int getGroupCount() {
        return treeNodes.size();
    }

    /**
     * 获取组id
     */
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 定义组员是否可选
     */
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    /**
     * 构造SimpleAdapter
     */
    private SimpleAdapter getMenuAdapter(int groupPosition) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int len = 0;
        if (treeNodes.get(groupPosition) != null && treeNodes.get(groupPosition).childs != null)
            len = treeNodes.get(groupPosition).childs.size();
        for (int i = 0; i < len; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemText", treeNodes.get(groupPosition).childs.get(i).get("name"));
            map.put("itemId", treeNodes.get(groupPosition).childs.get(i).get("id"));
            data.add(map);
        }
        simperAdapter = new SimpleAdapter(parentContext, data,
                R.layout.follow_items,
                new String[]{"itemText", "itemId"},
                new int[]{R.id.item_text});
        return simperAdapter;
    }

/*
    */
/**
 * 点击监听器
 *//*

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }
*/

}
