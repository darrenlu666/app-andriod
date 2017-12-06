package com.codyy.erpsportal.resource.models.entities;

/**
 * Created by gujiajia on 2017/12/4.
 */

public class VideoClarity {

    /**
     * definition : NORMAL
     * downloadUrl : http://10.1.70.15:8080/res/view/mobile/download/normal/af1679a5e90148dabedba781e5487d8a/b77c23fd6bd34ee4b4061e873208ddeb.html
     * playUrl : http://10.1.70.15:8080/res/view/mobile/viewVideo/af1679a5e90148dabedba781e5487d8a/b77c23fd6bd34ee4b4061e873208ddeb/normal.html
     */

    private String definition;
    private String downloadUrl;
    private String playUrl;

    private String definitionName;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    @Override
    public String toString() {
        return definitionName;
    }
}
