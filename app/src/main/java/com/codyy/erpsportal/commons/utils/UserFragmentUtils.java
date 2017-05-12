package com.codyy.erpsportal.commons.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.widgets.MyBottomSheet;
import com.codyy.erpsportal.commons.widgets.SimpleListDialog;
import com.codyy.erpsportal.groups.controllers.viewholders.SingleTextViewHolder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.SimpleHorizonDivider;
import com.codyy.tpmp.filterlibrary.widgets.SimpleRecyclerView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 为"我的"界面服务工具类
 * Created by poe on 16-3-3.
 */
public class UserFragmentUtils {
    public static final String TAG = "UserFragmentUtils";

    /**
     * 构造选择班级ＢｏｔｔｏｍＳｈｅｅｔ　.
     * @param act
     * @param userInfo
     * @param classContList
     * @param studentList
     * @return
     */
    public static MyBottomSheet createBottomSheet(final Activity act , final UserInfo userInfo , final List<ClassCont> classContList , final List<Student> studentList,BaseRecyclerAdapter.OnItemClickListener<String> clickListener){
        MyBottomSheet classDialog = new MyBottomSheet(act);
        View contentView = LayoutInflater.from(act).inflate(R.layout.dialog_bottom_sheet_single_list, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_title);
        SimpleRecyclerView recyclerView = (SimpleRecyclerView) contentView.findViewById(R.id.recycler_view);
        titleTv.setText("选择进入的班级");
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        recyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(act));
        BaseRecyclerAdapter<String,SingleTextViewHolder> adapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<SingleTextViewHolder>() {
            @Override
            public SingleTextViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new SingleTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_simple_text,parent,false));
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
        adapter.setOnItemClickListener(clickListener);
        recyclerView.setAdapter(adapter);
        List<String> classes = UserFragmentUtils.getClassRoom(userInfo,classContList,studentList);
        if(classes!=null && classes.size()>0){
            adapter.setData(classes);
        }
        classDialog.setContentView(contentView);
        View parent = (View) contentView.getParent();
        final BottomSheetBehavior classBehavior = BottomSheetBehavior.from(parent);
        contentView.measure(0,0);
//        classBehavior.setPeekHeight(act.getResources().getDisplayMetrics().heightPixels/3);
        classBehavior.setPeekHeight(contentView.getMeasuredHeight());
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        layoutParams.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(layoutParams);
        classDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                classBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return  classDialog;
    }
    /**
     * 获取不同的班级信息　.
     * @param userInfo
     * @param classContList
     * @param studentList
     */
    public static List<String> getClassRoom(UserInfo userInfo , List<ClassCont> classContList ,List<Student> studentList){
        ArrayList<String> classes = new ArrayList<>();
        switch (userInfo.getUserType()){
            case UserInfo.USER_TYPE_TEACHER://教师
                if(null != classContList && classContList.size() > 0){
                    for(int i = 0 ; i<classContList.size();i++){
                        ClassCont student = classContList.get(i);
                        classes.add(UIUtils.filterNull(student.getClassLevelName()+student.getBaseClassName()));
                    }
                }
                break;
            case UserInfo.USER_TYPE_STUDENT://学生
                break;
            case UserInfo.USER_TYPE_PARENT://家长
                if(studentList != null && studentList.size() >0){
                    for (int i = 0 ; i<studentList.size();i++) {
                        classes.add(UIUtils.filterNull(studentList.get(i).getClassLevelName()+studentList.get(i).getClassName()));
//                        studentList.add(studentList.get(i));
                    }
                }
                break;
        }
        return classes ;
    }


    /**
     * 构造班级类信息
     * @param students
     * @return
     */
    public static ArrayList<ClassCont> constructClassListInfo(List<Student> students) {
        ArrayList<ClassCont> result = new ArrayList<>();

        if(null != students && students.size() >0) {
            for(Student student : students){
                ClassCont cc = new ClassCont(student.getClassId() , student.getClassName() , student.getClassLevelName());
                result.add(cc);
            }
        }
        return result;
    }

    /**
     * 选择不同孩子学校跳转到不同的学校首页
     * @param studentList
     */
    public static void selectSchool(FragmentManager fragmentManager, final List<Student> studentList){
        ArrayList<String> classes = new ArrayList<>();

        //老师的班级信息

        if(studentList != null && studentList.size() >0){
            //过滤重复的
            HashMap<String ,Student> singleHash = new HashMap<>();
            for(Student student : studentList){
                singleHash.put(student.getSchoolId() , student) ;
            }
            //预防顺序错乱!
            studentList.clear();
            for (String s : singleHash.keySet()) {
                classes.add(UIUtils.filterNull(singleHash.get(s).getSchoolName()));
                studentList.add(singleHash.get(s));
            }
        }

        if(classes.size() > 0 ){
            final SimpleListDialog myDialog = SimpleListDialog.newInstance("选择进入的学校",classes );
            myDialog.setOnItemClickListener(new SimpleListDialog.OnItemClickListener() {
                @Override
                public void onClick(int pos) {
                    if(pos < studentList.size()){
                        // TODO: 16-5-30 跳转到首页  pass #schoolId
                        String schoolId = studentList.get(pos).getSchoolId();
                        if (TextUtils.isEmpty(schoolId)) return;
                        MainActivity.start(myDialog.getActivity(), schoolId);
                    }else{
                        LogUtils.log(TAG+" :"+"班级index越界 {@link PersonActivity: line 148");
                    }
                }
            });
            myDialog.showAllowStateLoss(fragmentManager,"classes");
        }
    }


    /**
     * 家长-设置班级显示
     * 根据老师所带班级
     */
    public static String  getTeacherClassName(List<ClassCont> classList ) {
        if(null == classList || classList.size() == 0) return "";
        StringBuilder sb_class = new StringBuilder();
        for(int i=0 ; i <classList.size();i++){
            ClassCont cc = classList.get(i);
            sb_class.append(UIUtils.filterNull(cc.getClassLevelName()+cc.getBaseClassName()));
            if(i<(classList.size()-1)){
                sb_class.append("、");
            }
        }
        if(!TextUtils.isEmpty(sb_class)){
            sb_class.delete(sb_class.length()-1,sb_class.length()-1);
        }
        return  sb_class.toString();
    }

    /**
     * 根据家长的孩子信息返回班级信息字段
     * @param studentList
     * @return
     */
    public static String getStudentClassName(List<Student> studentList) {
        if(null == studentList || studentList.size() == 0) return "";
        StringBuilder sb_class = new StringBuilder();
        for(int i = 0 ;i < studentList.size(); i++){
            Student student = studentList.get(i);
            if(!sb_class.toString().contains(student.getClassName())){
                if(i>0){
                    sb_class.append("、");
                }
                sb_class.append(student.getClassLevelName()+student.getClassName());
            }
        }
        return sb_class.toString();
    }

    /**
     * 根据家长的孩子信息返回学校信息字段
     * @param studentList
     * @return
     */
    public static String getStudentSchoolName(List<Student> studentList) {
        if(null == studentList || studentList.size() == 0) return "";
        StringBuilder sb_school = new StringBuilder();
        for(int i = 0 ;i < studentList.size(); i++){
            Student student = studentList.get(i);
            if(!sb_school.toString().contains(student.getSchoolName())){
                if(i>0){
                    sb_school.append("、");
                }
                sb_school.append(student.getSchoolName());
            }
        }
        return sb_school.toString();
    }

    //裁剪照片
    public static void cropImageUri(Fragment context , String imageHead, Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + imageHead));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        context.startActivityForResult(intent, requestCode);
    }

    //裁剪照片
    public static void cropImageUri2(Activity context , String imageHead, Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + imageHead));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 创建缓存目录
     * @return
     */
    public static File createDir() {
        String mCacheLoc = Environment.getExternalStorageDirectory().getPath();
        File file = new File(mCacheLoc + Constants.FOLDER_HEAD_PIC + "/");
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        return file;
    }
    /**
     * Android上传文件到服务端
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String RequestURL) {
        Cog.i(TAG,"上传地址："+RequestURL);
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10 * 1000);
            conn.setConnectTimeout(10 * 1000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuilder sb = new StringBuilder()
                    .append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\"")
                        .append(file.getName())
                        .append("\"")
                        .append(LINE_END)
                        .append("Content-Type: application/octet-stream; charset=").append("utf-8").append(LINE_END)
                        .append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len ;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                InputStream input = conn.getInputStream();
                StringBuilder sb1 = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Cog.d("upload image :",result.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
