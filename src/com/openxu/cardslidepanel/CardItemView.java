package com.openxu.cardslidepanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxu.cardpanel.R;

/**
 * 卡片View项
 * @author openXu
 */
public class CardItemView extends LinearLayout {

    private ImageView imageView;
    private TextView tv_num;
    public int num;
    public CardItemView(Context context) {
        this(context, null);
    }
    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.card_item, this);
        imageView = (ImageView) findViewById(R.id.imageView);
        tv_num = (TextView) findViewById(R.id.tv_num);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.carditem);
        int textNum = ta.getInteger(R.styleable.carditem_text_num, -1);
        num = textNum;
        tv_num.setText("第"+textNum+"个子控件");
        ta.recycle();
    }
    public void setImageSrc(int picId) {
        imageView.setImageResource(picId);
    }

}
