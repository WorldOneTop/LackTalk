package com.example.lacktalk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ChatList_In_ViewPager extends Fragment implements View.OnClickListener {
    private View rootView;
    private Context context;
    private boolean isUserList;
    private ImageView actionbar_search, actionbar_add, searchImage;
    private TextView actionbar_main;
    private AdapterList adapterList;
    private ListView listView;
    public static String myName;
    private String myPicture, myMsg;
    private RelativeLayout searchLayout;
    private EditText searchEdit;
    private InputMethodManager imm;
    private Handler handler;
    private Activity activity;
    public OnBackKeyPressM onBackKeyPressM;

    ChatList_In_ViewPager() {
    }//앱 재실행 오류 이슈 떄문에 작성만

    ChatList_In_ViewPager(ChatList c, boolean i) {
        activity = c;
        context = c;
        isUserList = i;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.viewpager_in_chatlist, container, false);

        init();
        return rootView;
    }

    private void init() {
        handler = new Handler();
        rootView.findViewById(R.id.include_actionbar).findViewById(R.id.action_bar_friend).setVisibility(View.VISIBLE);

        //채팅방 위에 액션바(뒤로가기 이름 등등 ) 뷰 선언 및 보이게
        View include_layout = rootView.findViewById(R.id.include_actionbar);
        include_layout.findViewById(R.id.action_bar_chatRoom).setVisibility(View.GONE);//챗룸에서쓰는액션바
        include_layout.findViewById(R.id.action_bar_friend).setVisibility(View.VISIBLE);//리스트에서쓰는액션바
        actionbar_search = include_layout.findViewById(R.id.icon_search);
        actionbar_add = include_layout.findViewById(R.id.icon_add);
        actionbar_main = include_layout.findViewById(R.id.action_bar_main);

        //검색부분
        searchLayout = rootView.findViewById(R.id.layout_search);
        searchEdit = rootView.findViewById(R.id.search_edittext);
        searchImage = rootView.findViewById(R.id.search_x);

        //리스트뷰 설정은 어뎁터에서
        adapterList = new AdapterList(isUserList);
        listView = rootView.findViewById(R.id.listview_list);
        listView.setAdapter(adapterList);

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용
        if (isUserList) {
            actionbar_main.setText("친구들");
            initFriendList();
        } else {
            actionbar_add.setImageResource(R.drawable.icon_addchat);
            actionbar_main.setText("채팅방들");
            initChatList();
        }


        init_OnClick();
    }

    public void initFriendList() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    adapterList.clearData();
                    FileReader fileReader = new FileReader(new File(context.getFilesDir(), Intro.FILENAME_LOGIN_PATH));
                    char[] buf = new char[2048];
                    fileReader.read(buf);
                    JSONObject jsonObject = new JSONObject(new String(buf));
                    fileReader.close();
                    File file = new File(context.getFilesDir(), Intro.FILENAME_IMAGE_PATH);
                    if (file.exists()) {
                        FileReader image = new FileReader(file);
                        char[] buf1 = new char[(int)file.length()];
                        image.read(buf1);
                        myPicture = String.valueOf(buf1);
                    } else
                        myPicture = "";
                    Log.d("asd","existes"+myPicture);

                    myName = jsonObject.getString("name");
                    myMsg = jsonObject.getString("msg");
                    if (isUserList) {
                        adapterList.addItem("", "", "", -1, "");
                        adapterList.addItem(myPicture, myName, myMsg, -1, "");//나자신도 추가해야함
                        adapterList.addItem("", "", "", -1, "");

                        new Thread() {
                            @Override
                            public void run() {
                                List<db_User> list = AppDatabase.getInstance(context).myDao().getUserAll();
                                for (db_User user : list) {
                                    if (user.id.equals(Intro.ID)) continue;
                                    adapterList.addItem(user.picture, user.name, user.msg, user.user_num, user.id);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapterList.notifyDataSetChanged();
                                    }
                                });

                            }
                        }.start();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initChatList() {
        adapterList.clearData();
        new Thread() {
            @Override
            public void run() {// ro.room_picture, ro.room_name , re.recode_text, ro.room_num_server, re.recode_date,ro.room_user
                Cursor list = AppDatabase.getInstance(context).myDao().getChatLastList();
                while (list.moveToNext()) {//한줄 만드는거
                    int count = AppDatabase.getInstance(context).myDao().getListAmount(list.getInt(3));
                    int sumMan = list.getString(5).split("/").length;
                    String text = list.getInt(6) == 2 ? "사진을 보냈습니다." : list.getString(2);
                    adapterList.addRoom(list.getString(0), list.getString(1), text, list.getInt(3), list.getString(4), count, sumMan, list.getString(5));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterList.notifyDataSetChanged();
                    }
                });
            }
        }.start();

    }


    public void init_OnClick() {
        actionbar_search.setOnClickListener(this);
        actionbar_add.setOnClickListener(this);
        searchImage.setOnClickListener(this);

        onBackKeyPressM = new OnBackKeyPressM() {
            @Override
            public boolean press() {
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    searchLayout.setVisibility(View.GONE);
                    searchEdit.setText("");
                    adapterList.getFilter().filter("");
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    return false;
                } else
                    return true;
            }
        };


        searchEdit.addTextChangedListener(new TextWatcher() {//필터링 하기위해
            @Override
            public void afterTextChanged(Editable edit) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterList.getFilter().filter(searchEdit.getText().toString());
                if (s.length() != 0 && s.charAt(start + count - 1) == '\n') {//엔터키 판정
                    searchEdit.setText(searchEdit.getText().toString().replace("\n", ""));
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                }
            }
        });


        if (isUserList) {//유저리스트라면
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Intent intent = new Intent(context, ProfileActivity.class);
                                                    ItemList item = (ItemList) adapterView.getAdapter().getItem(i);
                                                    intent.putExtra("name", item.getName());
                                                    intent.putExtra("message", item.getMessage());
                                                    intent.putExtra("picture", item.getImagePath());
                                                    intent.putExtra("isMe", i == 1);
                                                    intent.putExtra("num", item.getPrimary_num());
                                                    intent.putExtra("position", i);
                                                    intent.putExtra("item", adapterList.getItem(i));
                                                    if ((i != 0 && i != 2) || adapterList.getIsFilter()) {
                                                        startActivity(intent);
                                                    }

                                                }
                                            }
            );
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                final CharSequence[] logclickbuilderItem = {"1:1 대화방 추가", "친구 삭제"};

                @Override
                public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                    if (i > 2 || adapterList.getIsFilter()) {//필터안했을때 3이상이거나 했을때 전부
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//getContext>> 현재 활성화된activity의 context반환(View클래스에있음)
                        builder.setItems(logclickbuilderItem, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                if (index == 0) {//대화방 추가를 클릭했을떄
                                    String users = Intro.ID + "/" + ((ItemList) adapterView.getAdapter().getItem(i)).getId();
                                    AddActivity.addRoom(context, users, true, users, 1);

                                } else {//친구삭제를 클릭 했을 때
                                    final int pnum = ((ItemList) adapterView.getAdapter().getItem(i)).getPrimary_num();
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            AppDatabase.getInstance(context).myDao().deleteUser(new db_User(pnum));
                                        }
                                    }.start();
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("me", Intro.ID);
                                        jsonObject.put("friend", ((ItemList) adapterView.getAdapter().getItem(i)).getId());
                                        NodeJS.sendJson("deleteFriend", jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(context, ((ItemList) adapterView.getAdapter().getItem(i)).getName() + "님의 메시지를 볼 수 없습니다.", Toast.LENGTH_LONG).show();
                                    ((AdapterList) adapterView.getAdapter()).deleteItem_num(pnum);
                                }
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
            });
        } else {//채팅방리스트라면
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context, ChatRoom.class);
                    intent.putExtra("name", adapterList.getItem(i).getName());      //방의 이름
                    intent.putExtra("pnum", adapterList.getItem(i).getPrimary_num());//방번호(서버)
                    intent.putExtra("amount", adapterList.getItem(i).getAmount());//총 사람 수
                    intent.putExtra("users", adapterList.getItem(i).getUsers());
                    startActivity(intent);
                    adapterList.getFilter().filter("");
                    searchLayout.setVisibility(View.GONE);
                }
            });
        }


    }

    public void updateItemRoom(int pnum, String text, String date) {
        adapterList.updateItemRoom(pnum, text, date);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapterList.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_search:
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    searchLayout.setVisibility(View.GONE);
                    searchEdit.setText("");
                    adapterList.getFilter().filter("");
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                } else {
                    searchLayout.setVisibility(View.VISIBLE);
                    searchEdit.requestFocus();
                    imm.showSoftInput(searchEdit, 0);
                }
                break;
            case R.id.icon_add:
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("isFriend", isUserList);
                if (isUserList) {
                    startActivity(intent);
                } else {
                    intent.putExtra("itemList", ChatList.viewPager_chatList[0].adapterList.getListViewItemList());
                    startActivity(intent);
                }

                break;
            case R.id.search_x:
                searchEdit.setText("");
                adapterList.getFilter().filter("");
                break;
        }
    }

    public ChatList_In_ViewPager safeVar(Context c, boolean i) {//생성자가 안되다 되다하는 현상 발견으로 인해 만든 안전장치
        context = c;
        isUserList = i;
        return this;
    }

}


class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }
}
