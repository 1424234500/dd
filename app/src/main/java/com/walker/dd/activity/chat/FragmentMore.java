package com.walker.dd.activity.chat;
 
import java.util.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.walker.dd.R;
import com.walker.dd.activity.FragmentBase;
import com.walker.dd.util.AndroidTools;

public class FragmentMore extends FragmentBase implements  OnItemClickListener  {
   
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
    	 View view = inflater.inflate(R.layout.layout_more, container, false);
           
    	   gview = (GridView) view.findViewById(R.id.gview);
           //新建List
           listItems = new ArrayList<HashMap<String,Object>>();
           //获取数据
           listItems = getData();
           //新建适配器
           String [] from ={"image","text"};
           int [] to = {R.id.iv,R.id.tv};
           sim_adapter = new SimpleAdapter(getContext(), listItems, R.layout.item_image_text, from, to);
           //配置适配器
           gview.setAdapter(sim_adapter);
           gview.setOnItemClickListener(this);
	     return view;
    }  
	static int ACTIVITY_RESULT_FILE = 1;

    private GridView gview;
    private List<HashMap<String,Object>> listItems;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = { R.drawable.icon_filetype_dir};
    private String[] iconName = { "文件" };

    public List<HashMap<String,Object>> getData(){        
    	List<HashMap<String,Object>> listItems = new ArrayList<HashMap<String,Object>>();

        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            listItems.add(map);
        }
            
        return listItems;
    }
     
    Call call;
    public void setCall(Call call){
    	this.call = call;
    }

    /**
     * 传递引用对象数据初始化
     *
     * @param data
     */
    @Override
    public void setData(Object data) {
        
    }

    /**
     * 更新数据 后 通知更新页面
     */
    @Override
    public void notifyDataSetChanged() {

    }

    /**
     * 数据广播传递 activity通过baseAc收到广播后派发给当前fragment
     *
     * @param msg
     */
    @Override
    public void onReceive(String msg) {

    }

    public interface Call {
    	public void onChoseFile(String path);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int i, long l) {
		if(call != null){
		    HashMap<String,Object> map = listItems.get(i);
            AndroidTools.log(map.toString());
		}
	}

  
}  