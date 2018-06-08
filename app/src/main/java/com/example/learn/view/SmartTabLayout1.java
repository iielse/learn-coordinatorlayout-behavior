package com.example.learn.view;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


public class SmartTabLayout1 extends com.ogaclejapan.smarttablayout.SmartTabLayout {
    private ViewPager vPager;
    private View vLastTab;

    private ViewPager.OnPageChangeListener mSmartPageChangeListener;

    public void setSmartPageChangeListener(ViewPager.OnPageChangeListener cb) {
        mSmartPageChangeListener = cb;
    }

    public SmartTabLayout1(Context context) {
        this(context, null);
    }

    public SmartTabLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void useFakeBoldSelectedTextTab() {
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mSmartPageChangeListener != null)
                    mSmartPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (mSmartPageChangeListener != null)
                    mSmartPageChangeListener.onPageSelected(position);

                if (vLastTab != null) {
                    if (vLastTab instanceof TextView) {
                        ((TextView) vLastTab).getPaint().setFakeBoldText(false);
                    }
                }
                vLastTab = getTabAt(position);
                if (vLastTab instanceof TextView) {
                    ((TextView) vLastTab).getPaint().setFakeBoldText(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mSmartPageChangeListener != null)
                    mSmartPageChangeListener.onPageScrollStateChanged(state);
            }
        };

        onPageChangeListener.onPageSelected(vPager.getCurrentItem());
        setOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        vPager = viewPager;
        super.setViewPager(viewPager);
        useFakeBoldSelectedTextTab();
    }

    @Override
    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = super.createDefaultTabView(title);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        return textView;
    }
}
