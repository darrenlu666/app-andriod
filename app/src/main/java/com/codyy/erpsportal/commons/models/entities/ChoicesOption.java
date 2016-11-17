package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

import java.util.List;

/**
 * Created by gujiajia on 2016/3/7.
 */
public class ChoicesOption extends FilterItem {
    /**
     * 待选择项
     */
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(choices);
    }

    public ChoicesOption() {
    }

    protected ChoicesOption(Parcel in) {
        super(in);
        this.choices = in.createTypedArrayList(Choice.CREATOR);
    }

    public static final Creator<ChoicesOption> CREATOR = new Creator<ChoicesOption>() {
        public ChoicesOption createFromParcel(Parcel source) {
            return new ChoicesOption(source);
        }

        public ChoicesOption[] newArray(int size) {
            return new ChoicesOption[size];
        }
    };
}
