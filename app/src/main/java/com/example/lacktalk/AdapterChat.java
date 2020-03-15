package com.example.lacktalk;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterChat extends BaseAdapter implements Filterable {
    private ArrayList<ItemChat> listViewItemList = new ArrayList<ItemChat>() ;
    String[] now_arr;
    // ListViewAdapter의 생성자
    public AdapterChat() {//채팅리스트인지 채팅방안인지 구분 필요

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
        now_arr = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()).split("/");

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

        //이 밑 세개는 날짜 보여주기 위해서
        LinearLayout layout_date = convertView.findViewById(R.id.layout_date);
        TextView textview_date = convertView.findViewById(R.id.textview_date);
        layout_date.setVisibility(View.GONE);

        if(!listViewItem.getIsme()) {       // 상대방이 얘기했을때 필요한 뷰 선언
            convertView.findViewById(R.id.listview_left).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_right).setVisibility(View.GONE);
        }
        else{               //내가얘기했을때 필요한 뷰 선언
            item_text = (TextView) convertView.findViewById(R.id.item_textR);
            item_time = (TextView) convertView.findViewById(R.id.item_timeR);
            convertView.findViewById(R.id.listview_right).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_left).setVisibility(View.GONE);
        }


        item_name.setText(listViewItem.getName());
        item_text.setText(listViewItem.getText());
//설정이 계속 유지되는 듯 그래서 상태 바뀔때마다 바꾼것 모두 재설정

        if(position==0 || !listViewItemList.get(position-1).getTime().substring(0,10).equals(listViewItem.getTime().substring(0,10))){//첫대화이거나 전 대화와 날짜가달랐을때
            textview_date.setText(dateTypeChange(listViewItem.getTime(),true)) ;
            layout_date.setVisibility(View.VISIBLE);

            item_profile.setImageResource(R.drawable.defaultimg);
            item_profile.setMaxHeight(3000);
            item_profile.setVisibility(View.VISIBLE);
            item_text.setBackgroundResource(listViewItem.getIsme() ? R.drawable.rightchat : R.drawable.leftchat);
            item_name.setVisibility(View.VISIBLE);
        }
        //프사및닉넴 및 꼬퉁이 전용 값설정  및 날짜도
        else if(listViewItemList.get(position-1).getIsme() != listViewItem.getIsme()){//처음이거나 한사람의 말의 시작
            item_profile.setImageResource(R.drawable.defaultimg);
            item_profile.setMaxHeight(3000);
            item_profile.setVisibility(View.VISIBLE);
            item_text.setBackgroundResource(listViewItem.getIsme() ? R.drawable.rightchat : R.drawable.leftchat);
            item_name.setVisibility(View.VISIBLE);
        }
        else{
            item_profile.setVisibility(View.INVISIBLE);
            item_profile.setMaxHeight(0);
            item_text.setBackgroundResource(R.drawable.chat);
            item_name.setVisibility(View.GONE);
        }
        //시간 전용 값설정
        if(position+1 == listViewItemList.size()){//마지막이니 시간 무조건, out of index 방지
            item_time.setText(dateTypeChange(listViewItem.getTime(),false));
            item_time.setVisibility(View.VISIBLE);
        }
        else if(listViewItemList.get(position+1).getIsme() != listViewItem.getIsme()){//다음 말이 다른사람이 말했을때
            item_time.setText(dateTypeChange(listViewItem.getTime(),false));
            item_time.setVisibility(View.VISIBLE);
        }
        else if(listViewItemList.get(position+1).getTime().equals(listViewItem.getTime())) {//다음 시간이랑 지금이랑 같으면 지금꺼안보이게
            item_time.setVisibility(View.GONE);
        }
        else {//위아래시간이 달랐을때
            item_time.setText(dateTypeChange(listViewItem.getTime(),false));
            item_time.setVisibility(View.VISIBLE);

        }



        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text, String time,boolean isme) {
        listViewItemList.add(new ItemChat(imagePath, name, text, time, isme));
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
    //날짜 계산해서 하루까진 시간으로, 년도안바뀐데까진 월일, 년도바뀌면 년도월일
    public String dateTypeChange(String date,boolean is_diff_day){
        String[] str = date.split("/");
        if(!now_arr[0].equals(str[0]))
            return str[0]+"."+str[1]+"."+str[2];
        else if(is_diff_day || !now_arr[2].equals(str[2]) || !now_arr[1].equals(str[1]))
            return str[1]+"월 "+str[2]+"일";
        else {
            int temp = Integer.parseInt(str[3]);
            if(temp > 11)
                return (temp != 12 ? temp-12 : temp)+":"+str[4] + " pm"  ;
            else
                return (temp != 0 ? temp : 12)+":"+str[4] + " am"  ;
        }
    }
    public Object getStatus(int position){
        return "position : "+position+"  말한사람 : "+listViewItemList.get(position).getIsme()+"\n말한시각 : "+listViewItemList.get(position).getTime()+"\n닉네임 : "+listViewItemList.get(position).getName();
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}



class AdapterList extends BaseAdapter implements Filterable {
    private ArrayList<ItemList> listViewItemList = new ArrayList<ItemList>();//원본이 저장되는 아이템
    private ArrayList<ItemList> filteredItemList = listViewItemList;//필터해서보여질아이템리스트
    private boolean isUserList;
    private MyFilter myFilter;

    public AdapterList(boolean s) {
        isUserList = s;
    }

    private class ViewHolder {
        ImageView picture;
        TextView name, message, first_text;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득. 및 홀더 부분
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
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //구현부분
        ItemList listViewItem = filteredItemList.get(position);

        viewHolder.name.setText(listViewItem.getName());
        viewHolder.message.setText(listViewItem.getMessage());


        if(isUserList) {
            if (listViewItem.getMessage().isEmpty())
                viewHolder.message.setVisibility(View.INVISIBLE);
            else viewHolder.message.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.message.setBackground(null);
        }



        //디폴트 설정
        if (isUserList) {
            init_UserList(viewHolder, position);
        }
        else{

        }
        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text) {
        listViewItemList.add(new ItemList(imagePath, name, text));
    }

    @Override// 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    public long getItemId(int position) {
        return position;
    }

    @Override// 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    public ItemList getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    public void init_UserList(ViewHolder viewHolder, int position) {
        if (0 <= position && position < 3) { // 초기화 3개 부문
            viewHolder.picture.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.message.setVisibility(View.GONE);
            viewHolder.first_text.setVisibility(View.GONE);
            viewHolder.relativeLayout.setVisibility(View.GONE);
            if (position == 0) viewHolder.first_text.setVisibility(View.VISIBLE);
            else if (position == 1) {
                viewHolder.picture.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.VISIBLE);
                if (!listViewItemList.get(1).getMessage().isEmpty())
                    viewHolder.message.setVisibility(View.VISIBLE);//자신의 말이 비어잇지가 않을때만 보이게
            } else {
                viewHolder.relativeLayout.setVisibility(View.VISIBLE);
                ((TextView) (viewHolder.relativeLayout.findViewById(R.id.sumFriend))).setText(getCount() - 3 + "명");
            }
        } else {//이외 부분
            viewHolder.picture.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.message.setVisibility(View.VISIBLE);
            viewHolder.first_text.setVisibility(View.GONE);
            viewHolder.relativeLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public Filter getFilter() {//뷰페이저마다 객체 다 생성되므로 아래로해도 괜찮음
        if (myFilter == null) myFilter = new MyFilter(isUserList);

        return myFilter;
    }

    private class MyFilter extends Filter {
        boolean isUserList;
        MyFilter(boolean i){
            isUserList = i;
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            } else {
                ArrayList<ItemList> itemList = new ArrayList<ItemList>();

                for (ItemList item : listViewItemList) {
                    if (item.getName().toUpperCase().contains(charSequence.toString().toUpperCase())){
                        itemList.add(item);
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredItemList = (ArrayList<ItemList>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
