package com.jwl.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  lujunwu
 * date:    2018/10/10 09:05
 * desc:    下拉筛选框
 */
public class DropDownMenu extends LinearLayout {

    private static final String TAG = "abc";
    private final int mTabSelectedColor;
    private String[] mPopContents;
    private List<String[]> mContents ;
    private String[] mTabTitles;
    private int mTabCount;
    private int mTabHeight;
    private List<TextView> mTvs;
    private List<PopupWindow> mPopupWindows;


    public DropDownMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        mTabCount = t.getInteger(R.styleable.DropDownMenu_drop_tab_count, 2);
        mTabHeight = t.getDimensionPixelSize(R.styleable.DropDownMenu_drop_tab_height, dp2px(40));
        mTabSelectedColor = t.getColor(R.styleable.DropDownMenu_drop_tab_selected_color, 0XFF4DD5BD);
        mTabTitles = t.getString(R.styleable.DropDownMenu_drop_tab_titles).split(",");
        mPopContents = t.getString(R.styleable.DropDownMenu_drop_pop_content).split(":");
        setOrientation(HORIZONTAL);
        init();
        t.recycle();
    }

    private void init() {
        Log.d("abc", "init: "+mTabCount);
        Log.d(TAG, "init: "+mTabHeight);
        mContents = new ArrayList<>();
        for (int i = 0; i < mPopContents.length; i++) {
            mContents.add(mPopContents[i].split(","));
        }
        mTvs = new ArrayList<>();
        mPopupWindows = new ArrayList<>();
        for (int i = 0; i < mTabCount; i++) {
            final TextView tv = new TextView(getContext());
            tv.setText(mTabTitles[i]);
            tv.setTag(i);
            final Drawable rightDrawable = getContext().getResources().getDrawable(R.drawable.ic_expand_more_black_18dp);
            rightDrawable.setBounds(0,0,rightDrawable.getMinimumWidth(),rightDrawable.getMinimumHeight());
            tv.setCompoundDrawables(null,null,rightDrawable,null);
            mTvs.add(tv);
            final View v = LayoutInflater.from(getContext()).inflate(R.layout.pop_layout, null);
            final PopupWindow popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindows.add(popupWindow);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    tv.setSelected(false);
                    tv.setCompoundDrawables(null,null,getDrawable(tv, rightDrawable),null);
                }
            });
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i1 = 0; i1 < mPopupWindows.size(); i1++) {
                        if (i1!=((int) tv.getTag())) {
                            mPopupWindows.get(i1).dismiss();
                        }
                    }

                    if (tv.isSelected()) {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        tv.setSelected(false);
                    }else{

                        ListView lv = (ListView) v.findViewById(R.id.lv);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_pop_window, R.id.tv,mContents.get(((int) tv.getTag())));
                        lv.setAdapter(arrayAdapter);
                        lv.setHeaderDividersEnabled(true);
                        popupWindow.showAsDropDown(tv);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                tv.setText(arrayAdapter.getItem(i));
                                popupWindow.dismiss();

                            }
                        });
                        tv.setSelected(true);
                    }
                    for (TextView mTv : mTvs) {
                        if (mTv==tv) {
                            tv.setCompoundDrawables(null,null,getDrawable(tv, rightDrawable),null);
                            tv.setTextColor(mTv.isSelected()?mTabSelectedColor:Color.GRAY);
                        }else{
                            mTv.setSelected(false);
                            mTv.setCompoundDrawables(null,null,getDrawable(mTv, rightDrawable),null);
                            mTv.setTextColor(mTv.isSelected()?mTabSelectedColor:Color.GRAY);
                        }
                    }

                }
            });
            tv.setGravity(Gravity.CENTER);
            addView(tv,getContext().getResources().getDisplayMetrics().widthPixels/mTabCount,mTabHeight);
        }
    }

    @NonNull
    private Drawable getDrawable(TextView tv, Drawable rightDrawable) {
        final Drawable drawable = getContext().getResources().getDrawable(tv.isSelected() ? R.drawable.ic_expand_less_black_18dp : R.drawable.ic_expand_more_black_18dp);
        drawable.setBounds(0,0,rightDrawable.getMinimumWidth(),rightDrawable.getMinimumHeight());
        return drawable;
    }


    private int dp2px(int dpValue){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (scale*dpValue+0.5f);
    }


}
