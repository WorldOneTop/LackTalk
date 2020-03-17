package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isFriend;
    private ListView listView_friend;
    private HorizontalScrollView scrollView;
    private RelativeLayout profileLayout;
    private ImageView profile, search;
    private TextView name, message;
    private EditText editText;
    private InputMethodManager imm;
    private Handler handler;
    private View includeView;
    private TextView noSearch;
    private String id, imgPath;
    private static LinearLayout linearLayout;
    private Intent intent;
    private AdapterAdd adapterAdd;
    private LayoutInflater inflater;
    private HashMap<ItemList,View> hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
    }

    public void init() {
        //공통 변수설정
        editText = findViewById(R.id.add_editText);
        search = findViewById(R.id.add_search);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용
        handler = new Handler();
        intent = getIntent();
        isFriend = intent.getBooleanExtra("isFriend", false);

        //공통 뷰 디폴트 설정
        includeView = findViewById(R.id.add_actionbar);
        includeView.findViewById(R.id.action_bar_line).setVisibility(View.GONE);
        includeView.findViewById(R.id.action_bar_chatRoom).setVisibility(View.VISIBLE);
        includeView.findViewById(R.id.icon_menu).setVisibility(View.GONE);
        includeView.findViewById(R.id.icon_search_room).setVisibility(View.GONE);
        includeView.findViewById(R.id.icon_back).setOnClickListener(this);


        if (isFriend)
            addFriend();
        else
            addRoom();

        search.setOnClickListener(this);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && s.charAt(start + count - 1) == '\n') {//엔터키 판정
                    editText.setText(editText.getText().toString().replace("\n", ""));
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    if (isFriend)//친구추가
                        search.performClick();
                }
                if (!isFriend)//채팅방추가시엔 필터로
                    adapterAdd.getFilter().filter(editText.getText().toString());
            }

            @Override// 글자를 지웠을때임
            public void afterTextChanged(Editable arg0) {
            }

            @Override// 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });


    }

    public void addFriend() {
        editText.requestFocus();
        imm.showSoftInput(editText, 0);

        ((TextView) includeView.findViewById(R.id.icon_name)).setText("친구 추가");
        profileLayout = findViewById(R.id.add_profileLayout);
        profileLayout.setOnClickListener(this);

        name = findViewById(R.id.add_nickname);
        message = findViewById(R.id.add_profile_massage);
        profile = findViewById(R.id.add_profile);
        noSearch = findViewById(R.id.add_noSearch);

        profile.setOnClickListener(this);
        Intro.eventUserInfo = new EventUserInfo() {
            @Override
            public void messageArrive() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (NodeJS.recvUserInfo != null) {//한명이상있다는 뜻
                            try {
                                noSearch.setVisibility(View.GONE);
                                profileLayout.setVisibility(View.VISIBLE);
                                name.setText(NodeJS.recvUserInfo.getString("name"));
                                message.setText(NodeJS.recvUserInfo.getString("msg"));
                                id = NodeJS.recvUserInfo.getString("id");
                                imgPath = NodeJS.recvUserInfo.getString("picture");
                                if (imgPath.isEmpty())
                                    profile.setImageResource(R.drawable.defaultimg);
                                else
                                    Log.d("asd", NodeJS.recvUserInfo.getString("picture") + "      asdasd");
//                                    Intro.eventUserInfo = null;//뒤로가기시에 적용함
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            noSearch.setVisibility(View.VISIBLE);
                            profileLayout.setVisibility(View.GONE);

                        }
                    }
                });
            }
        };
    }

    public void addRoom() {
        ((TextView) includeView.findViewById(R.id.icon_name)).setText("채팅방 추가");
        scrollView = findViewById(R.id.add_scrollview);
        linearLayout = findViewById(R.id.add_linearLayout);
        listView_friend = findViewById(R.id.add_listview);
        listView_friend.setVisibility(View.VISIBLE);
        adapterAdd = new AdapterAdd((ArrayList<ItemList>) intent.getSerializableExtra("itemList"),this);
        listView_friend.setAdapter(adapterAdd);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hashMap = new HashMap<>();
        findViewById(R.id.add_resultImg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;
            case R.id.add_search:
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (isFriend) {//친구추가
                    JSONObject jsonObject = new JSONObject();
                    if (!editText.getText().toString().isEmpty()) {
                        try {
                            jsonObject.put("id", editText.getText());
                            NodeJS.sendJson("userInfo", jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.add_profile://친추의 이미지
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("name", name.getText());
                intent.putExtra("message", message.getText());
                intent.putExtra("picture", "");
                intent.putExtra("isMe", false);
                startActivity(intent);
                break;
            case R.id.add_profileLayout://친추의 루트레이아웃
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (noSearch.getVisibility() == View.GONE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("추가하시겠습니까?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            if (AppDatabase.getInstance(AddActivity.this).myDao().getUserSame(id) == null) {
                                                AppDatabase.getInstance(AddActivity.this).myDao().insertUser(new db_User(id, name.getText() + "", imgPath, message.getText() + ""));
                                                onBackPressed();
                                            } else
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (Intro.ID.equals(id))
                                                            Toast.makeText(AddActivity.this, "자기 자신은 등록 할 수 없습니다.", Toast.LENGTH_LONG).show();
                                                        else
                                                            Toast.makeText(AddActivity.this, "이미 친구로 등록 하셨습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                        }
                                    }.start();
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }
                break;
            case R.id.add_resultImg://대화창 만들기 버튼 눌렀을때

                break;
        }
    }

    public void addView(ItemList item,boolean isCheck) {
        if(!isCheck) {
            linearLayout.removeView(hashMap.get(item));
            hashMap.remove(item);
        }
        else{
            View view = inflater.inflate(R.layout.listview_horizontal, linearLayout, false);
            if(item.getImagePath().isEmpty())
                ((ImageView)view.findViewById(R.id.horizon_img)).setImageResource(R.drawable.defaultimg);
            ((TextView)view.findViewById(R.id.horizon_text)).setText(item.getName());
            hashMap.put(item,view);
            linearLayout.addView(view);
        }
        if(hashMap.size()==0) {
            scrollView.setVisibility(View.GONE);
            findViewById(R.id.add_resultImg).setVisibility(View.GONE);
        }
        else {
            scrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.add_resultImg).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intro.eventUserInfo = null;
        super.onBackPressed();
    }
}

class AdapterAdd extends BaseAdapter implements Filterable {
    private ArrayList<ItemList> listViewItemList = new ArrayList<ItemList>();//원본이 저장되는 아이템
    private ArrayList<ItemList> filteredItemList = null;//필터해서보여질아이템리스트
    private MyFilter myFilter;
    AddActivity instance;

    AdapterAdd(ArrayList<ItemList> userList,AddActivity inst) {
        listViewItemList.addAll(userList);
        listViewItemList.remove(0);listViewItemList.remove(0);listViewItemList.remove(0);
        filteredItemList = listViewItemList;
        instance = inst;
    }

    private class ViewHolder {
        ImageView picture;
        TextView name;
        CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득. 및 홀더 부분
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_add, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.picture = convertView.findViewById(R.id.add_list_imageview);
            viewHolder.name = convertView.findViewById(R.id.add_list_name);
            viewHolder.checkBox = convertView.findViewById(R.id.add_list_checkbok);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemList listViewItem = filteredItemList.get(position);

        if (listViewItem.getImagePath().isEmpty())
            viewHolder.picture.setImageResource(R.drawable.defaultimg);
        viewHolder.name.setText(listViewItem.getName());
        viewHolder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#f7e600")));
        viewHolder.checkBox.setOnCheckedChangeListener(changeListener);
        viewHolder.checkBox.setTag(listViewItem);
        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            instance.addView((ItemList) compoundButton.getTag(),b);
        }
    };

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

    @Override
    public Filter getFilter() {//뷰페이저마다 객체 다 생성되므로 아래로해도 괜찮음
        if (myFilter == null) myFilter = new MyFilter();
        return myFilter;
    }

    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            } else {
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
