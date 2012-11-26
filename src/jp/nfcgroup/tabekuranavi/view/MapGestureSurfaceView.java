package jp.nfcgroup.tabekuranavi.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import jp.nfcgroup.tabekuranavi.R;
import jp.nfcgroup.tabekuranavi.fragment.MapFragment;
import jp.nfcgroup.tabekuranavi.fragment.StoreDialogFragment;
import jp.nfcgroup.tabekuranavi.model.StoreColorVO;

public class MapGestureSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    private static final String TAG = "MapGestureSurfaceView";
    private static final RectF[] mShopRects = {
        new RectF(485.0f, 555.0f, 525.0f, 595.0f),
        new RectF(485.0f, 600.0f, 525.0f, 640.0f),
        new RectF(485.0f, 650.0f, 525.0f, 690.0f),
        new RectF(485.0f, 695.0f, 525.0f, 735.0f),
        new RectF(485.0f, 745.0f, 525.0f, 785.0f),
        new RectF(485.0f, 790.0f, 525.0f, 830.0f),
        new RectF(485.0f, 840.0f, 525.0f, 880.0f),
        new RectF(485.0f, 885.0f, 525.0f, 925.0f),
        new RectF(370.0f, 1030.0f, 410.0f, 1070.0f),
        new RectF(325.0f, 1035.0f, 365.0f, 1075.0f),
        new RectF(275.0f, 1040.0f, 315.0f, 1080.0f),
        new RectF(230.0f, 1045.0f, 270.0f, 1085.0f),
        new RectF(180.0f, 1050.0f, 220.0f, 1090.0f),
        new RectF(135.0f, 1055.0f, 175.0f, 1095.0f),
        new RectF(135.0f, 940.0f, 175.0f, 980.0f),
        new RectF(160.0f, 895.0f, 200.0f, 935.0f),
        new RectF(200.0f, 845.0f, 240.0f, 885.0f),
        new RectF(225.0f, 800.0f, 265.0f, 840.0f),
        new RectF(265.0f, 750.0f, 305.0f, 790.0f),
        new RectF(290.0f, 705.0f, 330.0f, 745.0f),
        new RectF(145.0f, 440.0f, 185.0f, 480.0f),
        new RectF(170.0f, 395.0f, 210.0f, 435.0f),
        new RectF(210.0f, 345.0f, 250.0f, 385.0f),
        new RectF(235.0f, 300.0f, 275.0f, 340.0f),
        new RectF(275.0f, 250.0f, 315.0f, 290.0f),
        new RectF(300.0f, 205.0f, 340.0f, 245.0f),
        new RectF(340.0f, 155.0f, 380.0f, 195.0f),
        new RectF(365.0f, 110.0f, 405.0f, 150.0f),
        new RectF(485.0f, 335.0f, 525.0f, 375.0f)
    };
	
	private SurfaceHolder mHolder;
	private Thread mThread;
	private int mScreenWidth,mScreenHeight;
	private BitmapDrawable mMapImage;
	private Matrix mMapMatrix;
	private Point mMapOffset;
	private float mMapScale;
	
	private Paint mShopPaint;
	private Paint mTextPaint;
	
	private ArrayList<StoreColorVO> mColors;
	
	public MapFragment mParentFragment;
	
	public MapGestureSurfaceView(Context context) {
        super(context);
        
        initialize();
    }
	
	public MapGestureSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initialize();
    }
	
	public MapGestureSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initialize();
    }
	
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        Log.d(TAG, "width="+mScreenWidth+" height="+mScreenHeight);
    }
    
    private void initialize(){
        mHolder = getHolder(); 
        mHolder.addCallback(this);
        
        mMapImage = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.map));
        Log.d(TAG,"imageWidth="+mMapImage.getIntrinsicWidth()+" imageHeight="+mMapImage.getIntrinsicHeight());
        
        mMapMatrix = new Matrix();
        mMapOffset = new Point();
        mMapScale = 1.0f;
        mShopPaint = new Paint();
        mShopPaint.setAntiAlias(true);
        
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(26);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Align.CENTER);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new Thread(this);
        mThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread = null;
    }

    public void run() {
        while(mThread != null){
            Canvas canvas = mHolder.lockCanvas();
            if(canvas != null){
                
                render(canvas);
                
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    
    private void render(Canvas canvas) {
        
        canvas.save();
        
        canvas.drawColor(Color.parseColor("#000000"));
        
        //mMapMatrix.postConcat(canvas.getMatrix());
        canvas.setMatrix(mMapMatrix);
        
        mMapImage.setBounds(mMapOffset.x,mMapOffset.y,
                mMapImage.getIntrinsicWidth()+mMapOffset.x,
                mMapImage.getIntrinsicHeight()+mMapOffset.y);
        mMapImage.draw(canvas);
        
        for(int i=0;i<mShopRects.length;i++){
            RectF rect = mShopRects[i];
            StoreColorVO c = mColors.get(i);
            mShopPaint.setColor(Color.argb(c.alpha, c.red, c.green, c.blue));
            mShopPaint.setShadowLayer(3.0f, 2.0f, 2.0f,Color.argb(c.alpha, c.red, c.green, c.blue));
            
            if(i>=14 && i<=27){
                canvas.save();
                
                Matrix mtrx = canvas.getMatrix();
                
                mtrx.preRotate(31.0f,
                        (rect.right-rect.left)/2+rect.left+mMapOffset.x,
                        (rect.bottom-rect.top)/2+rect.top+mMapOffset.y);
                canvas.setMatrix(mtrx);
                
                canvas.drawRect(rect.left+mMapOffset.x,
                        rect.top+mMapOffset.y,
                        rect.right+mMapOffset.x,
                        rect.bottom+mMapOffset.y,
                        mShopPaint);
                
                canvas.restore();
            }else if(i>=8 && i<= 13){
                canvas.save();
                
                Matrix mtrx = canvas.getMatrix();
                
                mtrx.preRotate(-8.0f,
                        (rect.right-rect.left)/2+rect.left+mMapOffset.x,
                        (rect.bottom-rect.top)/2+rect.top+mMapOffset.y);
                canvas.setMatrix(mtrx);
                
                canvas.drawRect(rect.left+mMapOffset.x,
                        rect.top+mMapOffset.y,
                        rect.right+mMapOffset.x,
                        rect.bottom+mMapOffset.y,
                        mShopPaint);
                
                canvas.restore();
            }else{
                
                canvas.drawRect(rect.left+mMapOffset.x,
                        rect.top+mMapOffset.y,
                        rect.right+mMapOffset.x,
                        rect.bottom+mMapOffset.y,
                        mShopPaint);
            }
            
            canvas.drawText(String.valueOf(i+1),
                    (rect.right-rect.left)/2+rect.left+mMapOffset.x-2,
                    (rect.bottom-rect.top)/2+rect.top+mMapOffset.y+8,
                    mTextPaint);
        }
        
        canvas.restore();
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        int historySize = event.getHistorySize();
        int pointSize = event.getPointerCount();
        
        
        
        if(pointSize == 1){
            if(historySize == 1){
                float velocityX = event.getX(0) - event.getHistoricalX(0, 0);
                float velocityY = event.getY(0) - event.getHistoricalY(0, 0);
                
                onDrag(velocityX,velocityY);
            }else if(historySize == 0){
                
                for(int i=0;i<mShopRects.length;i++){
                    RectF rect = mShopRects[i];
                    RectF hitArea = new RectF(rect.left+mMapOffset.x,
                            rect.top+mMapOffset.y,
                            rect.right+mMapOffset.x,
                            rect.bottom+mMapOffset.y);
                    mMapMatrix.mapRect(hitArea);
                    
                    if(hitArea.contains(event.getX(), event.getY())){
                        if(mParentFragment.getFragmentManager().findFragmentByTag("dialog") == null){
                            StoreDialogFragment sdialog = StoreDialogFragment.newInstance(i);
                            sdialog.show(mParentFragment.getFragmentManager(), "dialog");
                        }
                        
                    }
                }
                
            }
        }else if(pointSize == 2){
            if(historySize == 1){
                float oldDistance = FloatMath.sqrt((event.getHistoricalX(0,0) - event.getHistoricalX(1,0))*(event.getHistoricalX(0,0)
                        - event.getHistoricalX(1,0))+(event.getHistoricalY(0,0) - event.getHistoricalY(1,0))*(event.getHistoricalY(0,0) - event.getHistoricalY(1,0)));
                float distance = FloatMath.sqrt((event.getX(0) - event.getX(1))*(event.getX(0) - event.getX(1))+(event.getY(0)
                        - event.getY(1))*(event.getY(0) - event.getY(1)));
                PointF center = new PointF((event.getX(0) + event.getX(1))/2,(event.getY(0) + event.getY(1))/2);
                
                onPinch(distance/oldDistance,center);
            }
        }
        
        return true;
    }
    
    private void onDrag(float velocityX,float velocityY){
        RectF frame = new RectF(mMapOffset.x,
                mMapOffset.y,
                mMapImage.getIntrinsicWidth()+mMapOffset.x,
                mMapImage.getIntrinsicHeight()+mMapOffset.y);
        mMapMatrix.mapRect(frame);
        
        if(frame.left+velocityX > 0){
            velocityX = -(frame.left+velocityX);
        }else if(frame.right+velocityX < mScreenWidth){
            velocityX = mScreenWidth - (frame.right+velocityX);
        }
        if(frame.top+velocityY > 0){
            velocityY = -(frame.top+velocityY);
        }else if(frame.bottom+velocityY < mScreenHeight){
            velocityY = mScreenHeight - (frame.bottom+velocityY); 
        }
        
        mMapOffset.offset((int)velocityX, (int)velocityY);
    }
    
    private void onPinch(float scale,PointF center){
        float tempScale = mMapScale * scale;
        if(tempScale > 2 || tempScale < 1) scale = 1.0f;
        
        mMapScale *= scale;
        
        mMapMatrix.postScale(scale, scale, center.x, center.y);
        
        //写真枠が画面外に出たときにonDragの移動処理を利用して正しい位置に移動させてる
        onDrag(0,0);
    }
    
    public void updateColors(ArrayList<StoreColorVO> colors){
        mColors = colors;
    }
}
