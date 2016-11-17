package com.codyy.erpsportal.commons.models.entities.download;

import java.io.File;

public interface Transfer {

    String getDisplayName();

    String getStatus();

    int getProgress();

    long getSize();

    long getDownloadSpeed();

    boolean isComplete();

    void cancel();

    File getSavePath();

    boolean isDownloading();

    void cancel(boolean deleteData);

}
