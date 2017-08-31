package com.bozhong.document.junit;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

/**
 * Created by xiezg@317hu.com on 2017/4/26 0026.
 */
public class FileInfoDemo {
    public static void main(String args[]) {
        String accessKey = "45ydh5vmYJKSZ4mk6URz-Rqvj3VXtHO6nNqld3z8";
        String secretKey = "RChRCT0zU3P0OfK_hWz0uICoFTo_ANG-zDIImL1O";
        String bucket = "leorain";
        String key = "FgtlX-g6SHoFKSrCz-P_ionpfwrM";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth);
        try {
            FileInfo fileInfo = bucketManager.stat(bucket, key);
            System.out.println(fileInfo.hash);
            System.out.println(fileInfo.fsize);
            System.out.println(fileInfo.mimeType);
            System.out.println(fileInfo.putTime);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
    }
}
