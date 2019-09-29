package com.goertek.aitutu.camera.video.codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 解码工具类
 * 解码完成后的数据 通过 ISurface 回调出去
 */
public class VideoCodec {


    private ISurface mISurface;
    private String mPath;
    private MediaExtractor mMediaExtractor;
    private int mWidth;
    private int mHeight;
    private int mFps;
    private MediaCodec mMediaCodec;
    private boolean isCodeing;
    private byte[] outData;
    private CodecTask mCodecTask;

    /**
     * 要在prepare之前调用
     * @param surface
     */
    public void setDisplay(ISurface surface){
        mISurface = surface;
    }


    /**
     * 设置要解码的视频地址
     * @param path
     */
    public void setDataSource(String path){
        mPath = path;
    }


    /**
     *
     * 准备方法
     */
    public void prepare(){
        //MediaMuxer:复用器 封装器
        //解复用(解封装)
        mMediaExtractor = new MediaExtractor();
        try {
            //把视频给到 解复用器
            mMediaExtractor.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int videoIndex = -1;
        MediaFormat videoMediaFormat = null;
        // mp4 1路音频 1路视频
        int trackCount = mMediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            //获得这路流的格式
            MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
            //选择视频 获得格式
            // video/  audio/
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("video/")){
                videoIndex = i;
                videoMediaFormat = mediaFormat;
                break;
            }
        }
        //默认是-1
        if (null != videoMediaFormat){
            //解码 videoIndex 这一路流
            mWidth = videoMediaFormat.getInteger(MediaFormat.KEY_WIDTH);
            mHeight = videoMediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
            mFps = 20;
            if (videoMediaFormat.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                mFps = videoMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            }
            // 个别手机  小米(x型号) 解码出来不是yuv420p
            //所以设置 解码数据格式 指定为yuv420
            videoMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo
            .CodecCapabilities.COLOR_FormatYUV420Planar);

            try {
                //创建一个解码器
                mMediaCodec = MediaCodec.createDecoderByType(videoMediaFormat.getString(MediaFormat.KEY_MIME));
                mMediaCodec.configure(videoMediaFormat,null,null,0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //选择流 后续读取这个流
            mMediaExtractor.selectTrack(videoIndex);
        }
        if (null != mISurface){
            mISurface.setVideoParamerters(mWidth,mHeight,mFps);
        }
    }


    /**
     * 开始解码
     */
    public void start(){
        isCodeing = true;
        //接收 解码后的数据 yuv数据大小是 w*h*3/2
        outData = new byte[mWidth * mHeight * 3 / 2];
        mCodecTask = new CodecTask();
        mCodecTask.start();
    }

    /**
     * 停止
     */
    public void stop(){
        isCodeing = false;
        if (null != mCodecTask && mCodecTask.isAlive()){
            try {
                mCodecTask.join(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //3s后线程还没结束
            if (mCodecTask.isAlive()){
                //中断掉
                mCodecTask.interrupt();
            }
            mCodecTask = null;
        }

    }


    /**
     * 解码线程
     */
    private class CodecTask extends Thread {

        @Override
        public void run() {
            if (null == mMediaCodec) {
                return;
            }
              // 开启
            mMediaCodec.start();
            boolean isEOF = false;
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            //是否中断线程
            while(!isInterrupted()){
                if (!isCodeing){
                    break;
                }
                // 如果 eof是true 就表示读完了，就不执行putBuffer2Codec方法了
                //并不代表解码完了
                if (!isEOF) {
                    isEOF = putBuffer2Codec();
                }
                //...
                //从输出缓冲区获取数据  解码之后的数据
                int status = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100);
                //获取到有效的输出缓冲区 意味着能够获取到解码后的数据了
                if (status >= 0){
                    ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(status);
                    if (bufferInfo.size == outData.length){
                        //取出数据 存到outData yuv420
                        outputBuffer.get(outData);
                        if (null != mISurface){
                            mISurface.offer(outData);
                        }
                    }
                    //交付掉这个输出缓冲区 释放
                    mMediaCodec.releaseOutputBuffer(status,false);
                }
                //干完活了 ，全部解码完成了
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                    //解码完了
                    break;
                }
            }

            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
            mMediaExtractor.release();
            mMediaExtractor = null;
        }

        /**
         *
         * @return true:没有更多数据了
         *           false:还有
         */
        private boolean putBuffer2Codec(){
            // -1 就一直等待
            int status = mMediaCodec.dequeueInputBuffer(100);
            //有效的输入缓冲区 index
            if (status >=0 ){
                //把待解码数据加入MediaCodec
                ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(status);
                //清理脏数据
                inputBuffer.clear();
                // ByteBuffer当成byte数组 ，读数据存入 ByteBuffer 存到byte数组的第0个开始存
                int size = mMediaExtractor.readSampleData(inputBuffer, 0);
                //没读到数据 已经没有数据可读了
                if (size < 0){
                    //给个标记 表示没有更多数据可以从输出缓冲区获取了
                    mMediaCodec.queueInputBuffer(status,0,0,0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    return true;
                }else{
                    //把噻了数据的输入缓冲区噻回去
                    mMediaCodec.queueInputBuffer(status,0,size,
                            mMediaExtractor.getSampleTime(), 0);
                    //丢掉已经加入解码的数据 （不丢就会读重复的数据）
                    mMediaExtractor.advance();
                }
            }
            return false;
        }
    }

}
