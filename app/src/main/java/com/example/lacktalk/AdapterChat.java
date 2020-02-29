package com.example.lacktalk;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterChat extends BaseAdapter {
    private ArrayList<ItemChat> listViewItemList = new ArrayList<ItemChat>() ;
    final Date now = new Date();

    // ListViewAdapter의 생성자
    public AdapterChat() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_chatroom, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView item_profile = (ImageView) convertView.findViewById(R.id.item_profile) ;
        TextView item_name = (TextView) convertView.findViewById(R.id.item_name) ;
        TextView item_text = (TextView) convertView.findViewById(R.id.item_text) ;
        TextView item_time = (TextView) convertView.findViewById(R.id.item_time) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ItemChat listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        item_profile.setImageBitmap(BitmapFactory.decodeFile(listViewItem.getImagePath()));
        item_profile.setImageResource(R.drawable.defaultimg);

        item_name.setText(listViewItem.getName());
        item_text.setText(listViewItem.getText());
//날짜 계산해서 1시간까진 ~분전 , 하루까진 시간으로, 년도안바뀐데까진 월일, 년도바뀌면 년도월일
        item_time.setText(new SimpleDateFormat("yyyy.MM.dd hh.mm").format(listViewItem.getTime().getTime()));

        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text, Date time) {
        ItemChat item = new ItemChat();

        item.setImagePath(imagePath);
        item.setName(name);
        item.setText(text);
        item.setTime(time);

        listViewItemList.add(item);
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }


}