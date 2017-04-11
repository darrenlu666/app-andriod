package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.dao.ServerAddressDao;
import com.codyy.url.URLConfig;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_PRIVATE;

/**
 * 修改服务器地址弹窗
 * Created by gujiajia on 2016/12/20.
 */

public class ChangeServerDialog extends DialogFragment {

    private AutoCompleteTextView mServerAddressEt;

    private ServerChangedListener mServerChangedListener;

    public static ChangeServerDialog newInstance(){
        ChangeServerDialog changeServerDialog = new ChangeServerDialog();
        changeServerDialog.setStyle(STYLE_NO_TITLE, 0);
        return changeServerDialog;
    }

    public void setServerChangedListener(ServerChangedListener serverChangedListener) {
        mServerChangedListener = serverChangedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout((int) (0.8f * getResources().getDisplayMetrics().widthPixels + 0.5f),
                LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_server, container, false);
        mServerAddressEt = (AutoCompleteTextView) view.findViewById(R.id.et_server_address);
        Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        Button confirmBtn = (Button) view.findViewById(R.id.btn_confirm);

        List<String> serverAddressList = ServerAddressDao.findServerAddresses(getContext());
        if (serverAddressList != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                    , R.layout.item_dropdown_urls
                    , serverAddressList);
            mServerAddressEt.setAdapter(adapter);
        }

        mServerAddressEt.setText(URLConfig.BASE);

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverAddress = mServerAddressEt.getText().toString();
                String newBaseUrl = null;
                boolean serverChanged = false;
                if (TextUtils.isEmpty(serverAddress.trim())) {
                    Toast.makeText(getActivity(), "服务地址为空，保持原地址", Toast.LENGTH_SHORT).show();
                } else if (serverAddress.startsWith("http://") || serverAddress.startsWith("https://")){
                    newBaseUrl = serverAddress;
                    serverChanged = true;
                } else {
                    newBaseUrl = "http://" + serverAddress;
                    serverChanged = true;
                }
                if (newBaseUrl != null && !URLConfig.BASE.equals(newBaseUrl)) {
                    URLConfig.updateUrls(newBaseUrl);
                    saveBaseUrl();
                }
                saveServerAddress();
                dismiss();
                if (serverChanged && mServerChangedListener != null) {
                    mServerChangedListener.onServerChangedListener();
                }
            }
        });
        return view;
    }

    private void saveBaseUrl() {
        SharedPreferences sp = getContext().getSharedPreferences("url", MODE_PRIVATE);
        sp.edit().putString("base", URLConfig.BASE).apply();
    }

    /**
     * 保存历史填写地址，用于配置服务器地址时下拉补全
     */
    private void saveServerAddress() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new BaseUrlSaveTask(getContext().getApplicationContext()));
    }

    private static class BaseUrlSaveTask implements Runnable{

        private WeakReference<Context> mContextWeakRef;

        private BaseUrlSaveTask(Context context){
            mContextWeakRef = new WeakReference<>(context);
        }

        @Override
        public void run() {
            Context context = mContextWeakRef.get();
            if (context != null) {
                ServerAddressDao.saveServerAddress(context,
                        URLConfig.BASE);
            }
        }
    }

    public interface ServerChangedListener{
        void onServerChangedListener();
    }
}
