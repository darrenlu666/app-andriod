package com.codyy.erpsportal.commons.models.entities.evaluation;

import java.util.List;

/**
 * Created by poe on 16-9-22.
 */
public class EvaluationVideoParse {
    private String result;
    private List<EvaluationVideo> watchPath;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<EvaluationVideo> getWatchPath() {
        return watchPath;
    }

    public void setWatchPath(List<EvaluationVideo> watchPath) {
        this.watchPath = watchPath;
    }
}
