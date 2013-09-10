package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bpkazemi.Encore.R;

/**
 * Created with IntelliJ IDEA.
 * User: babakpourkazemi
 * Date: 9/7/13
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserSettingsFragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.settings,
                container, false);

        return view;
    }
}