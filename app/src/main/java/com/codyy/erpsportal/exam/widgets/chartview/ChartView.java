package com.codyy.erpsportal.exam.widgets.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.codyy.ScreenUtils;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eachann on 2016/3/6.
 */
public class ChartView extends View {
    private int r = 8;
    private int width;
    private int height;
    private int XPoint = 60;
    private int YPoint = 60;
    private int XScale = 8;  //刻度长度
    private int YScale = 75;
    private int XLength;
    private int YLength = 450;
    private int MaxDataSize = XLength / XScale;

    private List<Integer> data = new ArrayList<>();
    private String[] YLabel = new String[YLength / YScale + 1];


    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.width = ScreenUtils.getScreenWidth(context);
        this.height = UIUtils.dip2px(getContext(), 300f);
        XLength = width - 120;
        for (int i = 0; i < YLabel.length; i++) {
            YLabel[i] = String.valueOf(i * 25);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String[] XLabel = new String[7];
        // 画背景颜色
        Paint bg = new Paint();//// TODO: 2016/3/28 Avoid object allocations during draw/layout operations (preallocate and reuse instead) ,now do not fix
        bg.setColor(Color.WHITE);
        Rect bgR = new Rect(0, 0, width, height);//// TODO: 2016/3/28 Avoid object allocations during draw/layout operations (preallocate and reuse instead) ,now do not fix
        canvas.drawRect(bgR, bg);
        canvas.save();
        Paint paint = new Paint();//// TODO: 2016/3/28 Avoid object allocations during draw/layout operations (preallocate and reuse instead) ,now do not fix
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(Color.GRAY);
        //画Y轴
        //canvas.drawLine(XPoint, YPoint, XPoint, YPoint + YLength, paint);

        //Y轴箭头
        //canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint - YLength + 6, paint);  //箭头
        //canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint - YLength + 6, paint);
        paint.setTextSize(20f);
        //添加刻度和文字
        for (int i = 0; i < YLabel.length; i++) {
            canvas.drawCircle(XPoint, YPoint + YLength - i * YScale, r, paint);
//            canvas.drawLine(XPoint, YPoint + YLength - i * YScale, XPoint + 5, YPoint + YLength - i * YScale, paint);  //刻度
            canvas.drawText(YLabel[i], XPoint - 50, YPoint + YLength - i * YScale, paint);//文字
        }
        canvas.save();
        if (mMap.size() == 0) return;
        for (int i = 0; i < mMap.get(mPosition).size(); i++) {
            XLabel[i] = mMap.get(mPosition).get(i).getXLabel();
        }
        //画X轴
        // canvas.drawLine(XPoint, YPoint + YLength, XPoint + XLength, YPoint + YLength, paint);
        for (int i = 0; i < XLabel.length; i++) {
            if (XLabel[i] == null) break;
            canvas.drawCircle(XPoint + 120 * (i + 1), YPoint + YLength, r, paint);
//            canvas.drawLine(XPoint + 120 * (i + 1), YPoint + YLength, XPoint + 120 * (i + 1), YPoint + YLength + 10, paint);  //刻度
            canvas.drawText(XLabel[i], XPoint + 120 * (i + 1) - 20, YPoint + YLength + 40, paint);//文字
        }
        for (ChartBean bean : mMap.get(mPosition)) {
            paint.setColor(Color.parseColor("#FF3E74BE"));
            canvas.drawCircle(bean.getX(), bean.getY(), r, paint);//画折线图的点
        }
        canvas.save();
//        List<Float> floats = new ArrayList<>();
        paint.setStrokeWidth(UIUtils.dip2px(getContext(), 2f));//将点连成折线图
        for (int i = 0; i < mMap.get(mPosition).size(); i++) {
//            floats.add(mChartBeans.get(i).getX());
//            floats.add(mChartBeans.get(i).getY());
            if (i + 1 < mMap.get(mPosition).size())
                canvas.drawLine(mMap.get(mPosition).get(i).getX(), mMap.get(mPosition).get(i).getY(), mMap.get(mPosition).get(i + 1).getX(), mMap.get(mPosition).get(i + 1).getY(), paint);
        }
       /* float[] pts = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            pts[i] = floats.get(i);
        }
        paint.setStrokeWidth(UIUtils.dip2px(getContext(), 2f));//将点连成折线图
        canvas.drawLines(pts, paint);*/
        canvas.restore();
    }

    /*  @Override
      public boolean onTouchEvent(MotionEvent event) {
  //        getParent().requestDisallowInterceptTouchEvent(true);//拦截事件,当前消费
          return mGesture.onTouchEvent(event);
      }
  */
    private List<ChartBean> mChartBeans = new ArrayList<>();
    private Map<Integer, List<ChartBean>> mMap = new HashMap<>();
    private int mPosition;
    private int mTotalPage;

    public void setDataToView(JSONObject response) {
        mPosition = 0;
        mTotalPage = 0;
        try {
            mMap = new HashMap<>();
            mChartBeans = new ArrayList<>();
            if (response == null) return;
            JSONArray array = response.getJSONArray("list");
            mTotalPage = array.length() % 7 == 0 ? array.length() : array.length() / 7 + 1;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ChartBean bean = new ChartBean();
                bean.setExamResultId(object.optString("examResultId"));
                bean.setExamTaskId(object.optString("examTaskId"));
                bean.setScore(object.getString("score"));
                bean.setTime(object.optLong("time"));
                bean.setY(YPoint + YLength - ((object.isNull("score") || object.optString("score").equals("")) ? 0 : Float.parseFloat(object.getString("score")) / 25 * 75));
//                bean.setX(XPoint + 120 * (i + 1));
                bean.setXLabel(DateUtil.getDateStr(object.optLong("time"), "MM-dd"));
//                XLabel[i] = DateUtil.getDateStr(object.optLong("time"), "MM-dd");
                mChartBeans.add(bean);
            }
            if (array.length() % 7 == 0) {
                for (int j = 0; j < mTotalPage; j++) {
                    mMap.put(j, mChartBeans.subList(7 * j, (j + 1) * 7));
                    for (int k = 0; k < mMap.get(j).size(); k++) {
                        mMap.get(j).get(k).setX(XPoint + 120 * (k + 1));
                    }
                }
            } else {
                for (int j = 0; j < mTotalPage; j++) {
                    if (j + 1 == mTotalPage) {
                        mMap.put(j, mChartBeans.subList(7 * j, array.length()));
                    } else {
                        mMap.put(j, mChartBeans.subList(7 * j, (j + 1) * 7));
                    }
                    for (int k = 0; k < mMap.get(j).size(); k++) {
                        mMap.get(j).get(k).setX(XPoint + 120 * (k + 1));
                    }
                }
            }
            invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDataTypeToView(JSONObject response, String type) {
        mPosition = 0;
        mTotalPage = 0;
        try {
            mMap = new HashMap<>();
            mChartBeans = new ArrayList<>();
            if (response == null) {
                return;
            }
            JSONArray array = response.getJSONArray("list");
            JSONArray arrayFilter = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.optString("type").equals(type)) {
                    arrayFilter.put(object);
                }
            }
            for (int i = 0; i < arrayFilter.length(); i++) {
                JSONObject object = arrayFilter.getJSONObject(i);
                ChartBean bean = new ChartBean();
                bean.setExamResultId(object.optString("examResultId"));
                bean.setExamTaskId(object.optString("examTaskId"));
                bean.setScore(object.getString("score"));
                bean.setTime(object.optLong("time"));
                bean.setY(YPoint + YLength - ((object.isNull("score")|| object.optString("score").equals("")) ? 0 : Float.parseFloat(object.getString("score")) / 25 * 75));
                bean.setX(XPoint + 120 * (i + 1));
                bean.setXLabel(DateUtil.getDateStr(object.optLong("time"), "MM-dd"));
//                XLabel[i] = DateUtil.getDateStr(object.optLong("time"), "MM-dd");
                mChartBeans.add(bean);
            }
            mTotalPage = mChartBeans.size() % 7 == 0 ? mChartBeans.size() / 7 : mChartBeans.size() / 7 + 1;
            if (arrayFilter.length() % 7 == 0) {
                for (int j = 0; j < mTotalPage; j++) {
                    mMap.put(j, mChartBeans.subList(7 * j, (j + 1) * 7));
                    for (int k = 0; k < mMap.get(j).size(); k++) {
                        mMap.get(j).get(k).setX(XPoint + 120 * (k + 1));
                    }
                }
            } else {
                for (int j = 0; j < mTotalPage; j++) {
                    if (j + 1 == mTotalPage) {
                        mMap.put(j, mChartBeans.subList(7 * j, mChartBeans.size()));
                    } else {
                        mMap.put(j, mChartBeans.subList(7 * j, (j + 1) * 7));
                    }
                    for (int k = 0; k < mMap.get(j).size(); k++) {
                        mMap.get(j).get(k).setX(XPoint + 120 * (k + 1));
                    }
                }
            }
            invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    float mEventx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);//拦截事件,当前消费
                mEventx = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() == mEventx) {
                    for (ChartBean bean : mChartBeans) {
                        if (Math.pow(event.getX() - bean.getX(), 2) + Math.pow(event.getY() - bean.getY(), 2) <= Math.pow(r + 50, 2)) {
                            mListener.onPointClickListener(bean);
                        }
                    }
                } else if (event.getX() - mEventx > 50) {
                    if (mPosition - 1 >= 0) {
                        mPosition--;
                        invalidate();
                    }
                } else if (event.getX() - mEventx < -50) {
                    if (mPosition + 1 < mTotalPage) {
                        mPosition++;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    private OnPointClickListener mListener;

    public interface OnPointClickListener {
        void onPointClickListener(ChartBean bean);
    }

    public void setOnPointClickListener(OnPointClickListener listener) {
        this.mListener = listener;
    }

    public class ChartBean {
        String examTaskId;
        String examResultId;
        String score;
        long time;
        float y;
        float x;
        String date;
        String XLabel;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getExamResultId() {
            return examResultId;
        }

        public void setExamResultId(String examResultId) {
            this.examResultId = examResultId;
        }

        public String getExamTaskId() {
            return examTaskId;
        }

        public void setExamTaskId(String examTaskId) {
            this.examTaskId = examTaskId;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public String getXLabel() {
            return XLabel;
        }

        public void setXLabel(String XLabel) {
            this.XLabel = XLabel;
        }
    }
}
