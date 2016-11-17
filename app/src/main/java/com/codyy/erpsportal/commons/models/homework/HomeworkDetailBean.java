package com.codyy.erpsportal.commons.models.homework;

import java.util.List;

/**
 * Created by ningfeng on 2015/8/7.
 */
public class HomeworkDetailBean {

    /**
     * result : success
     * date : 2015-08-06
     * total : 2
     * teacherName : 王琦_/%琦2
     * subject : 数学
     * schoolName : 园区%一中
     * list : [{"total":10,"list":[{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_58b2e383-d5b5-4bc2-8c43-14b93b1191e6.review.jpg","classWorkId":"3296f343f2d04af1b6781b6f524682e5"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_743d04fb-f02d-4dbb-a530-689b61c488f7.jpg","classWorkId":"2b30f712b48544b6a7c208f8274df6b0"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_0b215358-e40d-44b0-a50c-98b61075b23f.jpg","classWorkId":"4320a602286643cbb2afd3de18ea3739"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_27498af0-c332-40da-b811-39a8bb5e1327.jpg","classWorkId":"643884fc10ea42d8964a2de9410c357f"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_9402e218-469b-44bf-a5fe-eb820da569ff.JPG","classWorkId":"9351b7687bc643ab971a2376b4b128a3"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_6a0f4901-f42b-4393-a0e9-126da4036358.JPG","classWorkId":"8f678317e46f48f7b241a118a23e9fbe"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_b34c3f1f-9905-40e0-ac24-013a02335f4a.jpg","classWorkId":"2f4c50ac750d4196b5c696346e44b97c"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_3fa64863-b4c7-44a6-90e0-611f08bdfd73.jpg","classWorkId":"e1714ceaa9ef4c0b94f6cafe3ef6ae68"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_c3f708e5-3a07-48e8-9af0-e26967fbc1cb.jpg","classWorkId":"16aa32ac85a04a32a2307a6010ca1f99"},{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_10be8c16-bd25-4a49-a2e6-95ea1069c60f.jpg","classWorkId":"4e510c6c38674bb59d70cdb3f8c0bbae"}]},{"total":1,"list":[{"imgUrl":"/classWorkImages/aee4675675824495b846e4a64700462a_b8263634618c4a649588ba5791e32ce7_a3aba910-8637-45cb-a3b7-04183ba42ae3.jpg","classWorkId":"b2d7e48d33cb48ffad1970c1afa09d5c"}]}]
     * order : 六
     */
    private String result;
    private String date;
    private int total;
    private String teacherName;
    private String subject;
    private String schoolName;
    private List<List<HomeworkBean>> list;
    private String order;

    public void setResult(String result) {
        this.result = result;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setList(List<List<HomeworkBean>> list) {
        this.list = list;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

    public int getTotal() {
        return total;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public List<List<HomeworkBean>> getList() {
        return list;
    }

    public String getOrder() {
        return order;
    }

    public static class HomeworkBean {
        /**
         * imgUrl : /classWorkImages/aee4675675824495b846e4a64700462a_dcd54064f866447faddc092a4ea29b47_58b2e383-d5b5-4bc2-8c43-14b93b1191e6.review.jpg
         * classWorkId : 3296f343f2d04af1b6781b6f524682e5
         */
        private String imgUrl;
        private String classWorkId;

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public void setClassWorkId(String classWorkId) {
            this.classWorkId = classWorkId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getClassWorkId() {
            return classWorkId;
        }
    }
}
