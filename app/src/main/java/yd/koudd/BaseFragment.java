package yd.koudd;


import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by zoushaohua on 2016/1/20.
 * qq:756350775
 */
public class BaseFragment extends Fragment {
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_in_from_right, R.anim.hold);
    }
}
