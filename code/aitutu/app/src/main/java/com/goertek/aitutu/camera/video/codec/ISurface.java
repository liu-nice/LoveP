package com.goertek.aitutu.camera.video.codec;

public interface ISurface {

    /**
     * 把数据加入队列
     * @param data
     */
   void offer(byte[] data);

    /**
     * 从队列中取出数据
     * @return
     */
    byte[] poll();

    /**
     * 把视频的宽 高 帧率 回调出去
     * @param width
     * @param height
     * @param fps
     */
    void setVideoParamerters(int width, int height, int fps);

}

