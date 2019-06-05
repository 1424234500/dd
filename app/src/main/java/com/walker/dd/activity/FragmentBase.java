package com.walker.dd.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walker.common.util.Bean;
import com.walker.dd.R;
import com.walker.dd.adapter.AdapterLvSession;
import com.walker.dd.util.AndroidTools;

import java.util.ArrayList;
import java.util.List;

public abstract class FragmentBase extends Fragment {

    /**
     * 更新数据
     */
    public abstract void notifyDataSetChanged();

    /**
     * 初始化数据
     */
    public abstract void init();


    /**
     * 修改数据
     */

    

}
