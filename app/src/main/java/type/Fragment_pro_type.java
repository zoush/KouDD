package type;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import activity.TypeActivity;
import bean.SortBean;
import bean.TypeBean;
import yd.koudd.R;


public class Fragment_pro_type extends Fragment {
    private ArrayList<SortBean> list;
    private ImageView hint_img;
    private GridView listView;
    private Pro_type_adapter adapter;
    private Type type;
    private ProgressBar progressBar;
    private String typename;
    private TypeBean typebean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pro_type, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        hint_img = (ImageView) view.findViewById(R.id.hint_img);
        listView = (GridView) view.findViewById(R.id.listView);
        typename = getArguments().getString("typename");
        ((TextView) view.findViewById(R.id.toptype)).setText(typename);
        GetTypeList();
        adapter = new Pro_type_adapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), TypeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("class_id", typebean.getClass_id());
                bundle.putString("sort_id", list.get(arg2).getSort_id());
                bundle.putString("typename", list.get(arg2).getSort_name());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }


    private void GetTypeList() {
        typebean = (TypeBean) getArguments().getSerializable("type");
        SortBean[] sortBeans = typebean.getSort_list();
        list = new ArrayList<SortBean>();
        for (int i = 0; i < sortBeans.length; i++) {
            list.add(sortBeans[i]);
        }
        progressBar.setVisibility(View.GONE);
    }


}
