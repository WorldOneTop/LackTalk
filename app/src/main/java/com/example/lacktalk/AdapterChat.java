package com.example.lacktalk;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterChat extends BaseAdapter {
    private ArrayList<ItemChat> listViewItemList = new ArrayList<ItemChat>() ;
    String[] now_arr;
    // ListViewAdapter의 생성자
    public AdapterChat() {//채팅리스트인지 채팅방안인지 구분 필요
        now_arr = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()).split("/");

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_chatroom, parent, false);
        }
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ItemChat listViewItem = listViewItemList.get(position);
        ImageView item_profile = (ImageView) convertView.findViewById(R.id.item_profile);
        TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
        TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
        TextView item_time = (TextView) convertView.findViewById(R.id.item_time);
        if(!listViewItem.getIsme()) {
            convertView.findViewById(R.id.listview_left).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_right).setVisibility(View.GONE);
        }
        else{
            item_text = (TextView) convertView.findViewById(R.id.item_textR);
            item_time = (TextView) convertView.findViewById(R.id.item_timeR);
            convertView.findViewById(R.id.listview_right).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_left).setVisibility(View.GONE);
        }

        // 아이템 내 각 위젯에 데이터 반영
//        item_profile.setImageBitmap(BitmapFactory.decodeFile(listViewItem.getImagePath()));

        item_name.setText(listViewItem.getName());
        item_text.setText(listViewItem.getText());
//설정이 계속 유지되는 듯 그래서 상태 바뀔때마다 바꾼것 모두 재설정
        //프사및닉넴 및 꼬퉁이 전용
        if(position==0 || listViewItemList.get(position-1).getIsme() != listViewItem.getIsme()){//처음이거나 한사람의 말의 시작
            item_profile.setImageResource(R.drawable.defaultimg);
            item_profile.setMaxHeight(3000);
            item_profile.setVisibility(View.VISIBLE);
            item_text.setBackgroundResource(listViewItem.getIsme() ? R.drawable.rightchat : R.drawable.leftchat);
            item_name.setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_interval).setVisibility(View.VISIBLE);
        }
        else{
            item_profile.setVisibility(View.INVISIBLE);
            item_profile.setMaxHeight(0);
            item_text.setBackgroundResource(R.drawable.chat);
            item_name.setVisibility(View.GONE);
        }
        //시간 전용
        if(position+1 == listViewItemList.size()){//마지막이니 시간 무조건, out of index 방지
            item_time.setText(dateTypeChange(listViewItem.getTime()));
            item_time.setVisibility(View.VISIBLE);
        }
        else if(listViewItemList.get(position+1).getIsme() != listViewItem.getIsme()){//다음 말이 다른사람이 말했을때
            item_time.setText(dateTypeChange(listViewItem.getTime()));
            item_time.setVisibility(View.VISIBLE);
        }
        else if(listViewItemList.get(position+1).getTime().equals(listViewItem.getTime())) {//다음 시간이랑 지금이랑 같으면 지금꺼안보이게
            item_time.setVisibility(View.GONE);

        }
        else {//위아래시간이 달랐을때
            item_time.setText(dateTypeChange(listViewItem.getTime()));
            item_time.setVisibility(View.VISIBLE);
        }






        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text, String time,boolean isme) {
        ItemChat item = new ItemChat(imagePath, name, text, time, isme);

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
    //날짜 계산해서 1시간까진 ~분전 , 하루까진 시간으로, 년도안바뀐데까진 월일, 년도바뀌면 년도월일
    public String dateTypeChange(String date){
        String[] str = date.split("/");

        if(!now_arr[0].equals(str[0]))
            return str[0]+"."+str[1]+"."+str[2];
        else if(!now_arr[2].equals(str[2]) && !now_arr[1].equals(str[1]))
            return str[1]+"월 "+str[2]+"일";
        else {
            int temp = Integer.parseInt(str[3]);
            if(temp > 11)
                return (temp != 12 ? temp-12 : temp)+":"+str[4] + " pm"  ;
            else
                return (temp != 0 ? temp : 12)+":"+str[4] + " am"  ;
        }

    }


}