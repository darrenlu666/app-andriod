package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.TeacherInfo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评课设置老师
 * Created by kmdai on 2015/5/18.
 */
public class SetTeacherActivity extends Activity {
    /**
     * 获取学科
     */
    private static final int GET_SUBJECT_SUCESS = 0x001;

    private static final int GET_SCHOOL_TEACHER = 0x002;
    /**
     * 设置老师成功
     */
    private static final int ADD_TEACHER_SUCESS = 0x003;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private AssessmentDetails assessmentDetails;
    private ListView mListView;
    private UserInfo userInfo;
    private List<SubTea> teachers;
    private List<SubTea> subjects;
    private List<TeacherInfo> teacherInfos;
    private LinearLayout mLinearLayout;
    private List<View> views;
    private DialogUtil dialogUtil;
    /**
     * 没有信息提示
     */
    private TextView mTextView;
    /**
     * 等待弹框
     */
    private DialogUtil mDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_teacher);
        assessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
        userInfo = getIntent().getParcelableExtra("userInfo");
        init();
        getSubject();
        dialogUtil = new DialogUtil(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtil.cancle();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = new HashMap<>();
                data.put("uuid", userInfo.getUuid());
                data.put("evaluationId", assessmentDetails.getEvaluationId());
                String baseUserIds = "";
                for (View view : views) {
                    baseUserIds += view.getTag().toString() + ",";
                }
                data.put("baseUserIds", baseUserIds);
                httpConnect(URLConfig.ADD_EVALUATION_TEACHER, data, ADD_TEACHER_SUCESS);
            }
        });
    }

    /**
     * view初始化
     */
    private void init() {
        mDialogUtil = new DialogUtil(this);
        views = new ArrayList<>();
        teachers = new ArrayList<>();
        subjects = new ArrayList<>();
        teacherInfos = new ArrayList<>();
        mSender = new RequestSender(this);
        mListView = (ListView) findViewById(R.id.set_teacher_listview);
        mTextView = (TextView) findViewById(R.id.set_teacher_no_info);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDialogUtil.showDialog();
                getTeacher(position);
            }
        });
        mLinearLayout = (LinearLayout) findViewById(R.id.set_teacher_linerlayout_teacher);
        findViewById(R.id.set_teacher_send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTeacher();
            }
        });
    }

    /**
     * 获得学科和老师
     */
    private void getSubject() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        httpConnect(URLConfig.GET_EVASUBJTEA, data, GET_SUBJECT_SUCESS);
    }

    private void getTeacher(int i) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        data.put("subjectId", subjects.get(i).getId());
        httpConnect(URLConfig.GET_SCHOOL_TEACHER, data, GET_SCHOOL_TEACHER);
    }

    /**
     * 网络连接
     * 成功 message atg1=0，失败=1
     * message.obg是json数据
     *
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mDialogUtil.cancle();
                switch (msg) {
                    case GET_SUBJECT_SUCESS:
                        setGetSubjectTea(response);
                        mListView.setAdapter(adapter);
                        adddTeacherInit();
                        if (subjects.size() == 0) {
                            mTextView.setVisibility(View.VISIBLE);
                        } else {
                            mTextView.setVisibility(View.GONE);
                        }
                        break;
                    case GET_SCHOOL_TEACHER:
                        teacherInfos.clear();
                        TeacherInfo.getTeacherInfo(response, teacherInfos);
                        mListView.setAdapter(teacherAdapter);
                        if (teacherInfos.size() == 0) {
                            mTextView.setVisibility(View.VISIBLE);
                        } else {
                            mTextView.setVisibility(View.GONE);
                        }
                        break;
                    case ADD_TEACHER_SUCESS:
                        JSONObject object = response;
                        if ("success".equals(object.optString("result"))) {
                            ToastUtil.showToast(SetTeacherActivity.this, "设置老师成功");
                            setResult(AssessmentDetailsActivity.REQUEST_CODE_SET_TEACHER);
                            finish();
                        } else {
                            ToastUtil.showToast(SetTeacherActivity.this, "设置老师失败");
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mDialogUtil.cancle();
                ToastUtil.showToast(SetTeacherActivity.this, getResources().getString(R.string.net_error));
            }
        }));
    }


    private void adddTeacherInit() {
        mLinearLayout.removeAllViews();
        for (int i = 0; i < teachers.size(); i++) {
            final SubTea subTea = teachers.get(i);
            addTeacher(subTea);
        }
    }

    private void addTeacher(final SubTea subTea) {
        final View view = getLayoutInflater().inflate(R.layout.teacher_select, null);
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.teacher_select_heart_image);
        ImageFetcher.getInstance(this).fetchSmall(simpleDraweeView,subTea.getHeadPic());
//        simpleDraweeView.setImageURI(Uri.parse(subTea.getHeadPic()));
        TextView textView = (TextView) view.findViewById(R.id.teacher_select_name);
        textView.setText(Html.fromHtml(subTea.getName()));
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearLayout.removeView(view);
                teachers.remove(subTea);
                views.remove(view);
                ToastUtil.showToast(SetTeacherActivity.this, "已删除老师：" + subTea.getName());
                teacherAdapter.notifyDataSetChanged();
            }
        });
        view.setTag(subTea.getId());
        views.add(view);
        mLinearLayout.addView(view);
    }

    class SubTea {
        String name;
        String id;
        String headPic;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }
    }

    /**
     *
     */
    private void setGetSubjectTea(JSONObject object) {
        if ("success".equals(object.opt("result"))) {
            JSONArray jsonArray = object.optJSONArray("subjects");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                SubTea subTea = new SubTea();
                subTea.setHeadPic(getPic(jsonObject.optString("subjectPic")));
                subTea.setId(jsonObject.optString("id"));
                subTea.setName(jsonObject.optString("name"));
                subjects.add(subTea);
            }
            jsonArray = object.optJSONArray("invitedTeachers");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                SubTea subTea = new SubTea();
                subTea.setHeadPic(jsonObject.optString("headPic"));
                subTea.setId(jsonObject.optString("baseUserId"));
                subTea.setName(jsonObject.optString("realName"));
                teachers.add(subTea);
            }
        }
    }
    public static String getPic(String str) {
        if (!"null".equals(str)) {
            return str;
        }
        return URLConfig.BASE + "/images/subjectDefault.png";
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return subjects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new viewHolder();
                convertView = getLayoutInflater().inflate(R.layout.set_teacher_item, null);
//                viewHolder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.set_teacher_heart_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.set_teacher_text_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (viewHolder) convertView.getTag();
            }
//            viewHolder.simpleDraweeView.setImageURI(Uri.parse(subjects.get(position).getHeadPic()));
            viewHolder.textView.setText(Html.fromHtml(subjects.get(position).getName()));
            return convertView;
        }
    };

    class viewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView textView;
    }

    private BaseAdapter teacherAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return teacherInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final viewHolder1 viewHolder;
            if (convertView == null) {
                viewHolder = new viewHolder1();
                convertView = getLayoutInflater().inflate(R.layout.set_teacher_item, null);
                viewHolder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.set_teacher_heart_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.set_teacher_text_name);
                viewHolder.title = (TextView) convertView.findViewById(R.id.set_teacher_text_title);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.set_teacher_check_btn);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (viewHolder1) convertView.getTag();
            }
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if (position == 0) {
                viewHolder.title.setVisibility(View.VISIBLE);
                viewHolder.title.setText(teacherInfos.get(position).getClassLevelName());
            } else {
                if (teacherInfos.get(position - 1).getClassLevelName().equals(teacherInfos.get(position).getClassLevelName())) {
                    viewHolder.title.setVisibility(View.GONE);
                } else {
                    viewHolder.title.setText(teacherInfos.get(position).getClassLevelName());
                    viewHolder.title.setVisibility(View.VISIBLE);
                }
            }
            RoundingParams roundingParams = viewHolder.simpleDraweeView.getHierarchy().getRoundingParams();
            roundingParams.setRoundAsCircle(false);
            viewHolder.simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);
            ImageFetcher.getInstance(SetTeacherActivity.this).fetchSmall(viewHolder.simpleDraweeView, teacherInfos.get(position).getHeadPic());
//            viewHolder.simpleDraweeView.setImageURI(Uri.parse(GetHeardUrl(teacherInfos.get(position).getHeadPic())));
            viewHolder.textView.setText(Html.fromHtml(teacherInfos.get(position).getRealName()));
            if (hasSelect(teacherInfos.get(position).getBaseUserId())) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.checkBox.isChecked()) {
                        SubTea subTea = new SubTea();
                        subTea.setName(teacherInfos.get(position).getRealName());
                        subTea.setId(teacherInfos.get(position).getBaseUserId());
                        subTea.setHeadPic(teacherInfos.get(position).getHeadPic());
//                        teachers.add(subTea);
                        addTeacher(subTea);
                    } else {
                        removeTeacher(teacherInfos.get(position).getBaseUserId());
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    };

    private void removeTeacher(String id) {
        for (View view : views) {
            if (id.equals(view.getTag().toString())) {
                views.remove(view);
                mLinearLayout.removeView(view);
                break;
            }
        }
    }

    private boolean hasSelect(String id) {
        for (View view : views) {
            if (id.equals(view.getTag().toString())) {
                return true;
            }
        }
        return false;
    }

    class viewHolder1 {
        SimpleDraweeView simpleDraweeView;
        TextView textView;
        TextView title;
        CheckBox checkBox;
    }

    private void setTeacher() {
        if (views.size() != 0) {
            dialogUtil.showDialog("确定设置参与老师吗？");
        } else {
            ToastUtil.showToast(this, "请选择参与老师！");
        }
    }

    public void backBtn(View view) {
        back();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return true;
    }

    private void back() {
        if (mListView.getAdapter() == teacherAdapter) {
            mListView.setAdapter(adapter);
            if (subjects.size() == 0) {
                mTextView.setVisibility(View.VISIBLE);
            } else {
                mTextView.setVisibility(View.GONE);
            }
        } else {
            finish();
            overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
        }
    }

    @Override
    protected void onDestroy() {
        mSender.stop();
        super.onDestroy();
    }
}
