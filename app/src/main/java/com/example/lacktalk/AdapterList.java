package com.example.lacktalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterList extends BaseAdapter {
    private ArrayList<ItemList> listViewItemList = new ArrayList<ItemList>();
    private int status;

    public AdapterList(int s){
        status = s;
    }
    private class ViewHolder{
        ImageView picture;
        TextView name,message,first_text;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.picture = convertView.findViewById(R.id.chatlist_profile);
            viewHolder.name = convertView.findViewById(R.id.chatlist_nickname);
            viewHolder.message = convertView.findViewById(R.id.chatlist_profile_massage);
            viewHolder.first_text = convertView.findViewById(R.id.only_for_layout);
            viewHolder.relativeLayout = convertView.findViewById(R.id.only_for_friendlist);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ItemList listViewItem = listViewItemList.get(position);

        viewHolder.name.setText(listViewItem.getName());
        viewHolder.message.setText(listViewItem.getMessage());


        //디폴트 설정
        if(status==0) {
            init_0_status(viewHolder,position);
        }
        switch (status){
            case 0:
                break;
            case 1:
                viewHolder.message.setBackground(null);
                break;
            case 2:
                viewHolder.message.setVisibility(View.GONE);
                viewHolder.picture.setVisibility(View.GONE);

                break;
        }






        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text) {
        listViewItemList.add(new ItemList(imagePath,name,text));
    }

    @Override// 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    public long getItemId(int position) { return position; }
    @Override// 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    public ItemList getItem(int position) { return listViewItemList.get(position) ; }
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }
    public void init_0_status(ViewHolder viewHolder,int position){
        if(0<=position&&position<3){ // 초기화 3개 부문
            viewHolder.picture.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.message.setVisibility(View.GONE);
            viewHolder.first_text.setVisibility(View.GONE);
            viewHolder.relativeLayout.setVisibility(View.GONE);
            if(position==0) viewHolder.first_text.setVisibility(View.VISIBLE);
            else if(position==1) {
                viewHolder.picture.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.VISIBLE);
                viewHolder.message.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.relativeLayout.setVisibility(View.VISIBLE);
                ((TextView) (viewHolder.relativeLayout.findViewById(R.id.sumFriend))).setText(getCount()-3 + "명");
            }
        }else{//이외 부분
            viewHolder.picture.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.message.setVisibility(View.VISIBLE);
            viewHolder.first_text.setVisibility(View.GONE);
            viewHolder.relativeLayout.setVisibility(View.GONE);
        }

    }
}
