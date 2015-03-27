package com.example.spground;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.example.spground.global.Img;
import com.example.spground.internet.CheckInternet;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.*;

public class MainTab01 extends Fragment implements BaseSliderView.OnSliderClickListener
{
	 private SliderLayout mDemoSlider;
	 private View view;
	 private int resImage[]={R.drawable.ic_action_cloudy,R.drawable.ic_action_news,R.drawable.ic_action_alarm,R.drawable.ic_action_calculator,R.drawable.ic_action_home,R.drawable.ic_action_tv,R.drawable.ic_action_alarm,R.drawable.ic_action_alarm,R.drawable.ic_action_alarm,R.drawable.ic_action_alarm};
	 private String resString[]={"大连天气","体育馆新闻","预约提醒","我的运动分析","互动社区","赛事资讯","附近的人","运动指南","校园生活"};
	 protected LinkedHashMap<String, String> url_maps;
	 LayoutInflater inflater;
	 ViewGroup container;
	 Bundle savedInstanceState;
	 public Handler parentHandler=new Handler(){
		 public void handleMessage(Message msg) {
			 if(msg.what==0x1237){
				 String str=msg.obj.toString();
					if(str!=null)
						Toast.makeText(getActivity(),str, Toast.LENGTH_LONG).show();
			 }
		 };
	 };
	 

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater=inflater;
		this.container=container;
		this.savedInstanceState=savedInstanceState;
		this.view=(getLayoutInflater(savedInstanceState)).inflate(R.layout.main_tab_01, null, false);
		mDemoSlider=(SliderLayout)view.findViewById(R.id.slider);
		//从本地加载
		HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
		file_maps.put("Hannibal",R.drawable.hannibal);
		file_maps.put("Big Bang Theory",R.drawable.bigbang);
		file_maps.put("House of Cards",R.drawable.house);
		file_maps.put("Game of Thrones", R.drawable.game_of_thrones);
		//从网络加载
		 url_maps = new LinkedHashMap<String, String>();
		 url_maps.put("学校第十届教职工代表大会第四次会议开幕", "http://www.dlut.edu.cn/_mediafile/testdlut2/2015/03/05/_thumb/2rntyetvov.jpg");
		 url_maps.put("高水平艺术团开考 郭东明校长视察现场", "http://upload.dlut.edu.cn/2015/0310/1425956025882.jpg");
		 url_maps.put("2014.11.28辽宁省各市知识产权局局长参观", "http://chuangxin.dlut.edu.cn/_mediafile/chuangxin/2014/11/28/_thumb/2p5u5s1dh8.jpg");
		 url_maps.put("3月4日大连市委书记唐军视察我院学生开发的3D打印机", "http://chuangxin.dlut.edu.cn/_mediafile/chuangxin/2015/03/09/4ppw8qfo2b.jpg");
		//网络操作，应该放在线程里面的额
		 
		if(Img.img!=null){
			url_maps=Img.img;
			
			
		}
		else{
			System.out.println("为空");
			 //新建一个线程去实时访问img可用不
			if(CheckInternet.isConn(getActivity())){
				//refresh();
			}
			else{
				System.out.println("网络连接不可用");
			}
			 
		}
		    
		 //
		 for(String name : url_maps.keySet()){
			 TextSliderView textSliderView = new TextSliderView(getActivity());//????
			 // initialize a SliderLayout
			 textSliderView
			 .description(name)
			 .image(url_maps.get(name))
			 .setScaleType(BaseSliderView.ScaleType.Fit)
			 .setOnSliderClickListener(this);
			 //add your extra information
			 textSliderView.getBundle()
			 .putString("extra",name);
			 mDemoSlider.addSlider(textSliderView);
			 }
		 mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
		 mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
		 //mDemoSlider.setCustomAnimation(new DescriptionAnimation());
		 mDemoSlider.setCustomAnimation(new ChildAnimationExample());
		 mDemoSlider.setDuration(4000);
		 MyGridView gridView=(MyGridView)view.findViewById(R.id.homegridview);
		 if(gridView!=null){
			 gridView.setAdapter(new MyGridViewAdapter());
			 gridView.setSelector(R.color.blue);
		 }
		
		return this.view;
	}
	//当创建fragment的activi被创建完成后执行
	
@Override
public void onResume() {
	// TODO Auto-generated method stub
	if(view!=null)
	     ((ScrollView) view).smoothScrollTo(0, 0);
	
	super.onResume();
}
	@Override
	public void onSliderClick(BaseSliderView slider) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
	}
	//girdview的适配器
	public class MyGridViewAdapter extends BaseAdapter{
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 9;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view=getActivity().getLayoutInflater().inflate(R.layout.grid_item_view, null);
			//BitmapFactory bF=new BitmapFactory();
			//bF.decodeResource(getResources(), R.drawable.ic_action_alarm);
			((ImageView)view.findViewById(R.id.grid_item_view_imageview)).setImageResource(resImage[position]);
			((TextView)view.findViewById(R.id.grid_item_view_textview)).setText(resString[position]);
			view.setTag(resString[position]);//给view加便签
			view.setOnClickListener(new GridViewOnClickListener());//添加监听事件
			return view;
		}
		
	}
   //监听gridview的点击事件
	class GridViewOnClickListener implements View.OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String tag=v.getTag().toString();
		switch(tag){
		case "大连天气":
			startActivity(new Intent(getActivity(), WebViewActivity.class));
			
			break;
		case "体育馆新闻":
			break;
		case "预约提醒":
			break;
		case "我的运动分析":
			break;
		
		}
	}
		
	}
	

}




