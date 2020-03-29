package com.example.lacktalk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import java.util.HashMap;

public class AdapterChat extends BaseAdapter {
    private ArrayList<ItemChat> listViewItemList = new ArrayList<ItemChat>();
    static Handler handler;
    HashMap<String,String> hashMap;
    RelativeLayout.LayoutParams layoutParamsText,layoutParamsImage;

    // ListViewAdapter의 생성자
    public AdapterChat(HashMap<String,String> hashMap) {//채팅리스트인지 채팅방안인지 구분 필요
        handler = new Handler();
        this.hashMap = hashMap;

        layoutParamsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsText.addRule(RelativeLayout.START_OF,R.id.item_textR);
        layoutParamsImage.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.item_textR);
        layoutParamsImage = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImage.addRule(RelativeLayout.START_OF, R.id.item_imagR);
        layoutParamsImage.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.item_imagR);


    }

    private class ViewHolder {
        ImageView item_profile;
        TextView item_name, textview_date;
        LinearLayout layout_date;
        RelativeLayout listview_right, listview_left;
    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    public void showProfile(Context context, String name, String message, String picture) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("message", message);
        intent.putExtra("picture", picture);
        intent.putExtra("isMe", false);
        context.startActivity(intent);
    }
    public void readChat(){
        for(ItemChat item : listViewItemList){
            item.setAmount(item.getAmount()-1);
        }
        notifyDataSetChanged();
    }

    public static void notifyItem(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItem();
            }
        });
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;

        final ItemChat listViewItem = listViewItemList.get(position);

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {//한 화면에 그려지는거만 인가봄 그래서 공통적인거만 초기화
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_chatroom, parent, false);
            viewHolder.item_profile = (ImageView) convertView.findViewById(R.id.item_profile);
            viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.layout_date = convertView.findViewById(R.id.layout_date);
            viewHolder.textview_date = convertView.findViewById(R.id.textview_date);
            viewHolder.listview_right = convertView.findViewById(R.id.listview_right);
            viewHolder.listview_left = convertView.findViewById(R.id.listview_left);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.layout_date.setVisibility(View.GONE);
        if (listViewItem.getIsme()) {
            viewHolder.listview_right.setVisibility(View.VISIBLE);
            viewHolder.listview_left.setVisibility(View.GONE);
        } else {
            viewHolder.listview_left.setVisibility(View.VISIBLE);
            viewHolder.listview_right.setVisibility(View.GONE);
        }
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득

//        ImageView item_profile = (ImageView) convertView.findViewById(R.id.item_profile);
//        TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
        TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
        TextView item_time = (TextView) convertView.findViewById(R.id.item_time);
        TextView item_read = convertView.findViewById(R.id.item_unread);
        ImageView item_image = convertView.findViewById(R.id.item_imag);
//        //이 밑 세개는 날짜 보여주기 위해서
//        LinearLayout layout_date = convertView.findViewById(R.id.layout_date);
//        TextView textview_date = convertView.findViewById(R.id.textview_date);
//        layout_date.setVisibility(View.GONE);
//
        if (!listViewItem.getIsme()) {       // 상대방이 얘기했을때 필요한 뷰 선언
            convertView.findViewById(R.id.listview_left).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_right).setVisibility(View.GONE);
        } else {               //내가얘기했을때 필요한 뷰 선언
            item_text = (TextView) convertView.findViewById(R.id.item_textR);
            item_time = (TextView) convertView.findViewById(R.id.item_timeR);
            item_read = convertView.findViewById(R.id.item_unreadR);
            item_image = convertView.findViewById(R.id.item_imagR);
            convertView.findViewById(R.id.listview_right).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.listview_left).setVisibility(View.GONE);
        }


        viewHolder.item_name.setText(listViewItem.getName());

        if(listViewItem.getType()==1) {
            item_text.setVisibility(View.VISIBLE);
            item_image.setVisibility(View.GONE);
            item_text.setText(listViewItem.getText());
        }
        else if(listViewItem.getType()==2){//이미지타입이면
            item_text.setVisibility(View.INVISIBLE);
            item_image.setVisibility(View.VISIBLE);
            item_image.setImageBitmap(Intro.getBitmapFromString(listViewItem.getText()));
            item_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,PhotoViewActivity.class);
                    intent.putExtra("picture",listViewItem.getText());
                    context.startActivity(intent);
                }
            });

        }



        if(listViewItem.getAmount()>0)
            item_read.setText(listViewItem.getAmount()+"");
        else
            item_read.setText("");



        viewHolder.item_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile(parent.getContext(), listViewItem.getName(), "", "");
            }
        });



//설정이 계속 유지되는 듯 그래서 상태 바뀔때마다 바꾼것 모두 재설정
        String temp = hashMap.get(listViewItem.getId());

        viewHolder.item_profile.setMaxHeight(0);
        if (position == 0 || !listViewItemList.get(position - 1).getTime().substring(0, 10).equals(listViewItem.getTime().substring(0, 10))) {//첫대화이거나 전 대화와 날짜가달랐을때
            viewHolder.textview_date.setText(Intro.dateTypeChange(listViewItem.getTime(), true));
            viewHolder.layout_date.setVisibility(View.VISIBLE);
            if(temp != null && !temp.isEmpty()){//이미지칸이 비어있지가 않으면
                viewHolder.item_profile.setImageBitmap(Intro.getBitmapFromString(temp));
            }else{
                viewHolder.item_profile.setImageResource(R.drawable.defaultimg);
            }
            viewHolder.item_profile.setMaxHeight(3000);
            viewHolder.item_profile.setVisibility(View.VISIBLE);
            item_text.setBackgroundResource(listViewItem.getIsme() ? R.drawable.rightchat : R.drawable.leftchat);
            viewHolder.item_name.setVisibility(View.VISIBLE);
        }
        //프사및닉넴 및 꼬퉁이 전용 값설정  및 날짜도
        else if (!listViewItemList.get(position - 1).getId().equals(listViewItem.getId())) {//한사람의 말의 시작
            if(temp != null && !temp.isEmpty()){//이미지칸이 비어있지가 않으면
                viewHolder.item_profile.setImageBitmap(Intro.getBitmapFromString(temp));
            }else{
                viewHolder.item_profile.setImageResource(R.drawable.defaultimg);
            }
            viewHolder.item_profile.setMaxHeight(3000);
            viewHolder.item_profile.setVisibility(View.VISIBLE);
            item_text.setBackgroundResource(listViewItem.getIsme() ? R.drawable.rightchat : R.drawable.leftchat);
            viewHolder.item_name.setVisibility(View.VISIBLE);
        } else {
            viewHolder.item_profile.setVisibility(View.INVISIBLE);
            viewHolder.item_profile.setMaxHeight(0);
            item_text.setBackgroundResource(R.drawable.chat);
            viewHolder.item_name.setVisibility(View.GONE);
        }
        //시간 전용 값설정
        if (position + 1 == listViewItemList.size()) {//마지막이니 시간 무조건, out of index 방지
            item_time.setText(Intro.dateTypeChange(listViewItem.getTime(), false));
            item_time.setVisibility(View.VISIBLE);
        } else if (!listViewItemList.get(position + 1).getId().equals(listViewItem.getIsme())) {//다음 말이 다른사람이 말했을때
            item_time.setText(Intro.dateTypeChange(listViewItem.getTime(), false));
            item_time.setVisibility(View.VISIBLE);
        } else if (listViewItemList.get(position + 1).getTime().equals(listViewItem.getTime())) {//다음 시간이랑 지금이랑 같으면 지금꺼안보이게
            item_time.setVisibility(View.GONE);
        } else {//위아래시간이 달랐을때
            item_time.setText(Intro.dateTypeChange(listViewItem.getTime(), false));
            item_time.setVisibility(View.VISIBLE);

        }

        if(listViewItem.getIsme()) {
            if (listViewItem.getType() == 1)
                item_time.setLayoutParams(layoutParamsText);
            else
                item_time.setLayoutParams(layoutParamsImage);
        }
        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text, String time, boolean isme,int amount,int type,String id,int p) {
        listViewItemList.add(new ItemChat(imagePath, name, text, time, isme,amount,type,id,p));
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }
    //날짜 계산해서 하루까진 시간으로, 년도안바뀐데까진 월일, 년도바뀌면 년도월일
    private void IDtoImg(String id){

    }


}


class AdapterList extends BaseAdapter implements Filterable {
    private ArrayList<ItemList> listViewItemList = new ArrayList<ItemList>();//원본이 저장되는 아이템
    private ArrayList<ItemList> filteredItemList = listViewItemList;//필터해서보여질아이템리스트
    private boolean isUserList, isFilter = false;
    private MyFilter myFilter;

    public AdapterList(boolean s) {
        isUserList = s;
    }

    private class ViewHolder {
        ImageView picture;
        TextView name, message, first_text;
        RelativeLayout relativeLayout;
        TextView text, unRead;
    }

    public boolean getIsFilter() {
        return isFilter;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득. 및 홀더 부분
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            if (isUserList)
                convertView = inflater.inflate(R.layout.listview_list, parent, false);
            else {
                convertView = inflater.inflate(R.layout.listview_chatlist, parent, false);
                viewHolder.unRead = convertView.findViewById(R.id.chatlist_unread);
                viewHolder.text = convertView.findViewById(R.id.chatlist_text);
            }
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

        //공통 설정 부분
        viewHolder.name.setText(listViewItem.getName());
//        viewHolder.picture.setImageResource();

        //디폴트 설정
        if (isUserList) {
            init_UserList(viewHolder, position);
            viewHolder.message.setText(listViewItem.getMessage());
            if (viewHolder.message.getText().toString().isEmpty())
                viewHolder.message.setVisibility(View.GONE);
            else
                viewHolder.message.setVisibility(View.VISIBLE);
            if(listViewItem.getImagePath() ==null || listViewItem.getImagePath().isEmpty())
                viewHolder.picture.setImageResource(R.drawable.defaultimg);
            else
                viewHolder.picture.setImageBitmap(Intro.getBitmapFromString(listViewItem.getImagePath()));

        } else {
            viewHolder.text.setText(listViewItem.getMessage());
            viewHolder.message.setText(Intro.dateTypeChange(listViewItem.getId(), false));//말한시간

            if(listViewItem.getUnread() == 0)
                viewHolder.unRead.setVisibility(View.GONE);
            else{
                viewHolder.unRead.setText(listViewItem.getUnread()+"");
                viewHolder.unRead.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String imagePath, String name, String text, int pnum, String idd) {
        listViewItemList.add(new ItemList(imagePath, name, text, pnum, idd));
    }
    public void updateItemRoom(int pnum,String text,String time){
        for(int i=0;i<listViewItemList.size();i++){
            if(listViewItemList.get(i).getPrimary_num() == pnum){//바꾸려는 방
                listViewItemList.get(i).setMessage(text);
                listViewItemList.get(i).setId(time);
                listViewItemList.get(i).setUnread(listViewItemList.get(i).getUnread()+1);
                return;
            }
        }

    }

    public void addRoom(String a, String b, String c, int d, String e, int f,int g,String h) {
        listViewItemList.add(new ItemList(a, b, c, d, e).initRoom(f,g,h));
    }

    public void clearData() {
        listViewItemList.clear();
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

    public ArrayList<ItemList> getListViewItemList() {
        return listViewItemList;
    }

    public void deleteItem_num(int p_num) {
        for (int i = 0; i < listViewItemList.size(); i++) {
            if (listViewItemList.get(i).getPrimary_num() == p_num) {
                listViewItemList.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void init_UserList(ViewHolder viewHolder, int position) {
        if (0 <= position && position < 3 && !isFilter) { // 초기화 3개 부문
            viewHolder.picture.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.first_text.setVisibility(View.GONE);
            viewHolder.relativeLayout.setVisibility(View.GONE);
            if (position == 0) viewHolder.first_text.setVisibility(View.VISIBLE);
            else if (position == 1) {
                viewHolder.picture.setVisibility(View.VISIBLE);
                viewHolder.name.setVisibility(View.VISIBLE);
            } else {
                viewHolder.relativeLayout.setVisibility(View.VISIBLE);
                ((TextView) (viewHolder.relativeLayout.findViewById(R.id.sumFriend))).setText(getCount() - 3 + "명");
            }
        } else {//이외 부분
            viewHolder.picture.setVisibility(View.VISIBLE);
            viewHolder.name.setVisibility(View.VISIBLE);
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

        MyFilter(boolean i) {
            isUserList = i;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
                isFilter = false;
            } else {
                isFilter = true;
                ArrayList<ItemList> itemList = new ArrayList<ItemList>();

                for (ItemList item : listViewItemList) {
                    if (!item.getName().isEmpty()) {
                        if (item.getName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                            itemList.add(item);//영어, 한글 검색
                        } else if (item.getInitial().contains(charSequence.toString())) {
                            itemList.add(item);//한글 초성검색
                        }
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
