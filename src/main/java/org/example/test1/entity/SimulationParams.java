package org.example.test1.entity;



public class SimulationParams {
    private Integer quality;  // 新增
    private Integer attitude; // 新增
    private Integer content;  // 新增

    private Integer method;   // 对应前端的 method (原 interaction)
    private Integer effect;   // 对应前端的 effect (原 tutoring)

    public SimulationParams(Integer quality, Integer attitude, Integer content, Integer method, Integer effect) {
        this.quality = quality;
        this.attitude = attitude;
        this.content = content;
        this.method = method;
        this.effect = effect;
    }

    public SimulationParams() {
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Integer getAttitude() {
        return attitude;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getContent() {
        return content;
    }

    public void setContent(Integer content) {
        this.content = content;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getEffect() {
        return effect;
    }

    public void setEffect(Integer effect) {
        this.effect = effect;
    }
}