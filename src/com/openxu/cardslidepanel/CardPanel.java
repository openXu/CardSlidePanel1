package com.openxu.cardslidepanel;

import java.util.ArrayList;
import java.util.List;

import com.openxu.cardpanel.R;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CardPanel extends ViewGroup {

	private String TAG = "CardPanel";
	private List<CardItemView> viewList = new ArrayList<CardItemView>();
	private List<CardItemView> releaseView = new ArrayList<CardItemView>();
	private int PIC_IDS[] = new int[]{R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5};
	private int TOP = -45;       //向上的偏移量
	private float SCALE_STEP = 0.08f;   //缩放的步长
	private int panelWidth, panelHight;   //面板的宽高
	private int itemWidth, itemHight;     //子控件的宽高
	private int startX, startY;           //子控件开始时坐标
	//拖动工具类
	private ViewDragHelper dragHelper;
	
	
	public CardPanel(Context context) {
		this(context, null);
	}
	public CardPanel(Context context, AttributeSet attrs) {
		this(context, null, 0);
	}
	public CardPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		dragHelper = ViewDragHelper.create(this, 1.0f, new DragCallBack());
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		panelWidth = getMeasuredWidth();
		panelHight = getMeasuredHeight();
		itemWidth = getChildAt(0).getMeasuredWidth();
		itemHight = getChildAt(0).getMeasuredHeight();
		MyUtil.LOG_V(TAG, "面板的宽和高："+panelWidth+"  "+panelHight);
		MyUtil.LOG_V(TAG, "卡片的宽和高："+itemWidth+"  "+itemHight);
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 4 3 2 1
		for(int i = getChildCount()-1;i>=0 ;i--){
			CardItemView view = (CardItemView)getChildAt(i);
			setImage(view);
			viewList.add(view);
		}
	}
	
	int picIndex = 0;   //当前显示的图片的索引
	private void setImage(CardItemView view){
		if(picIndex>=PIC_IDS.length){
			picIndex = 0;
		}
		view.setImageSrc(PIC_IDS[picIndex]);
		picIndex ++;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for(int i = 0 ;i<viewList.size(); i++){
			CardItemView view = viewList.get(i);
			l = (panelWidth-itemWidth)/2;
			t = (panelHight-itemHight)/2;
			view.layout(l, t, l+itemWidth, t+itemHight);
			int offset = TOP*i;
			float scale = 1 - (SCALE_STEP * i);
			if(i == 3){
				offset = TOP*2;
				scale = 1 - (SCALE_STEP * 2);
			}
			view.offsetTopAndBottom(offset);
			view.setScaleX(scale);
			view.setScaleY(scale);
		}
		viewList.get(viewList.size()-1).setVisibility(View.INVISIBLE);
		startX = viewList.get(0).getLeft();
		startY = viewList.get(0).getTop();
	}
	
	class DragCallBack extends ViewDragHelper.Callback{
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			int index = viewList.indexOf(child);
			if(index==0){
				return true;
			}
			return false;
		}
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
        	animToSide(releasedChild, xvel, yvel);
        }
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
        		int dx, int dy) {
        	processLinageView(changedView);
        }
        
	}
	
	private static final float MAX_DIS = 400.0f;
	/**
	 * 根据第一个拖动控件的位移联动
	 */
	private void processLinageView(View changedView) {
		float distance = Math.abs(changedView.getLeft()-startX)+Math.abs(changedView.getTop()-startY);
		float rate1 = distance / MAX_DIS;
		MyUtil.LOG_E(TAG, "联动比例："+rate1);
		float rate2 = rate1 - 0.2f;
		
		if(rate1>1){
			rate1 = 1;
		}
		if(rate2>1){
			rate2 = 1;
		}else if(rate2<0){
			rate2 = 0;
		}
		adjustLinageViewItem(1, rate1);
		adjustLinageViewItem(2, rate2);
		
	}
	
	private void adjustLinageViewItem(int index, float rate){
		CardItemView view = viewList.get(index);
		float initPosY = TOP*index;
		float initScale = 1-SCALE_STEP*index;
		
		float nextPosY = TOP*(index-1);
		float nextScale = 1-SCALE_STEP*(index-1);
		
		int offset = (int) (initPosY+(nextPosY - initPosY)*rate);
		//要联动的卡片相对于当前位置需要偏移的像素
		float scale = initScale + (nextScale-initScale)*rate;
		view.offsetTopAndBottom(offset-view.getTop()+startY);
		view.setScaleX(scale);
		view.setScaleY(scale);
		
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dragHelper.processTouchEvent(event);
		return true;
	}
	
	private static final int X_VEL = 900;   //X轴方向速度最大值
    private static final int Y_VEL = 900;   //Y轴方向速度最大值
    private static final int X_DIS = 300;   //X轴方向位移最大值
    private static final int Y_DIS = 800;   //Y轴方向位移最大值
    public static final int VANISH_TYPE_LEFT = 0;
    public static final int VANISH_TYPE_RIGHT = 1;
    public static final int VANISH_TYPE_TOP = 2;
    public static final int VANISH_TYPE_BUTTOM = 3;
    private int TYPE = -1;
    private void animToSide(View releasedChild, float xvel, float yvel){
    	int endX = startX;
    	int endY = startY;
    	int dx = releasedChild.getLeft() - startX;
    	int dy = releasedChild.getTop() - startY;
    	float tanA = (panelWidth/2)/(panelHight/2); 
    	if(Math.abs(xvel) > X_VEL || Math.abs(dx) > X_DIS
        		||Math.abs(yvel) > Y_VEL || Math.abs(dy) > Y_DIS){
            if(dx>0){
            	if(dy>=0){
            		if(dy/dx<tanA){
            			//0-45度，向右
                		TYPE = VANISH_TYPE_RIGHT;
            		}else{
            			//45-90
            			TYPE = VANISH_TYPE_BUTTOM;
            		}
            	}else{
            		if(-dy/dx<tanA){
            			//0-（-45）度，向右
                		TYPE = VANISH_TYPE_RIGHT;
            		}else{
            			//（-45）-（-90）
            			TYPE = VANISH_TYPE_TOP;
            		}
            	}
            }else if(dx<0){
            	if(dy>=0){
            		if(dy/-dx>tanA){
            			//90-135度，向下
                		TYPE = VANISH_TYPE_BUTTOM;
            		}else{
            			//135-180
            			TYPE = VANISH_TYPE_LEFT;
            		}
            	}else{
            		if(dy/dx<tanA){
            			//180-225度
                		TYPE = VANISH_TYPE_LEFT;
            		}else{
            			//225-270
            			TYPE = VANISH_TYPE_TOP;
            		}
            	}
            }else if(dx==0){
            	if(dy<0){
        			TYPE = VANISH_TYPE_TOP;
        		}else{
        			TYPE = VANISH_TYPE_BUTTOM;
        		}
            }
        }
    	if(TYPE >= 0){
    		switch (TYPE) {
			case VANISH_TYPE_TOP:
				endY = -itemHight;
	        	endX = startX - (itemHight + startY)*dx/dy;
	        	MyUtil.LOG_V(TAG, "向上滑动到x："+endX+"  y："+endY);
				break;
			case VANISH_TYPE_BUTTOM:
				endY = getBottom();
	        	endX = startX+(endY-startY)*dx/dy;
	        	MyUtil.LOG_V(TAG, "向下滑动到x："+endX+"  y："+endY);
				break;
			case VANISH_TYPE_LEFT:
				 endX = -itemWidth;
	             endY = dy * (itemWidth + startX) / (-dx) + startY;
	             MyUtil.LOG_V(TAG, "向左滑动到x："+endX+"  y："+endY);
				break;
			case VANISH_TYPE_RIGHT:
				 endX = panelWidth;
	             endY = dy * (endX - startX) / dx + startY;
	             MyUtil.LOG_V(TAG, "向右滑动到x："+endX+"  y："+endY);
				break;
			}
    	}
    	if(endX!=startX){
    		releaseView.add((CardItemView)releasedChild);
    	}
    	
    	//开启滑动动画
    	if(dragHelper.smoothSlideViewTo(releasedChild, endX, endY)){
    		//执行下一帧
    		ViewCompat.postInvalidateOnAnimation(this);
    	}
    }
    
    @Override
    public void computeScroll() {
    	super.computeScroll();
    	if(dragHelper.continueSettling(true)){
    		//执行下一帧
    		MyUtil.LOG_I(TAG, "执行下一帧");
    		ViewCompat.postInvalidateOnAnimation(this);
    	}else{
    		MyUtil.LOG_I(TAG, "滑动完毕了");
    		orderView();
    	}
    }
    /**
     * 对子控件重新排序
     */
	private void orderView() {
		if(releaseView.size()==0){
			return;
		}
		viewList.get(viewList.size()-1).setVisibility(View.VISIBLE);
		//将图片更新
		CardItemView view = releaseView.get(0);
		setImage(view);
		//将划出去的控件放到最后一个位置，缩放大小和第三个一样
		view.offsetLeftAndRight(view.getLeft() - startX);
		view.offsetTopAndBottom(startY-view.getTop()+TOP*2);
		float scale = 1.0f-SCALE_STEP*2;
		view.setScaleX(scale);
		view.setScaleY(scale);
		view.setVisibility(View.INVISIBLE);
		//将后面三个图片的z轴向前进一位
		//3 2 1 4      	3 2 1 4
		for(int i = viewList.size()-1; i>0; i--){
			viewList.get(i).bringToFront();
		}
		//将ViewList中的View集合重新排序
		viewList.remove(view);
		viewList.add(view);
		releaseView.remove(view);
	}
    
    
    
	
}
